package it.unibs.ingdsw.visite;

import it.unibs.ingdsw.tempo.InsiemeDate;

import java.util.ArrayList;
import java.util.HashMap;

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

    @Override
    public String toString() {
        if (listaVisite == null || listaVisite.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < listaVisite.size(); i++) {
            sb.append("-");
            sb.append(String.valueOf(listaVisite.get(i)));
            if(i!=listaVisite.size()-1)  sb.append("\n\n");
        }
        return sb.toString();
    }

    public HashMap<Visita, InsiemeDate> calendarioProvvisiorioVisiteDelMese(int meseRiferimento, int annoRiferimento){
        HashMap<Visita, InsiemeDate> calendarioDelMese = new HashMap<>();
        for(Visita v : this.listaVisite) {
            calendarioDelMese.put(v, v.getDatePerVisita(meseRiferimento, annoRiferimento));
        }
        return calendarioDelMese;
    }
}
