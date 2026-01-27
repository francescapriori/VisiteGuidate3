package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Utente;

import java.util.Optional;

public class ServiceAutenticazione {
    private final ListaUtenti listaUtenti;

    public ServiceAutenticazione(ListaUtenti listaUtenti) {
        this.listaUtenti = listaUtenti;
    }

    public Optional<Utente> login(String username, String password) {
        return listaUtenti.corrispondenzaUtente(username.toLowerCase(), password);
    }
}