package it.unibs.ingdsw.sicurezza;

import it.unibs.ingdsw.inputOutput.InputManager;
import it.unibs.ingdsw.utenti.ListaUtenti;
import it.unibs.ingdsw.utenti.Utente;

import java.util.Optional;

public class ServizioAutenticazione {
    private final ListaUtenti listaUtenti;

    public ServizioAutenticazione(ListaUtenti listaUtenti) {
        this.listaUtenti = listaUtenti;
    }

    public Optional<Utente> login(String username, String password) {
        return listaUtenti.corrispondenzaUtente(username.toLowerCase(), password);
    }

    public Utente autenticazione() {
        Utente utente = null;
        while (utente == null) {
            String username = InputManager.richiediUsernameLogin();
            String password = InputManager.richiediPasswordLogin();

            Optional<Utente> maybe = login(username, password);
            if (maybe.isPresent()) {
                utente = maybe.get();
            } else {
                System.out.println("Credenziali non valide. Riprova.\n");
            }
        }

        return utente;
    }
}
