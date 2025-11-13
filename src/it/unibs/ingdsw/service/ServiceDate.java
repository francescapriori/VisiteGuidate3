package it.unibs.ingdsw.service;

import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.utenti.Volontario;

public class ServiceDate {

    Applicazione applicazione;

    public ServiceDate(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public InsiemeDate getDateEscluse(int mesePerEsclusione, int annoPerEsclusione) {
        return this.applicazione.getDateEsclusePerMeseAnno(mesePerEsclusione, annoPerEsclusione);
    }

    public boolean aggiungiData(Data dataDaEscludere) {
        return this.applicazione.aggiungiData(dataDaEscludere);
    }

    public InsiemeDate getDatePerVolontario(int mese, int anno, Volontario v) {
        return this.applicazione.getDisponibilitaPerVol().computeIfAbsent(v, k -> new InsiemeDate());
    }
}
