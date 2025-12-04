package it.unibs.ingdsw.applicazione;

import java.time.LocalDate;
import java.time.YearMonth;

public class Target {

    public static final int SOGLIA_CAMBIO_MESE = 15;

    private final LocalDate oggi;

    public Target() {
        oggi = LocalDate.now();
    }

    public YearMonth calcolaDataTargetDisponibilita() {
        if (successivoASoglia()) {
            return calcolaDataTarget(3);
        }
        return calcolaDataTarget(2);
    }
    public YearMonth calcolaDataTargetEsclusione() {
        if (successivoASoglia()) {
            return calcolaDataTarget(4);
        }
        return calcolaDataTarget(3);
    }
    public YearMonth calcolaDataTargetProduzione() {
        if (successivoASoglia()) {
            return calcolaDataTarget(2);
        }
        return calcolaDataTarget(1);
    }

    public YearMonth calcolaDataTarget(int mesePlus) {
        return YearMonth.now().plusMonths(mesePlus);
    }

    public boolean successivoASoglia(){
        return this.oggi.getDayOfMonth() >= SOGLIA_CAMBIO_MESE;
    }
}
