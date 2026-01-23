package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.service.ServiceAutenticazione;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.model.utenti.Utente;

public class LoginController {
    private final ServiceAutenticazione auth;

    public LoginController(ServiceAutenticazione auth) {
        this.auth = auth;
    }

    public Utente autenticazione() {
        while (true) {
            String username = InputManager.richiediUsernameLogin();
            String password = InputManager.richiediPasswordLogin();

            return auth.login(username, password).orElseGet(() -> {
                        System.out.println("Credenziali non valide. Riprova.\n");
                        return null;
                    });
        }
    }
}