package it.unibs.ingdsw.persistence.mapper;

import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.persistence.dto.AppuntamentoDTO;

import java.util.ArrayList;
import java.util.List;

public class AppuntamentiMapper {
    private final ListaLuoghi listaLuoghi;
    private final ListaUtenti listaUtenti;

    public AppuntamentiMapper(ListaLuoghi listaLuoghi, ListaUtenti listaUtenti) {
        this.listaLuoghi = listaLuoghi;
        this.listaUtenti = listaUtenti;
    }

    public InsiemeAppuntamenti toDomain(List<AppuntamentoDTO> dtos) {
        ArrayList<Appuntamento> out = new ArrayList<>();
        if (dtos == null) return new InsiemeAppuntamenti(out);

        for (AppuntamentoDTO dto : dtos) {
            Appuntamento a = toDomain(dto);
            if (a != null) out.add(a);
        }
        return new InsiemeAppuntamenti(out);
    }

    public Appuntamento toDomain(AppuntamentoDTO dto) {
        Visita visita = resolveVisita(dto.luogoId(), dto.titoloVisita());
        if (visita == null) return null;

        Volontario guida = resolveVolontario(dto.usernameGuida());

        StatoAppuntamento stato = parseStato(dto.statoVisita());
        Appuntamento app = new Appuntamento(visita, dto.data(), guida);
        app.setStatoVisita(stato);
        app.setNumeroPersonePrenotate(dto.numeroPersonePrenotate());
        return app;
    }

    private StatoAppuntamento parseStato(String s) {
        if (s == null || s.isBlank()) return StatoAppuntamento.PROPOSTA;
        try { return StatoAppuntamento.valueOf(s.trim().toUpperCase()); }
        catch (Exception ex) { return StatoAppuntamento.PROPOSTA; }
    }

    private Visita resolveVisita(String luogoID, String titoloVisita) {
        if (listaLuoghi == null || luogoID == null || titoloVisita == null) return null;

        for (Luogo l : listaLuoghi.getListaLuoghi()) {
            if (luogoID.equals(l.getLuogoID()) && l.getInsiemeVisite() != null) {
                for (Visita v : l.getInsiemeVisite().getListaVisite()) {
                    if (v.getTitolo() != null && v.getTitolo().equalsIgnoreCase(titoloVisita)) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    private Volontario resolveVolontario(String username) {
        if (username == null || username.isBlank()) return null;

        if (listaUtenti != null) {
            for (Utente u : listaUtenti.getListaUtenti()) {
                if (u instanceof Volontario v &&
                        v.getUsername() != null &&
                        v.getUsername().equalsIgnoreCase(username)) {
                    return v;
                }
            }
        }
        return new Volontario(username, null);
    }
}
