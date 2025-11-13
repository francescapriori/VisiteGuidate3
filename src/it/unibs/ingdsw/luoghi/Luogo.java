package it.unibs.ingdsw.luoghi;

import it.unibs.ingdsw.visite.ListaVisite;

import java.util.regex.Pattern;

public class Luogo {

    public static final Pattern ID_PATTERN = Pattern.compile("^L(\\d+)$");

    private String luogoID;
    private String nome;
    private String descrizione;
    private Posizione posizione;
    private ListaVisite insiemeVisite;

    public Luogo(String luogoID, String nome, String descrizione, Posizione posizione, ListaVisite insiemeVisite) {
        this.luogoID = luogoID;
        this.nome = nome;
        this.descrizione = descrizione;
        this.posizione = posizione;
        this.insiemeVisite = insiemeVisite;
    }

    public String getLuogoID() {
        return luogoID;
    }

    public String getNome() {
        return nome;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public Posizione getPosizione() {
        return posizione;
    }
    public ListaVisite getInsiemeVisite() {
        return insiemeVisite;
    }

    public String stampaSoloLuogo(){
        return String.format("%s - %s", this.nome, this.descrizione) +
                "\n" + this.posizione.toString();
    }

    public boolean luogoUguale(Luogo l) {
        if (this.luogoID.equals(l.getLuogoID()) && this.nome.equalsIgnoreCase(l.getNome())) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - \n%s", this.luogoID, this.nome, this.descrizione) +
                "\n" + this.posizione.toString() +
                "\nVisite associate: \n" + this.insiemeVisite.toString();
    }

}
