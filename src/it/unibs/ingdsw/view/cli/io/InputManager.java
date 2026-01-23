package it.unibs.ingdsw.view.cli.io;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.luoghi.Posizione;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.Giornate;
import it.unibs.ingdsw.model.tempo.GiornoSettimana;
import it.unibs.ingdsw.model.tempo.Orario;
import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;

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
            System.out.println("Errore: inserire un intero valido"
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
            System.out.println("Errore: inserire un intero valido" + (min != null ? " >= " + min : ""));
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
            System.out.println(String.format(Locale.ITALY, "Errore: inserire un valore valido tra %.2f e %.2f.", min, max));
        }
    }

    public static String chiediSiNo(String messaggio) {
        while (true) {
            System.out.print(messaggio + " (sì/no): ");
            String in = scanner.nextLine().trim().toLowerCase();

            if (in.equals("sì") || in.equals("si") || in.equals("s")) {
                return "sì";
            }

            if (in.equals("no") || in.equals("n")) {
                return "no";
            }

            System.out.println("Risposta non valida. Scrivi \"sì\" o \"no\".");
        }
    }

    public static String richiediUsernameLogin(){
        return leggiStringaNonVuota("Inserisci l'username: ");
    }

    public static String richiediNuovoUsername(ListaUtenti listaUtenti){
        String username;
        do {
            username = leggiStringaNonVuota("Inserisci l'username: ");
            if(listaUtenti.usernameInUso(username)) {
                System.out.println("Non è possibile utilizzare questo username poiché già in uso.");
            }
        } while(listaUtenti.usernameInUso(username));
        return username;
    }

    //UtentiController
    public static String richiediPasswordLogin(){
        return leggiStringaNonVuota("Inserisci l'password: ");
    }

    //DataController
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
        if (mese == 1 || mese == 3 || mese == 5 || mese == 7 || mese == 8 || mese == 10 || mese == 12) {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 31);
        }
        else if (Data.isBisestile(anno) && mese == 2) {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 29);
        }
        else if (mese == 2) {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 28);
        }
        else {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 30);
        }
        return giorno;
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


    //VisitaController
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

    //UtentiController
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

    //UtentiController
    public static ArrayList<Volontario> scegliVolontari(Applicazione applicazione) {
        ArrayList<Volontario> scelti = new ArrayList<>();
        ArrayList<Volontario> volontariDisponibili =
                new ArrayList<>(applicazione.getListaUtenti().getVolontari());
        ListaUtenti sceltiUtenti = new ListaUtenti();
        boolean continua;
        do {
            String tipo = InputManager.chiediSiNo("Vuoi associare un volontario già registrato nell'applicativo?");
            boolean usaRegistrato = "sì".equalsIgnoreCase(tipo.trim());
            if (usaRegistrato && volontariDisponibili.isEmpty()) {
                System.out.println("Non ci sono volontari registrati disponibili. Inserimento di un nuovo volontario.");
                usaRegistrato = false;
            }
            if (usaRegistrato) {
                Volontario sel;
                do {
                    System.out.println("Seleziona il volontario:");
                    for (int i = 0; i < volontariDisponibili.size(); i++) {
                        System.out.println((i + 1) + ") " + volontariDisponibili.get(i).getUsername());
                    }
                    int scelta = InputManager.leggiInteroConMinMax("Scelta (1-" + volontariDisponibili.size() + "): ",1, volontariDisponibili.size());
                    sel = volontariDisponibili.get(scelta - 1);
                    if (sceltiUtenti.usernameInUso(sel.getUsername())) {
                        System.out.println("Volontario già selezionato, scegline un altro.");
                    }
                } while (sceltiUtenti.usernameInUso(sel.getUsername()));
                scelti.add(sel);
                sceltiUtenti.aggiungiUtente(sel);
                volontariDisponibili.remove(sel);
                System.out.println("Aggiunto: " + sel.getUsername());
            } else {
                Volontario nuovo;
                do {
                    nuovo = new Volontario(InputManager.richiediUsernameLogin(), InputManager.richiediPasswordLogin());
                    if (applicazione.getListaUtenti().usernameInUso(nuovo.getUsername())) {
                        System.out.println("Username già presente, riprova.");
                    }
                } while (applicazione.getListaUtenti().usernameInUso(nuovo.getUsername()));
                scelti.add(nuovo);
                sceltiUtenti.aggiungiUtente(nuovo);
                System.out.println("Aggiunto nuovo volontario: " + nuovo.getUsername());
            }
            continua = "sì".equalsIgnoreCase(InputManager.chiediSiNo("Vuoi aggiungere un altro volontario?").trim());

        } while (continua);

        return scelti;
    }

}
