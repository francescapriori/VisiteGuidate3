package it.unibs.ingdsw.view.cli.io;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.prenotazione.Prenotazione;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.view.cli.format.Formatters;

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
        System.out.println(Formatters.insiemeDate(date));

    }

    public static void visualizzaSoloVolontari(Applicazione applicazione) {
        int i = 1;
        for (Volontario v : applicazione.getListaUtenti().getVolontari()) {
            System.out.println(i + ") " + Formatters.utente(v));
            i++;
        }
    }

    public static void visualizzaAppuntamentiPerStato(InsiemeAppuntamenti appuntamenti, boolean ancheEffettuataeCancellata) {
        StatoAppuntamento[] tutti = StatoAppuntamento.values();

        for (StatoAppuntamento s : tutti) {
            if (!ancheEffettuataeCancellata && s == StatoAppuntamento.EFFETTUATA || !ancheEffettuataeCancellata && s == StatoAppuntamento.CANCELLATA) {
                continue; // passa al prossimo stato
            }

            System.out.println("Appuntamenti in stato: " + s);
            System.out.println(Formatters.calendarioAppuntamenti(appuntamenti.getAppuntamentiConStato(s)) + "\n");
        }
    }

    public static void visualizzaAppuntamentiPerStato(ArrayList<Prenotazione> prenotazioni, ArrayList<Appuntamento> appuntamenti, StatoAppuntamento[] statiDaEstrarre) {
        System.out.println("Gli appuntamenti disponibili sono:\n");

        for (StatoAppuntamento sv : statiDaEstrarre) {
            System.out.println("Stato: " + sv);
            boolean trovatoInQuestoStato = false;
            for (Appuntamento a : appuntamenti) {
                if (a.getStatoVisita() == sv) {
                    trovatoInQuestoStato = true;
                    System.out.println(a.getVisita().getTitolo() + " - " + Formatters.data(a.getData()));
                    ArrayList<Prenotazione> prenotazioniAss = a.getPrenotazioniAssociate(prenotazioni);
                    if (prenotazioniAss.isEmpty()) {
                        System.out.println("  Nessuna prenotazione effettuata.");
                    } else {
                        for (Prenotazione p : prenotazioniAss) {
                            System.out.println("  " + Formatters.prenotazione(p));
                        }
                    }
                }
            }
            if (!trovatoInQuestoStato) {
                System.out.println("  Non hai nessun appuntamento in questo stato.");
            }
            System.out.println();
        }
    }

    public static ArrayList<Appuntamento> visualizzaAppuntamentiPerPrenotazione(InsiemeAppuntamenti appuntamenti) {
        System.out.println("\nGli appuntamenti disponibili per la prenotazione sono:");
        ArrayList<Appuntamento> appuntamentiPrenotabili = new ArrayList<>();
        StatoAppuntamento[] tutti = StatoAppuntamento.values();
        int i = 1;
        for (Appuntamento a : appuntamenti.getAppuntamenti()) {
            if (a.getStatoVisita() == StatoAppuntamento.PROPOSTA) {
                System.out.println(i + ") " + Formatters.appuntamento(a));
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
        boolean vuoto = true;
        for (Luogo l : luoghi.getListaLuoghi()) {
            System.out.println(i + ") " + Formatters.soloLuogo(l));
            i++;
            vuoto = false;
        }
        if (vuoto) {
            System.out.println("Nessuna luogo registrato.");
        }
    }

    public static void visualizzaLuoghiEvisite(ListaLuoghi luoghi) {
        int i = 1;
        boolean vuoto = true;
        for (Luogo l : luoghi.getListaLuoghi()) {
            System.out.println(i + ") " + Formatters.luogoBase(l));
            for (Visita v : l.getInsiemeVisite().getListaVisite()) {
                System.out.println("\t-" + Formatters.visitaBase(v));
                vuoto = true;
            }
            i++;
        }
        if (vuoto) {
            System.out.println("Nessuna luogo registrato.");
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
        for (Visita v : listaVisite.getListaVisite()) {
            System.out.println(i + ") " + Formatters.visita(v));
            i++;
        }
    }

    public static void visualizzaCalendario(InsiemeAppuntamenti calendarioAppuntamenti, String nomeMeseV, int annoTargetV) {

        System.out.println("\n-----Lista degli appuntamenti per il mese di " + nomeMeseV + " " + annoTargetV + "-----");
        boolean vuoto = true;
        for (Appuntamento a : calendarioAppuntamenti.getAppuntamenti()) {
            System.out.println(Formatters.appuntamento(a));
            vuoto = false;
        }
        if(vuoto){
            System.out.println("Non sono disponibili appuntamenti per il mese di " + nomeMeseV + " " + annoTargetV);
        }
    }

    public static void visualizzaPrenotazioni(ArrayList<Prenotazione> prenotazioni, ArrayList<Appuntamento> appuntamenti, StatoAppuntamento[] statiDaEstrarre, String nomeMeseV, int annoTargetV) {
        System.out.println("I tuoi appuntamenti prenotati per il mese di " + nomeMeseV + " " + annoTargetV + "sono: ");

        for (StatoAppuntamento sv : statiDaEstrarre) {
            System.out.println("Stato: " + sv);
            boolean trovatoInQuestoStato = false;
            for (Prenotazione p : prenotazioni) {
                if (p.getAppuntamento().getStatoVisita() == sv) {
                    trovatoInQuestoStato = true;
                    System.out.println(" - " + Formatters.prenotazione(p) + " - PER " + p.getAppuntamento().getVisita().getTitolo() + " - " + Formatters.data(p.getAppuntamento().getData()));
                    ArrayList<Prenotazione> prenotazioniAss = p.getAppuntamento().getPrenotazioniAssociate(prenotazioni);
                    if (prenotazioniAss.isEmpty()) {
                        System.out.println("  Nessuna prenotazione effettuata.");
                    }
                }
            }
            if (!trovatoInQuestoStato) {
                System.out.println("  Non hai nessun appuntamento prenotato in questo stato.");
            }
            System.out.println();
        }
    }
}