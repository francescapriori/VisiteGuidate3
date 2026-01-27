package it.unibs.ingdsw.view.cli.menu;

import it.unibs.ingdsw.model.utenti.Configuratore;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.model.utenti.Volontario;

import java.time.YearMonth;

public abstract class MenuManager {

    protected final Utente utente;

    public MenuManager(Utente utente) {
        this.utente = utente;
    }

    public abstract Menu creaMenu(YearMonth nexDisponibilita);

    public abstract void inizializza();

    public static MenuManager mostraPer(Utente utente) {
        if (utente instanceof Configuratore c) {
            return new MenuConfiguratore(c);
        }
        if (utente instanceof Volontario v) {
            return new MenuVolontario(v);
        }
        return new MenuFruitore(utente);
    }

    public void cambioPassword() {
        while (true) {
            String nuova = InputManager.leggiStringaNonVuota("Dopo il primo accesso è necessario modificare la password: ");

            if (utente.pwUguale(nuova)) {
                System.out.println("La nuova password deve essere diversa dalla precedente.");
                continue;
            }

            utente.setPassword(nuova);
            System.out.println("Password aggiornata con successo.");
            return;
        }
    }

}