package it.unibs.ingdsw.model.tempo;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class Data {

    private int giorno;
    private int mese;
    private int anno;

    public Data (int giorno, int mese, int anno) {
        this.giorno = giorno;
        this.mese = mese;
        this.anno = anno;
    }
    public Data () {
    }

    public int getGiorno() {
        return giorno;
    }
    public int getMese() {
        return mese;
    }
    public int getAnno() {
        return anno;
    }

    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }
    public void setMese(int mese) {
        this.mese = mese;
    }
    public void setAnno(int anno) {
        this.anno = anno;
    }

    public boolean dataValida() {
        if (anno <= 0) return false;
        if (mese < 1 || mese > 12) return false;
        if (giorno < 1) return false;

        int giorniNelMese = getMaxGiorno(mese, anno);
        return giorno <= giorniNelMese;
    }

    public static int getMaxGiorno(int mese, int anno) {
        return switch (mese) {
            case 1, 3, 5, 7, 8, 10, 12 -> 31;
            case 4, 6, 9, 11 -> 30;
            case 2 -> isBisestile(anno) ? 29 : 28;
            default -> throw new IllegalArgumentException("Mese non valido: " + mese);
        };
    }

    public static boolean isBisestile(int anno) {
        return (anno % 4 == 0) && (anno % 100 != 0 || anno % 400 == 0);
    }

    public boolean dateUguali (Data data) {
        if (this.giorno == data.getGiorno() && this.mese == data.getMese() && this.anno == data.getAnno()) return true;
        return false;
    }

    public boolean precede (Data data) {
        if (this.anno < data.getAnno() ||
                (this.anno == data.getAnno() && this.mese < data.getMese()) ||
                (this.anno == data.getAnno() && this.mese == data.getMese() && this.giorno < data.getGiorno()))
            return true;
        return false;
    }

    public boolean segue(Data data) {
        if (this.anno > data.getAnno() ||
                (this.anno == data.getAnno() && this.mese > data.getMese()) ||
                (this.anno == data.getAnno() && this.mese == data.getMese() && this.giorno > data.getGiorno())) {
            return true;
        }
        return false;
    }

    public static String returnNomeMese(YearMonth target) {
        if (target == null) return "Non disponibile";
        return target.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
    }

    public static int returnAnno(YearMonth target) {
        if (target == null) return 0;
        return target.getYear();
    }

    public static int returnMese(YearMonth target) {
        if (target == null) return 0;
        return target.getMonthValue();
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(anno, mese, giorno);
    }
}
