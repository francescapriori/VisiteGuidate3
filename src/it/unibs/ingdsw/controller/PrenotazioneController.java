package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.prenotazione.Prenotazione;
import it.unibs.ingdsw.model.utenti.Fruitore;
import it.unibs.ingdsw.service.ServiceAppuntamenti;
import it.unibs.ingdsw.service.ServicePrenotazione;
import it.unibs.ingdsw.view.cli.format.Formatters;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.view.cli.io.OutputManager;

import java.util.ArrayList;

public class PrenotazioneController {

    private final Applicazione applicazione;
    private final ServicePrenotazione servicePrenotazione;
    private final ServiceAppuntamenti serviceAppuntamenti;

    public PrenotazioneController() {
        this.applicazione = Applicazione.getApplicazione();
        this.servicePrenotazione = new ServicePrenotazione(Applicazione.getApplicazione());
        this.serviceAppuntamenti = new ServiceAppuntamenti(Applicazione.getApplicazione().getCalendarioAppuntamenti());
    }

    public void disdirePrenotazione(Fruitore utente, String nomeMeseProduzione, int annoProduzione) {
        if (applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            if(servicePrenotazione.getPrenotazioniUtente(utente).isEmpty()) {
                System.out.println("Non hai ancora effettuato nessuna prenotazione");
                return;
            }
            String codicePDaRimuovere = InputManager.leggiStringaNonVuota("Inserisci il codice della tua prenotazione che vuoi rimuovere: ");
            if (servicePrenotazione.rimozionePrenotazioneConCodice(codicePDaRimuovere)) {
                OutputManager.visualizzaMessaggio("Hai cancellato la tua prenotazione " + codicePDaRimuovere);
            } else {
                OutputManager.visualizzaMessaggio("Nessuna prenotazione con codice " + codicePDaRimuovere);
            }
        } else {
            OutputManager.visualizzaMessaggio("Non è possibile visualizzare gli appuntamenti per stato: è necessario produrre prima il piano delle visite per il mese " +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }

    public void iscrizioneAppuntamento(Fruitore utente, int meseProduzione, String nomeMeseProduzione, int annoProduzione) {
        if (applicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
            ArrayList<Appuntamento> appuntamenti = OutputManager.visualizzaAppuntamentiPerPrenotazione(serviceAppuntamenti.getAppuntamentiDelMeseTarget(meseProduzione, annoProduzione)); // todo forse modificare la lista in modo dinamico: non vedo più gli appuntamenti per cui ho già fatto una prenotazione
            if (appuntamenti == null || appuntamenti.isEmpty()) {
                OutputManager.visualizzaMessaggio("Non ci sono appuntamenti disponibili per la prenotazione.");
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
                OutputManager.visualizzaMessaggio("Hai già effettuato una prenotazione per questo appuntamento.");
                return;
            }
            int postiDisponibili = appuntamentoSelezionato.getPostiDisponibili();

            if (postiDisponibili <= 0) {
                OutputManager.visualizzaMessaggio("Non ci sono più posti disponibili per l'appuntamento selezionato.");
                return;
            }

            int numIscriv;
            int maxIscrivibili = applicazione.getNumeroMassimoIscrivibili();
            int limiteSuperiore = Math.min(maxIscrivibili, postiDisponibili);

            do {
                OutputManager.visualizzaMessaggio("Sono rimasti solamente " + postiDisponibili + " posti per l'appuntamento selezionato.");
                numIscriv = InputManager.leggiInteroConMinMax(
                        "Inserisci il numero di persone che vuoi iscrivere: ",
                        1,
                        limiteSuperiore
                );
                if (numIscriv > postiDisponibili) {
                    OutputManager.visualizzaMessaggio("Inserire un numero minore o uguale a " + postiDisponibili);
                }
            } while (numIscriv > postiDisponibili);

            Prenotazione prenotazione = new Prenotazione(appuntamentoSelezionato, fruitore, numIscriv);


            servicePrenotazione.aggiungiPrenotazione(prenotazione);
            OutputManager.visualizzaMessaggio(Formatters.prenotazione(prenotazione));
        } else {
            OutputManager.visualizzaMessaggio("Non è possibile iscriversi ad un appuntamento: è necessario produrre prima il piano delle visite per il mese di" +
                    nomeMeseProduzione + " " + annoProduzione);
        }
    }
}
