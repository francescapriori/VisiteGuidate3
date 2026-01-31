package it.unibs.ingdsw.model.visite;

import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.luoghi.Posizione;
import it.unibs.ingdsw.model.tempo.*;
import it.unibs.ingdsw.model.utenti.Volontario;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Visita {

    private String titolo;
    private String descrizione;
    private String luogoID;
    private Posizione luogoIncontro;
    private Giornate giornateVisita;
    private Data inizioValiditaVisita;
    private Data fineValiditaVisita;
    private Orario oraInizioVisita;
    private int durataMinutiVisita;
    private boolean presenzaBiglietto;
    private List<Volontario> volontariVisita;
    private int numeroMinimoPartecipanti;
    private int numeroMassimoPartecipanti;
    private StatoAppuntamento statoAppuntamento;

    public Visita(String titolo, String descrizione, String luogoID, Posizione luogoIncontro,
                  Giornate giornateVisita, Data inizioValiditaVisita, Data fineValiditaVisita,
                  Orario oraInizioVisita,  int durataMinutiVisita, boolean presenzaBiglietto,
                  List<Volontario> volontariVisita, int numeroMinimoPartecipanti,
                  int numeroMassimoPartecipanti) {
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.luogoID = luogoID;
        this.luogoIncontro = luogoIncontro;
        this.giornateVisita = giornateVisita;
        this.inizioValiditaVisita = inizioValiditaVisita;
        this.fineValiditaVisita = fineValiditaVisita;
        this.oraInizioVisita = oraInizioVisita;
        this.durataMinutiVisita = durataMinutiVisita;
        this.presenzaBiglietto = presenzaBiglietto;
        this.volontariVisita = volontariVisita;
        this.numeroMinimoPartecipanti = numeroMinimoPartecipanti;
        this.numeroMassimoPartecipanti = numeroMassimoPartecipanti;
        this.statoAppuntamento = StatoAppuntamento.PROPOSTA; //sempre quando creata
    }

    public String getTitolo() {
        return titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public String getLuogoID() {
        return luogoID;
    }

    public Posizione getLuogoIncontro() {
        return luogoIncontro;
    }

    public Giornate getGiornateVisita() {
        return giornateVisita;
    }

    public Data getInizioValiditaVisita() {
        return inizioValiditaVisita;
    }

    public Data getFineValiditaVisita() {
        return fineValiditaVisita;
    }

    public Orario getOraInizioVisita() {
        return oraInizioVisita;
    }

    public int getDurataMinutiVisita() {
        return durataMinutiVisita;
    }

    public boolean isPresenzaBiglietto() {
        return presenzaBiglietto;
    }

    public List<Volontario> getVolontariVisita() {
        return volontariVisita;
    }

    public int getNumeroMinimoPartecipanti() {
        return numeroMinimoPartecipanti;
    }

    public int getNumeroMassimoPartecipanti() {
        return numeroMassimoPartecipanti;
    }

    public StatoAppuntamento getStatoVisita() {
        return statoAppuntamento;
    }

    public InsiemeDate dateDisponibiliTra(LocalDate from, LocalDate to) {
        InsiemeDate risultato = new InsiemeDate();

        LocalDate start = LocalDate.of(inizioValiditaVisita.getAnno(), inizioValiditaVisita.getMese(), inizioValiditaVisita.getGiorno());
        LocalDate end   = LocalDate.of(fineValiditaVisita.getAnno(), fineValiditaVisita.getMese(), fineValiditaVisita.getGiorno());

        if (from.isBefore(start)) from = start;
        if (to.isAfter(end)) to = end;
        if (from.isAfter(to)) return risultato;

        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            GiornoSettimana gs = switch (d.getDayOfWeek()) {
                case MONDAY    -> GiornoSettimana.LUNEDI;
                case TUESDAY   -> GiornoSettimana.MARTEDI;
                case WEDNESDAY -> GiornoSettimana.MERCOLEDI;
                case THURSDAY  -> GiornoSettimana.GIOVEDI;
                case FRIDAY    -> GiornoSettimana.VENERDI;
                case SATURDAY  -> GiornoSettimana.SABATO;
                case SUNDAY    -> GiornoSettimana.DOMENICA;
            };

            if (giornateVisita.contiene(gs)) {
                risultato.aggiungiData(new Data(d.getDayOfMonth(), d.getMonthValue(), d.getYear()));
            }
        }
        return risultato;
    }
}
