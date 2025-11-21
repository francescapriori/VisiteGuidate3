package it.unibs.ingdsw.applicazione;

import java.time.LocalDate;
import java.time.YearMonth;

public class Target {

    public static final int SOGLIA_CAMBIO_MESE = 16;

    private YearMonth targetPerEsclusione;
    private YearMonth targetDisponibilita;
    private YearMonth targetProduzione;
    private LocalDate oggi = LocalDate.now();

    public Target() {}
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
        if (successivoASoglia()) {
            return calcolaDataTarget(4);
        }
        return calcolaDataTarget(3);
    }
    public YearMonth calcolaDataTargetEsclusione() {
        if (successivoASoglia()) {
            return calcolaDataTarget(3);
        }
        return calcolaDataTarget(2);
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
