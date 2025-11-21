package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;
import it.unibs.ingdsw.visite.StatoVisita;
import it.unibs.ingdsw.visite.Visita;

import java.util.Iterator;

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

    public void aggiungiVisite(Luogo l, ListaVisite listaVisiteDaAggiungere) {
        l.aggiungiVisite(listaVisiteDaAggiungere);
    }

    public int getNumeroVisita(Luogo luogo) {
        return luogo.getInsiemeVisite().getNumeroVisita();
    }

    public Visita scegliVisita(Luogo l, int scelta) {
       return l.getVisitaIesima(scelta-1);
    }

    // forse da spostare la logica nell'Applicazione e richiamarla da qui
    public boolean rimuoviVisita(Visita v, Luogo luogo, Applicazione applicazione) {
        Iterator<Visita> it = luogo.getInsiemeVisite().getListaVisite().iterator();
        boolean rimosso = false;

        while (it.hasNext()) {
            Visita v1 = it.next();

            // Controllo su titolo e luogoID
            if (v1.getTitolo().equals(v.getTitolo())
                    && v1.getLuogoID().equalsIgnoreCase(v.getLuogoID())) {

                it.remove();
                rimosso = true;
                break; // esci dopo aver rimosso la prima visita trovata
            }
        }

        return rimosso;
    }


}
