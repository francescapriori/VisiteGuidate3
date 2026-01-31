package it.unibs.ingdsw.view;

import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.view.cli.io.Output;
import it.unibs.ingdsw.view.cli.io.OutputManager;
import it.unibs.ingdsw.view.format.Formatters;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateView {
    private final Output out;

    public DateView(Output out) {
        this.out = out;

    }

    //"escluse" e "in cui ti sei reso disponibile"
    public void visualizzaDatePerMeseAnno(InsiemeDate date, int mese, int anno, String stringa) {
        String nomeMese;
        nomeMese = Month.of(mese).getDisplayName(TextStyle.FULL, Locale.ITALIAN);

        if (date.getInsiemeDate().isEmpty()) {
            out.println("Non sono presenti date " + stringa + " per il mese di " + nomeMese + " " + anno);
            return;
        }
        out.println("Le date " + stringa + " per il mese di " + nomeMese + " " + anno + " sono:");
    }
}
