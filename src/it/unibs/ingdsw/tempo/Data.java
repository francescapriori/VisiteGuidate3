package it.unibs.ingdsw.tempo;

import java.time.DayOfWeek;
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
            case 4, 6, 9, 11          -> 30;
            case 2                    -> isBisestile(anno) ? 29 : 28;
            default                   -> throw new IllegalArgumentException("Mese non valido: " + mese);
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

    public boolean segue (Data data) {
        return !precede(data);
    }

    public GiornoSettimana getGiornoSettimana() {
        DayOfWeek dow = LocalDate.of(anno, mese, giorno).getDayOfWeek();
        return switch (dow) {
            case MONDAY    -> GiornoSettimana.LUNEDI;
            case TUESDAY   -> GiornoSettimana.MARTEDI;
            case WEDNESDAY -> GiornoSettimana.MERCOLEDI;
            case THURSDAY  -> GiornoSettimana.GIOVEDI;
            case FRIDAY    -> GiornoSettimana.VENERDI;
            case SATURDAY  -> GiornoSettimana.SABATO;
            case SUNDAY    -> GiornoSettimana.DOMENICA;
        };
    }

    public static String getMeseStringa(int mese) {
        switch (mese) {
            case 1: return "gennaio";
            case 2: return "febbraio";
            case 3: return "marzo";
            case 4: return "aprile";
            case 5: return "maggio";
            case 6: return "giugno";
            case 7: return "luglio";
            case 8: return "agosto";
            case 9: return "settembre";
            case 10: return "ottobre";
            case 11: return "novembre";
            case 12: return "dicembre";
            default: return "";
        }
    }

    public static String returnNomeMese(YearMonth target) {
        return target.getMonth().getDisplayName(TextStyle.FULL, Locale.ITALIAN);
    }
    public static int returnAnno(YearMonth target) {
        return target.getYear();
    }
    public static int returnMese(YearMonth target) {
        return target.getMonthValue();
    }

    @Override
    public String toString() {
        return String.format("%02d/%02d/%04d", this.giorno, this.mese, this.anno);
    }

}
