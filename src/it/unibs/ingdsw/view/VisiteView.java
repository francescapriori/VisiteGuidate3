package it.unibs.ingdsw.view;

import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.view.format.Formatters;
import it.unibs.ingdsw.view.cli.io.Output;

public class VisiteView {
    private final Output out;

    public VisiteView(Output out) {
        this.out = out;
    }

    public void visualizzaListaVisite(ListaVisite listaVisite) {
        int i = 1;
        for (Visita v : listaVisite.getListaVisite()) {
            out.println(i + ") " + Formatters.visita(v));
            i++;
        }
    }
}
