package it.unibs.ingdsw.applicazione;

import java.time.LocalDate;
import java.time.YearMonth;

public class Target {

    public static final int THRESHOLD = 16;

    private YearMonth targetPerEsclusione;
    private YearMonth targetDisponibilita;
    private YearMonth targetProduzione;
    private LocalDate oggi = LocalDate.now();

    public Target (YearMonth targetPerEsclusione, YearMonth targetDisponibilita, YearMonth targetProduzione) {
        this.targetPerEsclusione = targetPerEsclusione;
        this.targetDisponibilita = targetDisponibilita;
        this.targetProduzione = targetProduzione;
    }

    public YearMonth getTargetDisponibilita() {
        return targetDisponibilita;
    }
    public YearMonth getTargetPerEsclusione() {
        return targetPerEsclusione;
    }
    public YearMonth getTargetProduzione() {
        return targetProduzione;
    }
    public void setTargetDisponibilita(YearMonth targetDisponibilita) {
        this.targetDisponibilita = targetDisponibilita;
    }
    public void setTargetPerEsclusione(YearMonth targetPerEsclusione) {
        this.targetPerEsclusione = targetPerEsclusione;
    }
    public void setTargetProduzione(YearMonth targetProduzione) {
        this.targetProduzione = targetProduzione;
    }

    public YearMonth calcolaDataTargetDisponibilita() {
        if (isDayAfterThreshold()) {
            return calcolaDataTarget(4);
        }
        return calcolaDataTarget(3);
    }

    public YearMonth calcolaDataTarget(int mesePlus) {
        return YearMonth.now().plusMonths(mesePlus);
    }

    public boolean isDayAfterThreshold (){
        return this.oggi.getDayOfMonth() >= THRESHOLD;
    }
}
