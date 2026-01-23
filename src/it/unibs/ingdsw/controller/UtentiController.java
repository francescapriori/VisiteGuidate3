package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.service.ServiceUtenti;
import it.unibs.ingdsw.view.cli.io.InputManager;

import java.util.ArrayList;

public class UtentiController {

    private final ServiceUtenti serviceUtenti;

    public UtentiController(ServiceUtenti serviceUtenti) {
        this.serviceUtenti = serviceUtenti;
    }

    public ArrayList<Volontario> scegliVolontari() {
        ArrayList<Volontario> scelti = new ArrayList<>();
        ArrayList<Volontario> volontariDisponibili =
                new ArrayList<>(serviceUtenti.getVolontari());
        ListaUtenti sceltiUtenti = new ListaUtenti();
        boolean continua;
        do {
            String tipo = InputManager.chiediSiNo("Vuoi associare un volontario già registrato nell'applicativo?");
            boolean usaRegistrato = "sì".equalsIgnoreCase(tipo.trim());
            if (usaRegistrato && volontariDisponibili.isEmpty()) {
                System.out.println("Non ci sono volontari registrati disponibili. Inserimento di un nuovo volontario.");
                usaRegistrato = false;
            }
            if (usaRegistrato) {
                Volontario sel;
                do {
                    System.out.println("Seleziona il volontario:");
                    for (int i = 0; i < volontariDisponibili.size(); i++) {
                        System.out.println((i + 1) + ") " + volontariDisponibili.get(i).getUsername());
                    }
                    int scelta = InputManager.leggiInteroConMinMax("Scelta (1-" + volontariDisponibili.size() + "): ",1, volontariDisponibili.size());
                    sel = volontariDisponibili.get(scelta - 1);
                    if (sceltiUtenti.usernameInUso(sel.getUsername())) {
                        System.out.println("Volontario già selezionato, scegline un altro.");
                    }
                } while (sceltiUtenti.usernameInUso(sel.getUsername()));
                scelti.add(sel);
                sceltiUtenti.aggiungiUtente(sel);
                volontariDisponibili.remove(sel);
                System.out.println("Aggiunto: " + sel.getUsername());
            } else {
                Volontario nuovo;
                do {
                    nuovo = new Volontario(InputManager.richiediUsernameLogin(), InputManager.richiediPasswordLogin());
                    if (serviceUtenti.getListaUtenti().usernameInUso(nuovo.getUsername())) {
                        System.out.println("Username già presente, riprova.");
                    }
                } while (serviceUtenti.getListaUtenti().usernameInUso(nuovo.getUsername()));
                scelti.add(nuovo);
                sceltiUtenti.aggiungiUtente(nuovo);
                System.out.println("Aggiunto nuovo volontario: " + nuovo.getUsername());
            }
            continua = "sì".equalsIgnoreCase(InputManager.chiediSiNo("Vuoi aggiungere un altro volontario?").trim());

        } while (continua);

        return scelti;
    }

}
