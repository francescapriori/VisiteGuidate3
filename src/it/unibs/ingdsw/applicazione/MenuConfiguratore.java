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
import it.unibs.ingdsw.visite.Visita;

import java.time.YearMonth;
import java.util.HashMap;

public class MenuConfiguratore extends MenuManager {

    public MenuConfiguratore(Applicazione applicazione, Utente utente) {
        super(applicazione, utente);
    }

    @Override
    public Menu creaMenu() {
        Menu m = new Menu("Menu Configuratore");

        Target targetApplicazione = new Target();
        YearMonth targetDisponibilita, targetProduzione, targetPerEsclusione;
        targetDisponibilita = targetApplicazione.calcolaDataTargetDisponibilita();
        targetPerEsclusione = targetApplicazione.calcolaDataTargetEsclusione();
        targetProduzione = targetApplicazione.calcolaDataTargetProduzione();

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

        m.aggiungi(7, "Apri raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita, () -> {
            if(this.applicazione.getStato() == Stato.DISP_CHIUSE || this.applicazione.getStato() == Stato.PRODUZIONE) {
                System.out.println("Da ora è possibile raccogliere le disponibilità dei Volontari per il mese di "+ nomeMeseDisponibilita + " " + annoDisponibilita);
                this.applicazione.setStato(Stato.DISP_APERTE);
            }
            else {
                System.out.println("La raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita + " sono già in corso.");
            }
        });

        m.aggiungi(8, "Chiudi raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita, () -> {
            if(this.applicazione.getStato() == Stato.DISP_APERTE) {
                System.out.println("Hai chiuso la raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita);
                this.applicazione.setStato(Stato.DISP_CHIUSE);
            }
        });

        m.aggiungi(9, "Produci il piano delle visite per il mese di " + nomeMeseProduzione + " " + annoProduzione, () -> {
            if(this.applicazione.getStato() == Stato.DISP_CHIUSE) {

                HashMap<Visita, InsiemeDate> calendarioMensile = serviceApplicazione.produciVisitePerIlMese(meseProduzione, annoProduzione);

                OutputManager.visualizzaCalendario(calendarioMensile, nomeMeseProduzione, annoProduzione);
                if(!calendarioMensile.isEmpty()) {
                    this.applicazione.setCalendarioVisite(calendarioMensile); //viene sovrascritto tutte le volte
                }

                // forse nel metodo è necessario cambiare lo stato della visita in PIANIFICATA se rientra nel calendario definitivo, nel caso ha senso mostrare la data se è pianificata nello stato delle visite opzione 6
                this.applicazione.setStato(Stato.PRODUZIONE);
            }
            else {
                System.out.println("Non è possibile produrre il piano delle visite per il mese di " + nomeMeseProduzione + " " +
                        annoProduzione + " perchè è ancora aperta la raccolta disponibilità dei volontari per il mese di " +
                            nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(10, "Aggiungi un nuovo luogo all'elenco", () -> {
            if(this.applicazione.getStato() == Stato.PRODUZIONE) {
                serviceLuoghi.aggiungiLuoghiSeNonPresenti(InputManager.chiediLuoghi(this.applicazione), this.applicazione.getListaLuoghi());
            }
            else {
                System.out.println("Non è possibile aggiungere un nuovo luogo all'elenco: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(11, "Elimina un luogo", () -> {
            if(this.applicazione.getStato() == Stato.PRODUZIONE) {
                OutputManager.visualizzaLuoghi(serviceLuoghi.getListaLuoghi());
                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il luogo che si vuole rimuovere: ",
                        1, serviceLuoghi.getNumeroLuogo());
                Luogo luogo = serviceLuoghi.scegliLuogo(scelta);
                serviceLuoghi.rimuoviLuogo(luogo, this.applicazione);
                System.out.println("Hai eliminato il luogo  " + luogo.getNome() + " - " + luogo.getLuogoID());
            }
            else {
                System.out.println("Non è possibile rimuovere nessun luogo: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(12, "Aggiungi una o più visite ad un luogo già esistente", () -> {
            if(this.applicazione.getStato() == Stato.PRODUZIONE) {
                OutputManager.visualizzaLuoghi(serviceLuoghi.getListaLuoghi());
                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il luogo di cui si vuole aggiungere la/e visita/e: ",
                        1, serviceLuoghi.getNumeroLuogo());
                Luogo luogo = serviceLuoghi.scegliLuogo(scelta);

                serviceVisite.aggiungiVisite(luogo, InputManager.chiediVisite(luogo.getPosizione(), luogo.getLuogoID(), this.applicazione));

            }
            else {
                System.out.println("Non è possibile nessuna visita a nessun luogo: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(13, "Aggiungi uno o più volontari ad una visita", () -> {
            if(this.applicazione.getStato() == Stato.PRODUZIONE) {
                OutputManager.visualizzaLuoghiEvisite(this.applicazione.getListaLuoghi());
                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il luogo di cui si vuole selezionare la visita: ",
                        1, serviceLuoghi.getNumeroLuogo());
                Luogo luogo = serviceLuoghi.scegliLuogo(scelta);
                OutputManager.visualizzaListaVisite(luogo.getInsiemeVisite());
                int scelta2 = InputManager.leggiInteroConMinMax(
                        "\nSeleziona la visita a cui si vuole aggiungere un volontario: ",
                        1, serviceVisite.getNumeroVisita(luogo));
                Visita visita = serviceVisite.scegliVisita(luogo, scelta);

                //aggiungi Volontari alla Visita
                serviceVolontari.aggiungiVolontariAllaVisita(visita, InputManager.associaVolontariAvisita(this.applicazione));

            }
            else {
                System.out.println("Non è possibile aggiungere volontari a nessuna visita: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(14, "Elimina una visita associata ad un luogo", () -> {
            if(this.applicazione.getStato() == Stato.PRODUZIONE) {
                OutputManager.visualizzaLuoghiEvisite(this.applicazione.getListaLuoghi());
                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il luogo di cui si vuole selezionare la visita: ",
                        1, serviceLuoghi.getNumeroLuogo());
                Luogo luogo = serviceLuoghi.scegliLuogo(scelta);
                OutputManager.visualizzaListaVisite(luogo.getInsiemeVisite());
                int scelta2 = InputManager.leggiInteroConMinMax(
                        "\nSeleziona la visita che si vuole rimuovere: ",
                        1, serviceVisite.getNumeroVisita(luogo));
                Visita visita = serviceVisite.scegliVisita(luogo, scelta2);

                serviceVisite.rimuoviVisita(visita, luogo, this.applicazione);

                // Se un luogo rimane senza visite, allora il luogo viene rimosso
                if (luogo.luogoSenzaVisite()) {
                    serviceLuoghi.rimuoviLuogo(luogo, this.applicazione);
                    System.out.println("Hai eliminato il luogo  " + luogo.getNome() + " - " + luogo.getLuogoID() + " perchè non sono presenti visite associate.");
                }
            }
            else {
                System.out.println("Non è possibile rimuovere nessuna visita associata a nessun luogo: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        m.aggiungi(15, "Elimina un volontario", () -> {

            if(this.applicazione.getStato() == Stato.PRODUZIONE) {
                OutputManager.visualizzaSoloVolontari(this.applicazione);
                int scelta = InputManager.leggiInteroConMinMax(
                        "\nSeleziona il volontario che si vuole eliminare: ",
                        1, serviceVolontari.getNumeroVolontari()); //+1????? da verificare
                serviceVolontari.eliminaVolontari(this.applicazione, scelta);

                // se una visita risulta essere senza volontari viene rimossa
                // se un luogo rimane senza visite viene rimosso
                for(Luogo l : this.applicazione.getListaLuoghi().getListaLuoghi()) {
                    for(Visita v : l.getInsiemeVisite().getListaVisite()) {
                        if(v.visitaSenzaVolontari()) {
                            serviceVisite.rimuoviVisita(v, l, this.applicazione);
                        }
                        if(l.luogoSenzaVisite()) {
                            serviceLuoghi.rimuoviLuogo(l, this.applicazione);
                            System.out.println("Hai eliminato il luogo  " + l.getNome() + " - " + l.getLuogoID() + " perchè non sono presenti visite associate.");
                        }
                    }
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
    public void primaInizializzazione() {
        ServiceLuoghi serviceLuoghi = new ServiceLuoghi(applicazione);
        ServiceApplicazione serviceApplicazione = new ServiceApplicazione(applicazione);
        serviceApplicazione.setAmbitoTerritoriale(InputManager.richiediAmbitoTerritorialeApplicazione());
        serviceApplicazione.setNumeroMassimoIscrivibili(InputManager.richiediNumeroMassimoIscrivibili());
        serviceLuoghi.aggiungiLuoghiSeNonPresenti(InputManager.chiediLuoghi(this.applicazione), this.applicazione.getListaLuoghi());
        serviceApplicazione.setDaConfigurare(false);
    }
}
