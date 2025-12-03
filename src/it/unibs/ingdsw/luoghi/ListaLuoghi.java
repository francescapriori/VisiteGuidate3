package it.unibs.ingdsw.luoghi;

import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;
import it.unibs.ingdsw.visite.Visita;

import java.util.ArrayList;
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

    public String visualizzaListaLuoghi() {
        int i = 1;
        String luoghi = "";
        for (Luogo luogo : this.listaLuoghi) {
            luoghi += i + ") " + luogo.stampaSoloLuogo();
            i++;
        }
        return luoghi;
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
        return formatId(max + 1);
    }

    private static String formatId(int n) {
        if (n < 1) n = 1;
        return (n < 1000) ? String.format("L%03d", n) : "L" + n;
    }

    public ListaVisite visiteDelVolontario(Volontario volontario) {
        ListaVisite lista = new ListaVisite();
        String user = volontario.getUsername();

        for (Luogo l : this.getListaLuoghi()) {
            for (Visita v : l.getInsiemeVisite().getListaVisite()) {
                boolean assegnato = v.getVolontariVisita().stream()
                        .anyMatch(vol -> vol != null
                                && vol.getUsername() != null
                                && vol.getUsername().equalsIgnoreCase(user));
                if (assegnato) {
                    lista.aggiungiVisita(v);
                }
            }
        }
        return lista;
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
                return false; // già presente
            }
        }
        this.listaLuoghi.add(luogo);
        return true; // aggiunto ora
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
}
