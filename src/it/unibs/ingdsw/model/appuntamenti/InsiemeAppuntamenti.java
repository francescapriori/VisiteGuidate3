package it.unibs.ingdsw.model.appuntamenti;

import java.util.ArrayList;
import java.util.List;

public class InsiemeAppuntamenti {
    List<Appuntamento> insiemeAppuntamenti;

    public InsiemeAppuntamenti() {
        this.insiemeAppuntamenti = new ArrayList<>();
    }

    public InsiemeAppuntamenti(List<Appuntamento> insiemeAppuntamenti) {
        this.insiemeAppuntamenti = insiemeAppuntamenti;
    }

    public List<Appuntamento> getAppuntamenti() {
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
