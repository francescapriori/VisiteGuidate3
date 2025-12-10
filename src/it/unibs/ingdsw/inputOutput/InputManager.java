package it.unibs.ingdsw.inputOutput;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.luoghi.Posizione;
import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.tempo.Giornate;
import it.unibs.ingdsw.tempo.GiornoSettimana;
import it.unibs.ingdsw.tempo.Orario;
import it.unibs.ingdsw.utenti.ListaUtenti;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;
import it.unibs.ingdsw.visite.StatoVisita;
import it.unibs.ingdsw.visite.Visita;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class InputManager {

    private static final Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

    public static String leggiStringaNonVuota(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            System.out.println("Errore: il campo non può essere vuoto.");
        }
    }
    public static int leggiInteroConMinMax(String messaggio, Integer min, Integer max) {
        while (true) {
            System.out.print(messaggio);
            if (scanner.hasNextInt()) {
                int numero = scanner.nextInt();
                scanner.nextLine();
                if ((min == null || numero >= min) && (max == null || numero <= max)) return numero;
            } else scanner.nextLine();
            System.out.println("Errore: inserire un numero valido"
                    + (min != null ? " >= " + min : "")
                    + (max != null ? " e <= " + max : "") + ".");
        }
    }

    public static int leggiInteroConMin(String messaggio, Integer min) {
        while (true) {
            System.out.print(messaggio);
            if (scanner.hasNextInt()) {
                int numero = scanner.nextInt();
                scanner.nextLine();
                if ((min == null || numero >= min)) return numero;
            } else scanner.nextLine();
            System.out.println("Errore: inserire un numero valido" + (min != null ? " >= " + min : ""));
        }
    }

    public static double leggiDouble(String messaggio, double min, double max) {
        while (true) {
            System.out.print(messaggio);
            if (scanner.hasNextDouble()) {
                double val = scanner.nextDouble();
                scanner.nextLine();
                if (val >= min && val <= max) return val;
            } else scanner.next();
            System.out.println("Errore: inserire un valore valido tra " + min + " e " + max + ".");
        }
    }

    public static String chiediSiNo(String messaggio) {
        while (true) {
            System.out.print(messaggio + " (sì/no): ");
            String in = scanner.nextLine().trim().toLowerCase(); // lowercase

            if (in.equals("sì") || in.equals("si") || in.equals("s")) {
                return "sì";
            }

            if (in.equals("no") || in.equals("n")) {
                return "no";
            }

            System.out.println("Risposta non valida. Scrivi \"sì\" o \"no\".");
        }
    }

    public static String richiediAmbitoTerritorialeApplicazione(){
        return leggiStringaNonVuota("Inserire l'ambito territoriale di competenza dell'applicazione: ");
    }

    public static int richiediNumeroMassimoIscrivibili() {
        return leggiInteroConMin("Inserire il numero massimo di persone che possono essere iscritte con una singola iscrizione: ", 1);
    }

    public static String richiediUsernameLogin(){
        return leggiStringaNonVuota("Inserisci l'username: ");
    }

    public static String richiediNuovoUsername(ListaUtenti listaUtenti){
        String username;
        do {
            username = leggiStringaNonVuota("Inserisci l'username: ");
            if(listaUtenti.usernameInUso(username)) {
                System.out.println("Non è possibile utilizzare questo username poichè già in uso.");
            }
        } while(listaUtenti.usernameInUso(username));
        return username;
    }

    public static String richiediPasswordLogin(){
        return leggiStringaNonVuota("Inserisci l'password: ");
    }

    // la data ritornata è sempre valida
    public static Data chiediData() {
        Data d = new Data();
        do {
            d.setGiorno(InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 31));
            d.setMese(InputManager.leggiInteroConMinMax("Inserisci il mese: ", 1, 12));
            d.setAnno(InputManager.leggiInteroConMin("Inserisci l'anno: ", 1970));
            if(!d.dataValida()) {
                System.out.println("Errore: data invalida.");
            }
        } while(!d.dataValida());

        return d;
    }

    public static int chiediGiorno(int mese, int anno) {
        int giorno = 1;
        if (Data.isBisestile(anno) && mese == 2) {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 29);
            return giorno;
        }
        if (!Data.isBisestile(anno) && mese == 2) {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 28);
            return giorno;
        }
        if (mese == 1 || mese == 3 || mese == 5 || mese == 7 || mese == 8 || mese == 10 || mese == 12) {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 31);
            return giorno;
        }
        else {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 30);
            return giorno;
        }
    }

    public static StatoVisita chiediStatoVisita() {
        StatoVisita[] vals = StatoVisita.values();
        for (int i = 0; i < vals.length; i++) {
            System.out.println((i + 1) + ") " + vals[i]);
        }
        int scelta = leggiInteroConMinMax("Seleziona (1-" + vals.length + "): ", 1, vals.length);
        return vals[scelta - 1];
    }


    public static Posizione chiediPosizione() {
        return new Posizione(
                InputManager.leggiStringaNonVuota("Inserisci il nome del paese: "),
                InputManager.leggiStringaNonVuota("Inserisci il nome della via: "),
                chiediCAP(),
                InputManager.leggiDouble("Latitudine (usa la virgola per i decimali): ", Posizione.LAT_MIN, Posizione.LAT_MAX),
                InputManager.leggiDouble("Longitudine (usa la virgola per i decimali): ", Posizione.LON_MIN, Posizione.LON_MAX)
        );
    }

    public static String chiediCAP() {
        while (true) {
            String cap = InputManager.leggiStringaNonVuota("Inserire il CAP: ").trim();
            if (cap.matches("\\d{" + Posizione.LUNGHEZZA_CAP + "}")) return cap;
            System.out.println("Errore: il CAP deve essere un numero di " + Posizione.LUNGHEZZA_CAP + " cifre.");
        }
    }


    public static GiornoSettimana chiediGiornoSettimana() {
        GiornoSettimana[] vals = GiornoSettimana.values();
        for (int i = 0; i < vals.length; i++) System.out.println((i + 1) + ") " + vals[i]);
        int scelta = InputManager.leggiInteroConMinMax("Seleziona (1-" + vals.length + "): ", 1, vals.length);
        return vals[scelta - 1];
    }

    public static ListaLuoghi chiediLuoghi(Applicazione applicazione) {
        ListaLuoghi lista = new ListaLuoghi();
        do {
            lista.aggiungiLuogo(chiediLuogo(applicazione));
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un altro luogo?")));
        return lista;
    }

    public static Luogo chiediLuogo(Applicazione applicazione) {
        String id = applicazione.getListaLuoghi().generaProssimoId();
        String nome = InputManager.leggiStringaNonVuota("Inserisci il nome del luogo: ");
        String descrizione = InputManager.leggiStringaNonVuota("Inserisci la descrizione del luogo: ");
        Posizione posizione = InputManager.chiediPosizione();
        ListaVisite listaVisite = chiediVisite(posizione, id, applicazione);
        return new Luogo(id, nome, descrizione, posizione, listaVisite);
    }

    public static ListaVisite chiediVisite(Posizione posizioneLuogo, String luogoId, Applicazione applicazione) {
        ListaVisite lista = new ListaVisite();
        do {
            lista.aggiungiVisita(chiediVisita(posizioneLuogo, luogoId, applicazione));
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un'altra visita?")));
        return lista;
    }

    public static Visita chiediVisita(Posizione posizioneLuogo, String luogoId, Applicazione applicazione) {
        String titolo = InputManager.leggiStringaNonVuota("Inserisci il titolo della visita: ");
        String descrizione = InputManager.leggiStringaNonVuota("Inserisci la descrizione della visita: ");

        // luogo di incontro (può coincidere con la posizione del luogo)
        boolean diverso = "sì".equals(InputManager.chiediSiNo(
                "Vuoi definire un luogo di incontro differente rispetto alla posizione del luogo?"));
        Posizione luogoIncontro = diverso ? InputManager.chiediPosizione() : posizioneLuogo;

        // giorni della settimana
        Giornate giornate = new Giornate();
        ArrayList<GiornoSettimana> disponibili = new ArrayList<>(
                Arrays.asList(GiornoSettimana.values())
        );
        do {
            if (disponibili.isEmpty()) {
                System.out.println("Non ci sono altri giorni disponibili.");
                break;
            }
            System.out.println("Seleziona il giorno della settimana:");
            for (int i = 0; i < disponibili.size(); i++) {
                System.out.println((i + 1) + ") " + disponibili.get(i));
            }
            int scelta = InputManager.leggiInteroConMinMax(
                    "Seleziona (1-" + disponibili.size() + "): ",
                    1,
                    disponibili.size()
            );
            GiornoSettimana g = disponibili.get(scelta - 1);
            giornate.aggiungiGiornoDellaSettimana(g);
            System.out.println("Aggiunto: " + g);
            disponibili.remove(g);
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un'altra giornata per la visita?")));


        // validità (inizio <= fine)
        Data inizio, fine;
        do {
            System.out.println("Inserire la data di inizio validità della visita: ");
            inizio = InputManager.chiediData();
            System.out.println("Inserire la data di fine validità della visita: ");
            fine = InputManager.chiediData();
            if (!inizio.precede(fine)) System.out.println("Errore: la data di fine deve essere successiva alla data di inizio.");
        } while (!inizio.precede(fine));

        // orario inizio
        System.out.println("Inserire l'orario di inizio della visita: ");
        int hh = InputManager.leggiInteroConMinMax("Ora: ", 0, 23);
        int mm = InputManager.leggiInteroConMinMax("Minuti: ", 0, 59);
        Orario oraInizio = new Orario(hh, mm);

        // durata
        int durataMin = InputManager.leggiInteroConMin("Inserire la durata in minuti della visita: ", 1);

        // biglietto
        boolean biglietto = "sì".equals(InputManager.chiediSiNo("È presente un biglietto di ingresso da pagare?"));

        // volontari: usa la lista utenti dell'istanza
        ArrayList<Volontario> volontariThisVisita = scegliVolontari(applicazione);

        // capienze
        int numMinP = InputManager.leggiInteroConMin("Numero minimo partecipanti: ", 1);
        int numMaxP = InputManager.leggiInteroConMin("Numero massimo partecipanti: ", numMinP);

        return new Visita(
                titolo, descrizione, luogoId, luogoIncontro, giornate,
                inizio, fine, oraInizio, durataMin, biglietto, volontariThisVisita, numMinP, numMaxP
        );
    }

    public static ArrayList<Volontario> associaVolontariAvisita(Applicazione applicazione, Visita visita) {
        String scelta = InputManager.chiediSiNo("Vuoi associare volontari già registrati nell'applicativo?");
        ArrayList<Volontario> volontariThisVisita = new ArrayList<>();
        if (scelta.equals("sì")){
            volontariThisVisita = scegliVolontari(applicazione, visita);
        } else {
            do {
                Volontario nuovo = new Volontario(InputManager.richiediNuovoUsername(applicazione.getListaUtenti()), InputManager.richiediPasswordLogin());
                applicazione.getListaUtenti().aggiungiUtente(nuovo); // volontari aggiunti anche alla lista degli utenti generale
                volontariThisVisita.add(nuovo);
            } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un altro volontario?")));
        }
        return volontariThisVisita;
    }

    public static ArrayList<Volontario> scegliVolontari(Applicazione applicazione, Visita visita) {
        ArrayList<Volontario> scelti = new ArrayList<>();

        ArrayList<Volontario> tuttiVolontari = applicazione.getListaUtenti().getVolontari();

        ArrayList<Volontario> volontariDisponibili = new ArrayList<>(tuttiVolontari);

        ArrayList<Volontario> volontariPresenti = visita.getVolontariVisita();
        volontariDisponibili.removeIf(v ->
                volontariPresenti.stream()
                        .anyMatch(p -> p.getUsername().equalsIgnoreCase(v.getUsername()))
        );

        if (volontariDisponibili.isEmpty()) {
            System.out.println("Nessun volontario disponibile.");
            return scelti;
        }

        boolean continua;
        do {
            if (volontariDisponibili.isEmpty()) {
                System.out.println("Non ci sono altri volontari disponibili.");
                break;
            }

            System.out.println("Seleziona il volontario:");
            for (int i = 0; i < volontariDisponibili.size(); i++) {
                System.out.println((i + 1) + ") " + volontariDisponibili.get(i).getUsername());
            }

            int scelta = InputManager.leggiInteroConMinMax(
                    "Scelta (1-" + volontariDisponibili.size() + "): ",
                    1,
                    volontariDisponibili.size()
            );

            Volontario sel = volontariDisponibili.get(scelta - 1);

            scelti.add(sel);
            System.out.println("Aggiunto: " + sel.getUsername());

            volontariDisponibili.remove(sel);

            if (volontariDisponibili.isEmpty()) {
                System.out.println("Non ci sono altri volontari disponibili.");
                break;
            }

            continua = "sì".equals(InputManager.chiediSiNo("Vuoi scegliere un altro volontario?"));
        } while (continua);

        return scelti;
    }

    public static ArrayList<Volontario> scegliVolontari(Applicazione applicazione) {
        ArrayList<Volontario> scelti = new ArrayList<>();

        ArrayList<Volontario> volontariDisponibili = applicazione.getListaUtenti().getVolontari();

        if (volontariDisponibili.isEmpty()) {
            System.out.println("Nessun volontario disponibile.");
            return scelti;
        }

        boolean continua;
        do {
            if (volontariDisponibili.isEmpty()) {
                System.out.println("Non ci sono altri volontari disponibili.");
                break;
            }

            System.out.println("Seleziona il volontario:");
            for (int i = 0; i < volontariDisponibili.size(); i++) {
                System.out.println((i + 1) + ") " + volontariDisponibili.get(i).getUsername());
            }

            int scelta = InputManager.leggiInteroConMinMax(
                    "Scelta (1-" + volontariDisponibili.size() + "): ",
                    1,
                    volontariDisponibili.size()
            );

            Volontario sel = volontariDisponibili.get(scelta - 1);

            scelti.add(sel);
            System.out.println("Aggiunto: " + sel.getUsername());

            volontariDisponibili.remove(sel);

            if (volontariDisponibili.isEmpty()) {
                System.out.println("Non ci sono altri volontari disponibili.");
                break;
            }

            continua = "sì".equals(InputManager.chiediSiNo("Vuoi scegliere un altro volontario?"));
        } while (continua);

        return scelti;
    }

}
