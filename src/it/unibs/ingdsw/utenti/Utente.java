package it.unibs.ingdsw.utenti;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.applicazione.InputManager;
import it.unibs.ingdsw.applicazione.MenuManager;

public abstract class Utente {

    private String username;
    private String password;
    private Ruolo ruolo;
    private boolean pwProvvisoria;

    public Utente(String username, String password, Ruolo ruolo) {
        this.username = username;
        this.password = password;
        this.ruolo = ruolo;
        this.pwProvvisoria = true;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public Ruolo getRuolo() {
        return ruolo;
    }
    public boolean isPwProvvisoria() {
        return pwProvvisoria;
    }

    public void setPassword(String password) {
        this.password = password;
        this.pwProvvisoria = false;
    }
    public void setPwProvvisoria(boolean pwProvvisoria) {
        this.pwProvvisoria = pwProvvisoria;
    }

    public boolean verificaPassword(String pwd) {
        return this.password != null && this.password.equals(pwd);
    }

    public void operazioni(Applicazione applicazione) {
        MenuManager menu = MenuManager.mostraPer(applicazione, this);
        if(this.isPwProvvisoria()) {
            menu.cambioPassword();
        }
        if (applicazione.isDaConfigurare()) {
            menu.primaInizializzazione();
        }
        menu.creaMenu().mostra();
    }

    public boolean ugualeA(Utente u) {
        if (userUguale(u.getUsername()) && pwUguale(u.getPassword())) {
            return true;
        }
        return false;
    }

    public boolean pwUguale(String pwd) {
        if (this.password.equals(pwd)) {
            return true;
        }
        return false;
    }

    public boolean userUguale(String user) {
        if (this.username.equalsIgnoreCase(user)) {
            return true;
        }
        return false;
    }

    public boolean forzaCambioPasswordSeNecessario() {
        if (!isPwProvvisoria()) return true;

        String nuova;
        do {
            nuova = InputManager.leggiStringaNonVuota(
                    "Dopo il primo accesso è necessario modificare la password: ");
            if (pwUguale(nuova)) {
                return false;
            }
        } while (pwUguale(nuova));

        setPassword(nuova);
        setPwProvvisoria(false);
        return true;
    }


    @Override
    public String toString() {
        return this.username;
    }

}
