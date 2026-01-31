package it.unibs.ingdsw.view.cli.menu;

import it.unibs.ingdsw.controller.AppuntamentiController;
import it.unibs.ingdsw.controller.UtentiController;
import it.unibs.ingdsw.controller.VisiteController;
import it.unibs.ingdsw.model.applicazione.*;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.view.cli.io.Output;

import java.time.YearMonth;

public class MenuVolontario extends MenuManager {

    private final AppuntamentiController appuntamentiController;
    private final UtentiController utentiController;
    private final VisiteController visiteController;
    private final Output out;

    public MenuVolontario(Utente utente, Output out) {
        super(utente, out);
        this.appuntamentiController = new AppuntamentiController(out);
        this.utentiController = new UtentiController(out);
        this.visiteController = new VisiteController(utentiController, out);
        this.out = out;
    }
    @Override
    public void inizializza() {}

    @Override
    public Menu creaMenu(YearMonth targetDisponibilita) {
        Menu m = new Menu("Menu Volontario", out);

        Target targetApplicazione = new Target();
        YearMonth targetProduzione = targetApplicazione.calcolaDataTarget(TargetTipo.PRODUZIONE);

        String nomeMeseDisponibilita = Data.returnNomeMese(targetDisponibilita);
        int annoDisponibilita = Data.returnAnno(targetDisponibilita);
        int meseDisponibilita = Data.returnMese(targetDisponibilita);
        String nomeMeseProduzione = Data.returnNomeMese(targetProduzione);
        int annoProduzione = Data.returnAnno(targetProduzione);
        int meseProduzione = Data.returnMese(targetProduzione);

        m.aggiungi(1, "Visualizza le visite a cui sei stato associato",
                () -> visiteController.visualizzaVisiteDelVol(utente));
        m.aggiungi(2, "Indica le tue disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita,
                () -> utentiController.indicaDisponibilita(meseDisponibilita, nomeMeseDisponibilita, annoDisponibilita, (Volontario) utente));
        m.aggiungi(3, "Visualizza gli appuntamenti confermati e cancellati a cui sei stato associato per il mese di " + nomeMeseProduzione + " " + annoProduzione,
                () -> appuntamentiController.visualizzaAppuntamentiPerStato(nomeMeseProduzione, annoProduzione, meseProduzione));
        return m;
    }

}
