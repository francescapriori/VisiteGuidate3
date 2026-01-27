package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.luoghi.Posizione;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.Giornate;
import it.unibs.ingdsw.model.tempo.GiornoSettimana;
import it.unibs.ingdsw.model.tempo.Orario;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.service.ServiceLuoghi;
import it.unibs.ingdsw.service.ServiceUtenti;
import it.unibs.ingdsw.service.ServiceVisite;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.view.cli.io.OutputManager;

import java.util.ArrayList;
import java.util.Arrays;

public class VisiteController {

    private final Applicazione applicazione;
    private final ServiceVisite serviceVisite;
    private final UtentiController utentiController;
    private final ServiceUtenti serviceVolontari;

    public VisiteController(UtentiController utentiController) {
        this.applicazione = Applicazione.getApplicazione();
        this.serviceVisite = new ServiceVisite(Applicazione.getApplicazione().getListaLuoghi().getTotaleVisite());
        this.utentiController = utentiController;
        this.serviceVolontari = new ServiceUtenti(Applicazione.getApplicazione());
    }

    public ListaVisite chiediVisite(Posizione posizioneLuogo, String luogoId) {
        ListaVisite lista = new ListaVisite();
        do {
            lista.aggiungiVisita(chiediVisita(posizioneLuogo, luogoId));
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un'altra visita?")));
        return lista;
    }

    public Visita chiediVisita(Posizione posizioneLuogo, String luogoId) {
        String titolo = InputManager.leggiStringaNonVuota("Inserisci il titolo della visita: ");
        String descrizione = InputManager.leggiStringaNonVuota("Inserisci la descrizione della visita: ");

        boolean diverso = "sì".equals(InputManager.chiediSiNo(
                "Vuoi definire un luogo di incontro differente rispetto alla posizione del luogo?"));
        Posizione luogoIncontro = diverso ? LuogoController.chiediPosizione() : posizioneLuogo;

        Giornate giornate = new Giornate();
        ArrayList<GiornoSettimana> disponibili = new ArrayList<>(Arrays.asList(GiornoSettimana.values()));
        do {
            if (disponibili.isEmpty()) {
                OutputManager.visualizzaMessaggio("Non ci sono altri giorni disponibili.");
                break;
            }
            OutputManager.visualizzaMessaggio("Definisci i giorni della settimana in cui verrà effettuata la visita: ");
            for (int i = 0; i < disponibili.size(); i++) {
                OutputManager.visualizzaMessaggio((i + 1) + ") " + disponibili.get(i));
            }
            int scelta = InputManager.leggiInteroConMinMax(
                    "Seleziona (1-" + disponibili.size() + "): ",
                    1,
                    disponibili.size()
            );
            GiornoSettimana g = disponibili.get(scelta - 1);
            giornate.aggiungiGiornoDellaSettimana(g);
            OutputManager.visualizzaMessaggio("Aggiunto: " + g);
            disponibili.remove(g);
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un'altra giornata per la visita?")));

        Data inizio, fine;
        do {
            OutputManager.visualizzaMessaggio("Inserire la data di inizio validità della visita: ");
            inizio = DateController.chiediData();
            OutputManager.visualizzaMessaggio("Inserire la data di fine validità della visita: ");
            fine = DateController.chiediData();
            if (!inizio.precede(fine))
                OutputManager.visualizzaMessaggio("Errore: la data di fine deve essere successiva alla data di inizio.");
        } while (!inizio.precede(fine));

        OutputManager.visualizzaMessaggio("Inserire l'orario di inizio della visita: ");
        int hh = InputManager.leggiInteroConMinMax("Ora: ", 0, 23);
        int mm = InputManager.leggiInteroConMinMax("Minuti: ", 0, 59);
        Orario oraInizio = new Orario(hh, mm);

        int durataMin = InputManager.leggiInteroConMin("Inserire la durata in minuti della visita: ", 1);

        boolean biglietto = "sì".equals(InputManager.chiediSiNo("È presente un biglietto di ingresso da pagare?"));

        ArrayList<Volontario> volontariThisVisita = utentiController.scegliVolontari();

        int numMinP = InputManager.leggiInteroConMin("Numero minimo partecipanti: ", 1);
        int numMaxP = InputManager.leggiInteroConMin("Numero massimo partecipanti: ", numMinP);

        return new Visita(
                titolo, descrizione, luogoId, luogoIncontro, giornate,
                inizio, fine, oraInizio, durataMin, biglietto, volontariThisVisita, numMinP, numMaxP
        );
    }

    public ArrayList<Volontario> associaVolontariAvisita(Applicazione applicazione) {
        String scelta = InputManager.chiediSiNo("Vuoi associare volontari già registrati nell'applicativo?");
        ArrayList<Volontario> volontariThisVisita = new ArrayList<>();
        if (scelta.equals("sì")){
            volontariThisVisita = utentiController.scegliVolontari();
        } else {
            do {
                Volontario nuovo = new Volontario(InputManager.richiediNuovoUsername(applicazione.getListaUtenti()), InputManager.richiediPasswordLogin());
                applicazione.getListaUtenti().aggiungiUtente(nuovo); // volontari aggiunti anche alla lista degli utenti generale
                volontariThisVisita.add(nuovo);
            } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un altro volontario?")));
        }
        return volontariThisVisita;
    }

    public void aggiungiVisita(String nomeMeseProduzione, int annoProduzione) {
        if(applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            OutputManager.visualizzaLuoghi(applicazione.getListaLuoghi());
            int scelta = InputManager.leggiInteroConMinMax(
                    "\nSeleziona il luogo di cui si vuole aggiungere la/e visita/e: ",
                    1, this.applicazione.getListaLuoghi().getNumeroLuogo());
            Luogo luogo = this.applicazione.getListaLuoghi().scegliLuogo(scelta-1);
            ListaVisite listaDaAggiungere = chiediVisite(luogo.getPosizione(), luogo.getLuogoID());
            luogo.aggiungiVisite(listaDaAggiungere);
        }
        else {
            OutputManager.visualizzaMessaggio("Non è possibile aggiungere nessuna visita a nessun luogo: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

    public void aggiungiVolontatiAVisita(String nomeMeseProduzione, int annoProduzione) {
        if(applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            OutputManager.visualizzaLuoghiEvisite(this.applicazione.getListaLuoghi());
            int scelta = InputManager.leggiInteroConMinMax(
                    "\nSeleziona il luogo di cui si vuole selezionare la visita: ",
                    1, this.applicazione.getListaLuoghi().getNumeroLuogo());
            Luogo luogo = this.applicazione.getListaLuoghi().scegliLuogo(scelta-1);
            OutputManager.visualizzaListaVisite(luogo.getInsiemeVisite());
            int scelta2 = InputManager.leggiInteroConMinMax(
                    "\nSeleziona la visita a cui si vuole aggiungere un volontario: ",
                    1, luogo.getInsiemeVisite().getNumeroVisite());
            Visita visita = serviceVisite.scegliVisita(luogo, scelta2);

            serviceVolontari.aggiungiVolontariAllaVisita(visita, associaVolontariAvisita(this.applicazione));
        }
        else {
            OutputManager.visualizzaMessaggio("Non è possibile aggiungere volontari a nessuna visita: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

    public void visualizzaVisiteDelVol(Utente utente) {
        OutputManager.visualizzaMessaggio("Volontario " + utente.toString() + " sei stato associato alle seguenti visite: ");
        ListaVisite lista = serviceVisite.visiteDelVolontario((Volontario) utente);
        OutputManager.visualizzaListaVisite(lista);
        if (lista.getListaVisite().isEmpty()) {
            OutputManager.visualizzaMessaggio("Nessuna visita disponibile.");
        }
    }
}
