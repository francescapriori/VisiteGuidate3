package it.unibs.ingdsw.applicazione;

import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.tempo.*;
import it.unibs.ingdsw.utenti.ListaUtenti;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.Visita;
import it.unibs.ingdsw.parsing.*;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;

public class Applicazione {

    private String ambitoTerritoriale;
    private int numeroMassimoIscrivibili;
    private ListaUtenti listaUtenti;
    private ListaLuoghi listaLuoghi;
    private boolean daConfigurare = true;
    private InsiemeDate dateEscluse;
    private HashMap<Volontario, InsiemeDate> disponibilitaPerVol;
    private HashMap<Visita, InsiemeDate> calendarioVisite;
    private Stato stato;
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

    public void setStato(Stato stato) {
        this.stato = stato;
    }
    public Stato getStato() {
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

    public void aggiungiLuoghi(ListaLuoghi nuovi) {
        for (Luogo l : nuovi.getListaLuoghi()) {
            if (this.listaLuoghi.aggiungiLuogoSeNonPresente(l)) {
                System.out.println("Aggiunto: " + l.getNome());
            } else {
                System.out.println("Il luogo " + l.getNome() + " è già presente nell'elenco.");
            }
        }
    }

    @Deprecated
    public void mostraDateEsclusePerMeseAnno (int mese, int anno) {
        String nomeMese = Month.of(mese).getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        boolean meseConDate = false;
        for(Data d : this.dateEscluse.getInsiemeDate()) {
            if(d.getAnno() == anno && d.getMese() == mese) {
                if(meseConDate == false) {
                    System.out.println("Le date escluse per le visite del mese di " + nomeMese + " " + anno + " sono:");
                }
                System.out.println(d.toString());
                meseConDate = true;
            }
        }
        if(!meseConDate) {
            System.out.println("Non sono presenti date escluse per le visite del mese di " + nomeMese + " " + anno);
        }
    }

    public InsiemeDate getDateEsclusePerMeseAnno (int mese, int anno) {
        return this.getInsiemeDate().getDateEsclusePerMeseAnno(mese, anno);
    }

    public void mostraDateDisponibilitaPerMeseAnno (int mese, int anno, Volontario v) {
        String nomeMese = Month.of(mese).getDisplayName(TextStyle.FULL, Locale.ITALIAN);
        boolean meseConDate = false;
        for(Data d : this.disponibilitaPerVol.get(v).getInsiemeDate()) {
            if(this.disponibilitaPerVol.get(v).getInsiemeDate().isEmpty()) {
                System.out.println("Non ti sei reso disponibile per nessuna data per il mese di " + nomeMese + " " + anno + ".");
            }
            if(d.getAnno() == anno && d.getMese() == mese) {
                if(meseConDate == false) {
                    System.out.println("Le date in cui ti sei già reso disponibile per le visite del mese di " + nomeMese + " " + anno + " sono:");
                }
                System.out.println(d.toString());
                meseConDate = true;
            }
        }
        if(!meseConDate) {
            System.out.println("Non ti sei reso disponibile per nessuna data nel mese di " + nomeMese + " " + anno);
        }
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


//    public HashMap<Visita, InsiemeDate> produciVisitePerIlMese(int meseTargetV, int annoTargetV) {
//
//        HashMap<Visita, InsiemeDate> calendarioProvvisorio = this.listaLuoghi.getTotaleVisite().calendarioProvvisiorioVisiteDelMese(meseTargetV, annoTargetV);
//        HashMap<Visita, InsiemeDate> calendarioDefinitivo = new HashMap();
//
//        // se c'è corrispondenza allora la data viene aggiunta al calendario definitivo
//        for (Map.Entry<Visita, InsiemeDate> entry : calendarioProvvisorio.entrySet()) {
//            Visita visita = entry.getKey();
//            InsiemeDate dateCalendarioProvvisorio = entry.getValue();
//            InsiemeDate disponibilitaVolontariAssociatiThisVisita = InsiemeDate.dateDeiVolontari(this.disponibilitaPerVol, visita.getVolontariVisita());
//            InsiemeDate dateDefinitiveVisita = new InsiemeDate();
//
//            for (Data d : dateCalendarioProvvisorio.getInsiemeDate()) {
//                // la data è presente nelle disponibilità delle date associate ai volontari della visita?
//                for(Data d2 : disponibilitaVolontariAssociatiThisVisita.getInsiemeDate()) {
//                    if(d.dateUguali(d2)) {
//                        // c'è almeno un volontario che ha dato disponibilità per quel giorno
//                        dateDefinitiveVisita.aggiungiData(d);
//                        //uscire dal for d2 e tornare al for d
//                    }
//                }
//            }
//            if(!dateDefinitiveVisita.getInsiemeDate().isEmpty()) {
//                calendarioDefinitivo.put(visita, dateDefinitiveVisita);
//            }
//        }
//        return calendarioDefinitivo;
//    }

}
