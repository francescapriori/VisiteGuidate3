package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;

public class ServiceApplicazione {
    public Applicazione applicazione;

    public ServiceApplicazione(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public void modificaNumeroMassimoIscrivibili(int numeroMassimoIscrivibili) {
        this.applicazione.setNumeroMassimoIscrivibili(numeroMassimoIscrivibili);
    }
}
