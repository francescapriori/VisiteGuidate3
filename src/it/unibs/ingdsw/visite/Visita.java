package it.unibs.ingdsw.visite;

import it.unibs.ingdsw.applicazione.Target;
import it.unibs.ingdsw.luoghi.Posizione;
import it.unibs.ingdsw.tempo.*;
import it.unibs.ingdsw.utenti.Volontario;

import java.time.LocalDate;
import java.util.ArrayList;

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
    private ArrayList<Volontario> volontariVisita;
    private int numeroMinimoPartecipanti;
    private int numeroMassimoPartecipanti;
    private StatoVisita statoVisita;

    public Visita(String titolo,
                  String descrizione,
                  String luogoID,
                  Posizione luogoIncontro,
                  Giornate giornateVisita,
                  Data inizioValiditaVisita,
                  Data fineValiditaVisita,
                  Orario oraInizioVisita,
                  int durataMinutiVisita,
                  boolean presenzaBiglietto,
                  ArrayList<Volontario> volontariVisita,
                  int numeroMinimoPartecipanti,
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
        this.statoVisita = StatoVisita.PROPOSTA; //sempre quando creata
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

    public ArrayList<Volontario> getVolontariVisita() {
        return volontariVisita;
    }
    public int getNumeroMinimoPartecipanti() {
        return numeroMinimoPartecipanti;
    }
    public int getNumeroMassimoPartecipanti() {
        return numeroMassimoPartecipanti;
    }
    public StatoVisita getStatoVisita() {
        return statoVisita;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append("\tTitolo della visita: ").append(titolo)
                .append("\n\tDescrizione della visita: ").append(descrizione)
                .append("\n\tLuogoID: ").append(luogoID)
                .append("\n\tGiornate di visita: ").append(giornateVisita)
                .append("\n\tData inizio validità visita: ").append(inizioValiditaVisita)
                .append("\tData fine validità visita: ").append(fineValiditaVisita)
                .append("\n\tOrario inizio visita: ").append(oraInizioVisita)
                .append("\tDurata in minuti: ").append(durataMinutiVisita)
                .append("\n\tPresenza di un biglietto di ingresso: ").append(presenzaBiglietto)
                .append("\n\tVolontari disponibili per la visita: ").append(volontariVisita)
                .append("\n\tNumero minimo partecipanti: ").append(numeroMinimoPartecipanti)
                .append("\tNumero massimo partecipanti: ").append(numeroMassimoPartecipanti)
                .append("\n\tStato: ").append(statoVisita);//
        return sb.toString();
    }

    public String stampaVisitaBase(){
        return String.format("%s - %s", this.titolo, this.descrizione);
    }

    public InsiemeDate getDatePerVisita(int meseRiferimento, int annoRiferimento) {
        InsiemeDate risultato = new InsiemeDate();

        LocalDate start = LocalDate.of(inizioValiditaVisita.getAnno(), inizioValiditaVisita.getMese(), inizioValiditaVisita.getGiorno());
        LocalDate end   = LocalDate.of(fineValiditaVisita.getAnno(), fineValiditaVisita.getMese(), fineValiditaVisita.getGiorno());

        java.time.YearMonth ym = java.time.YearMonth.of(annoRiferimento, meseRiferimento);
        int safeDay = Math.min(Target.SOGLIA_CAMBIO_MESE, ym.lengthOfMonth());

        LocalDate from = LocalDate.of(annoRiferimento, meseRiferimento, safeDay);
        LocalDate to   = from.plusMonths(1).minusDays(1);

        if (from.isBefore(start)) from = start;
        if (to.isAfter(end)) to = end;

        if (from.isAfter(to)) {
            return risultato;
        }

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

            if (giornateVisita.presenteLaGiornata(gs)) {
                risultato.aggiungiData(new Data(d.getDayOfMonth(),
                        d.getMonthValue(),
                        d.getYear()));
            }
        }
        return risultato;
    }
}
