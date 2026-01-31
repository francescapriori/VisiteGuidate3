package it.unibs.ingdsw;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.persistence.xml.XmlApplicazioneRepository;
import it.unibs.ingdsw.controller.LoginController;
import it.unibs.ingdsw.view.cli.io.ConsoleOutput;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.view.cli.io.Output;
import it.unibs.ingdsw.view.cli.menu.Menu;
import it.unibs.ingdsw.view.cli.menu.MenuManager;

public class Main {
    public static void main(String[] args) {
        Output out = new ConsoleOutput(System.out);
        XmlApplicazioneRepository appR = new XmlApplicazioneRepository();
        Applicazione applicazione = appR.configuraApplicazione();
        Applicazione.setInstance(applicazione);

        LoginController login = new LoginController(out);

        out.println("Login");
        while (true) {
            Utente utenteAutenticato = login.autenticazione();

            out.println("Benvenuto utente " + utenteAutenticato.getUsername()
                    + " (" + utenteAutenticato.getRuolo() + ")");

            //inversione del controllo
            MenuManager menuManager = utenteAutenticato.operazioni(applicazione, out);
            boolean continua = true;
            while (continua) {
                Menu menu = menuManager.creaMenu(applicazione.getNextDisponibilita());
                continua = menu.mostra();
            }

            appR.salvaApplicazione(applicazione);

            String risposta = InputManager.chiediSiNo("\nVuoi fare un altro accesso?");
            if (!"sì".equals(risposta)) {
                break;
            }
        }
    }
}