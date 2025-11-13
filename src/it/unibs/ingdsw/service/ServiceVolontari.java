package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.output.OutputManager;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;

import java.util.HashMap;

public class ServiceVolontari {
    public Applicazione applicazione;

    public ServiceVolontari(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public HashMap<Volontario, ListaVisite> getVolontariConVisiteAssociate() {

        HashMap<Volontario, ListaVisite> volontarioPerVisita = new HashMap<Volontario, ListaVisite>();

        for (Volontario vol : this.applicazione.getListaUtenti().getVolontari()) {
            ListaVisite lv = this.applicazione.getListaLuoghi().visiteDelVolontario(vol);
            volontarioPerVisita.put(vol, lv);
        }

        return volontarioPerVisita;
    }
}
