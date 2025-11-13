package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.output.OutputManager;

import java.util.ArrayList;
import java.util.List;

public class ServiceLuoghi {
    public Applicazione applicazione;

    public ServiceLuoghi(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public void aggiungiLuoghi(ListaLuoghi nuovi, ListaLuoghi listaLuoghiApplicazione) {
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

    public Luogo scegliLuogo(int scelta) {
        Luogo luogoSelezionato;
        ListaLuoghi luoghi = this.applicazione.getListaLuoghi();
        luogoSelezionato = luoghi.scegliLuogo(scelta - 1);
        return luogoSelezionato;
    }
}
