package it.unibs.ingdsw.persistence.dto;

import it.unibs.ingdsw.model.tempo.Data;

public record PrenotazioneDTO(
        String codicePrenotazione,
        String luogoId,
        String titoloVisita,
        Data data,
        String usernameFruitore,
        int numeroPersonePerPrenotazione
) {}
