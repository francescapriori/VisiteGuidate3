package it.unibs.ingdsw.applicazione;

import it.unibs.ingdsw.inputOutput.InputManager;
import it.unibs.ingdsw.inputOutput.OutputManager;
import it.unibs.ingdsw.service.ServiceApplicazione;
import it.unibs.ingdsw.service.ServicePrenotazione;
import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.utenti.Fruitore;
import it.unibs.ingdsw.utenti.Utente;
import it.unibs.ingdsw.visite.Appuntamento;
import it.unibs.ingdsw.visite.Prenotazione;

import java.time.YearMonth;
import java.util.ArrayList;

public class MenuFruitore extends MenuManager {

    public MenuFruitore(Applicazione applicazione, Utente u) {
        super(applicazione, u);
    }

    @Override
    public void primaInizializzazione() {}

    @Override
    public Menu creaMenu() {
        Menu m = new Menu("Menu Fruitore");

        Target targetApplicazione = new Target();
        YearMonth targetProduzione = targetApplicazione.calcolaDataTargetProduzione();
        ServiceApplicazione serviceApplicazione = new ServiceApplicazione(applicazione);
        ServicePrenotazione servicePrenotazione = new ServicePrenotazione(applicazione);

        String nomeMeseProduzione = Data.returnNomeMese(targetProduzione);
        int annoProduzione = Data.returnAnno(targetProduzione);
        int meseProduzione = Data.returnMese(targetProduzione);

        m.aggiungi(1, "Visualizza gli appuntamenti del mese di " + nomeMeseProduzione + " " + annoProduzione + " suddivise per stato:", () -> {
            if (serviceApplicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
                OutputManager.visualizzaAppuntamentiPerStato(serviceApplicazione.getAppuntamenti(meseProduzione, annoProduzione), false);
            } else {
                System.out.println("Non è possibile visualizzare gli appuntamenti per stato: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(2, "Iscriviti ad un appuntamento per il mese di " + nomeMeseProduzione + " " + annoProduzione, () -> {

            ArrayList<Appuntamento> appuntamenti = OutputManager
                    .visualizzaAppuntamentiPerPrenotazione(serviceApplicazione.getAppuntamenti(meseProduzione, annoProduzione)); // todo forse modificare la lista in modo dinamico: non vedo più gli appuntamenti per cui ho già fatto una prenotazione

            // Sicurezza: nessun appuntamento disponibile
            if (appuntamenti == null || appuntamenti.isEmpty()) {
                System.out.println("Non ci sono appuntamenti disponibili per la prenotazione.");
                return;
            }

            int indiceAppuntamento = InputManager.leggiInteroConMinMax(
                    "Scegli l'appuntamento a cui vuoi iscriverti: ",
                    1,
                    appuntamenti.size()
            ) - 1;

            Appuntamento appuntamentoSelezionato = appuntamenti.get(indiceAppuntamento);

            int postiDisponibili = servicePrenotazione.numeroPostiDisponibili(appuntamentoSelezionato);

            if (postiDisponibili <= 0) {
                System.out.println("Non ci sono più posti disponibili per l'appuntamento selezionato.");
                return;
            }

            int numIscriv;
            int maxIscrivibili = serviceApplicazione.getNumMaxIscrivibili();
            int limiteSuperiore = Math.min(maxIscrivibili, postiDisponibili);

            do {
                System.out.println("Sono rimasti solamente " + postiDisponibili + " posti per l'appuntamento selezionato.");
                numIscriv = InputManager.leggiInteroConMinMax(
                        "Inserisci il numero di persone che vuoi iscrivere: ",
                        1,
                        limiteSuperiore
                );
                if (numIscriv > postiDisponibili) {
                    System.out.println("Inserire un numero minore o uguale a " + postiDisponibili);
                }
            } while (numIscriv > postiDisponibili);

            Fruitore fruitore = (Fruitore) this.utente;
            Prenotazione prenotazione = new Prenotazione(appuntamentoSelezionato, fruitore, numIscriv);

            if (servicePrenotazione.prenotazioneGiaPresente(prenotazione)) {
                System.out.println("Hai già effettuato una prenotazione per questo appuntamento");
                return;
            }

            serviceApplicazione.aggiungiPrenotazione(prenotazione);
            System.out.println(prenotazione);
        });


        m.aggiungi(3, "Visualizza lo stato degli appuntamenti a cui sei iscritto per il mese di " + nomeMeseProduzione + " " + annoProduzione, () -> {
            OutputManager.visualizzaPrenotazioni(servicePrenotazione.prenotazioniDi(this.utente, meseProduzione, annoProduzione), nomeMeseProduzione, annoProduzione);
        });

        m.aggiungi(4, "Disdici un appuntamento per il mese di " + nomeMeseProduzione + " " + annoProduzione, () -> {
            ArrayList<Prenotazione> prenotazioni = servicePrenotazione.prenotazioniDi(this.utente, meseProduzione, annoProduzione);
            OutputManager.visualizzaPrenotazioni(prenotazioni, nomeMeseProduzione, annoProduzione);
            int scelta = InputManager.leggiInteroConMinMax("Scegli la prenotazione che vuoi eliminare: ", 1, prenotazioni.size());
            servicePrenotazione.rimuoviPrenotazione(prenotazioni.get(scelta - 1));
        });

        // todo in tutti questi casi c'è da gestire: non è ancora stato prodotto il piano delle visite per quel mese, il piano delle visite per quel mese è nullo

        return m;
    }

}
