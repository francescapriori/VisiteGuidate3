package it.unibs.ingdsw.utenti;

public class Configuratore extends Utente {

    public Configuratore(String username, String password) {
        super(username, password, Ruolo.CONFIGURATORE);
    }

}
