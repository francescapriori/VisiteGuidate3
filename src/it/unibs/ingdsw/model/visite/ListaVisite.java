package it.unibs.ingdsw.model.visite;

import java.util.ArrayList;

public class ListaVisite {

    private ArrayList<Visita> listaVisite;

    public ListaVisite() {
        this.listaVisite = new ArrayList<>();
    }

    public ArrayList<Visita> getListaVisite() {
        return listaVisite;
    }

    public void aggiungiVisita(Visita visita) {
        this.listaVisite.add(visita);
    }

    public int getNumeroVisite() {
        return this.listaVisite.size();
    }
}
