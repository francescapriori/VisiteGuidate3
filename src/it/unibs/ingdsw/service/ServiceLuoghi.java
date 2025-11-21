package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.luoghi.Luogo;

import java.util.ArrayList;
import java.util.Iterator;


public class ServiceLuoghi {
    public Applicazione applicazione;

    public ServiceLuoghi(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public void aggiungiLuoghiSeNonPresenti(ListaLuoghi nuovi, ListaLuoghi listaLuoghiApplicazione) {
        for (Luogo l : nuovi.getListaLuoghi()) {
            if (listaLuoghiApplicazione.aggiungiLuogoSeNonPresente(l)) {
                System.out.println("Aggiunto: " + l.getNome());
            } else {
                System.out.println("Il luogo " + l.getNome() + " è già presente nell'elenco.");
            }
        }
    }

    public ListaLuoghi getListaLuoghi() {
        return this.applicazione.getListaLuoghi();
    }

    public int getNumeroLuogo() {
        return this.applicazione.getListaLuoghi().getNumeroLuogo();
    }

    // forse da spostare la logica nell'Applicazione e richiamarla da qui
    public Luogo scegliLuogo(int scelta) {
        Luogo luogoSelezionato;
        ListaLuoghi luoghi = this.applicazione.getListaLuoghi();
        luogoSelezionato = luoghi.scegliLuogo(scelta - 1);
        return luogoSelezionato;
    }

    // forse da spostare la logica nell'Applicazione e richiamarla da qui
    public boolean rimuoviLuogo(Luogo luogo, Applicazione applicazione) {
        ArrayList<Luogo> listaLuoghi = applicazione.getListaLuoghi().getListaLuoghi();
        Iterator<Luogo> it = listaLuoghi.iterator();
        boolean rimosso = false;

        while (it.hasNext()) {
            Luogo l1 = it.next();
            if (l1.getNome().equals(luogo.getNome())) {
                it.remove();
                rimosso = true;
                break;
            }
        }

        return rimosso;
    }

}
