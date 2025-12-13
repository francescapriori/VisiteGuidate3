package it.unibs.ingdsw.visite;

import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.utenti.Volontario;

import java.time.LocalDate;
import java.util.ArrayList;

public class Appuntamento {
    private Visita visita;
    private Data data;
    private Volontario guida;
    private StatoVisita statoVisita;
    private int numeroPersonePrenotate;

    public Appuntamento(Visita visita, Data data, Volontario guida) {
        this.visita = visita;
        this.data = data;
        this.guida = guida;
        this.statoVisita = StatoVisita.PROPOSTA;
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
    public StatoVisita getStatoVisita() {
        return statoVisita;
    }
    public void setStatoVisita(StatoVisita statoVisita) {
        this.statoVisita = statoVisita;
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

    @Override
    public String toString() {
        return String.format(this.visita.getTitolo() + "\t-\t" + data.toString() + " alle ore " + this.visita.getOraInizioVisita().toString() + "\t-\tguida: " + guida.toString() + "\t-\tstato: " + statoVisita.toString());
    }

    public int getPostiDisponibili() {
        return visita.getNumeroMassimoPartecipanti() - numeroPersonePrenotate;
    }

    public void cambiaStato3giorniPrima(){
        if (this.numeroPersonePrenotate < this.visita.getNumeroMinimoPartecipanti()) {
            this.statoVisita = StatoVisita.CANCELLATA;
        } else {
            this.statoVisita = StatoVisita.CONFERMATA;
        }
    }

    public boolean treGiorniPrima() {
        LocalDate dataVisita = LocalDate.of(this.data.getAnno(), this.data.getMese(), this.data.getGiorno());
        return LocalDate.now().equals(dataVisita.minusDays(3));
        //return LocalDate.of(2026, 1, 21).equals(dataVisita.minusDays(3)); //test
    }
    public boolean superato() {
        LocalDate dataVisita = LocalDate.of(this.data.getAnno(), this.data.getMese(), this.data.getGiorno());
        return LocalDate.now().isAfter(dataVisita);
        //return LocalDate.of(2026, 1, 21).isAfter(dataVisita); //test

    }

    public ArrayList<Prenotazione> getPrenotazioniAssociate(ArrayList<Prenotazione> prenotazioni) {
        ArrayList<Prenotazione> prenotazioniDellAppuntamento = new ArrayList<>();

        for (Prenotazione prenotazione : prenotazioni) {
            if (prenotazione.getAppuntamento() == this) {
                prenotazioniDellAppuntamento.add(prenotazione);
            }
        }

        return prenotazioniDellAppuntamento;
    }
}
