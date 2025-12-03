package it.unibs.ingdsw.utenti;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.applicazione.MenuManager;

import java.util.Objects;

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

    public boolean pwUguale(String pwd) {
        if (this.password.equals(pwd)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return this.username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;                 // stesso oggetto
        if (!(o instanceof Utente)) return false;   // deve essere Utente o sottoclasse
        Utente utente = (Utente) o;
        // confronto basato SOLO sullo username
        return Objects.equals(username, utente.username);
    }

    @Override
    public int hashCode() {
        // coerente con equals: usa solo username
        return Objects.hash(username);
    }

}
