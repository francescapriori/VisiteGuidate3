package it.unibs.ingdsw.applicazione;

import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.tempo.*;
import it.unibs.ingdsw.utenti.ListaUtenti;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.utenti.Utente;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.Appuntamento;
import it.unibs.ingdsw.visite.CalendarioAppuntamenti;
import it.unibs.ingdsw.visite.Visita;
import it.unibs.ingdsw.parsing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Applicazione {

    private String ambitoTerritoriale;
    private int numeroMassimoIscrivibili;
    private ListaUtenti listaUtenti;
    private ListaLuoghi listaLuoghi;
    private boolean daConfigurare = true;
    private InsiemeDate dateEscluse;
    private HashMap<Volontario, InsiemeDate> disponibilitaPerVol;
    private CalendarioAppuntamenti calendarioAppuntamenti;
    private StatoRichiestaDisponibilita statoDisp;
    private StatoProduzioneVisite statoProduzione;

    public Applicazione() {}

    public void setAmbitoTerritoriale(String ambitoTerritoriale) {
        this.ambitoTerritoriale = ambitoTerritoriale;
    }

    public void setNumeroMassimoIscrivibili(int numeroMassimoIscrivibili) {
        this.numeroMassimoIscrivibili = numeroMassimoIscrivibili;
    }

    public void setListaLuoghi(ListaLuoghi listaLuoghi) {
        this.listaLuoghi = listaLuoghi;
    }

    public void setListaUtenti(ListaUtenti listaUtenti) {
        this.listaUtenti = listaUtenti;
    }

    public void setDaConfigurare(boolean daConfigurare) {
        this.daConfigurare = daConfigurare;
    }

    public void setDateEscluse(InsiemeDate dateEscluse) {
        this.dateEscluse = dateEscluse;
    }

    public String getAmbitoTerritoriale() {
        return ambitoTerritoriale;
    }

    public int getNumeroMassimoIscrivibili() {
        return numeroMassimoIscrivibili;
    }

    public ListaLuoghi getListaLuoghi() {
        return listaLuoghi;
    }

    public ListaUtenti getListaUtenti() {
        return listaUtenti;
    }

    public InsiemeDate getInsiemeDate() {
        return dateEscluse;
    }

    public void setStatoProduzione(StatoProduzioneVisite statoProd) {
        this.statoProduzione = statoProd;
    }
    public StatoProduzioneVisite getStatoProd() {
        return this.statoProduzione;
    }
    public CalendarioAppuntamenti getCalendarioAppuntamenti() {
        return calendarioAppuntamenti;
    }
    public void setCalendarioAppuntamenti(CalendarioAppuntamenti calendario){
        this.calendarioAppuntamenti=calendario;
    }

    public void setInsiemeDate(InsiemeDate dateEscluse) {
        this.dateEscluse = dateEscluse;
    }

    public boolean isDaConfigurare() {
        return daConfigurare;
    }

    public HashMap<Volontario, InsiemeDate> getDisponibilitaPerVol() {
        return disponibilitaPerVol;
    }

    public void setDisponibilitaPerVol(HashMap<Volontario, InsiemeDate> disponibilitaPerVol) {
        this.disponibilitaPerVol = disponibilitaPerVol;
    }

    public void setStatoDisp(StatoRichiestaDisponibilita statoDisp) {
        this.statoDisp = statoDisp;
    }
    public StatoRichiestaDisponibilita getStatoDisp() {
        return statoDisp;
    }

    public static Applicazione configuraApplicazione () {
        Applicazione app = new Applicazione();
        ParsParametriAppXMLFile pa = new ParsParametriAppXMLFile();
        ParsUtentiXMLFile ut = new ParsUtentiXMLFile();
        ParsDateEscluseXMLFile d = new ParsDateEscluseXMLFile();
        ParsLuoghiXMLFile l = new ParsLuoghiXMLFile();
        ParsDisponibilitaVolontariXMLFile dV = new ParsDisponibilitaVolontariXMLFile(ut.getListaUtenti());
        ParsAppuntamentiXMLFile a = new ParsAppuntamentiXMLFile(l.getListaLuoghi(), ut.getListaUtenti());

        app.setAmbitoTerritoriale(pa.getAmbitoTerritoriale());
        app.setNumeroMassimoIscrivibili(pa.getNumeroMassimoIscrivibili());
        app.setDaConfigurare(pa.isAmbienteDaConfigurare());
        app.setListaUtenti(ut.getListaUtenti());
        app.setListaLuoghi(l.getListaLuoghi());
        app.setDateEscluse(d.getInsiemeDate());
        app.setDisponibilitaPerVol(dV.getDisponibilitaPerVol());
        app.setCalendarioAppuntamenti(a.getAppuntamenti());
        // se si accede con il nuovo mese (Target.SOGLIA_CAMBIO_MESE), il piano delle visite del mese prima è stato già
        // prodotto, per cui viene settato di nuovo a NON_PRODOTTE, altrimenti vado a leggere quello che c'è sull'XML
        if((new Target()).successivoASoglia() && pa.getStatoProduzione() == StatoProduzioneVisite.PRODOTTE) {
            app.setStatoProduzione(StatoProduzioneVisite.NON_PRODOTTE);
        } else {
            app.setStatoProduzione(pa.getStatoProduzione());
        }
        app.setStatoDisp(pa.getStato());
        return app;
    }

    public void salvaApplicazione() {
        ParsParametriAppXMLFile.salvaParametri(
                this.ambitoTerritoriale,
                this.numeroMassimoIscrivibili,
                this.statoDisp,
                this.statoProduzione
        );
        ParsUtentiXMLFile.salvaListaUtenti(this.listaUtenti);
        ParsDateEscluseXMLFile.salvaListaDate(this.dateEscluse);
        ParsLuoghiXMLFile.salvaLuoghi(this.listaLuoghi);
        ParsDisponibilitaVolontariXMLFile.salvaDisponibilitaVolontari(this.disponibilitaPerVol);
        ParsAppuntamentiXMLFile.salvaAppuntamenti(this.calendarioAppuntamenti.getCalendarioVisite());
    }

    public InsiemeDate getDateEsclusePerMeseAnno (int mese, int anno) {
        return this.getInsiemeDate().getDateEsclusePerMeseAnno(mese, anno);
    }

    public boolean aggiungiData(Data data) {
        return this.dateEscluse.aggiungiData(data);
    }

    @Override
    public String toString() {
        return "Ambito territoriale di competenza: " + this.ambitoTerritoriale +
                "\nNumero massimo di persone iscrivibili per ogni prenotazione: " + this.numeroMassimoIscrivibili +
                "\nLuoghi Visitabili: " + this.listaLuoghi.estraiNomeLuoghi().toString();
    }

    public void aggiungiVolontariAllaVisita(Visita visita, ArrayList<Volontario> volontari) {
        for(Volontario v: volontari) {
            visita.getVolontariVisita().add(v); //non è necessario fare controllo se già presente poichè fatto già prima
        }
    }

    public int getNumeroVolontari() {
        return this.listaUtenti.getVolontari().size();
    }

    public void rimuoviVolontarioIesimo(int posizione) {
        ArrayList<Utente> utenti = this.listaUtenti.getListaUtenti();
        ArrayList<Volontario> vol = this.listaUtenti.getVolontari();
        Volontario vDaRimuovere = vol.get(posizione);

        Iterator<Utente> it = utenti.iterator();
        while (it.hasNext()) {
            Utente u = it.next();
            if (u.getUsername().equalsIgnoreCase(vDaRimuovere.getUsername())) {
                it.remove();
                this.listaLuoghi.rimuoviVolontarioDaVisite(vDaRimuovere);
                break;
            }
        }

        setListaUtenti(new ListaUtenti(utenti));
    }


    public CalendarioAppuntamenti produciVisitePerIlMese(int meseTargetV, int annoTargetV) {

        HashMap<Visita, InsiemeDate> calendarioProvvisorio =
                this.listaLuoghi.getTotaleVisite().calendarioProvvisiorioVisiteDelMese(meseTargetV, annoTargetV);

        CalendarioAppuntamenti calendarioAppuntamenti = new CalendarioAppuntamenti();
        HashMap<Volontario, InsiemeDate> volontariConDate = this.disponibilitaPerVol;

        for (Map.Entry<Visita, InsiemeDate> entry : calendarioProvvisorio.entrySet()) {
            Visita visita = entry.getKey();
            InsiemeDate dateCalendarioProvvisorio = entry.getValue();

            for (Data d1 : dateCalendarioProvvisorio.getInsiemeDate()) {

                boolean assegnato = false;  //voglio AL MASSIMO un volontario per (visita, d1)

                for (Map.Entry<Volontario, InsiemeDate> entry2 : volontariConDate.entrySet()) {
                    Volontario volontario = entry2.getKey();
                    InsiemeDate dateDisponibilitaVolontario = entry2.getValue();

                    if (dateDisponibilitaVolontario.dataPresente(d1) &&
                            !calendarioAppuntamenti.volontarioGiaPresenteInData(d1, volontario)) {

                        calendarioAppuntamenti.getCalendarioVisite()
                                .add(new Appuntamento(visita, d1, volontario));

                        assegnato = true;
                        break; //esco dal ciclo dei volontari: per questa (visita, d1) ho già una guida
                    }
                }

            }
        }

        return calendarioAppuntamenti;
    }


    public Luogo scegliLuogo(int scelta) {
        return getListaLuoghi().scegliLuogo(scelta - 1);
    }

    public void rimuoviLuogo(Luogo luogo) {
        String nomeTarget = luogo.getNome();
        ArrayList<Luogo> lista = getListaLuoghi().getListaLuoghi();

        for (Iterator<Luogo> it = lista.iterator(); it.hasNext(); ) {
            Luogo corrente = it.next();
            if (corrente != null && nomeTarget.equals(corrente.getNome())) {
                it.remove();
            }
        }
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

        return false; //nessuna visita trovata e rimossa
    }

    public void eliminaSeSenzaVisita() {
        ArrayList<Utente> volDaRimuovere = new ArrayList<>();
        for(Utente u : this.listaUtenti.getListaUtenti()) {
            if (u instanceof Volontario) {
                // ha almeno una visita?
                if(!this.listaLuoghi.volConAlmenoUnaVisita((Volontario) u)) {
                    volDaRimuovere.add(u);
                }
            }
        }

        this.listaUtenti.getListaUtenti().removeAll(volDaRimuovere);


    }

    public void eliminaSeSenzaVolontari() {
        ArrayList<Visita> visDaRimuovere = new ArrayList<>();

        for (Luogo l : this.listaLuoghi.getListaLuoghi()) {
            for (Visita v : l.getInsiemeVisite().getListaVisite()) {
                if(v.getVolontariVisita().isEmpty()) {
                    visDaRimuovere.add(v);
                }
            }
            l.getInsiemeVisite().getListaVisite().removeAll(visDaRimuovere);
        }

    }

    public void rimuoviLuogoSeSenzaVisite() {
        ArrayList<Luogo> luoghiDaRimuovere = new ArrayList<>();
        for (Luogo l : this.listaLuoghi.getListaLuoghi()) {
            if (l.getInsiemeVisite().getListaVisite().isEmpty()) {
                luoghiDaRimuovere.add(l);
            }
        }
        this.listaLuoghi.getListaLuoghi().removeAll(luoghiDaRimuovere);
    }
}
