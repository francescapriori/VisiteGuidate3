package it.unibs.ingdsw.inputOutput;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.*;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class OutputManager {

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

    public static void visualizzaSoloVolontari(Applicazione applicazione) {
        int i = 1;
        for (Volontario v : applicazione.getListaUtenti().getVolontari()) {
            System.out.println(i + ") " + v.toString());
            i++;
        }
    }

    public static void visualizzaAppuntamentiPerStato(CalendarioAppuntamenti appuntamenti, boolean ancheEffettuataeCancellata) {
        System.out.println("Gli appuntamenti disponibili suddivisi nei vari stati sono:\n");

        StatoVisita[] tutti = StatoVisita.values();

        for (StatoVisita s : tutti) {
            if (!ancheEffettuataeCancellata && s == StatoVisita.EFFETTUATA || !ancheEffettuataeCancellata && s == StatoVisita.CANCELLATA) {
                continue; // passa al prossimo stato
            }

            System.out.println("Stato: " + s.toString());
            System.out.println(appuntamenti.getAppuntamentiConStato(s).toString() + "\n");
        }
    }

    public static ArrayList<Appuntamento> visualizzaAppuntamentiPerPrenotazione (CalendarioAppuntamenti appuntamenti) {
        System.out.println("Gli appuntamenti disponibili per la prenotazione sono:\n");
        ArrayList<Appuntamento> appuntamentiPrenotabili = new ArrayList<>();
        StatoVisita[] tutti = StatoVisita.values();
        int i = 1;
        for (Appuntamento a : appuntamenti.getAppuntamenti()) {
            if(a.getStatoVisita() == StatoVisita.PROPOSTA) {
                System.out.println(i + ") " + a.toString());
                appuntamentiPrenotabili.add(a);
                i++;
            }
        }
        return appuntamentiPrenotabili;
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

    public static void visualizzaLuoghiEvisite(ListaLuoghi luoghi) {
        int i = 1;
        for(Luogo l : luoghi.getListaLuoghi()) {
            System.out.println(i + ") " + l.stampaLuogoBase());
            for(Visita v : l.getInsiemeVisite().getListaVisite()) {
                System.out.println("\t-" + v.stampaVisitaBase());
            }
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

    public static void visualizzaListaVisite(ListaVisite listaVisite) {
        int i = 1;
        for(Visita v : listaVisite.getListaVisite()) {
            System.out.println(i + ") " + v.toString());
            i++;
        }
    }

    public static void visualizzaCalendario(CalendarioAppuntamenti calendarioAppuntamenti, String nomeMeseV, int annoTargetV) {

        System.out.println("\n-----Lista degli appuntamenti per il mese di " + nomeMeseV + " " + annoTargetV + "-----");
        for(Appuntamento a : calendarioAppuntamenti.getAppuntamenti()) {
            System.out.println(a.toString());
        }
    }

    public static void visualizzaPrenotazioni(ArrayList<Prenotazione> listaPrenotazioni, String nomeMeseV, int annoTargetV) {
        System.out.println("\n-----I tuoi appuntamenti per il mese di " + nomeMeseV + " " + annoTargetV + "-----");
        int i = 1;
        for (Prenotazione p : listaPrenotazioni) {
            System.out.println(i + ") " + p.toString()); //todo fare alternativa a questo tostring
            i++;
        }
    }
}