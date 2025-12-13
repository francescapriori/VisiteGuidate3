package it.unibs.ingdsw.menu;

import it.unibs.ingdsw.applicazione.Applicazione;
import it.unibs.ingdsw.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.applicazione.StatoRichiestaDisponibilita;
import it.unibs.ingdsw.applicazione.Target;
import it.unibs.ingdsw.inputOutput.*;
import it.unibs.ingdsw.service.ServiceApplicazione;
import it.unibs.ingdsw.service.ServiceDate;
import it.unibs.ingdsw.service.ServicePrenotazione;
import it.unibs.ingdsw.service.ServiceVisite;
import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.utenti.Utente;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;
import it.unibs.ingdsw.visite.StatoVisita;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class MenuVolontario extends MenuManager {

    public MenuVolontario(Applicazione applicazione, Utente u) {
        super(applicazione, u);
    }

    @Override
    public void primaInizializzazione() {}

    @Override
    public Menu creaMenu() {
        Menu m = new Menu("Menu Volontario");

        Target targetApplicazione = new Target();
        ServiceVisite serviceVisite = new ServiceVisite(applicazione);
        ServiceDate serviceDate = new ServiceDate(applicazione);
        ServicePrenotazione servicePrenotazione = new ServicePrenotazione(applicazione);
        ServiceApplicazione serviceApplicazione = new ServiceApplicazione(applicazione);
        YearMonth targetDisponibilita = serviceApplicazione.getNextDisponibilita();
        YearMonth targetProduzione = targetApplicazione.calcolaDataTargetProduzione();

        String nomeMeseDisponibilita = Data.returnNomeMese(targetDisponibilita);
        int annoDisponibilita = Data.returnAnno(targetDisponibilita);
        int meseDisponibilita = Data.returnMese(targetDisponibilita);
        String nomeMeseProduzione = Data.returnNomeMese(targetProduzione);
        int annoProduzione = Data.returnAnno(targetProduzione);
        int meseProduzione = Data.returnMese(targetProduzione);

        m.aggiungi(1, "Visualizza le visite a cui sei stato associato: ", () -> {
            System.out.println("Volontario " + this.utente.toString() + " sei stato associato alle seguenti visite: ");
            ListaVisite lista = serviceVisite.visiteDelVolontario((Volontario) this.utente);
            OutputManager.visualizzaListaVisite(lista);
            if (lista.getListaVisite().isEmpty()) {
                System.out.println("Nessuna visita disponibile.");
            }
        });

        m.aggiungi(2, "Indica le tue disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita, () -> {
            if(serviceApplicazione.getStatoDisp()== StatoRichiestaDisponibilita.DISP_APERTE) {
                InsiemeDate dateEscluse = serviceDate.getDateEscluse(meseDisponibilita, annoDisponibilita);
                OutputManager.visualizzaDatePerMeseAnno(dateEscluse, meseDisponibilita, annoDisponibilita, OutputManager.TipoRichiestaData.ESCLUSIONE);
                String nomeMese = Month.of(meseDisponibilita).getDisplayName(TextStyle.FULL, Locale.ITALIAN);
                InsiemeDate insiemePerVolontario = serviceDate.getDatePerVolontario(meseDisponibilita, annoDisponibilita, (Volontario) this.utente);
                insiemePerVolontario = insiemePerVolontario.filtraDateDopo(new Data(Target.SOGLIA_CAMBIO_MESE, meseDisponibilita, annoDisponibilita)); // le date restituite sono solo quelle del mese di interesse
                OutputManager.visualizzaDatePerMeseAnno(insiemePerVolontario, meseDisponibilita, annoDisponibilita, OutputManager.TipoRichiestaData.DISPONIBILITA);
                System.out.println("Procedi inserendo le tue disponibilità per " + nomeMese + " " + annoDisponibilita);
                do {
                    Data data;
                    do {
                        int giorno = InputManager.chiediGiorno(meseDisponibilita, annoDisponibilita);
                        data = new Data(giorno, meseDisponibilita, annoDisponibilita);
                        if (!dateEscluse.getInsiemeDate().isEmpty() && dateEscluse.dataPresente(data)) {
                            System.out.println("Non puoi dare disponibilità per " + data + " perché è una data esclusa.");
                        } else if (insiemePerVolontario.dataPresente(data)) {
                            System.out.println("Hai già dato disponibilità per " + data + ".");
                        }
                    } while ((dateEscluse.dataPresente(data)) || insiemePerVolontario.dataPresente(data));

                    if (!insiemePerVolontario.aggiungiData(data)) {
                        System.out.println("La data è già presente nell'elenco.");
                    }
                } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un'altra data in cui sei disponibile?")));
            }
            else {
                System.out.println("La raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita + " sono chiuse.");
            }
        });
        m.aggiungi(3, "Visualizza gli appuntamenti confermati e cancellati a cui sei stato associato per il mese di " + nomeMeseProduzione + " " + annoProduzione, () -> {
            if (serviceApplicazione.getStatoProd() == StatoProduzioneVisite.PRODOTTE) {
                OutputManager.visualizzaAppuntamentiPerStato(servicePrenotazione.getPrenotazioni(), serviceApplicazione.getAppuntamentiDellUtente((Volontario) this.utente), new StatoVisita[] {StatoVisita.CONFERMATA, StatoVisita.CANCELLATA});
            } else {
                System.out.println("Non è possibile visualizzare le tue visite confermate: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione);
            }
        });

        return m;
    }

}
