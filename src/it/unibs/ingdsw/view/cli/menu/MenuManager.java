package it.unibs.ingdsw.view.cli.menu;

import it.unibs.ingdsw.model.utenti.Configuratore;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.view.cli.io.Output;
import it.unibs.ingdsw.view.cli.io.OutputManager;

import java.time.YearMonth;

public abstract class MenuManager {

    protected final Utente utente;
    private final Output out;

    public MenuManager(Utente utente, Output out) {
        this.utente = utente;
        this.out = out;
    }

    public abstract Menu creaMenu(YearMonth nexDisponibilita);

    public abstract void inizializza();

    public static MenuManager mostraPer(Utente utente, Output out) {
        if (utente instanceof Configuratore c) {
            return new MenuConfiguratore(c, out);
        }
        if (utente instanceof Volontario v) {
            return new MenuVolontario(v, out);
        }
        return new MenuFruitore(utente, out);
    }

    public void cambioPassword() {
        while (true) {
            String nuova = InputManager.leggiStringaNonVuota("Dopo il primo accesso è necessario modificare la password: ");

            if (utente.pwUguale(nuova)) {
                out.println("La nuova password deve essere diversa dalla precedente.");
                continue;
            }

            utente.setPassword(nuova);
            out.println("Password aggiornata con successo.");
            return;
        }
    }

}