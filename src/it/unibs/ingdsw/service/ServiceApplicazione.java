package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.applicazione.StatoRichiestaDisponibilita;

import java.time.YearMonth;

public class ServiceApplicazione {
    private final Applicazione applicazione;

    public ServiceApplicazione(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public void setAmbitoTerritoriale(String ambitoTerritoriale) {
        this.applicazione.setAmbitoTerritoriale(ambitoTerritoriale);
    }

    public void setNumeroMassimoIscrivibili(int numeroMassimoIscrivibili) {
        this.applicazione.setNumeroMassimoIscrivibili(numeroMassimoIscrivibili);
    }

    public void setDaConfigurare(boolean val) {
        this.applicazione.setDaConfigurare(val);
    }

    public void setStatoDisp(StatoRichiestaDisponibilita statoRichiestaDisponibilita) {
        this.applicazione.setStatoDisp(statoRichiestaDisponibilita);
    }

    public StatoRichiestaDisponibilita getStatoDisp() {
        return this.applicazione.getStatoDisp();
    }

    public StatoProduzioneVisite getStatoProd() {
        return this.applicazione.getStatoProd();
    }

    public YearMonth getNextDisponibilita() {
        return this.applicazione.getNextDisponibilita();
    }

    public void setNextDisponibilita(YearMonth nextDisponibilita) {
        this.applicazione.setNextDisponibilita(nextDisponibilita);
    }

    public Integer getNumMaxIscrivibili() {
        return this.applicazione.getNumeroMassimoIscrivibili();
    }
}
