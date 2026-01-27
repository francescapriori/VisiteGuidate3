package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;

import java.util.ArrayList;
import java.util.Iterator;

public class ServiceLuoghi {

    private final ListaLuoghi listaLuoghi;

    public ServiceLuoghi(ListaLuoghi listaLuoghi) {
        this.listaLuoghi = listaLuoghi;
    }

    public void aggiungiLuoghiSeNonPresenti(ListaLuoghi nuovi) {
        for (Luogo l : nuovi.getListaLuoghi()) {
            if (this.listaLuoghi.aggiungiLuogoSeNonPresente(l)) {
                System.out.println("Aggiunto: " + l.getNome());
            } else {
                System.out.println("Il luogo " + l.getNome() + " è già presente nell'elenco.");
            }
        }
    }

    public void rimuoviLuogoSeSenzaVisite() {
        this.listaLuoghi.getListaLuoghi().removeIf(Luogo::isSenzaVisite);
    }
}
