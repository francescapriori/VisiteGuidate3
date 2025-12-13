package it.unibs.ingdsw.menu;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.utenti.Configuratore;
import it.unibs.ingdsw.utenti.Utente;
import it.unibs.ingdsw.inputOutput.InputManager;
import it.unibs.ingdsw.utenti.Volontario;

public abstract class MenuManager {

    public final Applicazione applicazione;
    public final Utente utente;

    public MenuManager(Applicazione applicazione, Utente utente) {
        this.applicazione = applicazione;
        this.utente = utente;
    }

    public abstract Menu creaMenu();

    public abstract void primaInizializzazione();


    public static MenuManager mostraPer(Applicazione applicazione, Utente utente) {
        if (utente instanceof Configuratore) {
            return new MenuConfiguratore(applicazione, utente);
        }
        if (utente instanceof Volontario) {
            return new MenuVolontario(applicazione, utente);
        }
        else {
            return new MenuFruitore(applicazione, utente);
        }
    }

    public void cambioPassword() {
        String nuova;
        do {
            nuova = InputManager.leggiStringaNonVuota(
                    "Dopo il primo accesso è necessario modificare la password: ");
            if (this.utente.pwUguale(nuova)) {
                System.out.println("La nuova password deve essere diversa dalla precedente.");
            }
        } while (this.utente.pwUguale(nuova));

        this.utente.setPassword(nuova);

        System.out.println("Password aggiornata con successo.");
    }
}