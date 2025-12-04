package it.unibs.ingdsw.utenti;

import java.util.ArrayList;
import java.util.Optional;

public class ListaUtenti {
    private ArrayList<Utente> listaUtenti;

    public ListaUtenti() {
        this.listaUtenti = new ArrayList<>();
    }
    public ListaUtenti(ArrayList<Utente> listaUtenti) {this.listaUtenti = listaUtenti;}
    public ArrayList<Utente> getListaUtenti() {
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

    public ArrayList<Volontario> getVolontari() {
        ArrayList<Volontario> volontari = new ArrayList<>();
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

    @Override
    public String toString() {
        return this.listaUtenti.toString();
    }
}

