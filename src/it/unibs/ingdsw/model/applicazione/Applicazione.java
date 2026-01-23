package it.unibs.ingdsw.model.applicazione;

import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.prenotazione.Prenotazione;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Volontario;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;

public class Applicazione {

    private String ambitoTerritoriale;
    private int numeroMassimoIscrivibili;
    private ListaUtenti listaUtenti;
    private ListaLuoghi listaLuoghi;
    private boolean daConfigurare = true;
    private InsiemeDate dateEscluse;
    private HashMap<Volontario, InsiemeDate> disponibilitaPerVol;
    private InsiemeAppuntamenti calendarioAppuntamenti;
    private StatoRichiestaDisponibilita statoDisp;
    private YearMonth nextDisponibilita;
    private StatoProduzioneVisite statoProduzione;
    private ArrayList<Prenotazione> prenotazioni;

    public Applicazione() {}

    public void setAmbitoTerritoriale(String ambitoTerritoriale) {
        this.ambitoTerritoriale = ambitoTerritoriale;
    }

    public void setNumeroMassimoIscrivibili(int numeroMassimoIscrivibili) {
        this.numeroMassimoIscrivibili = numeroMassimoIscrivibili;
    }

    public void setListaLuoghi(ListaLuoghi listaLuoghi) {
        this.listaLuoghi = listaLuoghi;
    }

    public void setListaUtenti(ListaUtenti listaUtenti) {
        this.listaUtenti = listaUtenti;
    }

    public void setDaConfigurare(boolean daConfigurare) {
        this.daConfigurare = daConfigurare;
    }

    public void setDateEscluse(InsiemeDate dateEscluse) {
        this.dateEscluse = dateEscluse;
    }

    public String getAmbitoTerritoriale() {
        return ambitoTerritoriale;
    }

    public int getNumeroMassimoIscrivibili() {
        return numeroMassimoIscrivibili;
    }

    public ListaLuoghi getListaLuoghi() {
        return listaLuoghi;
    }

    public ListaUtenti getListaUtenti() {
        return listaUtenti;
    }

    public InsiemeDate getDateEscluse() {
        return dateEscluse;
    }

    public void setStatoProduzione(StatoProduzioneVisite statoProd) {
        this.statoProduzione = statoProd;
    }

    public StatoProduzioneVisite getStatoProd() {
        return this.statoProduzione;
    }

    public InsiemeAppuntamenti getCalendarioAppuntamenti() {
        return calendarioAppuntamenti;
    }

    public void setCalendarioAppuntamenti(InsiemeAppuntamenti calendario){
        this.calendarioAppuntamenti=calendario;
    }

    public boolean isDaConfigurare() {
        return daConfigurare;
    }

    public HashMap<Volontario, InsiemeDate> getDisponibilitaPerVol() {
        return disponibilitaPerVol;
    }

    public void setDisponibilitaPerVol(HashMap<Volontario, InsiemeDate> disponibilitaPerVol) {
        this.disponibilitaPerVol = disponibilitaPerVol;
    }

    public void setStatoDisp(StatoRichiestaDisponibilita statoDisp) {
        this.statoDisp = statoDisp;
    }

    public StatoRichiestaDisponibilita getStatoDisp() {
        return statoDisp;
    }

    public YearMonth getNextDisponibilita() {
        return nextDisponibilita;
    }

    public void setNextDisponibilita(YearMonth nextDisponibilita) {
        this.nextDisponibilita = nextDisponibilita;
    }

    public ArrayList<Prenotazione> getPrenotazioni() {
        return this.prenotazioni;
    }

    public void setPrenotazioni (ArrayList<Prenotazione> prenotazioni) {
        this.prenotazioni = prenotazioni;
    }

    public void aggiungiPrenotazione (Prenotazione prenotazione) {
        this.prenotazioni.add(prenotazione);
    }
}
