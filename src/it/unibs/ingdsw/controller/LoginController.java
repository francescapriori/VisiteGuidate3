package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.service.ServiceAutenticazione;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.view.cli.io.Output;

public class LoginController {
    private final ServiceAutenticazione auth;
    private final Output out;

    public LoginController(Output out) {
        this.auth = new ServiceAutenticazione(Applicazione.getApplicazione().getListaUtenti());
        this.out = out;
    }

    public Utente autenticazione() {
        while (true) {
            String username = InputManager.richiediUsernameLogin();
            String password = InputManager.richiediPasswordLogin();

            return auth.login(username, password).orElseGet(() -> {
                        out.println("Credenziali non valide. Riprova.\n");
                        return null;
                    });
        }
    }
}