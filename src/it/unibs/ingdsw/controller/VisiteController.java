package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.luoghi.Posizione;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.Giornate;
import it.unibs.ingdsw.model.tempo.GiornoSettimana;
import it.unibs.ingdsw.model.tempo.Orario;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.service.ServiceUtenti;
import it.unibs.ingdsw.service.ServiceVisite;
import it.unibs.ingdsw.view.cli.io.InputManager;

import java.util.ArrayList;
import java.util.Arrays;

public class VisiteController {

    private final ServiceVisite serviceVisite;
    private final UtentiController utentiController;

    public VisiteController(ServiceVisite serviceVisite, UtentiController utentiController) {
        this.serviceVisite = serviceVisite;
        this.utentiController = utentiController;
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

        // luogo di incontro (può coincidere con la posizione del luogo)
        boolean diverso = "sì".equals(InputManager.chiediSiNo(
                "Vuoi definire un luogo di incontro differente rispetto alla posizione del luogo?"));
        Posizione luogoIncontro = diverso ? InputManager.chiediPosizione() : posizioneLuogo;

        // giorni della settimana
        Giornate giornate = new Giornate();
        ArrayList<GiornoSettimana> disponibili = new ArrayList<>(
                Arrays.asList(GiornoSettimana.values())
        );
        do {
            if (disponibili.isEmpty()) {
                System.out.println("Non ci sono altri giorni disponibili.");
                break;
            }
            System.out.println("Definisci i giorni della settimana in cui verrà effettuata la visita: ");
            for (int i = 0; i < disponibili.size(); i++) {
                System.out.println((i + 1) + ") " + disponibili.get(i));
            }
            int scelta = InputManager.leggiInteroConMinMax(
                    "Seleziona (1-" + disponibili.size() + "): ",
                    1,
                    disponibili.size()
            );
            GiornoSettimana g = disponibili.get(scelta - 1);
            giornate.aggiungiGiornoDellaSettimana(g);
            System.out.println("Aggiunto: " + g);
            disponibili.remove(g);
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un'altra giornata per la visita?")));


        // validità (inizio <= fine)
        Data inizio, fine;
        do {
            System.out.println("Inserire la data di inizio validità della visita: ");
            inizio = InputManager.chiediData();
            System.out.println("Inserire la data di fine validità della visita: ");
            fine = InputManager.chiediData();
            if (!inizio.precede(fine)) System.out.println("Errore: la data di fine deve essere successiva alla data di inizio.");
        } while (!inizio.precede(fine));

        // orario inizio
        System.out.println("Inserire l'orario di inizio della visita: ");
        int hh = InputManager.leggiInteroConMinMax("Ora: ", 0, 23);
        int mm = InputManager.leggiInteroConMinMax("Minuti: ", 0, 59);
        Orario oraInizio = new Orario(hh, mm);

        // durata
        int durataMin = InputManager.leggiInteroConMin("Inserire la durata in minuti della visita: ", 1);

        // biglietto
        boolean biglietto = "sì".equals(InputManager.chiediSiNo("È presente un biglietto di ingresso da pagare?"));

        // volontari: usa la lista utenti dell'istanza
        ArrayList<Volontario> volontariThisVisita = utentiController.scegliVolontari();

        // capienze
        int numMinP = InputManager.leggiInteroConMin("Numero minimo partecipanti: ", 1);
        int numMaxP = InputManager.leggiInteroConMin("Numero massimo partecipanti: ", numMinP);

        return new Visita(
                titolo, descrizione, luogoId, luogoIncontro, giornate,
                inizio, fine, oraInizio, durataMin, biglietto, volontariThisVisita, numMinP, numMaxP
        );
    }

    public ArrayList<Volontario> associaVolontariAvisita(Applicazione applicazione, Visita visita, ServiceUtenti serviceUtenti) {
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
}
