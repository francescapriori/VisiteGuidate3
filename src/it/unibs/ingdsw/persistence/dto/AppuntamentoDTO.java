package it.unibs.ingdsw.persistence.dto;

import it.unibs.ingdsw.model.tempo.Data;

public record AppuntamentoDTO(
        String luogoId,
        String titoloVisita,
        Data data,
        String usernameGuida,
        String statoVisita,
        int numeroPersonePrenotate
) {}
