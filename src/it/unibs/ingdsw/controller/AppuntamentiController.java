package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.service.ServiceVisite;
import it.unibs.ingdsw.view.PrenotazioniView;
import it.unibs.ingdsw.view.cli.io.Output;
import it.unibs.ingdsw.service.ServiceAppuntamenti;
import it.unibs.ingdsw.view.AppuntamentiView;

import java.util.HashMap;
import java.util.Map;

public class AppuntamentiController {

    private final Applicazione applicazione;
    private final ServiceAppuntamenti serviceAppuntamenti;
    private final UtentiController utentiController;
    private final AppuntamentiView appuntamentiView;
    private final PrenotazioniView prenotazioniView;
    private final Output out;

    public AppuntamentiController(Output out) {
        this.applicazione = Applicazione.getApplicazione();
        this.serviceAppuntamenti = new ServiceAppuntamenti(Applicazione.getApplicazione().getCalendarioAppuntamenti());
        this.utentiController = new UtentiController(out);
        this.appuntamentiView = new AppuntamentiView(out);
        this.prenotazioniView = new PrenotazioniView(out);
        this.out = out;
    }

    public void visualizzaAppuntamentiPerStato(int meseProduzione, int annoProduzione) {
        StatoAppuntamento[] tutti = StatoAppuntamento.values();
        for (StatoAppuntamento stato : tutti) {
            appuntamentiView.visualizzaAppuntamentiPerStato(serviceAppuntamenti.getAppuntamentiDelMeseTarget(meseProduzione, annoProduzione), stato);
        }
    }

    public void produzioneVisite(int meseProduzione, String nomeMeseProduzione, int annoProduzione) {
        if(applicazione.getStatoProd() == StatoProduzioneVisite.NON_PRODOTTE) {
            InsiemeAppuntamenti calendario = produciVisitePerIlMese(meseProduzione, annoProduzione);
            appuntamentiView.visualizzaCalendario(calendario, nomeMeseProduzione, annoProduzione);
            this.applicazione.setCalendarioAppuntamenti(calendario);
            this.applicazione.setStatoProduzione(StatoProduzioneVisite.PRODOTTE);
        }
        else {
            out.println("È stato già prodotto il piano delle visite per il mese di " + nomeMeseProduzione + " " + annoProduzione);
        }
    }

    // forse è più service...
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
                for (Map.Entry<Volontario, InsiemeDate> entry2 : volontariConDate.entrySet()) {
                    Volontario volontario = entry2.getKey();
                    InsiemeDate dateDisponibilitaVolontario = entry2.getValue();

                    if (dateDisponibilitaVolontario.dataPresente(d1) &&
                            !utentiController.volontarioGiaPresenteInData(d1, volontario)) {

                        calendarioAppuntamenti.getAppuntamenti()
                                .add(new Appuntamento(visita, d1, volontario));
                        break;
                    }
                }

            }
        }

        return calendarioAppuntamenti;
    }

    public void visualizzaAppuntamentiPerStato(String nomeMeseProduzione, int annoProduzione, int meseProduzione) {
        if (applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            StatoAppuntamento[] tutti = {
                    StatoAppuntamento.CONFERMATA,
                    StatoAppuntamento.CANCELLATA
            };
            for (StatoAppuntamento stato : tutti) {
                appuntamentiView.visualizzaAppuntamentiPerStato(serviceAppuntamenti.getAppuntamentiDelMeseTarget(meseProduzione, annoProduzione), stato);
            }
        } else {
            out.println("Non è possibile visualizzare le tue visite: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione + " da parte del Configuratore.");
        }
    }

    public void visualizzaAppuntamentiIscritto (String nomeMeseProduzione, int annoProduzione) {
        if (applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            prenotazioniView.visualizzaPrenotazioni(applicazione.getPrenotazioni(), new StatoAppuntamento[] {StatoAppuntamento.PROPOSTA, StatoAppuntamento.CANCELLATA, StatoAppuntamento.CONFERMATA}, nomeMeseProduzione, annoProduzione); // todo forse modificare la lista in modo dinamico: non vedo più gli appuntamenti per cui ho già fatto una prenotazione
        } else {
            out.println("Non è possibile visualizzare gli appuntamenti per stato: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

    public void visualizzaAppuntamentiDisponibili(int meseProduzione, String nomeMeseProduzione, int annoProduzione) {
        if (applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            StatoAppuntamento[] tutti = {
                    StatoAppuntamento.PROPOSTA,
                    StatoAppuntamento.CONFERMATA
            };
            for (StatoAppuntamento stato : tutti) {
                appuntamentiView.visualizzaAppuntamentiPerStato(serviceAppuntamenti.getAppuntamentiDelMeseTarget(meseProduzione, annoProduzione), stato);
            }
        } else {
            out.println("Non è possibile visualizzare gli appuntamenti per stato: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

}
