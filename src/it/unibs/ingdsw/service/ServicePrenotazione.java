package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.utenti.Utente;
import it.unibs.ingdsw.visite.Appuntamento;
import it.unibs.ingdsw.visite.Prenotazione;

import java.util.ArrayList;

public class ServicePrenotazione {
    public Applicazione applicazione;

    public ServicePrenotazione(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public int numeroPostiDisponibili(Appuntamento appuntamento) {
        return appuntamento.getPostiDisponibili();
    }

    public boolean prenotazioneGiaPresente(Prenotazione p) {
        return this.applicazione.prenotazioneGiaPresente(p);
    }

    public ArrayList<Prenotazione> prenotazioniDi(Utente u, int mese, int anno) {
        return this.applicazione.prenotazioniDi(u, mese, anno);
    }

    public void rimuoviPrenotazione(Prenotazione p) {
        this.applicazione.rimuoviPrenotazione(p);
    }
}
