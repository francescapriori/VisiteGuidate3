package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.utenti.Fruitore;
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

    public boolean prenotazioneGiaPresente(Appuntamento appuntamento, Fruitore fruitore) {
        return this.applicazione.prenotazioneGiaPresente(appuntamento, fruitore);
    }

    public ArrayList<Prenotazione> prenotazioniDi(Utente u, int mese, int anno) {
        return this.applicazione.prenotazioniDi(u, mese, anno);
    }

    public ArrayList<Prenotazione> getPrenotazioni() {
        return this.applicazione.getPrenotazioni();
    }

    public boolean rimozionePrenotazioneConCodice(String codicePDaRimuovere) {
        return this.applicazione.rimozionePrenotazioneConCodice(codicePDaRimuovere);
    }

    public ArrayList<Prenotazione> getPrenotazioniUtente(Fruitore utente) {
        return this.applicazione.getPrenotazioniUtente(utente);
    }
}
