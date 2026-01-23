package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;

import java.util.ArrayList;
import java.util.Iterator;

public class ServiceLuoghi {
    private final Applicazione applicazione;

    public ServiceLuoghi(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public void aggiungiLuoghiSeNonPresenti(ListaLuoghi nuovi) {
        for (Luogo l : nuovi.getListaLuoghi()) {
            if (this.applicazione.getListaLuoghi().aggiungiLuogoSeNonPresente(l)) {
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

    public void setListaLuoghi(ListaLuoghi lista) {
        this.applicazione.setListaLuoghi(lista);
    }

    public Luogo scegliLuogo(int scelta) {
        return getListaLuoghi().scegliLuogo(scelta - 1);
    }

    public void rimuoviLuogo(Luogo luogo) {
        String nomeTarget = luogo.getNome();
        ArrayList<Luogo> lista = getListaLuoghi().getListaLuoghi();

        for (Iterator<Luogo> it = lista.iterator(); it.hasNext(); ) {
            Luogo corrente = it.next();
            if (corrente != null && nomeTarget.equals(corrente.getNome())) {
                it.remove();
            }
        }
    }

    public void rimuoviLuogoSeSenzaVisite() {
        ArrayList<Luogo> luoghiDaRimuovere = new ArrayList<>();
        for (Luogo l : this.applicazione.getListaLuoghi().getListaLuoghi()) {
            if (l.getInsiemeVisite().getListaVisite().isEmpty()) {
                luoghiDaRimuovere.add(l);
            }
        }
        this.applicazione.getListaLuoghi().getListaLuoghi().removeAll(luoghiDaRimuovere);
    }
}
