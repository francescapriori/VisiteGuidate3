package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.applicazione.Target;
import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.view.format.Formatters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceAppuntamenti {

    private final InsiemeAppuntamenti insiemeAppuntamenti;

    public ServiceAppuntamenti (InsiemeAppuntamenti insiemeAppuntamenti) {
        this.insiemeAppuntamenti = insiemeAppuntamenti;
    }

    public void aggiornaStati(LocalDate oggi) {
        for (Appuntamento a : this.insiemeAppuntamenti.getAppuntamenti()) {
            a.aggiornaStato(oggi);
        }
    }

    public InsiemeAppuntamenti getAppuntamentiDelMeseTarget(int mese, int anno) {
        InsiemeAppuntamenti result = new InsiemeAppuntamenti();

        LocalDate start = LocalDate.of(anno, mese, Target.SOGLIA_CAMBIO_MESE);

        int meseSuccessivo = (mese == 12) ? 1 : mese + 1;
        int annoSuccessivo = (mese == 12) ? anno + 1 : anno;

        LocalDate endExclusive = LocalDate.of(annoSuccessivo, meseSuccessivo, Target.SOGLIA_CAMBIO_MESE);

        for (Appuntamento a : this.insiemeAppuntamenti.getAppuntamenti()) {
            LocalDate d = a.getData().toLocalDate();
            if (!d.isBefore(start) && d.isBefore(endExclusive)) {
                result.getAppuntamenti().add(a);
            }
        }
        return result;
    }

    public List<Appuntamento> getAppuntamentiPrenotabili() {
        ArrayList<Appuntamento> appuntamentiPrenotabili = new ArrayList<>();
        for (Appuntamento a : insiemeAppuntamenti.getAppuntamenti()) {
            if (a.getStatoVisita() == StatoAppuntamento.PROPOSTA) {
                appuntamentiPrenotabili.add(a);
            }
        }
        return appuntamentiPrenotabili;
    }
}
