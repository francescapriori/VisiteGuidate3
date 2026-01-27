package it.unibs.ingdsw.model.utenti;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.view.cli.menu.Menu;
import it.unibs.ingdsw.view.cli.menu.MenuManager;

import java.util.Objects;

public abstract class Utente {

    private final String username;
    private String password;
    private final Ruolo ruolo;
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

    public MenuManager operazioni(Applicazione applicazione) {
        MenuManager menuManager = MenuManager.mostraPer(this);
        if (this.isPwProvvisoria()) {
            menuManager.cambioPassword();
        }
        if (applicazione.isDaConfigurare()) {
            menuManager.inizializza();
        }
        return menuManager;
    }

    public boolean utenteUguale(Utente utente) {
        return utente != null && this.username != null && utente.getUsername() != null && this.username.equalsIgnoreCase(utente.getUsername());
    }


    public boolean pwUguale(String pwd) {
        return this.password.equals(pwd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente)) return false;
        Utente utente = (Utente) o;
        return Objects.equals(username, utente.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

}
