package it.unibs.ingdsw.view;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.view.cli.io.Output;
import it.unibs.ingdsw.view.format.Formatters;

import java.util.HashMap;

public class UtentiView {
    private final Output out;

    public UtentiView(Output out) {
        this.out = out;
    }

    public void visualizzaVolontariConVisiteAssociate(HashMap<Volontario, ListaVisite> map) {
        if (map.isEmpty()) {
            out.println("Nessun volontario registrato.");
        }
        for (HashMap.Entry<Volontario, ListaVisite> entry : map.entrySet()) {
            out.println("Volontario: " + entry.getKey().getUsername());
            ListaVisite lv = entry.getValue();
            if (lv.getListaVisite().isEmpty()) out.println("  (nessuna visita)");
            else lv.getListaVisite().forEach(v ->
                    out.println("  - " + v.getTitolo() + " - " + v.getLuogoID()));
            out.println("");
        }
    }

    public void visualizzaSoloVolontari(Applicazione applicazione) {
        int i = 1;
        for (Volontario v : applicazione.getListaUtenti().getVolontari()) {
            out.println(i + ") " + Formatters.utente(v));
            i++;
        }
    }
}
