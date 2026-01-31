package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.Target;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ServiceVisite {

    private final ListaVisite listaVisite;

    public ServiceVisite(ListaVisite listaVisite) {
        this.listaVisite = listaVisite;
    }

    // controller
    public ListaVisite visiteDelVolontario(Volontario volontario) {
        ListaVisite lista = new ListaVisite();
        String user = volontario.getUsername();

        for (Luogo l : Applicazione.getApplicazione().getListaLuoghi().getListaLuoghi()) {
            for (Visita v : l.getInsiemeVisite().getListaVisite()) {
                boolean assegnato = v.getVolontariVisita().stream()
                        .anyMatch(vol -> vol != null
                                && vol.getUsername() != null
                                && vol.getUsername().equalsIgnoreCase(user));
                if (assegnato) {
                    lista.aggiungiVisita(v);
                }
            }
        }
        return lista;
    }

    public Visita scegliVisita(Luogo l, int scelta) {
       return l.getVisitaIesima(scelta-1);
    }

    public boolean rimuoviVisita(Visita visitaDaRimuovere, Luogo luogo) {
        String titoloTarget = visitaDaRimuovere.getTitolo();
        String luogoIdTarget = visitaDaRimuovere.getLuogoID();
        Iterator<Visita> it = luogo.getInsiemeVisite().getListaVisite().iterator();

        while (it.hasNext()) {
            Visita corrente = it.next();
            if (corrente == null) {
                continue;
            }

            if (titoloTarget.equals(corrente.getTitolo())
                    && luogoIdTarget.equalsIgnoreCase(corrente.getLuogoID())) {

                it.remove();
                return true;
            }
        }
        return false;
    }

    public void eliminaSeSenzaVolontari() {
        List<Visita> visDaRimuovere = new ArrayList<>();

        for (Luogo l : Applicazione.getApplicazione().getListaLuoghi().getListaLuoghi()) {
            for (Visita v : l.getInsiemeVisite().getListaVisite()) {
                if(v.getVolontariVisita().isEmpty()) {
                    visDaRimuovere.add(v);
                }
            }
            l.getInsiemeVisite().getListaVisite().removeAll(visDaRimuovere);
        }
    }

    public InsiemeDate dateVisitaNelMeseTarget(Visita visita, int mese, int anno) {
        YearMonth ym = YearMonth.of(anno, mese);
        int safeDay = Math.min(Target.SOGLIA_CAMBIO_MESE, ym.lengthOfMonth());

        LocalDate from = LocalDate.of(anno, mese, safeDay);
        LocalDate to = from.plusMonths(1).minusDays(1);

        return visita.dateDisponibiliTra(from, to);
    }

    public HashMap<Visita, InsiemeDate> calendarioProvvisiorioVisiteDelMese(int meseRiferimento, int annoRiferimento){
        HashMap<Visita, InsiemeDate> calendarioDelMese = new HashMap<>();
        for(Visita v : Applicazione.getApplicazione().getListaLuoghi().getTotaleVisite().getListaVisite()) {
            calendarioDelMese.put(v, dateVisitaNelMeseTarget(v, meseRiferimento, annoRiferimento));
        }
        return calendarioDelMese;
    }

    public void rimuoviVolontarioDaVisite(Volontario vDaRimuovere) {
        for (Luogo luogo : Applicazione.getApplicazione().getListaLuoghi().getListaLuoghi()) {
            for (Visita v : luogo.getInsiemeVisite().getListaVisite()) {
                if (v.getVolontariVisita().contains(vDaRimuovere)) {
                    v.getVolontariVisita().remove(vDaRimuovere);
                }
            }
        }
    }
}
