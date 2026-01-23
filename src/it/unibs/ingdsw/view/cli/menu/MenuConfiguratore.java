package it.unibs.ingdsw.view.cli.menu;

import it.unibs.ingdsw.controller.ApplicazioneController;
import it.unibs.ingdsw.controller.LuogoController;
import it.unibs.ingdsw.controller.UtentiController;
import it.unibs.ingdsw.controller.VisiteController;
import it.unibs.ingdsw.model.applicazione.*;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.service.*;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.view.cli.io.OutputManager;

import java.time.YearMonth;
import java.util.HashMap;

public class MenuConfiguratore extends MenuManager {

    private final ServiceDate serviceDate;
    private final ServiceApplicazione serviceApplicazione;
    private final ServiceLuoghi serviceLuoghi;
    private final ServiceUtenti serviceVolontari;
    private final ServiceVisite serviceVisite;
    private final ServiceAppuntamenti serviceAppuntamenti;

    public MenuConfiguratore(Applicazione applicazione, Utente utente) {
        super(applicazione, utente);
        this.serviceDate = new ServiceDate(applicazione);
        this.serviceApplicazione = new ServiceApplicazione(applicazione);
        this.serviceLuoghi = new ServiceLuoghi(applicazione);
        this.serviceVolontari = new ServiceUtenti(applicazione);
        this.serviceVisite = new ServiceVisite(applicazione);
        this.serviceAppuntamenti = new ServiceAppuntamenti(applicazione);
    }
    @Override
    public Menu creaMenu() {
        Menu m = new Menu("Menu Configuratore");

        Target targetApplicazione = new Target();
        YearMonth targetDisponibilita, targetProduzione, targetPerEsclusione;

        targetPerEsclusione = targetApplicazione.calcolaDataTarget(TargetTipo.ESCLUSIONE);
        targetDisponibilita = serviceApplicazione.getNextDisponibilita();
        targetProduzione = targetApplicazione.calcolaDataTarget(TargetTipo.PRODUZIONE);

        String nomeMesePerEsclusione = Data.returnNomeMese(targetPerEsclusione);
        int annoPerEsclusione = Data.returnAnno(targetPerEsclusione);
        int mesePerEsclusione = Data.returnMese(targetPerEsclusione);
        String nomeMeseDisponibilita = Data.returnNomeMese(targetDisponibilita);
        int annoDisponibilita = Data.returnAnno(targetDisponibilita);
        String nomeMeseProduzione = Data.returnNomeMese(targetProduzione);
        int annoProduzione = Data.returnAnno(targetProduzione);
        int meseProduzione = Data.returnMese(targetProduzione);

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
            ApplicazioneController appController = new ApplicazioneController(serviceApplicazione);
            appController.modificaNumeroMassimoIscrivibili();
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
            System.out.println("-----\nLista de Luoghi registrati con relative visite: ");
            OutputManager.visualizzaLuoghiEvisite(serviceLuoghi.getListaLuoghi());

        });

        m.aggiungi(6, "Visualizza appuntamenti per stato", () -> {
            OutputManager.visualizzaAppuntamentiPerStato(serviceAppuntamenti.getAppuntamentiDelMeseTarget(meseProduzione, annoProduzione), true);
        });

        m.aggiungi(7, "Apri raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita, () -> {
            // la raccolta disponibilità non può essere riaperta dopo la produzione delle visite, ma può essere riaperta a partire dal mese dopo.
            if(serviceApplicazione.getStatoDisp() == StatoRichiestaDisponibilita.DISP_CHIUSE) {
                System.out.println("Da ora è possibile raccogliere le disponibilità dei Volontari per il mese di "+ nomeMeseDisponibilita + " " + annoDisponibilita);
                serviceApplicazione.setStatoDisp(StatoRichiestaDisponibilita.DISP_APERTE);
            }
            else {
                System.out.println("La raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita + " sono già in corso.");
            }
        });

        m.aggiungi(8, "Chiudi raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita, () -> {
            if(serviceApplicazione.getStatoDisp() == StatoRichiestaDisponibilita.DISP_APERTE) {
                System.out.println("Hai chiuso la raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita);
                serviceApplicazione.setStatoDisp(StatoRichiestaDisponibilita.DISP_CHIUSE);
                // incrementa mese-anno
                serviceApplicazione.setNextDisponibilita(targetDisponibilita.plusMonths(1));
            }
            else {
                System.out.println("Non è possibile chiudere la raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita + " perchè la raccolta disponibilità non è stata ancora aperta");
            }
        });

        m.aggiungi(9, "Produci il piano delle visite per il mese di " + nomeMeseProduzione + " " + annoProduzione, () -> {
            if(serviceApplicazione.getStatoProd() == StatoProduzioneVisite.NON_PRODOTTE) {

                InsiemeAppuntamenti calendario = serviceAppuntamenti.produciVisitePerIlMese(meseProduzione, annoProduzione);

                OutputManager.visualizzaCalendario(calendario, nomeMeseProduzione, annoProduzione);
                serviceAppuntamenti.salvaCalendario(calendario);

                this.applicazione.setStatoProduzione(StatoProduzioneVisite.PRODOTTE);
            }
            else {
                System.out.println("È stato già prodotto il piano delle visite per il mese di " + nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(10, "Aggiungi un nuovo luogo all'elenco", () -> {
            if(serviceApplicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
                UtentiController utentiController = new UtentiController(serviceVolontari);
                VisiteController visiteController = new VisiteController(serviceVisite, utentiController);
                LuogoController luogoController = new LuogoController(serviceLuoghi, visiteController);
                luogoController.aggiungiLuoghiSeNonPresenti();
            }
            else {
                System.out.println("Non è possibile aggiungere un nuovo luogo all'elenco: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(11, "Elimina un luogo", () -> {
            if(serviceApplicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
                if(!serviceLuoghi.getListaLuoghi().getListaLuoghi().isEmpty()) {
                    OutputManager.visualizzaLuoghi(serviceLuoghi.getListaLuoghi());
                    int scelta = InputManager.leggiInteroConMinMax(
                            "\nSeleziona il luogo che si vuole rimuovere: ",
                            1, serviceLuoghi.getNumeroLuogo());
                    Luogo luogo = serviceLuoghi.scegliLuogo(scelta);
                    serviceLuoghi.rimuoviLuogo(luogo);
                    System.out.println("Hai eliminato il luogo  " + luogo.getNome() + " - " + luogo.getLuogoID());

                    // Se un volontario rimane senza visite, allora viene eliminato
                    serviceVolontari.eliminaSeSenzaVisita();
                }
                else {
                    System.out.println("Nessun luogo registrato, impossibile rimuovere un luogo.");
                }

            }
            else {
                System.out.println("Non è possibile rimuovere nessun luogo: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(12, "Aggiungi una o più visite ad un luogo già esistente", () -> {
            if(serviceApplicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
                OutputManager.visualizzaLuoghi(serviceLuoghi.getListaLuoghi());
                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il luogo di cui si vuole aggiungere la/e visita/e: ",
                        1, serviceLuoghi.getNumeroLuogo());
                Luogo luogo = serviceLuoghi.scegliLuogo(scelta);
                UtentiController utentiController = new UtentiController(serviceVolontari);
                VisiteController visiteController = new VisiteController(serviceVisite, utentiController);
                visiteController.chiediVisite(luogo.getPosizione(), luogo.getLuogoID());

            }
            else {
                System.out.println("Non è possibile aggiungere nessuna visita a nessun luogo: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(13, "Aggiungi uno o più volontari ad una visita", () -> {
            if(serviceApplicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
                OutputManager.visualizzaLuoghiEvisite(serviceLuoghi.getListaLuoghi());
                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il luogo di cui si vuole selezionare la visita: ",
                        1, serviceLuoghi.getNumeroLuogo());
                Luogo luogo = serviceLuoghi.scegliLuogo(scelta);
                OutputManager.visualizzaListaVisite(luogo.getInsiemeVisite());
                int scelta2 = InputManager.leggiInteroConMinMax(
                        "\nSeleziona la visita a cui si vuole aggiungere un volontario: ",
                        1, serviceVisite.getNumeroVisita(luogo));
                Visita visita = serviceVisite.scegliVisita(luogo, scelta2);

                //aggiungi Volontari alla Visita
                UtentiController utentiController = new UtentiController(serviceVolontari);
                VisiteController visiteController = new VisiteController(serviceVisite, utentiController);
                serviceVolontari.aggiungiVolontariAllaVisita(visita, InputManager.associaVolontariAvisita(this.applicazione, visita));
            }
            else {
                System.out.println("Non è possibile aggiungere volontari a nessuna visita: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(14, "Elimina una visita associata ad un luogo", () -> {
            if(serviceApplicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
                if(!serviceLuoghi.getListaLuoghi().getListaLuoghi().isEmpty()) {
                    OutputManager.visualizzaLuoghiEvisite(serviceLuoghi.getListaLuoghi());
                    int scelta = InputManager.leggiInteroConMinMax(
                            "\nSeleziona il luogo di cui si vuole selezionare la visita: ",
                            1, serviceLuoghi.getNumeroLuogo());
                    Luogo luogo = serviceLuoghi.scegliLuogo(scelta);
                    OutputManager.visualizzaListaVisite(luogo.getInsiemeVisite());
                    int scelta2 = InputManager.leggiInteroConMinMax(
                            "\nSeleziona la visita che si vuole rimuovere: ",
                            1, serviceVisite.getNumeroVisita(luogo));
                    Visita visita = serviceVisite.scegliVisita(luogo, scelta2);

                    serviceVisite.rimuoviVisita(visita, luogo);

                    // Se un luogo rimane senza visite, allora il luogo viene rimosso
                    serviceLuoghi.rimuoviLuogoSeSenzaVisite();

                    // Se un volontario rimane senza visite, allora viene eliminato
                    serviceVolontari.eliminaSeSenzaVisita();
                }
                else {
                    System.out.println("Nessun luogo registrato, impossibile rimuovere una visita associata ad un luogo.");
                }

            }
            else {
                System.out.println("Non è possibile rimuovere nessuna visita associata a nessun luogo: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(15, "Elimina un volontario", () -> {

            if(serviceApplicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
                if(!serviceVolontari.getVolontari().isEmpty()) {
                    OutputManager.visualizzaSoloVolontari(this.applicazione);

                    int scelta = InputManager.leggiInteroConMinMax(
                            "\nSeleziona il volontario che si vuole eliminare: ",
                            1, serviceVolontari.getNumeroVolontari());
                    serviceVolontari.eliminaVolontari(scelta-1);

                    // se una visita rimane senza volontari viene rimossa
                    serviceVisite.eliminaSeSenzaVolontari();

                    // se un luogo rimane senza visite viene rimosso
                    serviceLuoghi.rimuoviLuogoSeSenzaVisite();
                }
                else {
                    System.out.println("Nessun volontario registrato, impossibile rimuovere un volontario.");
                }

            }
            else {
                System.out.println("Non è possibile rimuovere un volontario: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        return m;
    }

    @Override
    public void inizializza() {
        ApplicazioneController appController = new ApplicazioneController(serviceApplicazione);
        appController.configuraParametriBase();
        UtentiController utentiController = new UtentiController(serviceVolontari);
        VisiteController visiteController = new VisiteController(serviceVisite, utentiController);
        LuogoController luogoController = new LuogoController(serviceLuoghi, visiteController);
        luogoController.configuraLuoghi();
    }

}
