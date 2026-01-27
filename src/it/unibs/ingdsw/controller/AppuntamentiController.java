package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.service.ServiceVisite;
import it.unibs.ingdsw.view.cli.io.OutputManager;
import it.unibs.ingdsw.service.ServiceAppuntamenti;

import java.util.HashMap;
import java.util.Map;

public class AppuntamentiController {

    private final Applicazione applicazione;
    private final ServiceAppuntamenti serviceAppuntamenti;
    private final UtentiController utentiController;

    public AppuntamentiController() {
        this.applicazione = Applicazione.getApplicazione();
        this.serviceAppuntamenti = new ServiceAppuntamenti(Applicazione.getApplicazione().getCalendarioAppuntamenti());
        this.utentiController = new UtentiController();
    }

    public void visualizzaAppuntamentiPerStato(int meseProduzione, int annoProduzione) {
        OutputManager.visualizzaAppuntamentiPerStato(serviceAppuntamenti.getAppuntamentiDelMeseTarget(meseProduzione, annoProduzione), true);
    }

    public void produzioneVisite(int meseProduzione, String nomeMeseProduzione, int annoProduzione) {
        if(applicazione.getStatoProd() == StatoProduzioneVisite.NON_PRODOTTE) {

            InsiemeAppuntamenti calendario = produciVisitePerIlMese(meseProduzione, annoProduzione);

            OutputManager.visualizzaCalendario(calendario, nomeMeseProduzione, annoProduzione);
            this.applicazione.setCalendarioAppuntamenti(calendario);
            this.applicazione.setStatoProduzione(StatoProduzioneVisite.PRODOTTE);
        }
        else {
            OutputManager.visualizzaMessaggio("È stato già prodotto il piano delle visite per il mese di " + nomeMeseProduzione + " " + annoProduzione);
        }
    }

    public InsiemeAppuntamenti produciVisitePerIlMese(int meseTargetV, int annoTargetV) {
        ServiceVisite serviceVisite = new ServiceVisite(Applicazione.getApplicazione().getListaLuoghi().getTotaleVisite());
        HashMap<Visita, InsiemeDate> calendarioProvvisorio =
                serviceVisite.calendarioProvvisiorioVisiteDelMese(meseTargetV, annoTargetV);

        InsiemeAppuntamenti calendarioAppuntamenti = new InsiemeAppuntamenti();
        HashMap<Volontario, InsiemeDate> volontariConDate = Applicazione.getApplicazione().getDisponibilitaPerVol();

        for (Map.Entry<Visita, InsiemeDate> entry : calendarioProvvisorio.entrySet()) {
            Visita visita = entry.getKey();
            InsiemeDate dateCalendarioProvvisorio = entry.getValue();

            for (Data d1 : dateCalendarioProvvisorio.getInsiemeDate()) {

                boolean assegnato = false;

                for (Map.Entry<Volontario, InsiemeDate> entry2 : volontariConDate.entrySet()) {
                    Volontario volontario = entry2.getKey();
                    InsiemeDate dateDisponibilitaVolontario = entry2.getValue();

                    if (dateDisponibilitaVolontario.dataPresente(d1) &&
                            !utentiController.volontarioGiaPresenteInData(d1, volontario)) {

                        calendarioAppuntamenti.getAppuntamenti()
                                .add(new Appuntamento(visita, d1, volontario));

                        assegnato = true;
                        break;
                    }
                }

            }
        }

        return calendarioAppuntamenti;
    }

    public void visualizzaAppuntamentiPerStato(String nomeMeseProduzione, int annoProduzione, Utente utente) {
        if (applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            OutputManager.visualizzaAppuntamentiPerStato(applicazione.getPrenotazioni(), serviceAppuntamenti.getAppuntamentiDellUtente((Volontario) utente), new StatoAppuntamento[] {StatoAppuntamento.CONFERMATA, StatoAppuntamento.CANCELLATA});
        } else {
            OutputManager.visualizzaMessaggio("Non è possibile visualizzare le tue visite confermate: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione + " da parte del Configuratore.");
        }
    }

    public void visualizzaAppuntamentiIscritto (int meseProduzione, String nomeMeseProduzione, int annoProduzione) {
        if (applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            OutputManager.visualizzaPrenotazioni(applicazione.getPrenotazioni(), serviceAppuntamenti.getAppuntamentiDelMeseTarget(meseProduzione, annoProduzione).getAppuntamenti(), new StatoAppuntamento[] {StatoAppuntamento.PROPOSTA, StatoAppuntamento.CANCELLATA, StatoAppuntamento.CONFERMATA}, nomeMeseProduzione, annoProduzione); // todo forse modificare la lista in modo dinamico: non vedo più gli appuntamenti per cui ho già fatto una prenotazione
        } else {
            OutputManager.visualizzaMessaggio("Non è possibile visualizzare gli appuntamenti per stato: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

    public void visualizzaAppuntamentiTot(int meseProduzione, String nomeMeseProduzione, int annoProduzione) {
        if (applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            OutputManager.visualizzaAppuntamentiPerStato(serviceAppuntamenti.getAppuntamentiDelMeseTarget(meseProduzione, annoProduzione), false);
        } else {
            OutputManager.visualizzaMessaggio("Non è possibile visualizzare gli appuntamenti per stato: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

}
