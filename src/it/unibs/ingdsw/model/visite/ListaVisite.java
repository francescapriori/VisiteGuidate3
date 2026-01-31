package it.unibs.ingdsw.model.visite;

import java.util.ArrayList;
import java.util.List;

public class ListaVisite {

    private List<Visita> listaVisite;

    public ListaVisite() {
        this.listaVisite = new ArrayList<>();
    }

    public List<Visita> getListaVisite() {
        return listaVisite;
    }

    public void aggiungiVisita(Visita visita) {
        this.listaVisite.add(visita);
    }

    public int getNumeroVisite() {
        return this.listaVisite.size();
    }
}
