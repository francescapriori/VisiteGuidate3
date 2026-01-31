package it.unibs.ingdsw.model.utenti;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class ListaUtenti {
    private List<Utente> listaUtenti;

    public ListaUtenti() {
        this.listaUtenti = new ArrayList<>();
    }
    public ListaUtenti(List<Utente> listaUtenti) {this.listaUtenti = listaUtenti;}
    public List<Utente> getListaUtenti() {
        return listaUtenti;
    }

    public void aggiungiUtente(Utente utente) {
        this.listaUtenti.add(utente);
    }

    public Optional<Utente> corrispondenzaUtente(String username, String password) {
        Optional<Utente> optionalUtente = trovaUsername(username);
        if (optionalUtente.isPresent() && optionalUtente.get().verificaPassword(password)) {
            return optionalUtente;
        }
        return Optional.empty();
    }

    public Optional<Utente> trovaUsername(String username) {
        for (Utente u : listaUtenti) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return Optional.of(u);
            }
        }
        return Optional.empty();
    }

    public List<Volontario> getVolontari() {
        List<Volontario> volontari = new ArrayList<>();
        for (Utente u : this.listaUtenti) {
            if (u instanceof Volontario) volontari.add((Volontario) u);
        }
        return volontari;
    }

    public boolean usernameInUso(String username) {
        for (Utente u : this.listaUtenti) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return true;
            }
        }
        return false;
    }

    public int getNumeroVolontari() {
        return getVolontari().size();
    }
}

