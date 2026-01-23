package it.unibs.ingdsw.model.prenotazione;

import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.utenti.Fruitore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Prenotazione {
    public final String codicePrenotazione;
    private Appuntamento appuntamento;
    private Fruitore utenteChePrenota;
    private int numeroPersonePerPrenotazione;
    private int contatore = 0;

    public Prenotazione (Appuntamento appuntamento, Fruitore utenteChePrenota, int numeroPersonePerPrenotazione) {
        this.codicePrenotazione = produciProssimoCodiceUtile();
        this.appuntamento = appuntamento;
        this.utenteChePrenota = utenteChePrenota;
        this.numeroPersonePerPrenotazione = numeroPersonePerPrenotazione;
        appuntamento.aggiungiPersonePrenotate(numeroPersonePerPrenotazione);
        if (appuntamento.getNumeroPersonePrenotate() == appuntamento.getVisita().getNumeroMassimoPartecipanti()) {
            appuntamento.setStatoVisita(StatoAppuntamento.COMPLETA);
        }
    }

    public Prenotazione(String codicePrenotazione, Appuntamento appuntamento, Fruitore utenteChePrenota, int numeroPersonePerPrenotazione) {
        this.codicePrenotazione = codicePrenotazione;
        this.appuntamento = appuntamento;
        this.utenteChePrenota = utenteChePrenota;
        this.numeroPersonePerPrenotazione = numeroPersonePerPrenotazione;
    }

    private synchronized String produciProssimoCodiceUtile() {
        contatore++;
        String data = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE); // es: 20251212
        return creaPrenotazione(data, contatore);
    }

    public String getCodicePrenotazione() {
        return codicePrenotazione;
    }

    public Appuntamento getAppuntamento() {
        return appuntamento;
    }

    public void setAppuntamento(Appuntamento appuntamento) {
        this.appuntamento = appuntamento;
    }

    public Fruitore getUtenteChePrenota() {
        return utenteChePrenota;
    }

    public int getNumeroPersonePerPrenotazione() {
        return numeroPersonePerPrenotazione;
    }

    public static String creaPrenotazione(String data, int contatore) {
        return String.format("PR-%s-%04d", data, contatore);
    }
}
