package it.unibs.ingdsw.persistence.mapper;

import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.prenotazione.Prenotazione;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.utenti.Fruitore;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.persistence.dto.PrenotazioneDTO;

import java.util.ArrayList;
import java.util.List;

public class PrenotazioniMapper {

    private final List<Appuntamento> appuntamenti;

    public PrenotazioniMapper(List<Appuntamento> appuntamenti) {
        this.appuntamenti = appuntamenti;
    }

    public List<Prenotazione> toDomain(List<PrenotazioneDTO> dtos) {
        List<Prenotazione> out = new ArrayList<>();
        if (dtos == null) return out;

        for (PrenotazioneDTO dto : dtos) {
            Appuntamento app = resolveAppuntamento(dto.luogoId(), dto.titoloVisita(), dto.data());
            if (app == null) {
                System.err.println("Appuntamento non trovato per prenotazione: luogoID=" + dto.luogoId()
                        + ", titolo=" + dto.titoloVisita() + ", data=" + dto.data());
                continue;
            }

            Fruitore f = new Fruitore(dto.usernameFruitore(), null);
            Prenotazione p = (dto.codicePrenotazione() != null && !dto.codicePrenotazione().isBlank())
                    ? new Prenotazione(dto.codicePrenotazione(), app, f, dto.numeroPersonePerPrenotazione())
                    : new Prenotazione(app, f, dto.numeroPersonePerPrenotazione());

            out.add(p);
        }

        return out;
    }

    private Appuntamento resolveAppuntamento(String luogoID, String titoloVisita, Data data) {
        if (appuntamenti == null || data == null || luogoID == null || titoloVisita == null) return null;

        for (Appuntamento app : appuntamenti) {
            Visita v = app.getVisita();
            if (v == null) continue;

            String luogoApp = v.getLuogoID();
            String titoloApp = v.getTitolo();
            Data dataApp = app.getData();

            boolean stessoLuogo = (luogoApp == null && luogoID == null) || (luogoApp != null && luogoApp.equals(luogoID));
            boolean stessoTitolo = (titoloApp == null && titoloVisita == null) || (titoloApp != null && titoloApp.equals(titoloVisita));
            boolean stessaData = dataApp.getGiorno() == data.getGiorno() &&
                    dataApp.getMese() == data.getMese() &&
                    dataApp.getAnno() == data.getAnno();

            if (stessoLuogo && stessoTitolo && stessaData) return app;
        }
        return null;
    }
}
