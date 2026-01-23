package it.unibs.ingdsw.view.cli.menu;

import it.unibs.ingdsw.model.applicazione.*;
import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.service.*;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.view.cli.io.OutputManager;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;

public class MenuVolontario extends MenuManager {

    private final ServiceDate serviceDate;
    private final ServiceApplicazione serviceApplicazione;
    private final ServiceVisite serviceVisite;
    private final ServiceAppuntamenti serviceAppuntamenti;
    private final ServicePrenotazione servicePrenotazione;

    public MenuVolontario(Applicazione applicazione, Utente utente) {
        super(applicazione, utente);
        this.serviceDate = new ServiceDate(applicazione);
        this.serviceApplicazione = new ServiceApplicazione(applicazione);
        this.serviceVisite = new ServiceVisite(applicazione);
        this.serviceAppuntamenti = new ServiceAppuntamenti(applicazione);
        this.servicePrenotazione = new ServicePrenotazione(applicazione);
    }
    @Override
    public void inizializza() {}

    @Override
    public Menu creaMenu() {
        Menu m = new Menu("Menu Volontario");

        Target targetApplicazione = new Target();
        YearMonth targetDisponibilita = serviceApplicazione.getNextDisponibilita();
        YearMonth targetProduzione = targetApplicazione.calcolaDataTarget(TargetTipo.PRODUZIONE);

        String nomeMeseDisponibilita = Data.returnNomeMese(targetDisponibilita);
        int annoDisponibilita = Data.returnAnno(targetDisponibilita);
        int meseDisponibilita = Data.returnMese(targetDisponibilita);
        String nomeMeseProduzione = Data.returnNomeMese(targetProduzione);
        int annoProduzione = Data.returnAnno(targetProduzione);

        m.aggiungi(1, "Visualizza le visite a cui sei stato associato", () -> {
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
                OutputManager.visualizzaAppuntamentiPerStato(servicePrenotazione.getPrenotazioni(), serviceAppuntamenti.getAppuntamentiDellUtente((Volontario) this.utente), new StatoAppuntamento[] {StatoAppuntamento.CONFERMATA, StatoAppuntamento.CANCELLATA});
            } else {
                System.out.println("Non è possibile visualizzare le tue visite confermate: è necessario produrre prima il piano delle visite per il mese " +
                        nomeMeseProduzione + " " + annoProduzione + " da parte del Configuratore.");
            }
        });

        return m;
    }

}
