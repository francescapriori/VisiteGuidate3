package it.unibs.ingdsw.persistence.mapper;

import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.persistence.dto.DisponibilitaVolontarioDTO;

import java.util.HashMap;
import java.util.List;

public class DisponibilitaVolontariMapper {

    private final ListaUtenti listaUtenti;

    public DisponibilitaVolontariMapper(ListaUtenti listaUtenti) {
        this.listaUtenti = listaUtenti;
    }

    public HashMap<Volontario, InsiemeDate> toDomain(List<DisponibilitaVolontarioDTO> dtos) {
        HashMap<Volontario, InsiemeDate> map = new HashMap<>();
        if (dtos == null) return map;

        for (DisponibilitaVolontarioDTO dto : dtos) {
            Volontario v = resolveVolontario(dto.usernameVolontario());
            map.put(v, dto.disponibilita() != null ? dto.disponibilita() : new InsiemeDate());
        }
        return map;
    }

    private Volontario resolveVolontario(String username) {
        if (username == null || username.isBlank()) return new Volontario("", null);

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
