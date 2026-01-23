package it.unibs.ingdsw.model.appuntamenti;

import java.util.ArrayList;

public class InsiemeAppuntamenti {
    ArrayList<Appuntamento> insiemeAppuntamenti;

    public InsiemeAppuntamenti() {
        this.insiemeAppuntamenti = new ArrayList<>();
    }

    public InsiemeAppuntamenti(ArrayList<Appuntamento> insiemeAppuntamenti) {
        this.insiemeAppuntamenti = insiemeAppuntamenti;
    }

    public ArrayList<Appuntamento> getAppuntamenti() {
        return insiemeAppuntamenti;
    }

    public InsiemeAppuntamenti getAppuntamentiConStato(StatoAppuntamento stato) {
        ArrayList<Appuntamento> insiemeAppuntamenti = new ArrayList<>();
        for(Appuntamento a : this.insiemeAppuntamenti) {
            if(a.getStatoVisita().equals(stato)) {
                insiemeAppuntamenti.add(a);
            }
        }
        return new InsiemeAppuntamenti(insiemeAppuntamenti);
    }

}
