package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoRichiestaDisponibilita;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.view.cli.io.Output;

import java.time.YearMonth;

public class ApplicazioneController {

    private final Applicazione applicazione;
    private final Output out;

    public ApplicazioneController(Output out) {
        this.applicazione = Applicazione.getApplicazione();
        this.out = out;
    }

    public void configuraParametriBase() {
        String ambitoTerritoriale = InputManager.leggiStringaNonVuota(
                "Inserire l'ambito territoriale di competenza dell'applicazione: ");
        int numeroMassimoIscrivibili = InputManager.leggiInteroConMin(
                "Inserire il numero massimo di persone per iscrizione: ", 1);
        this.applicazione.setAmbitoTerritoriale(ambitoTerritoriale);
        this.applicazione.setNumeroMassimoIscrivibili(numeroMassimoIscrivibili);
        this.applicazione.setDaConfigurare(false);
    }

    public void modificaNumeroMassimoIscrivibili() {
        int numeroMassimoIscrivibili = InputManager.leggiInteroConMin(
                "Inserire il nuovo numero massimo di persone per iscrizione: ", 1
        );
        this.applicazione.setNumeroMassimoIscrivibili(numeroMassimoIscrivibili);
    }

    public void aperturaDisponibilita(String nomeMeseDisponibilita, int annoDisponibilita) {
        // la raccolta disponibilità non può essere riaperta dopo la produzione delle visite, ma può essere riaperta a partire dal mese dopo.
        if(applicazione.getStatoDisp() == StatoRichiestaDisponibilita.DISP_CHIUSE) {
            out.println("Da ora è possibile raccogliere le disponibilità dei Volontari per il mese di "+ nomeMeseDisponibilita + " " + annoDisponibilita);
            applicazione.setStatoDisp(StatoRichiestaDisponibilita.DISP_APERTE);
        }
        else {
            out.println("La raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita + " sono già in corso.");
        }
    }

    public void chiudiDisponibilita(String nomeMeseDisponibilita, int annoDisponibilita, YearMonth targetDisponibilita) {
        if(applicazione.getStatoDisp() == StatoRichiestaDisponibilita.DISP_APERTE) {
            out.println("Hai chiuso la raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita);
            applicazione.setStatoDisp(StatoRichiestaDisponibilita.DISP_CHIUSE);
            // incrementa mese-anno
            applicazione.setNextDisponibilita(targetDisponibilita.plusMonths(1));
        }
        else {
            out.println("Non è possibile chiudere la raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita + " perchè la raccolta disponibilità non è stata ancora aperta");
        }
    }


}
