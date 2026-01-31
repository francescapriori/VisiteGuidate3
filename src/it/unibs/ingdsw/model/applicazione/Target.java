package it.unibs.ingdsw.model.applicazione;

import java.time.LocalDate;
import java.time.YearMonth;

/*
    La classe Target è responsabile del calcolo della data target dell’applicazione, determinando
    il numero di mesi da aggiungere alla data corrente in base al tipo di target richiesto
    e alla posizione temporale rispetto a una soglia prefissata del mese.
    La classe incapsula la logica comune di calcolo e delega le variazioni dipendenti dal tipo
    di target all’enum TargetTipo.
 */

public class Target {

    public static final int SOGLIA_CAMBIO_MESE = 15;

    private final LocalDate oggi;

    public Target() {oggi = LocalDate.now();}

    public YearMonth calcolaDataTarget(TargetTipo tipo) {
        int mesiDaAggiungere = successivoASoglia() ? tipo.mesiDopoSoglia() : tipo.mesiPrimaSoglia();
        return YearMonth.from(oggi).plusMonths(mesiDaAggiungere);
    }

    public boolean successivoASoglia(){
        return this.oggi.getDayOfMonth() >= SOGLIA_CAMBIO_MESE;
    }
}
