package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.luoghi.Posizione;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.service.ServiceLuoghi;
import it.unibs.ingdsw.service.ServiceUtenti;
import it.unibs.ingdsw.service.ServiceVisite;
import it.unibs.ingdsw.view.VisiteView;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.view.LuoghiView;
import it.unibs.ingdsw.view.cli.io.Output;

public class LuogoController {

    private final Applicazione applicazione;
    private final ServiceLuoghi serviceLuoghi;
    private final VisiteController visiteController;
    private final ServiceUtenti serviceVolontari;
    private final ServiceVisite serviceVisite;
    private final LuoghiView luoghiView;
    private final VisiteView visiteView;
    private final Output out;

    public LuogoController(VisiteController visiteController, Output out) {
        this.applicazione = Applicazione.getApplicazione();
        this.serviceLuoghi = new ServiceLuoghi(Applicazione.getApplicazione().getListaLuoghi());
        this.visiteController = visiteController;
        this.serviceVolontari = new ServiceUtenti(Applicazione.getApplicazione());
        this.serviceVisite = new ServiceVisite(Applicazione.getApplicazione().getListaLuoghi().getTotaleVisite());
        this.luoghiView = new LuoghiView(out);
        this.visiteView = new VisiteView(out);
        this.out = out;
    }

    public void configuraLuoghi() {
        ListaLuoghi lista = new ListaLuoghi();
        do {
            lista.aggiungiLuogo(chiediLuogo());
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un altro luogo?")));
        this.applicazione.setListaLuoghi(lista);
    }

    public void aggiungiLuoghiSeNonPresenti() {
        ListaLuoghi lista = new ListaLuoghi();
        do {
            lista.aggiungiLuogo(chiediLuogo());
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un altro luogo?")));
        serviceLuoghi.aggiungiLuoghiSeNonPresenti(lista);
    }

    public Luogo chiediLuogo() {
        String id = this.applicazione.getListaLuoghi().generaProssimoId();
        String nome = InputManager.leggiStringaNonVuota("Inserisci il nome del luogo: ");
        String descrizione = InputManager.leggiStringaNonVuota("Inserisci la descrizione del luogo: ");
        Posizione posizione = chiediPosizione();
        ListaVisite listaVisite = visiteController.chiediVisite(posizione, id);
        return new Luogo(id, nome, descrizione, posizione, listaVisite);
    }

    public static Posizione chiediPosizione() {
        return new Posizione(
                InputManager.leggiStringaNonVuota("Inserisci il nome del paese: "),
                InputManager.leggiStringaNonVuota("Inserisci il nome della via: "),
                chiediCAP(),
                InputManager.leggiDouble("Latitudine (usa la virgola per i decimali): ", Posizione.LAT_MIN, Posizione.LAT_MAX),
                InputManager.leggiDouble("Longitudine (usa la virgola per i decimali): ", Posizione.LON_MIN, Posizione.LON_MAX)
        );
    }

    public static String chiediCAP() {
        while (true) {
            String cap = InputManager.leggiStringaNonVuota("Inserire il CAP: ").trim();
            if (cap.matches("\\d{" + Posizione.LUNGHEZZA_CAP + "}")) return cap;
            System.out.println("Errore: il CAP deve essere un numero di " + Posizione.LUNGHEZZA_CAP + " cifre.");
        }
    }

    public void visualizzaElencoLuoghi() {
        out.println("Elenco dei luoghi visitabili disponibili: ");
        luoghiView.visualizzaLuoghi(applicazione.getListaLuoghi());
    }

    public void visualizzaLuoghiEvisite() {
        out.println("-----\nLista de Luoghi registrati con relative visite: ");
        luoghiView.visualizzaLuoghiEvisite(applicazione.getListaLuoghi());
    }

    public void aggiungiLuogo(String nomeMeseProduzione, int annoProduzione) {
        if(applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            aggiungiLuoghiSeNonPresenti();
        }
        else {
            out.println("Non è possibile aggiungere un nuovo luogo all'elenco: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

    public void eliminaLuogo(String nomeMeseProduzione, int annoProduzione) {
        if(applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            if(!this.applicazione.getListaLuoghi().getListaLuoghi().isEmpty()) {
                luoghiView.visualizzaLuoghi(this.applicazione.getListaLuoghi());
                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il luogo che si vuole rimuovere: ",
                        1, this.applicazione.getListaLuoghi().getNumeroLuogo());
                Luogo luogo = this.applicazione.getListaLuoghi().scegliLuogo(scelta-1);
                this.applicazione.getListaLuoghi().rimuoviLuogo(luogo.getNome());
                out.println("Hai eliminato il luogo  " + luogo.getNome() + " - " + luogo.getLuogoID());
                // Se un volontario rimane senza visite, allora viene eliminato
                serviceVolontari.eliminaSeSenzaVisita();
            }
            else {
                out.println("Nessun luogo registrato, impossibile rimuovere un luogo.");
            }

        }
        else {
            out.println("Non è possibile rimuovere nessun luogo: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

    public void eliminaVisitaDaLuogo(String nomeMeseProduzione, int annoProduzione) {
        if(applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            if(!this.applicazione.getListaLuoghi().getListaLuoghi().isEmpty()) {
                luoghiView.visualizzaLuoghiEvisite(this.applicazione.getListaLuoghi());
                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il luogo di cui si vuole selezionare la visita: ",
                        1, this.applicazione.getListaLuoghi().getNumeroLuogo());
                Luogo luogo = this.applicazione.getListaLuoghi().scegliLuogo(scelta-1);
                visiteView.visualizzaListaVisite(luogo.getInsiemeVisite());
                int scelta2 = InputManager.leggiInteroConMinMax(
                        "\nSeleziona la visita che si vuole rimuovere: ",
                        1, luogo.getInsiemeVisite().getNumeroVisite());
                Visita visita = serviceVisite.scegliVisita(luogo, scelta2);

                serviceVisite.rimuoviVisita(visita, luogo);

                // Se un luogo rimane senza visite, allora il luogo viene rimosso
                serviceLuoghi.rimuoviLuogoSeSenzaVisite();

                // Se un volontario rimane senza visite, allora viene eliminato
                serviceVolontari.eliminaSeSenzaVisita();
            }
            else {
                out.println("Nessun luogo registrato, impossibile rimuovere una visita associata ad un luogo.");
            }

        }
        else {
            out.println("Non è possibile rimuovere nessuna visita associata a nessun luogo: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }
}
