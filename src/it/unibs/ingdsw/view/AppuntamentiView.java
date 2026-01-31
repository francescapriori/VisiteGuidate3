package it.unibs.ingdsw.view;

import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.view.format.Formatters;
import it.unibs.ingdsw.view.cli.io.Output;

public class AppuntamentiView {
    private final Output out;

    public AppuntamentiView(Output out) {
        this.out = out;
    }

    public void visualizzaAppuntamenti(InsiemeAppuntamenti appuntamenti) {
        out.println("\nAppuntamenti disponibili per la prenotazione sono::");
        if (appuntamenti.getAppuntamenti().isEmpty()) {
            out.println("  (nessun appuntamento disponibile)");
            out.println("");
            return;
        }

        out.println(Formatters.calendarioAppuntamenti(appuntamenti));
        out.println("");
    }

    public void visualizzaAppuntamentiPerStato(
            InsiemeAppuntamenti appuntamenti,
            StatoAppuntamento stato) {

        out.println("Appuntamenti in stato: " + stato);

        var lista = appuntamenti.getAppuntamentiConStato(stato);
        if (lista.getAppuntamenti().isEmpty()) {
            out.println("  (nessun appuntamento in stato " + stato.toString() + ")");
            out.println("");
            return;
        }

        out.println(Formatters.calendarioAppuntamenti(lista));
        out.println("");
    }

    public void visualizzaCalendario(InsiemeAppuntamenti calendarioAppuntamenti, String nomeMeseV, int annoTargetV) {
        out.println("\n-----Lista degli appuntamenti per il mese di " + nomeMeseV + " " + annoTargetV + "-----");
        boolean vuoto = true;
        for (Appuntamento a : calendarioAppuntamenti.getAppuntamenti()) {
            out.println(Formatters.appuntamento(a));
            vuoto = false;
        }
        if(vuoto){
            out.println("Non sono disponibili appuntamenti per il mese di " + nomeMeseV + " " + annoTargetV);
        }
    }
}
