package it.unibs.ingdsw.utenti;

public class Volontario extends Utente{

    public Volontario(String username, String password) {
        super(username, password, Ruolo.VOLONTARIO);
    }

    public String getUsername() {
        return super.getUsername();
    }
}
