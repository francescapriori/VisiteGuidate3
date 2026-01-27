package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.Volontario;

public class ServiceDate {

    private final InsiemeDate insiemeDate;

    public ServiceDate(InsiemeDate insiemeDate) {
        this.insiemeDate = insiemeDate;
    }

    // solo questo: da spostare in controller???
    public InsiemeDate getDatePerVolontario(int mese, int anno, Volontario v) {
        InsiemeDate tutte = Applicazione.getApplicazione().getDisponibilitaPerVol()
                .computeIfAbsent(v, k -> new InsiemeDate());

        return tutte.getDatePerMeseAnno(mese, anno);
    }
}
