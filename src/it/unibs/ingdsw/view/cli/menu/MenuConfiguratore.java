package it.unibs.ingdsw.view.cli.menu;

import it.unibs.ingdsw.controller.*;
import it.unibs.ingdsw.model.applicazione.*;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.utenti.Utente;

import java.time.YearMonth;

public class MenuConfiguratore extends MenuManager {

    private final ApplicazioneController controller;
    private final DateController dateController;
    private final UtentiController utentiController;
    private final LuogoController luogoController;
    private final VisiteController visiteController;
    private final AppuntamentiController appuntamentiController;

    public MenuConfiguratore(Utente utente) {
        super(utente);
        this.controller = new ApplicazioneController();
        this.dateController = new DateController();
        this.utentiController = new UtentiController();
        this.visiteController = new VisiteController(utentiController);
        this.luogoController = new LuogoController(visiteController);
        this.appuntamentiController = new AppuntamentiController();
    }

    @Override
    public Menu creaMenu(YearMonth targetDisponibilita) {
        Menu m = new Menu("Menu Configuratore");

        Target targetApplicazione = new Target();
        YearMonth targetProduzione, targetPerEsclusione;

        targetPerEsclusione = targetApplicazione.calcolaDataTarget(TargetTipo.ESCLUSIONE);
        targetProduzione = targetApplicazione.calcolaDataTarget(TargetTipo.PRODUZIONE);

        String nomeMesePerEsclusione = Data.returnNomeMese(targetPerEsclusione);
        int annoPerEsclusione = Data.returnAnno(targetPerEsclusione);
        int mesePerEsclusione = Data.returnMese(targetPerEsclusione);
        String nomeMeseDisponibilita = Data.returnNomeMese(targetDisponibilita);
        int annoDisponibilita = Data.returnAnno(targetDisponibilita);
        String nomeMeseProduzione = Data.returnNomeMese(targetProduzione);
        int annoProduzione = Data.returnAnno(targetProduzione);
        int meseProduzione = Data.returnMese(targetProduzione);

        m.aggiungi(1, "Indica le date da escludere per il mese di " + nomeMesePerEsclusione + " " + annoPerEsclusione,
                () -> dateController.indicaDateDaEscludere(mesePerEsclusione, annoPerEsclusione));

        m.aggiungi(2, "Modifica numero massimo persone per iscrizione",
                controller::modificaNumeroMassimoIscrivibili);

        m.aggiungi(3, "Visualizza elenco volontari con relative visite",
                utentiController::visualizzaVolontariConVisite);

        m.aggiungi(4, "Visualizza elenco luoghi visitabili",
                luogoController::visualizzaElencoLuoghi);

        m.aggiungi(5, "Visualizza tipi di visita per ciascun luogo",
                luogoController::visualizzaLuoghiEvisite);

        m.aggiungi(6, "Visualizza appuntamenti per stato",
                () -> appuntamentiController.visualizzaAppuntamentiPerStato(meseProduzione, annoProduzione));

        m.aggiungi(7, "Apri raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita,
                () -> controller.aperturaDisponibilita(nomeMeseDisponibilita, annoDisponibilita));

        m.aggiungi(8, "Chiudi raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita,
                () -> controller.chiudiDisponibilita(nomeMeseDisponibilita, annoDisponibilita, targetDisponibilita));

        m.aggiungi(9, "Produci il piano delle visite per il mese di " + nomeMeseProduzione + " " + annoProduzione,
                () -> appuntamentiController.produzioneVisite(meseProduzione, nomeMeseProduzione, annoProduzione));

        m.aggiungi(10, "Aggiungi un nuovo luogo all'elenco",
                () -> luogoController.aggiungiLuogo(nomeMeseProduzione, annoProduzione));

        m.aggiungi(11, "Elimina un luogo",
                () -> luogoController.eliminaLuogo(nomeMeseProduzione, annoProduzione));

        m.aggiungi(12, "Aggiungi una o più visite ad un luogo già esistente",
                () -> visiteController.aggiungiVisita(nomeMeseProduzione, annoProduzione));

        m.aggiungi(13, "Aggiungi uno o più volontari ad una visita",
                () -> visiteController.aggiungiVolontatiAVisita(nomeMeseProduzione, annoProduzione));

        m.aggiungi(14, "Elimina una visita associata ad un luogo",
                () -> luogoController.eliminaVisitaDaLuogo(nomeMeseProduzione, annoProduzione));

        m.aggiungi(15, "Elimina un volontario",
                () -> utentiController.eliminaVolontario(nomeMeseProduzione, annoProduzione));
        return m;
    }

    @Override
    public void inizializza() {
        controller.configuraParametriBase();
        luogoController.configuraLuoghi();
    }

}
