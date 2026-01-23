package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.luoghi.Posizione;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.service.ServiceLuoghi;
import it.unibs.ingdsw.view.cli.io.InputManager;

public class LuogoController {

    private final ServiceLuoghi serviceLuoghi;
    private final VisiteController visiteController;

    public LuogoController(ServiceLuoghi serviceLuoghi, VisiteController visiteController) {
        this.serviceLuoghi = serviceLuoghi;
        this.visiteController = visiteController;
    }

    public void configuraLuoghi() {
        ListaLuoghi lista = new ListaLuoghi();
        do {
            lista.aggiungiLuogo(chiediLuogo());
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un altro luogo?")));
        serviceLuoghi.setListaLuoghi(lista);
    }

    public void aggiungiLuoghiSeNonPresenti() {
        ListaLuoghi lista = new ListaLuoghi();
        do {
            lista.aggiungiLuogo(chiediLuogo());
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un altro luogo?")));
        serviceLuoghi.aggiungiLuoghiSeNonPresenti(lista);
    }

    public Luogo chiediLuogo() {
        String id = serviceLuoghi.getListaLuoghi().generaProssimoId();
        String nome = InputManager.leggiStringaNonVuota("Inserisci il nome del luogo: ");
        String descrizione = InputManager.leggiStringaNonVuota("Inserisci la descrizione del luogo: ");
        Posizione posizione = InputManager.chiediPosizione();
        ListaVisite listaVisite = visiteController.chiediVisite(posizione, id);
        return new Luogo(id, nome, descrizione, posizione, listaVisite);
    }
}
