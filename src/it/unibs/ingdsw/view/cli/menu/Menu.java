package it.unibs.ingdsw.view.cli.menu;

import it.unibs.ingdsw.view.cli.io.InputManager;

import java.util.LinkedHashMap;
import java.util.Map;

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
        System.out.println("\n--- " + titolo + " ---");
        for (var e : voci.entrySet()) {
            System.out.println(e.getKey() + ". " + e.getValue().etichetta);
        }
        System.out.println("0. Esci");

        int max = voci.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        int scelta = InputManager.leggiInteroConMinMax("Scelta: ", 0, max);

        if (scelta == 0) {
            return false;
        }

        Voce voce = voci.get(scelta);
        if (voce == null) {
            System.out.println("Scelta non valida.");
            return true;
        }

        try {
            voce.azione.run();
        } catch (Exception ex) {
            System.out.println("Errore: " + ex.getMessage());
        }

        return true;
    }

    private static class Voce {
        final String etichetta;
        final Runnable azione;
        Voce(String etichetta, Runnable azione) {
            this.etichetta = etichetta;
            this.azione = azione;
        }
    }
}
