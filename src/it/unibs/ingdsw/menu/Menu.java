package it.unibs.ingdsw.menu;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

public class Menu {
    private final String titolo;
    private final Map<Integer, Voce> voci = new LinkedHashMap<>();

    public Menu(String titolo) {
        this.titolo = titolo;
    }

    public void aggiungi(int numero, String etichetta, Runnable azione) {
        voci.put(numero, new Voce(etichetta, azione));
    }

    public boolean mostra() {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n--- " + titolo + " ---");
        for (var e : voci.entrySet()) {
            System.out.println(e.getKey() + ". " + e.getValue().etichetta);
        }
        System.out.println("0. Esci");
        System.out.print("Scelta: ");

        while (!sc.hasNextInt()) {
            System.out.println("Inserisci un numero.");
            sc.next();
        }
        int scelta = sc.nextInt();
        sc.nextLine();

        if (scelta == 0) {
            System.out.println("Uscita dal menu.");
            return false;
        }

        Voce voce = voci.get(scelta);
        if (voce != null) {
            try {
                voce.azione.run();
            } catch (Exception ex) {
                System.out.println("Errore: " + ex.getMessage());
            }
        } else {
            System.out.println("Scelta non valida.");
        }

        return true;
    }
    private static class Voce {
        final String etichetta;
        final Runnable azione;
        Voce(String etichetta, Runnable azione) { this.etichetta = etichetta; this.azione = azione; }
    }
}