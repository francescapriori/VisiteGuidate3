package it.unibs.ingdsw.utenti;

import java.util.Objects;

public class Fruitore extends Utente{

    public Fruitore(String username, String password) {
        super(username, password, Ruolo.FRUITORE);
    }

    public String getUsername() {
        return super.getUsername();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utente)) return false;
        Utente utente = (Utente) o;
        return Objects.equals(getUsername(), utente.getUsername());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUsername());
    }

}
