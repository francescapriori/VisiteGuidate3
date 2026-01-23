package it.unibs.ingdsw.persistence;

import it.unibs.ingdsw.model.applicazione.Applicazione;

public interface ApplicazioneRepository {
    Applicazione configuraApplicazione();
    void salvaApplicazione(Applicazione app);
}

