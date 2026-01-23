package it.unibs.ingdsw.model.tempo;

import java.util.ArrayList;

public class Giornate {

    private ArrayList<GiornoSettimana> giornate;

    public Giornate() {
        this.giornate = new ArrayList<>();
    }

    public ArrayList<GiornoSettimana> getGiornate() {
        return giornate;
    }

    public void aggiungiGiornoDellaSettimana(GiornoSettimana giorno) {
        if(!contiene(giorno)) {
            giornate.add(giorno);
        }
    }

    public boolean contiene(GiornoSettimana giornoDaVerificare) {
        for (GiornoSettimana g : this.giornate) {
            if (giornoDaVerificare.toString().equalsIgnoreCase(g.toString())) { //da verificare
                return true;
            }
        }
        return false;
    }
}
