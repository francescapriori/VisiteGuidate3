package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;
import it.unibs.ingdsw.visite.StatoVisita;
import it.unibs.ingdsw.visite.Visita;

public class ServiceVisite {

    public Applicazione applicazione;

    public ServiceVisite(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public ListaVisite getListaVisite() {
        return this.applicazione.getListaLuoghi().getTotaleVisite();
    }

    public ListaVisite listaPerStato(StatoVisita stato) {
        ListaVisite lista = new ListaVisite();
        for (Luogo l : this.applicazione.getListaLuoghi().getListaLuoghi()) {
            for (Visita v : l.getInsiemeVisite().getListaVisite()) {
                if (v.getStatoVisita().equals(stato)) {
                    lista.aggiungiVisita(v);
                }
            }
        }
        return lista;
    }

    public ListaVisite visiteDelVolontario(Volontario volontario) {
        return this.applicazione.getListaLuoghi().visiteDelVolontario(volontario);
    }

}
