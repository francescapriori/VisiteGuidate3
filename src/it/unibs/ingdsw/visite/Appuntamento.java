package it.unibs.ingdsw.visite;

import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.utenti.Volontario;

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

    public boolean appuntamentoUguale(Appuntamento appuntamento){
        if (this.visita.getTitolo().equalsIgnoreCase(appuntamento.getVisita().getTitolo()) && this.visita.getLuogoID().equalsIgnoreCase(appuntamento.getVisita().getLuogoID())) {
            if (this.data.dateUguali(appuntamento.getData())) {
                if (this.guida.utenteUguale(appuntamento.getGuida())) {
                    return true;
                }
            }
        }
        return false;
    }
}
