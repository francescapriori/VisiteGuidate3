package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.model.prenotazione.Prenotazione;
import it.unibs.ingdsw.model.utenti.Fruitore;
import it.unibs.ingdsw.service.ServiceAppuntamenti;
import it.unibs.ingdsw.service.ServicePrenotazione;
import it.unibs.ingdsw.view.AppuntamentiView;
import it.unibs.ingdsw.view.cli.io.Output;
import it.unibs.ingdsw.view.format.Formatters;
import it.unibs.ingdsw.view.cli.io.InputManager;

import java.util.List;

public class PrenotazioneController {

    private final Applicazione applicazione;
    private final ServicePrenotazione servicePrenotazione;
    private final ServiceAppuntamenti serviceAppuntamenti;
    private final AppuntamentiView appuntamentiView;
    private final Output out;

    public PrenotazioneController(Output out) {
        this.applicazione = Applicazione.getApplicazione();
        this.servicePrenotazione = new ServicePrenotazione(Applicazione.getApplicazione());
        this.serviceAppuntamenti = new ServiceAppuntamenti(Applicazione.getApplicazione().getCalendarioAppuntamenti());
        this.appuntamentiView = new AppuntamentiView(out);
        this.out = out;
    }

    public void disdirePrenotazione(Fruitore utente, String nomeMeseProduzione, int annoProduzione) {
        if (applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            if(servicePrenotazione.getPrenotazioniUtente(utente).isEmpty()) {
                System.out.println("Non hai ancora effettuato nessuna prenotazione");
                return;
            }
            String codicePDaRimuovere = InputManager.leggiStringaNonVuota("Inserisci il codice della tua prenotazione che vuoi rimuovere: ");
            if (servicePrenotazione.rimozionePrenotazioneConCodice(codicePDaRimuovere)) {
                out.println("Hai cancellato la tua prenotazione " + codicePDaRimuovere);
            } else {
                out.println("Nessuna prenotazione con codice " + codicePDaRimuovere);
            }
        } else {
            out.println("Non è possibile visualizzare gli appuntamenti per stato: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

    public void iscrizioneAppuntamento(Fruitore utente, int meseProduzione, String nomeMeseProduzione, int annoProduzione) {
        if (applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            List<Appuntamento> appuntamenti = serviceAppuntamenti.getAppuntamentiPrenotabili();
            appuntamentiView.visualizzaAppuntamenti(new InsiemeAppuntamenti(appuntamenti));
            if (appuntamenti == null || appuntamenti.isEmpty()) {
                out.println("Non ci sono appuntamenti disponibili per la prenotazione.");
                return;
            }
            int indiceAppuntamento = InputManager.leggiInteroConMinMax(
                    "Scegli il numero corrispondente all'appuntamento a cui vuoi iscriverti: ",
                    1,
                    appuntamenti.size()
            ) - 1;
            Appuntamento appuntamentoSelezionato = appuntamenti.get(indiceAppuntamento);
            Fruitore fruitore = utente;
            if (servicePrenotazione.prenotazioneGiaPresente(appuntamentoSelezionato, fruitore)) {
                out.println("Hai già effettuato una prenotazione per questo appuntamento.");
                return;
            }
            int postiDisponibili = appuntamentoSelezionato.getPostiDisponibili();

            if (postiDisponibili <= 0) {
                out.println("Non ci sono più posti disponibili per l'appuntamento selezionato.");
                return;
            }

            int numIscriv;
            int maxIscrivibili = applicazione.getNumeroMassimoIscrivibili();
            int limiteSuperiore = Math.min(maxIscrivibili, postiDisponibili);

            do {
                out.println("Sono rimasti solamente " + postiDisponibili + " posti per l'appuntamento selezionato.");
                numIscriv = InputManager.leggiInteroConMinMax(
                        "Inserisci il numero di persone che vuoi iscrivere: ",
                        1,
                        limiteSuperiore
                );
                if (numIscriv > postiDisponibili) {
                    out.println("Inserire un numero minore o uguale a " + postiDisponibili);
                }
            } while (numIscriv > postiDisponibili);

            Prenotazione prenotazione = new Prenotazione(appuntamentoSelezionato, fruitore, numIscriv);


            servicePrenotazione.aggiungiPrenotazione(prenotazione);
            out.println(Formatters.prenotazione(prenotazione));
        } else {
            out.println("Non è possibile iscriversi ad un appuntamento: è necessario produrre prima il piano delle visite per il mese di" +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }
}
