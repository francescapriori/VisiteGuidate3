package it.unibs.ingdsw.view.cli.menu;

import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.view.cli.io.Output;

import java.util.LinkedHashMap;
import java.util.Map;

public class Menu {
    private final String titolo;
    private final Map<Integer, Voce> voci = new LinkedHashMap<>();
    private final Output out;

    public Menu(String titolo, Output out) {
        this.titolo = titolo;
        this.out = out;
    }

    public void aggiungi(int numero, String etichetta, Runnable azione) {
        voci.put(numero, new Voce(etichetta, azione));
    }

    public boolean mostra() {
        out.println("\n--- " + titolo + " ---");
        for (var e : voci.entrySet()) {
            out.println(e.getKey() + ". " + e.getValue().etichetta);
        }
        out.println("0. Esci");

        int max = voci.keySet().stream().mapToInt(Integer::intValue).max().orElse(0);
        int scelta = InputManager.leggiInteroConMinMax("Scelta: ", 0, max);

        if (scelta == 0) {
            return false;
        }

        Voce voce = voci.get(scelta);
        if (voce == null) {
            out.println("Scelta non valida.");
            return true;
        }

        try {
            voce.azione.run();
        } catch (Exception ex) {
            out.println("Errore: " + ex.getMessage());
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
