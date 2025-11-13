package it.unibs.ingdsw.output;

import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;
import it.unibs.ingdsw.visite.Visita;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class OutputManager {

    //escluse, disponibilità
    public static void visualizzaDatePerMeseAnno(InsiemeDate date, int mese, int anno, TipoRichiestaData tipo) {
        String nomeMese;
        nomeMese = Month.of(mese).getDisplayName(TextStyle.FULL, Locale.ITALIAN);

        switch (tipo) {
            case ESCLUSIONE -> {
                if (date.getInsiemeDate().isEmpty()) {
                    System.out.println("Non sono presenti date escluse per il mese di " + nomeMese + " " + anno);
                    return;
                }
                System.out.println("Le date escluse per il mese di " + nomeMese + " " + anno + " sono:");
            }
            case DISPONIBILITA -> {
                if (date.getInsiemeDate().isEmpty()) {
                    System.out.println("Non hai ancora dato nessuna disponibilità per il mese di " + nomeMese + " " + anno + ".");
                    return;
                }
                System.out.println("Le date in cui ti sei già reso disponibile per le visite del mese di " + nomeMese + " " + anno + " sono:");
            }
        }
        System.out.println(date.toString());

    }

    public enum TipoRichiestaData {
        ESCLUSIONE, DISPONIBILITA
    }

    public static void visualizzaLuoghi(ListaLuoghi luoghi) {
        int i = 1;
        for(Luogo l : luoghi.getListaLuoghi()) {
            System.out.println(i + ") " + l.stampaSoloLuogo());
            i++;
        }
    }

    public static void visualizzaVolontariConVisiteAssociate(HashMap<Volontario, ListaVisite> map) {
        if (map.isEmpty()) {
            System.out.println("Nessun volontario registrato.");
        }
        for (HashMap.Entry<Volontario, ListaVisite> entry : map.entrySet()) {
            System.out.println("Volontario: " + entry.getKey().getUsername());
            ListaVisite lv = entry.getValue();
            if (lv.getListaVisite().isEmpty()) System.out.println("  (nessuna visita)");
            else lv.getListaVisite().forEach(v ->
                    System.out.println("  - " + v.getTitolo() + " - " + v.getLuogoID()));
            System.out.println();
        }
    }

    public static void visualizzaListaLuoghi(ListaLuoghi listaLuoghi) {
        System.out.println("-----\nLista de Luoghi registrati: ");
        System.out.println(listaLuoghi.visualizzaListaLuoghi());
    }

    public static void visualizzaListaVisite(ListaVisite listaVisite) {
        int i = 1;
        for(Visita v : listaVisite.getListaVisite()) {
            System.out.println(i + ") " + v.toString());
            i++;
        }
    }
}