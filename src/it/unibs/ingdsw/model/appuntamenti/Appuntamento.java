package it.unibs.ingdsw.model.appuntamenti;

import it.unibs.ingdsw.model.prenotazione.Prenotazione;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.Visita;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Appuntamento {
    private Visita visita;
    private Data data;
    private Volontario guida;
    private StatoAppuntamento statoAppuntamento;
    private int numeroPersonePrenotate;

    public Appuntamento(Visita visita, Data data, Volontario guida) {
        this.visita = visita;
        this.data = data;
        this.guida = guida;
        this.statoAppuntamento = StatoAppuntamento.PROPOSTA;
    }

    public Visita getVisita() {
        return visita;
    }
    public void setVisita(Visita visita) {
        this.visita = visita;
    }
    public Data getData() {
        return data;
    }
    public void setData(Data data) {
        this.data = data;
    }
    public Volontario getGuida() {
        return guida;
    }
    public StatoAppuntamento getStatoVisita() {
        return statoAppuntamento;
    }
    public void setStatoVisita(StatoAppuntamento statoAppuntamento) {
        this.statoAppuntamento = statoAppuntamento;
    }

    public int getNumeroPersonePrenotate() {
        return numeroPersonePrenotate;
    }

    public void setNumeroPersonePrenotate(int numeroPersonePrenotate) {
        this.numeroPersonePrenotate = numeroPersonePrenotate;
    }

    public void aggiungiPersonePrenotate (int numero) {
        this.numeroPersonePrenotate += numero;
    }

    public int getPostiDisponibili() {
        return visita.getNumeroMassimoPartecipanti() - numeroPersonePrenotate;
    }

    public void aggiornaStato(LocalDate oggi) {
        if (treGiorniPrima(oggi)) {
            // a -3 giorni dalla visita diventa confermata o cancellata
            if (numeroPersonePrenotate < visita.getNumeroMinimoPartecipanti()) {
                statoAppuntamento = StatoAppuntamento.CANCELLATA;
            } else {
                statoAppuntamento = StatoAppuntamento.CONFERMATA;
            }
        }

        if (superato(oggi)) {
            // se non confermata entro il giorno, la visita viene cancellata, altrimenti effettuata
            if (statoAppuntamento == StatoAppuntamento.PROPOSTA || statoAppuntamento == StatoAppuntamento.COMPLETA) {
                statoAppuntamento = StatoAppuntamento.CANCELLATA;
            } else if (statoAppuntamento == StatoAppuntamento.CONFERMATA) {
                statoAppuntamento = StatoAppuntamento.EFFETTUATA;
            }
        }
    }

    public boolean treGiorniPrima(LocalDate oggi) {
        LocalDate dataVisita = LocalDate.of(data.getAnno(), data.getMese(), data.getGiorno());
        return oggi.equals(dataVisita.minusDays(3));
    }

    public boolean superato(LocalDate oggi) {
        LocalDate dataVisita = LocalDate.of(data.getAnno(), data.getMese(), data.getGiorno());
        return oggi.isAfter(dataVisita);
    }

    public List<Prenotazione> getPrenotazioniAssociate(List<Prenotazione> prenotazioni) {
        List<Prenotazione> prenotazioniDellAppuntamento = new ArrayList<>();

        for (Prenotazione prenotazione : prenotazioni) {
            if (prenotazione.getAppuntamento() == this) {
                prenotazioniDellAppuntamento.add(prenotazione);
            }
        }

        return prenotazioniDellAppuntamento;
    }
}
