package it.unibs.ingdsw.persistence.xml;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.applicazione.Target;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.persistence.ApplicazioneRepository;
import it.unibs.ingdsw.persistence.mapper.*;
import it.unibs.ingdsw.persistence.xml.reader.*;
import it.unibs.ingdsw.persistence.xml.writer.*;
import it.unibs.ingdsw.service.ServiceAppuntamenti;

import java.io.File;
import java.time.LocalDate;

public class XmlApplicazioneRepository implements ApplicazioneRepository {

    private static final File FILE_PARAMETRI = new File("parametriApplicazione.xml");
    private static final File FILE_UTENTI = new File("utenti.xml");
    private static final File FILE_DATE_ESCLUSE = new File("dateEscluse.xml");
    private static final File FILE_LUOGHI = new File("luoghi.xml");
    private static final File FILE_DISPONIBILITA = new File("disponibilitaVolontari.xml");
    private static final File FILE_APPUNTAMENTI = new File("appuntamenti.xml");
    private static final File FILE_PRENOTAZIONI = new File("prenotazioni.xml");

    @Override
    public Applicazione configuraApplicazione() {
        Applicazione app = new Applicazione();

        ParametriApplicazioneXmlReader.Result par = new ParametriApplicazioneXmlReader(FILE_PARAMETRI).readOrDefault();
        var listaUtenti = new UtentiXmlReader(FILE_UTENTI).read();
        var listaLuoghi = new LuoghiXmlReader(FILE_LUOGHI).read();
        var dateEscluse = new DateEscluseXmlReader(FILE_DATE_ESCLUSE).read();
        var dispDtos = new DisponibilitaVolontariXmlReader(FILE_DISPONIBILITA).readAll();
        var disponibilitaPerVol = new DisponibilitaVolontariMapper(listaUtenti).toDomain(dispDtos);
        var appDtos = new AppuntamentiXmlReader(FILE_APPUNTAMENTI).readAll();
        var calendario = new AppuntamentiMapper(listaLuoghi, listaUtenti).toDomain(appDtos);
        if (calendario == null) calendario = new InsiemeAppuntamenti();
        var prenDtos = new PrenotazioniXmlReader(FILE_PRENOTAZIONI).readAll();
        var prenotazioni = new PrenotazioniMapper(calendario.getAppuntamenti()).toDomain(prenDtos);

        app.setAmbitoTerritoriale(par.ambitoTerritoriale);
        app.setNumeroMassimoIscrivibili(par.numeroMax);
        app.setDaConfigurare(par.ambienteDaConfigurare);
        app.setListaUtenti(listaUtenti);
        app.setListaLuoghi(listaLuoghi);
        app.setDateEscluse(dateEscluse);
        app.setDisponibilitaPerVol(disponibilitaPerVol);
        app.setCalendarioAppuntamenti(calendario);

        if ((new Target()).successivoASoglia() && par.statoProduzione == StatoProduzioneVisite.PRODOTTE) {
            app.setStatoProduzione(StatoProduzioneVisite.NON_PRODOTTE);
        } else {
            app.setStatoProduzione(par.statoProduzione);
        }

        app.setStatoDisp(par.statoDisp);
        app.setNextDisponibilita(par.nextDisponibilita);
        app.setPrenotazioni(prenotazioni);
        ServiceAppuntamenti serviceApp = new ServiceAppuntamenti(app.getCalendarioAppuntamenti());
        serviceApp.aggiornaStati(LocalDate.now());

        if (par.shouldRewrite) {
            new ParametriApplicazioneXmlWriter(FILE_PARAMETRI).write(
                    app.getAmbitoTerritoriale(),
                    app.getNumeroMassimoIscrivibili(),
                    app.isDaConfigurare(),
                    app.getStatoDisp(),
                    app.getStatoProd(),
                    app.getNextDisponibilita()
            );
        }

        return app;
    }

    @Override
    public void salvaApplicazione(Applicazione app) {
        new ParametriApplicazioneXmlWriter(FILE_PARAMETRI).write(
                app.getAmbitoTerritoriale(),
                app.getNumeroMassimoIscrivibili(),
                app.isDaConfigurare(),
                app.getStatoDisp(),
                app.getStatoProd(),
                app.getNextDisponibilita()
        );

        new UtentiXmlWriter(FILE_UTENTI).write(app.getListaUtenti());
        new DateEscluseXmlWriter(FILE_DATE_ESCLUSE).write(app.getDateEscluse());
        new LuoghiXmlWriter(FILE_LUOGHI).write(app.getListaLuoghi());
        new DisponibilitaVolontariXmlWriter(FILE_DISPONIBILITA).write(app.getDisponibilitaPerVol());
        new AppuntamentiXmlWriter(FILE_APPUNTAMENTI).upsertAll(app.getCalendarioAppuntamenti().getAppuntamenti());
        new PrenotazioniXmlWriter(FILE_PRENOTAZIONI).writeAll(app.getPrenotazioni());
    }
}
