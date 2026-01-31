package it.unibs.ingdsw.view.cli.menu;

import it.unibs.ingdsw.controller.AppuntamentiController;
import it.unibs.ingdsw.controller.PrenotazioneController;
import it.unibs.ingdsw.model.applicazione.Target;
import it.unibs.ingdsw.model.applicazione.TargetTipo;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.utenti.Fruitore;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.view.cli.io.Output;

import java.time.YearMonth;

public class MenuFruitore extends MenuManager {

    private final AppuntamentiController appuntamentiController;
    private final PrenotazioneController prenotazioneController;
    private final Output out;

    public MenuFruitore(Utente utente, Output out) {
        super(utente, out);
        this.appuntamentiController = new AppuntamentiController(out);
        this.prenotazioneController = new PrenotazioneController(out);
        this.out = out;
    }

    @Override
    public void inizializza() {}

    @Override
    public Menu creaMenu(YearMonth targetDisponibilita) {
        Menu m = new Menu("Menu Fruitore", out);

        Target targetApplicazione = new Target();
        YearMonth targetProduzione = targetApplicazione.calcolaDataTarget(TargetTipo.PRODUZIONE);

        String nomeMeseProduzione = Data.returnNomeMese(targetProduzione);
        int annoProduzione = Data.returnAnno(targetProduzione);
        int meseProduzione = Data.returnMese(targetProduzione);

        m.aggiungi(1, "Visualizza gli appuntamenti per il mese di " + nomeMeseProduzione + " " + annoProduzione + " suddivisi nei vari stati",
                () -> appuntamentiController.visualizzaAppuntamentiDisponibili(meseProduzione, nomeMeseProduzione,annoProduzione));

        m.aggiungi(2, "Iscriviti ad un appuntamento per il mese di " + nomeMeseProduzione + " " + annoProduzione,
                () -> prenotazioneController.iscrizioneAppuntamento((Fruitore) utente, meseProduzione, nomeMeseProduzione,annoProduzione));

        m.aggiungi(3, "Visualizza lo stato degli appuntamenti a cui sei iscritto per il mese di " + nomeMeseProduzione + " " + annoProduzione,
                () -> appuntamentiController.visualizzaAppuntamentiIscritto(nomeMeseProduzione, annoProduzione)); //forse non funziona

        m.aggiungi(4, "Disdici un appuntamento per il mese di " + nomeMeseProduzione + " " + annoProduzione,
                () -> prenotazioneController.disdirePrenotazione((Fruitore) utente, nomeMeseProduzione, annoProduzione));

        return m;
    }

}
