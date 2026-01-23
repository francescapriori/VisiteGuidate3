package it.unibs.ingdsw.persistence.xml;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.applicazione.Target;
import it.unibs.ingdsw.persistence.ApplicazioneRepository;
import it.unibs.ingdsw.persistence.xml.parser.*;
import it.unibs.ingdsw.service.ServiceAppuntamenti;

import java.time.LocalDate;

public class XmlApplicazioneRepository implements ApplicazioneRepository {
    public Applicazione configuraApplicazione () {
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
        app.setNextDisponibilita(pa.getNextDisponibilita());
        ParsPrenotazioniXMLFile pp = new ParsPrenotazioniXMLFile(app.getCalendarioAppuntamenti().getAppuntamenti());
        app.setPrenotazioni(pp.getPrenotazioni());

        ServiceAppuntamenti serviceApp = new ServiceAppuntamenti(app);
        serviceApp.aggiornaStati(app.getCalendarioAppuntamenti(), LocalDate.now());

        return app;
    }

    public void salvaApplicazione(Applicazione app) {
        ParsParametriAppXMLFile.salvaParametri(app.getAmbitoTerritoriale(), app.getNumeroMassimoIscrivibili(), app.getStatoDisp(), app.getStatoProd(), app.getNextDisponibilita());
        ParsUtentiXMLFile.salvaListaUtenti(app.getListaUtenti());
        ParsDateEscluseXMLFile.salvaListaDate(app.getDateEscluse());
        ParsLuoghiXMLFile.salvaLuoghi(app.getListaLuoghi());
        ParsDisponibilitaVolontariXMLFile.salvaDisponibilitaVolontari(app.getDisponibilitaPerVol());
        ParsAppuntamentiXMLFile.salvaAppuntamenti(app.getCalendarioAppuntamenti().getAppuntamenti());
        ParsPrenotazioniXMLFile.salvaPrenotazioni(app.getPrenotazioni());
    }
}
