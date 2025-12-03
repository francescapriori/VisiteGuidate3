package it.unibs.ingdsw;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.applicazione.InputManager;
import it.unibs.ingdsw.sicurezza.ServizioAutenticazione;
import it.unibs.ingdsw.utenti.Utente;


import java.util.Optional;

public class Main {
    public static void main(String[] args) {

        Applicazione applicazione = Applicazione.configuraApplicazione();

        ServizioAutenticazione auth = new ServizioAutenticazione(applicazione.getListaUtenti());

        System.out.println("Login");
        while (true) {
            Utente utenteAutenticato = auth.autenticazione();

            System.out.println("Benvenuto utente " + utenteAutenticato.getUsername()
                    + " (" + utenteAutenticato.getRuolo() + ")");
            utenteAutenticato.operazioni(applicazione);

            applicazione.salvaApplicazione();

            String risposta = InputManager.chiediSiNo("\nVuoi fare un altro accesso?");
            if (!"sì".equals(risposta)) {
                break;
            }
        }
    }
}