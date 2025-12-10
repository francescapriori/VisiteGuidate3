package it.unibs.ingdsw.visite;

import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.utenti.Volontario;

import java.util.ArrayList;

public class CalendarioAppuntamenti {
    ArrayList<Appuntamento> calendarioVisite;

    public CalendarioAppuntamenti() {
        this.calendarioVisite = new ArrayList<>();
    }

    public CalendarioAppuntamenti(ArrayList<Appuntamento> appuntamenti) {
        this.calendarioVisite = appuntamenti;
    }

    public ArrayList<Appuntamento> getAppuntamenti() {
        return calendarioVisite;
    }
    public void setCalendarioVisite(ArrayList<Appuntamento> calendarioVisite) {
        this.calendarioVisite = calendarioVisite;
    }

    @Override
    public String toString() {
        if (calendarioVisite == null || calendarioVisite.isEmpty()) return "Nessun appuntamento disponibile in questo stato";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < calendarioVisite.size(); i++) {
            sb.append("-");
            sb.append(String.valueOf(calendarioVisite.get(i)));
            if(i!=calendarioVisite.size()-1)  sb.append("\n");
        }
        return sb.toString();
    }

    public boolean volontarioGiaPresenteInData(Data data, Volontario vol) {
        for (Appuntamento a : this.calendarioVisite) {
            if (a.getData().dateUguali(data)) {
                Volontario guida = a.getGuida();
                if (guida != null && guida.getUsername() != null
                        && vol != null && vol.getUsername() != null
                        && guida.getUsername().equalsIgnoreCase(vol.getUsername())) {
                    return true; // stessa data e stesso volontario già impegnato
                }
            }
        }
        return false;
    }

    public CalendarioAppuntamenti getAppuntamentiConStato(StatoVisita stato) {
        ArrayList<Appuntamento> appuntamenti = new ArrayList<>();
        for(Appuntamento a : this.calendarioVisite) {
            if(a.getStatoVisita().equals(stato)) {
                appuntamenti.add(a);
            }
        }
        return new CalendarioAppuntamenti(appuntamenti);
    }

}
