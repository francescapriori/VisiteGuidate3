package it.unibs.ingdsw.persistence.dto;

import it.unibs.ingdsw.model.tempo.InsiemeDate;

public record DisponibilitaVolontarioDTO(
        String usernameVolontario,
        InsiemeDate disponibilita
) {}
