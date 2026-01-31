package it.unibs.ingdsw.view.cli.io;

import it.unibs.ingdsw.model.utenti.ListaUtenti;

import java.util.Locale;
import java.util.Scanner;

public class InputManager {

    private static final Scanner scanner = new Scanner(System.in).useLocale(Locale.US);

    public static String leggiStringaNonVuota(String messaggio) {
        while (true) {
            OutputManager.visualizzaMessaggio(messaggio);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) return input;
            OutputManager.visualizzaMessaggio("Errore: il campo non può essere vuoto.");
        }
    }
    public static int leggiInteroConMinMax(String messaggio, Integer min, Integer max) {
        while (true) {
            OutputManager.visualizzaMessaggio(messaggio);
            if (scanner.hasNextInt()) {
                int numero = scanner.nextInt();
                scanner.nextLine();
                if ((min == null || numero >= min) && (max == null || numero <= max)) return numero;
            } else scanner.nextLine();
            OutputManager.visualizzaMessaggio("Errore: inserire un intero valido"
                    + (min != null ? " >= " + min : "")
                    + (max != null ? " e <= " + max : "") + ".");
        }
    }

    public static int leggiInteroConMin(String messaggio, Integer min) {
        while (true) {
            OutputManager.visualizzaMessaggio(messaggio);
            if (scanner.hasNextInt()) {
                int numero = scanner.nextInt();
                scanner.nextLine();
                if ((min == null || numero >= min)) return numero;
            } else scanner.nextLine();
            OutputManager.visualizzaMessaggio("Errore: inserire un intero valido" + (min != null ? " >= " + min : ""));
        }
    }

    public static double leggiDouble(String messaggio, double min, double max) {
        while (true) {
            OutputManager.visualizzaMessaggio(messaggio);
            if (scanner.hasNextDouble()) {
                double val = scanner.nextDouble();
                scanner.nextLine();
                if (val >= min && val <= max) return val;
            } else scanner.next();
            OutputManager.visualizzaMessaggio(String.format(Locale.ITALY, "Errore: inserire un valore valido tra %.2f e %.2f.", min, max));
        }
    }

    public static String chiediSiNo(String messaggio) {
        while (true) {
            OutputManager.visualizzaMessaggio(messaggio + " (sì/no): ");
            String in = scanner.nextLine().trim().toLowerCase();

            if (in.equals("sì") || in.equals("si") || in.equals("s")) {
                return "sì";
            }

            if (in.equals("no") || in.equals("n")) {
                return "no";
            }
            OutputManager.visualizzaMessaggio("Risposta non valida. Scrivi \"sì\" o \"no\".");
        }
    }

    // spostare
    public static String richiediUsernameLogin(){
        return leggiStringaNonVuota("Inserisci l'username: ");
    }

    public static String richiediNuovoUsername(ListaUtenti listaUtenti){
        String username;
        do {
            username = leggiStringaNonVuota("Inserisci l'username: ");
            if(listaUtenti.usernameInUso(username)) {
                OutputManager.visualizzaMessaggio("Non è possibile utilizzare questo username poiché già in uso.");
            }
        } while(listaUtenti.usernameInUso(username));
        return username;
    }

    public static String richiediPasswordLogin(){
        return leggiStringaNonVuota("Inserisci l'password: ");
    }
}
