package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.service.ServiceApplicazione;
import it.unibs.ingdsw.view.cli.io.InputManager;

public class ApplicazioneController {

    private final ServiceApplicazione serviceApplicazione;

    public ApplicazioneController(ServiceApplicazione serviceApplicazione) {
        this.serviceApplicazione = serviceApplicazione;
    }

    public void configuraParametriBase() {
        String ambito = InputManager.leggiStringaNonVuota(
                "Inserire l'ambito territoriale di competenza dell'applicazione: "
        );
        int max = InputManager.leggiInteroConMin(
                "Inserire il numero massimo di persone per iscrizione: ", 1
        );

        serviceApplicazione.setAmbitoTerritoriale(ambito);
        serviceApplicazione.setNumeroMassimoIscrivibili(max);
        serviceApplicazione.setDaConfigurare(false);
    }

    public void modificaNumeroMassimoIscrivibili() {
        int max = InputManager.leggiInteroConMin(
                "Inserire il nuovo numero massimo di persone per iscrizione: ", 1
        );
        serviceApplicazione.setNumeroMassimoIscrivibili(max);
    }
}
