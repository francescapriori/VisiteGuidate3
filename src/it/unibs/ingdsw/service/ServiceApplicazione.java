package it.unibs.ingdsw.service;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.applicazione.StatoRichiestaDisponibilita;
import it.unibs.ingdsw.visite.CalendarioAppuntamenti;
import it.unibs.ingdsw.visite.Prenotazione;

import java.time.YearMonth;


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

    public CalendarioAppuntamenti getAppuntamenti(int mese, int anno) {
        return this.applicazione.getAppuntamentiDelMese(mese, anno);
    }

    public void salvaCalendario(CalendarioAppuntamenti calendario) {
        this.applicazione.setCalendarioAppuntamenti(calendario);
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

    public void aggiungiPrenotazione(Prenotazione p){
        this.applicazione.aggiungiPrenotazione(p);
    }
}
