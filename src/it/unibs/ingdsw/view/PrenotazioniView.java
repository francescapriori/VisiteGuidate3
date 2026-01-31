package it.unibs.ingdsw.view;

import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.prenotazione.Prenotazione;
import it.unibs.ingdsw.view.cli.io.Output;
import it.unibs.ingdsw.view.format.Formatters;

import java.util.List;

public class PrenotazioniView {
    private final Output out;

    public PrenotazioniView(Output out) {
        this.out = out;
    }

    public void visualizzaPrenotazioni(List<Prenotazione> prenotazioni, StatoAppuntamento[] statiDaEstrarre, String nomeMeseV, int annoTargetV) {
        out.println("I tuoi appuntamenti prenotati per il mese di " + nomeMeseV + " " + annoTargetV + "sono: ");

        for (StatoAppuntamento sv : statiDaEstrarre) {
            out.println("Stato: " + sv);
            boolean trovatoInQuestoStato = false;
            for (Prenotazione p : prenotazioni) {
                if (p.getAppuntamento().getStatoVisita() == sv) {
                    trovatoInQuestoStato = true;
                    out.println(" - " + Formatters.prenotazione(p) + " - PER " + p.getAppuntamento().getVisita().getTitolo() + " - " + Formatters.data(p.getAppuntamento().getData()));
                    List<Prenotazione> prenotazioniAss = p.getAppuntamento().getPrenotazioniAssociate(prenotazioni);
                    if (prenotazioniAss.isEmpty()) {
                        out.println("  Nessuna prenotazione effettuata.");
                    }
                }
            }
            if (!trovatoInQuestoStato) {
                out.println("  Non hai nessun appuntamento prenotato in questo stato.");
            }
            out.println("");
        }
    }
}
