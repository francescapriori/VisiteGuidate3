package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.service.ServiceAutenticazione;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.view.cli.io.OutputManager;

public class LoginController {
    private final ServiceAutenticazione auth;

    public LoginController() {
        this.auth = new ServiceAutenticazione(Applicazione.getApplicazione().getListaUtenti());
    }

    public Utente autenticazione() {
        while (true) {
            String username = InputManager.richiediUsernameLogin();
            String password = InputManager.richiediPasswordLogin();

            return auth.login(username, password).orElseGet(() -> {
                        OutputManager.visualizzaMessaggio("Credenziali non valide. Riprova.\n");
                        return null;
                    });
        }
    }
}