package it.unibs.ingdsw;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.persistence.xml.XmlApplicazioneRepository;
import it.unibs.ingdsw.controller.LoginController;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.service.ServiceAutenticazione;
import it.unibs.ingdsw.model.utenti.Utente;

public class Main {
    public static void main(String[] args) {
        XmlApplicazioneRepository appR = new XmlApplicazioneRepository();
        Applicazione applicazione = appR.configuraApplicazione();

        ServiceAutenticazione auth = new ServiceAutenticazione(applicazione.getListaUtenti());
        LoginController login = new LoginController(auth);

        System.out.println("Login");
        while (true) {
            Utente utenteAutenticato = login.autenticazione();

            System.out.println("Benvenuto utente " + utenteAutenticato.getUsername()
                    + " (" + utenteAutenticato.getRuolo() + ")");
            utenteAutenticato.operazioni(applicazione);

            appR.salvaApplicazione(applicazione);

            String risposta = InputManager.chiediSiNo("\nVuoi fare un altro accesso?");
            if (!"sì".equals(risposta)) {
                break;
            }
        }
    }
}