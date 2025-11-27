package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.visite.CalendarioAppuntamenti;
import it.unibs.ingdsw.visite.Visita;

import java.util.HashMap;

public class ServiceApplicazione {
    public Applicazione applicazione;

    public ServiceApplicazione(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public void modificaNumeroMassimoIscrivibili(int numeroMassimoIscrivibili) {
        this.applicazione.setNumeroMassimoIscrivibili(numeroMassimoIscrivibili);
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

    public CalendarioAppuntamenti produciVisitePerIlMese (int mese, int anno) {
        return applicazione.produciVisitePerIlMese(mese, anno);

    }
}
