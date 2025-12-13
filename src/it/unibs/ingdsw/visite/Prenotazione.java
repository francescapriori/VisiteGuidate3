package it.unibs.ingdsw.visite;

import it.unibs.ingdsw.utenti.Fruitore;

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
            appuntamento.setStatoVisita(StatoVisita.COMPLETA);
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
        return String.format("PR-%s-%04d", data, contatore);
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

    @Override
    public String toString() {
        return "Codice: " + codicePrenotazione + " - Numero persone: " + numeroPersonePerPrenotazione;
    }

    public String toStringLungo() {
        return "Codice: " + codicePrenotazione + " - Data appuntamento: " + this.appuntamento.getData().toString() + " - Visita: " + this.appuntamento.getVisita().getTitolo() +
                " - Numero persone prenotate: " + numeroPersonePerPrenotazione;
    }
}
