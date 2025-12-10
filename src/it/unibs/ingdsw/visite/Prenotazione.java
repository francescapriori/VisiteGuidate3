package it.unibs.ingdsw.visite;

import it.unibs.ingdsw.utenti.Fruitore;

public class Prenotazione {
    private Appuntamento appuntamento;
    private Fruitore utenteChePrenota;
    private int numeroPersonePerPrenotazione;

    public Prenotazione (Appuntamento appuntamento, Fruitore utenteChePrenota, int numeroPersonePerPrenotazione) {
        this.appuntamento = appuntamento;
        this.utenteChePrenota = utenteChePrenota;
        this.numeroPersonePerPrenotazione = numeroPersonePerPrenotazione;
        appuntamento.aggiungiPersonePrenotate(numeroPersonePerPrenotazione);
        if (appuntamento.getNumeroPersonePrenotate() == appuntamento.getVisita().getNumeroMassimoPartecipanti()) {
            appuntamento.setStatoVisita(StatoVisita.COMPLETA);
        }
    }

    public Appuntamento getAppuntamento() {
        return appuntamento;
    }
    public void setAppuntamento(Appuntamento appuntamento) {
        this.appuntamento = appuntamento;
    }
    public Fruitore getUtenteChePrenota() {
        return utenteChePrenota;
    }
    public void setUtenteChePrenota (Fruitore utenteChePrenota) {
        this.utenteChePrenota = utenteChePrenota;
    }
    public int getNumeroPersonePerPrenotazione() {
        return numeroPersonePerPrenotazione;
    }
    public void setNumeroPersonePerPrenotazione (int numeroPersonePerPrenotazione) {
        this.numeroPersonePerPrenotazione = numeroPersonePerPrenotazione;
    }

    @Override
    public String toString() {
        return "Hai prenotato la visita '" + appuntamento.getVisita().getTitolo() + "' per il giorno " + appuntamento.getData().toString() + " per " + numeroPersonePerPrenotazione + " persone";
    }

    public boolean prenotazioneUguale(Prenotazione p) {
        if(this.appuntamento.appuntamentoUguale(p.getAppuntamento())) {
            if (this.utenteChePrenota.utenteUguale(p.getUtenteChePrenota())) {
                return true;
            }
        }
        return false;
    }
}
