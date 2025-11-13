package it.unibs.ingdsw.applicazione;

import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.output.OutputManager;
import it.unibs.ingdsw.service.*;
import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.utenti.Utente;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;
import it.unibs.ingdsw.visite.StatoVisita;

import java.time.YearMonth;
import java.util.HashMap;

public class MenuConfiguratore extends MenuManager {

    public MenuConfiguratore(Applicazione applicazione, Utente utente) {
        super(applicazione, utente);
    }

    @Override
    public Menu creaMenu() {
        Menu m = new Menu("Menu Configuratore");

        YearMonth targetPerEsclusione, targetDisponibilita, targetProduzione;

        // esattamente il 16 o dopo
        if(isDayAfterThreshold()==0 || isDayAfterThreshold()==1) {
            targetPerEsclusione = calcolaDataTarget(4);
            targetDisponibilita = calcolaDataTarget(3);
            targetProduzione = calcolaDataTarget(2);
        } else { // prima del 16
            targetPerEsclusione = calcolaDataTarget(3);
            targetDisponibilita = calcolaDataTarget(2);
            targetProduzione = calcolaDataTarget(1);
        }
        String nomeMesePerEsclusione = Data.returnNomeMese(targetPerEsclusione);
        int annoPerEsclusione = Data.returnAnno(targetPerEsclusione);
        int mesePerEsclusione = Data.returnMese(targetPerEsclusione);
        String nomeMeseDisponibilita = Data.returnNomeMese(targetDisponibilita);
        int annoDisponibilita = Data.returnAnno(targetDisponibilita);
        int meseDisponibilita = Data.returnMese(targetDisponibilita);
        String nomeMeseProduzione = Data.returnNomeMese(targetProduzione);
        int annoProduzione = Data.returnAnno(targetProduzione);
        int meseProduzione = Data.returnMese(targetProduzione);

        ServiceDate serviceDate = new ServiceDate(applicazione);
        ServiceApplicazione serviceApplicazione = new ServiceApplicazione(applicazione);
        ServiceLuoghi serviceLuoghi = new ServiceLuoghi(applicazione);
        ServiceVolontari serviceVolontari = new ServiceVolontari(applicazione);
        ServiceVisite serviceVisite = new ServiceVisite(applicazione);


        m.aggiungi(1, "Indica le date da escludere per il mese di " + nomeMesePerEsclusione + " " + annoPerEsclusione, () -> {
            InsiemeDate dateEscluse = serviceDate.getDateEscluse(mesePerEsclusione, annoPerEsclusione);
            OutputManager.visualizzaDatePerMeseAnno(dateEscluse, mesePerEsclusione, annoPerEsclusione, OutputManager.TipoRichiestaData.ESCLUSIONE);
            do {
                Data dataDaEscludere = new Data(InputManager.chiediGiorno(mesePerEsclusione, annoPerEsclusione), mesePerEsclusione, annoPerEsclusione);
                if (!serviceDate.aggiungiData(dataDaEscludere)) {
                    System.out.println("La data è già presente nell'elenco.");
                }
            } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un'altra data da escludere?")));
            dateEscluse = serviceDate.getDateEscluse(mesePerEsclusione, annoPerEsclusione);
            OutputManager.visualizzaDatePerMeseAnno(dateEscluse, mesePerEsclusione, annoPerEsclusione, OutputManager.TipoRichiestaData.ESCLUSIONE);
        });

        m.aggiungi(2, "Modifica numero massimo persone per iscrizione", () -> {
            int nuovoNumIscrivibili = InputManager.richiediNumeroMassimoIscrivibili();
            serviceApplicazione.modificaNumeroMassimoIscrivibili(nuovoNumIscrivibili);
        });

        m.aggiungi(3, "Visualizza elenco volontari con relative visite", () -> {
            HashMap<Volontario, ListaVisite> volontarioPerVisita = serviceVolontari.getVolontariConVisiteAssociate();
            OutputManager.visualizzaVolontariConVisiteAssociate(volontarioPerVisita);
        });

        m.aggiungi(4, "Visualizza elenco luoghi visitabili", () -> {
            System.out.println("Elenco dei luoghi visitabili disponibili: ");
            OutputManager.visualizzaLuoghi(serviceLuoghi.getListaLuoghi());
        });

        m.aggiungi(5, "Visualizza tipi di visita per ciascun luogo", () -> {
            System.out.println("-----\nLista de Luoghi registrati: ");
            OutputManager.visualizzaLuoghi(serviceLuoghi.getListaLuoghi());

            do {
                OutputManager.visualizzaLuoghi(serviceLuoghi.getListaLuoghi());
                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il luogo di cui si vogliono visualizzare le visite: ",
                        1, serviceLuoghi.getNumeroLuogo());
                Luogo luogo = serviceLuoghi.scegliLuogo(scelta);
                System.out.println("Visite associate al luogo \"" + luogo.getNome() + "\"");
                OutputManager.visualizzaListaVisite(serviceVisite.getListaVisite());
            } while ("sì".equals(InputManager.chiediSiNo("Vuoi visualizzare le visite per un altro luogo?")));
        });

        m.aggiungi(6, "Visualizza visite per stato", () -> {
            do {
                StatoVisita statoScelto = InputManager.chiediStatoVisita();
                ListaVisite listaPerStato = serviceVisite.listaPerStato(statoScelto);
                System.out.println("Lista delle visite con stato: " + statoScelto.toString());
                OutputManager.visualizzaListaVisite(listaPerStato);
                if (listaPerStato.getListaVisite().isEmpty()) {
                    System.out.println("Nessuna visita disponibile.");
                }
            } while("sì".equals(InputManager.chiediSiNo("Vuoi visualizzare le visite per un altro stato?")));
        });


        /**
         * è possibile riaprire/chiudere la racolta disponibilità visite per il mese i+2 ogni volta che si vuole fino al
         * raggiungimento del giorno soglia a partire dal quale, se non è stata chiusa la raccolta disponibilità per il
         * mese precedente, viene chiusa in automatico
         */

        if(this.applicazione.getStato() == Stato.PRODUZIONE) {
            meseDisponibilita++;

        }

        m.aggiungi(7, "Apri raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita, () -> {

//            if(this.applicazione.getStato() == Stato.DISP_CHIUSE || this.applicazione.getStato() == Stato.PRODUZIONE) {
//
//                System.out.println("Da ora è possibile raccogliere le disponibilità dei Volontari per il mese di "+ nomeMeseDisponibilita + " " + annoDisponibilita);
//                this.applicazione.setStato(Stato.DISP_APERTE);
//            }
//
//            else {
//                System.out.println("La raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita + " sono già in corso.");
//            }

        });


        m.aggiungi(8, "Chiudi raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita, () -> {


        });

        m.aggiungi(9, "Produci il piano delle visite per il mese di " + nomeMeseProduzione + " " + annoProduzione, () -> {

        });

        m.aggiungi(10, "Aggiungi un nuovo luogo all'elenco", () -> {

        });

        m.aggiungi(11, "Elimina un luogo", () -> {

        });

        m.aggiungi(12, "Aggiungi una o più visite ad un luogo già esistente", () -> {

        });

        m.aggiungi(13, "Aggiungi uno o più volontari ad una visita", () -> {});

        m.aggiungi(14, "Elimina una visita associata ad un luogo", () -> {});

        m.aggiungi(15, "Elimina un volontario", () -> {

        });

        return m;
    }

    @Override
    public void primaInizializzazione() {
        applicazione.setAmbitoTerritoriale(InputManager.richiediAmbitoTerritorialeApplicazione());
        applicazione.setNumeroMassimoIscrivibili(InputManager.richiediNumeroMassimoIscrivibili());
        applicazione.aggiungiLuoghi(InputManager.chiediLuoghi(this.applicazione));
        applicazione.setDaConfigurare(false);
    }
}
