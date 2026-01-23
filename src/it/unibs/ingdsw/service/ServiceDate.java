package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.Volontario;

public class ServiceDate {

    private final Applicazione applicazione;

    public ServiceDate(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public InsiemeDate getDateEscluse(int mesePerEsclusione, int annoPerEsclusione) {
        return this.applicazione.getDateEscluse().getDatePerMeseAnno(mesePerEsclusione, annoPerEsclusione);
    }

    public boolean aggiungiData(Data data) {
        return this.applicazione.getDateEscluse().aggiungiData(data);
    }

    public InsiemeDate getDatePerVolontario(int mese, int anno, Volontario v) {
        InsiemeDate tutte = this.applicazione.getDisponibilitaPerVol()
                .computeIfAbsent(v, k -> new InsiemeDate());

        return tutte.getDatePerMeseAnno(mese, anno);
    }

}
