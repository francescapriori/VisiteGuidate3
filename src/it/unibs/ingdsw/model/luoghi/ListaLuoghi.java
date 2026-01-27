package it.unibs.ingdsw.model.luoghi;

import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.model.utenti.Volontario;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;

public class ListaLuoghi {

    private ArrayList<Luogo> listaLuoghi;

    public ListaLuoghi() {
        this.listaLuoghi = new ArrayList<>();
    }

    public ArrayList<Luogo> getListaLuoghi() {
        return listaLuoghi;
    }

    public void aggiungiLuogo(Luogo luogo) {
        this.listaLuoghi.add(luogo);
    }

    public int getNumeroLuogo() {
        return this.listaLuoghi.size();
    }

    public Luogo scegliLuogo(int posizione) {
        return listaLuoghi.get(posizione);
    }

    public ArrayList<String> estraiNomeLuoghi() {
        ArrayList<String> listaNomeLuoghi = new ArrayList<>();
        for (Luogo luogo : this.listaLuoghi) {
            listaNomeLuoghi.add(luogo.getNome());
        }
        return listaNomeLuoghi;
    }

    public String generaProssimoId() {
        int max = 0;
        for (Luogo l : this.listaLuoghi) {
            if (l == null || l.getLuogoID() == null) continue;
            String id = l.getLuogoID().trim();
            Matcher m = Luogo.ID_PATTERN.matcher(id);
            if (m.matches()) {
                int n = Integer.parseInt(m.group(1));
                if (n > max) max = n;
            }
        }
        return Luogo.creaIdLuogo(max + 1);
    }

    public ListaVisite getTotaleVisite() {
        ListaVisite lista = new ListaVisite();

        for (Luogo l : this.getListaLuoghi()) {
            for (Visita v : l.getInsiemeVisite().getListaVisite()) {
                lista.aggiungiVisita(v);
            }
        }
        return lista;
    }

    public boolean aggiungiLuogoSeNonPresente(Luogo luogo) {
        for (Luogo esistente : this.listaLuoghi) {
            if (esistente.luogoUguale(luogo)) {
                return false;
            }
        }
        this.listaLuoghi.add(luogo);
        return true;
    }

    public boolean volConAlmenoUnaVisita(Volontario volontario) {
        for (Luogo luogo : this.listaLuoghi) {
            for (Visita v : luogo.getInsiemeVisite().getListaVisite()) {
                if (v.getVolontariVisita().contains(volontario)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void rimuoviLuogo(String nomeTarget){
        for (Iterator<Luogo> it = this.listaLuoghi.iterator(); it.hasNext(); ) {
            Luogo corrente = it.next();
            if (corrente != null && nomeTarget.equals(corrente.getNome())) {
                it.remove();
            }

        }
    }
}
