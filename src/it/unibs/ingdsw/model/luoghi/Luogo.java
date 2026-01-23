package it.unibs.ingdsw.model.luoghi;

import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;

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

    public boolean luogoUguale(Luogo l) {
        if (this.luogoID.equals(l.getLuogoID()) && this.nome.equalsIgnoreCase(l.getNome())) {
            return true;
        }
        return false;
    }

    public void aggiungiVisite(ListaVisite listaVisite) {
        for(Visita v : listaVisite.getListaVisite()) {
            this.insiemeVisite.getListaVisite().add(v);
        }
    }

    public Visita getVisitaIesima(int i) {
        return this.getInsiemeVisite().getListaVisite().get(i);
    }

    public static String creaIdLuogo(int n) {
        if (n < 1) n = 1;
        return (n < 1000) ? String.format("L%03d", n) : "L" + n;
    }
}
