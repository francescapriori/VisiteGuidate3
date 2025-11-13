package it.unibs.ingdsw.tempo;

import java.util.ArrayList;

public class Giornate {

    private ArrayList<GiornoSettimana> giornate;

    public Giornate() {
        this.giornate = new ArrayList<>();
    }

    public ArrayList<GiornoSettimana> getGiornate() {
        return giornate;
    }

    public void aggiungiGiornoDellaSettimana (GiornoSettimana giorno) {
        if(!giornoPresente(giorno)) {
            giornate.add(giorno);
        }

    }

    public boolean giornoPresente(GiornoSettimana g){
        for(GiornoSettimana gi : giornate) {
            if(gi.equals(g)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return this.giornate.toString();
    }

    public boolean presenteLaGiornata(GiornoSettimana giornoDaVerificare) {
        for (GiornoSettimana g : this.giornate) {
            if (giornoDaVerificare.toString().equalsIgnoreCase(g.toString())) {
                return true;
            }
        }
        return false;
    }
}
