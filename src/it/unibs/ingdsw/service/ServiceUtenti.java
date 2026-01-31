package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ServiceUtenti {

    private final Applicazione applicazione;

    public ServiceUtenti(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public HashMap<Volontario, ListaVisite> getVolontariConVisiteAssociate() {
        ServiceVisite sv = new ServiceVisite(applicazione.getListaLuoghi().getTotaleVisite());
        HashMap<Volontario, ListaVisite> volontarioPerVisita = new HashMap<Volontario, ListaVisite>();

        for (Volontario vol : this.applicazione.getListaUtenti().getVolontari()) {
            ListaVisite lv = sv.visiteDelVolontario(vol);
            volontarioPerVisita.put(vol, lv);
        }

        return volontarioPerVisita;
    }

    public void eliminaVolontari(int posizione) {
        ServiceUtenti sv = new ServiceUtenti(this.applicazione);
        sv.rimuoviVolontarioIesimo(posizione);

    }

    public void aggiungiVolontariAllaVisita(Visita visita, List<Volontario> volontari) {
        for(Volontario v: volontari) {
            visita.getVolontariVisita().add(v); //non è necessario fare controllo se già presente poiché fatto già prima
        }
    }

    public void eliminaSeSenzaVisita() {
        List<Utente> volDaRimuovere = new ArrayList<>();
        for(Utente u : this.applicazione.getListaUtenti().getListaUtenti()) {
            if (u instanceof Volontario) {
                if(!this.applicazione.getListaLuoghi().volConAlmenoUnaVisita((Volontario) u)) {
                    volDaRimuovere.add(u);
                }
            }
        }

        this.applicazione.getListaUtenti().getListaUtenti().removeAll(volDaRimuovere);
    }

    public void rimuoviVolontarioIesimo(int posizione) {
        List<Utente> utenti = this.applicazione.getListaUtenti().getListaUtenti();
        List<Volontario> vol = this.applicazione.getListaUtenti().getVolontari();
        Volontario vDaRimuovere = vol.get(posizione);
        ServiceVisite sv = new ServiceVisite(this.applicazione.getListaLuoghi().getTotaleVisite());

        Iterator<Utente> it = utenti.iterator();
        while (it.hasNext()) {
            Utente u = it.next();
            if (u.getUsername().equalsIgnoreCase(vDaRimuovere.getUsername())) {
                it.remove();
                sv.rimuoviVolontarioDaVisite(vDaRimuovere);
                break;
            }
        }
        this.applicazione.setListaUtenti(new ListaUtenti(utenti));
    }

    public ListaUtenti getListaUtenti() {
        return this.applicazione.getListaUtenti();
    }
}
