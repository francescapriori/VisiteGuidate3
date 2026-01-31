package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.applicazione.StatoRichiestaDisponibilita;
import it.unibs.ingdsw.model.applicazione.Target;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.service.ServiceDate;
import it.unibs.ingdsw.service.ServiceLuoghi;
import it.unibs.ingdsw.service.ServiceUtenti;
import it.unibs.ingdsw.service.ServiceVisite;
import it.unibs.ingdsw.view.DateView;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.view.cli.io.Output;
import it.unibs.ingdsw.view.UtentiView;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UtentiController {

    private final Applicazione applicazione;
    private final ServiceUtenti serviceUtenti;
    private final ServiceVisite serviceVisite;
    private final ServiceLuoghi serviceLuoghi;
    private final ServiceDate serviceDate;
    private final UtentiView utentiView;
    private final DateView dateView;
    private final DateController dateController;
    private final Output out;

    public UtentiController(Output out) {
        this.applicazione = Applicazione.getApplicazione();
        this.serviceUtenti = new ServiceUtenti(Applicazione.getApplicazione());
        this.serviceVisite = new ServiceVisite(Applicazione.getApplicazione().getListaLuoghi().getTotaleVisite());
        this.serviceLuoghi = new ServiceLuoghi(Applicazione.getApplicazione().getListaLuoghi());
        this.serviceDate = new ServiceDate(Applicazione.getApplicazione().getDateEscluse());
        this.utentiView = new UtentiView(out);
        this.dateView = new DateView(out);
        this.dateController = new DateController(out);
        this.out = out;
    }

    public List<Volontario> scegliVolontari() {
        List<Volontario> scelti = new ArrayList<>();
        List<Volontario> volontariDisponibili =
                new ArrayList<>(this.applicazione.getListaUtenti().getVolontari());
        ListaUtenti sceltiUtenti = new ListaUtenti();
        boolean continua;
        do {
            String tipo = InputManager.chiediSiNo("Vuoi associare un volontario già registrato nell'applicativo?");
            boolean usaRegistrato = "sì".equalsIgnoreCase(tipo.trim());
            if (usaRegistrato && volontariDisponibili.isEmpty()) {
                out.println("Non ci sono volontari registrati disponibili. Inserimento di un nuovo volontario.");
                usaRegistrato = false;
            }
            if (usaRegistrato) {
                Volontario sel;
                do {
                    System.out.println("Seleziona il volontario:");
                    for (int i = 0; i < volontariDisponibili.size(); i++) {
                        out.println((i + 1) + ") " + volontariDisponibili.get(i).getUsername());
                    }
                    int scelta = InputManager.leggiInteroConMinMax("Scelta (1-" + volontariDisponibili.size() + "): ",1, volontariDisponibili.size());
                    sel = volontariDisponibili.get(scelta - 1);
                    if (sceltiUtenti.usernameInUso(sel.getUsername())) {
                        out.println("Volontario già selezionato, scegline un altro.");
                    }
                } while (sceltiUtenti.usernameInUso(sel.getUsername()));
                scelti.add(sel);
                sceltiUtenti.aggiungiUtente(sel);
                volontariDisponibili.remove(sel);
                out.println("Aggiunto: " + sel.getUsername());
            } else {
                Volontario nuovo;
                do {
                    nuovo = new Volontario(InputManager.richiediUsernameLogin(), InputManager.richiediPasswordLogin());
                    if (serviceUtenti.getListaUtenti().usernameInUso(nuovo.getUsername())) {
                        out.println("Username già presente, riprova.");
                    }
                } while (serviceUtenti.getListaUtenti().usernameInUso(nuovo.getUsername()));
                scelti.add(nuovo);
                sceltiUtenti.aggiungiUtente(nuovo);
                out.println("Aggiunto nuovo volontario: " + nuovo.getUsername());
            }
            continua = "sì".equalsIgnoreCase(InputManager.chiediSiNo("Vuoi aggiungere un altro volontario?").trim());

        } while (continua);

        return scelti;
    }

    public void visualizzaVolontariConVisite(){
        HashMap<Volontario, ListaVisite> volontarioPerVisita = serviceUtenti.getVolontariConVisiteAssociate();
        utentiView.visualizzaVolontariConVisiteAssociate(volontarioPerVisita);
    }

    public void eliminaVolontario(String nomeMeseProduzione, int annoProduzione) {
        if(applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            if(!this.applicazione.getListaUtenti().getVolontari().isEmpty()) {
                utentiView.visualizzaSoloVolontari(this.applicazione);

                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il volontario che si vuole eliminare: ",
                        1, this.applicazione.getListaUtenti().getNumeroVolontari());
                serviceUtenti.eliminaVolontari(scelta-1);

                // se una visita rimane senza volontari viene rimossa
                serviceVisite.eliminaSeSenzaVolontari();

                // se un luogo rimane senza visite viene rimosso
                serviceLuoghi.rimuoviLuogoSeSenzaVisite();
            }
            else {
                out.println("Nessun volontario registrato, impossibile rimuovere un volontario.");
            }
        }
        else {
            out.println("Non è possibile rimuovere un volontario: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

    public void indicaDisponibilita(int meseDisponibilita, String nomeMeseDisponibilita, int annoDisponibilita, Volontario utente) {
        if(applicazione.getStatoDisp()== StatoRichiestaDisponibilita.DISP_APERTE) {
            InsiemeDate dateEscluse = applicazione.getDateEscluse().getDatePerMeseAnno(meseDisponibilita, annoDisponibilita);
            dateView.visualizzaDatePerMeseAnno(dateEscluse, meseDisponibilita, annoDisponibilita, "in cui ti sei reso disponibile");
            String nomeMese = Month.of(meseDisponibilita).getDisplayName(TextStyle.FULL, Locale.ITALIAN);
            InsiemeDate insiemePerVolontario = serviceDate.getDatePerVolontario(meseDisponibilita, annoDisponibilita, utente);
            insiemePerVolontario = insiemePerVolontario.filtraDateDopo(new Data(Target.SOGLIA_CAMBIO_MESE, meseDisponibilita, annoDisponibilita)); // le date restituite sono solo quelle del mese di interesse
            dateView.visualizzaDatePerMeseAnno(insiemePerVolontario, meseDisponibilita, annoDisponibilita, "in cui ti sei reso disponibile");
            out.println("Procedi inserendo le tue disponibilità per " + nomeMese + " " + annoDisponibilita);
            do {
                Data data;
                do {
                    int giorno = dateController.chiediGiorno(meseDisponibilita, annoDisponibilita);
                    data = new Data(giorno, meseDisponibilita, annoDisponibilita);
                    if (!dateEscluse.getInsiemeDate().isEmpty() && dateEscluse.dataPresente(data)) {
                        out.println("Non puoi dare disponibilità per " + data + " perché è una data esclusa.");
                    } else if (insiemePerVolontario.dataPresente(data)) {
                        out.println("Hai già dato disponibilità per " + data + ".");
                    }
                } while ((dateEscluse.dataPresente(data)) || insiemePerVolontario.dataPresente(data));

                if (!insiemePerVolontario.aggiungiData(data)) {
                    out.println("La data è già presente nell'elenco.");
                }
            } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un'altra data in cui sei disponibile?")));
        }
        else {
            out.println("La raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita + " sono chiuse.");
        }
    }

    public boolean volontarioGiaPresenteInData(Data data, Volontario vol) {
        for (Appuntamento a : this.applicazione.getCalendarioAppuntamenti().getAppuntamenti()) {
            if (a.getData().dateUguali(data) && a.getGuida() != null && a.getGuida().utenteUguale(vol)) {
                return true;
            }
        }
        return false;
    }
}
