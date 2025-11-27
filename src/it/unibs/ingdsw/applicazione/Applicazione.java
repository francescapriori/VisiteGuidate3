package it.unibs.ingdsw.applicazione;

import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.tempo.*;
import it.unibs.ingdsw.utenti.ListaUtenti;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.Appuntamento;
import it.unibs.ingdsw.visite.CalendarioAppuntamenti;
import it.unibs.ingdsw.visite.Visita;
import it.unibs.ingdsw.parsing.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Applicazione {

    private String ambitoTerritoriale;
    private int numeroMassimoIscrivibili;
    private ListaUtenti listaUtenti;
    private ListaLuoghi listaLuoghi;
    private boolean daConfigurare = true;
    private InsiemeDate dateEscluse;
    private HashMap<Volontario, InsiemeDate> disponibilitaPerVol;
    private HashMap<Visita, InsiemeDate> calendarioVisite;
    private StatoRichiestaDisponibilita stato;
    private StatoProduzioneVisite statoProduzione;
    private LocalDate oggi;

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

    public HashMap<Visita,InsiemeDate> getCalendarioVisite() {return calendarioVisite;}

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


    public void setInsiemeDate(InsiemeDate dateEscluse) {
        this.dateEscluse = dateEscluse;
    }

    public void setCalendarioVisite(HashMap<Visita, InsiemeDate> calendarioVisite) {this.calendarioVisite=calendarioVisite;}

    public boolean isDaConfigurare() {
        return daConfigurare;
    }

    public HashMap<Volontario, InsiemeDate> getDisponibilitaPerVol() {
        return disponibilitaPerVol;
    }

    public void setDisponibilitaPerVol(HashMap<Volontario, InsiemeDate> disponibilitaPerVol) {
        this.disponibilitaPerVol = disponibilitaPerVol;
    }

    public void setStato(StatoRichiestaDisponibilita stato) {
        this.stato = stato;
    }
    public StatoRichiestaDisponibilita getStato() {
        return stato;
    }

    public static Applicazione configuraApplicazione () {
        Applicazione app = new Applicazione();
        ParsParametriAppXMLFile pa = new ParsParametriAppXMLFile();
        ParsUtentiXMLFile ut = new ParsUtentiXMLFile();
        ParsDateEscluseXMLFile d = new ParsDateEscluseXMLFile();
        ParsLuoghiXMLFile l = new ParsLuoghiXMLFile();
        ParsDisponibilitaVolontariXMLFile dV = new ParsDisponibilitaVolontariXMLFile(ut.getListaUtenti());
        ParsCalendarioVisiteXMLFile cv = new ParsCalendarioVisiteXMLFile();
        app.setAmbitoTerritoriale(pa.getAmbitoTerritoriale());
        app.setNumeroMassimoIscrivibili(pa.getNumeroMassimoIscrivibili());
        app.setDaConfigurare(pa.isAmbienteDaConfigurare());
        app.setListaUtenti(ut.getListaUtenti());
        app.setListaLuoghi(l.getListaLuoghi());
        app.setDateEscluse(d.getInsiemeDate());
        app.setDisponibilitaPerVol(dV.getDisponibilitaPerVol());
        app.setCalendarioVisite(cv.getCalendarioVisite());
        app.setStato(pa.getStato());
        return app;
    }

    public void salvaApplicazione(){
        ParsParametriAppXMLFile.salvaParametri(this.ambitoTerritoriale, this.numeroMassimoIscrivibili);
        ParsUtentiXMLFile.salvaListaUtenti(this.listaUtenti);
        ParsDateEscluseXMLFile.salvaListaDate(this.dateEscluse);
        ParsLuoghiXMLFile.salvaLuoghi(this.listaLuoghi);
        ParsDisponibilitaVolontariXMLFile.salvaDisponibilitaVolontari(this.disponibilitaPerVol);
        ParsCalendarioVisiteXMLFile.salvaCalendarioVisite(this.calendarioVisite);
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
        this.listaUtenti.getVolontari().remove(posizione);
    }


    public CalendarioAppuntamenti produciVisitePerIlMese(int meseTargetV, int annoTargetV) {

        HashMap<Visita, InsiemeDate> calendarioProvvisorio = this.listaLuoghi.getTotaleVisite().calendarioProvvisiorioVisiteDelMese(meseTargetV, annoTargetV);
        CalendarioAppuntamenti calendarioAppuntamenti = new CalendarioAppuntamenti();
        HashMap<Volontario, InsiemeDate> volontariConDate = this.disponibilitaPerVol;

        for (Map.Entry<Visita, InsiemeDate> entry : calendarioProvvisorio.entrySet()) {
            Visita visita = entry.getKey();
            InsiemeDate dateCalendarioProvvisorio = entry.getValue();

            for (Map.Entry<Volontario, InsiemeDate> entry2 : volontariConDate.entrySet()) {
                Volontario volontario = entry2.getKey();
                InsiemeDate dateDisponibilitaVolontario = entry2.getValue();

                for (Data d1 : dateCalendarioProvvisorio.getInsiemeDate()) {

                    for(Data d2 : dateDisponibilitaVolontario.getInsiemeDate()) {

                        if(d1.dateUguali(d2) && !calendarioAppuntamenti.volontarioGiaPresenteInData(d2, volontario)) {
                            calendarioAppuntamenti.getCalendarioVisite().add(new Appuntamento(visita, d1, volontario));
                            break;
                        }
                    }
                }
            }
        }
        return calendarioAppuntamenti;
    }

}
