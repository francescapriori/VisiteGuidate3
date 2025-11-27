package it.unibs.ingdsw.visite;

import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.utenti.Volontario;

import java.util.ArrayList;

public class CalendarioAppuntamenti {
    ArrayList<Appuntamento> calendarioVisite;

    public CalendarioAppuntamenti() {
        this.calendarioVisite = new ArrayList<>();
    }

    public ArrayList<Appuntamento> getCalendarioVisite() {
        return calendarioVisite;
    }
    public void setCalendarioVisite(ArrayList<Appuntamento> calendarioVisite) {
        this.calendarioVisite = calendarioVisite;
    }

    public String toString() {
        return calendarioVisite.toString();
    }

    public boolean volontarioGiaPresenteInData(Data data, Volontario vol) {
        for (Appuntamento a : this.calendarioVisite) {
            if(a.getData().dateUguali(data) && a.getVisita().getVolontariVisita().contains(vol)) {
                return true;
            }
        }
        return false;
    }
}
