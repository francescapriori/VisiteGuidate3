package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;
import it.unibs.ingdsw.visite.Visita;


public class ServiceVisite {

    public Applicazione applicazione;

    public ServiceVisite(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public ListaVisite getListaVisite() {
        return this.applicazione.getListaLuoghi().getTotaleVisite();
    }

    public ListaVisite visiteDelVolontario(Volontario volontario) {
        return this.applicazione.getListaLuoghi().visiteDelVolontario(volontario);
    }

    public void aggiungiVisite(Luogo l, ListaVisite listaVisiteDaAggiungere) {
        l.aggiungiVisite(listaVisiteDaAggiungere);
    }

    public int getNumeroVisita(Luogo luogo) {
        return luogo.getInsiemeVisite().getNumeroVisita();
    }

    public Visita scegliVisita(Luogo l, int scelta) {
       return l.getVisitaIesima(scelta-1);
    }

    public boolean rimuoviVisita(Visita v, Luogo luogo) {
        return this.applicazione.rimuoviVisita(v, luogo);
    }


    public void eliminaSeSenzaVolontari() {
        this.applicazione.eliminaSeSenzaVolontari();
    }
}
