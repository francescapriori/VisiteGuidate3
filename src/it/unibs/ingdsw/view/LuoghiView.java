package it.unibs.ingdsw.view;

import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.view.format.Formatters;
import it.unibs.ingdsw.view.cli.io.Output;

public class LuoghiView {
    private final Output out;

    public LuoghiView(Output out) {
        this.out = out;
    }

    public void visualizzaLuoghi(ListaLuoghi luoghi) {
        int i = 1;
        boolean vuoto = true;
        for (Luogo l : luoghi.getListaLuoghi()) {
            out.println(i + ") " + Formatters.soloLuogo(l));
            i++;
            vuoto = false;
        }
        if (vuoto) {
            out.println("Nessuna luogo registrato.");
        }
    }

    public void visualizzaLuoghiEvisite(ListaLuoghi luoghi) {
        int i = 1;
        boolean vuoto = true;
        for (Luogo l : luoghi.getListaLuoghi()) {
            out.println(i + ") " + Formatters.luogoBase(l));
            for (Visita v : l.getInsiemeVisite().getListaVisite()) {
                out.println("\t-" + Formatters.visitaBase(v));
                vuoto = true;
            }
            i++;
        }
        if (vuoto) {
            out.println("Nessuna luogo registrato.");
        }
    }
}
