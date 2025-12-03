package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.luoghi.Luogo;

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


    public Luogo scegliLuogo(int scelta) {
        return this.applicazione.scegliLuogo(scelta);
    }

    public void rimuoviLuogo (Luogo luogo) {
        this.applicazione.rimuoviLuogo(luogo);

    }

}
