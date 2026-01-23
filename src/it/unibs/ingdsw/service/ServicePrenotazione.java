package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.utenti.Fruitore;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.prenotazione.Prenotazione;

import java.util.ArrayList;
import java.util.Iterator;

public class ServicePrenotazione {

    private final Applicazione applicazione;

    public ServicePrenotazione(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    // togliere???
    public int numeroPostiDisponibili(Appuntamento appuntamento) {
        return appuntamento.getPostiDisponibili();
    }

    public boolean prenotazioneGiaPresente(Appuntamento appuntamento, Fruitore fruitore) {
        for (Prenotazione p : this.applicazione.getPrenotazioni()) {
            if (appuntamento.equals(p.getAppuntamento()) &&
                    p.getUtenteChePrenota().utenteUguale(fruitore)) {
                return true;
            }

        }
        return false;
    }

    // togliere???
    public ArrayList<Prenotazione> getPrenotazioni() {
        return this.applicazione.getPrenotazioni();
    }


    public boolean rimozionePrenotazioneConCodice(String codicePDaRimuovere) {
        if (codicePDaRimuovere == null) {
            return false;
        }

        Iterator<Prenotazione> it = this.applicazione.getPrenotazioni().iterator();

        while (it.hasNext()) {
            Prenotazione p = it.next();

            if (p.getCodicePrenotazione() != null &&
                    p.getCodicePrenotazione().equalsIgnoreCase(codicePDaRimuovere)) {

                Appuntamento app = p.getAppuntamento();
                if (app != null) {
                    int num = p.getNumeroPersonePerPrenotazione();
                    app.aggiungiPersonePrenotate(-num);

                    // se prima era COMPLETA e ora non lo è più, rimetto la visita in PROPOSTA (o APERTA)
                    if (app.getNumeroPersonePrenotate() < app.getVisita().getNumeroMassimoPartecipanti()
                            && app.getStatoVisita() == StatoAppuntamento.COMPLETA) {
                        app.setStatoVisita(StatoAppuntamento.PROPOSTA);
                    }
                }

                it.remove();
                return true;
            }
        }
        return false;
    }

    public ArrayList<Prenotazione> getPrenotazioniUtente(Fruitore utente) {
        ArrayList<Prenotazione> prenotazioniDellUtente = new ArrayList<>();
        for (Prenotazione p : this.applicazione.getPrenotazioni()) {
            if (p.getUtenteChePrenota().utenteUguale(utente)) {
                prenotazioniDellUtente.add(p);
            }
        }
        return prenotazioniDellUtente;
    }

    public void aggiungiPrenotazione(Prenotazione p){
        this.applicazione.aggiungiPrenotazione(p);
    }

}
