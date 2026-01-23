package it.unibs.ingdsw.model.utenti;

import java.util.Objects;

public class Volontario extends Utente{

    public Volontario(String username, String password) {
        super(username, password, Ruolo.VOLONTARIO);
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
