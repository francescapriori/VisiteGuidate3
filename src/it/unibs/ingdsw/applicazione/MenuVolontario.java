package it.unibs.ingdsw.applicazione;

import it.unibs.ingdsw.output.OutputManager;
import it.unibs.ingdsw.service.ServiceDate;
import it.unibs.ingdsw.service.ServiceVisite;
import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.utenti.Utente;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;

import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

public class MenuVolontario extends MenuManager {

    public static final int INIZIO_PERIODO_ESCLUSIONE_DATE = 13;

    public MenuVolontario(Applicazione applicazione, Utente u) {
        super(applicazione, u);
    }

    @Override
    public void primaInizializzazione() {}

    @Override
    public Menu creaMenu() {
        Menu m = new Menu("Menu Volontario");

        YearMonth targetDisponibilita;
        ServiceVisite serviceVisite = new ServiceVisite(applicazione);
        ServiceDate serviceDate = new ServiceDate(applicazione);


        // esattamente il 16 o dopo
        if (isDayAfterThreshold()==0 || isDayAfterThreshold()==1) {
            targetDisponibilita = calcolaDataTarget(3);
        } else { // prima del 16
            targetDisponibilita = calcolaDataTarget(2);
        }
        String nomeMeseDisponibilita = Data.returnNomeMese(targetDisponibilita);
        int annoDisponibilita = Data.returnAnno(targetDisponibilita);
        int meseDisponibilita = Data.returnMese(targetDisponibilita);

        m.aggiungi(1, "Visualizza le visite a cui sei stato associato: ", () -> {
            System.out.println("Volontario " + this.utente.toString() + " sei stato associato alle seguenti visite: ");
            ListaVisite lista = serviceVisite.visiteDelVolontario((Volontario) this.utente);
            OutputManager.visualizzaListaVisite(lista);
            if (lista.getListaVisite().isEmpty()) {
                System.out.println("Nessuna visita disponibile.");
            }
        });

        m.aggiungi(2, "Indica le tue disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita, () -> {
            InsiemeDate dateEscluse = serviceDate.getDateEscluse(meseDisponibilita, annoDisponibilita);
            OutputManager.visualizzaDatePerMeseAnno(dateEscluse, meseDisponibilita, annoDisponibilita, OutputManager.TipoRichiestaData.DISPONIBILITA);
            String nomeMese = Month.of(meseDisponibilita).getDisplayName(TextStyle.FULL, Locale.ITALIAN);
            InsiemeDate insiemePerVolontario = serviceDate.getDatePerVolontario(meseDisponibilita, annoDisponibilita, (Volontario) this.utente);
            System.out.println("Procedi inserendo le tue disponibilità per " + nomeMese + " " + annoDisponibilita);
            do {
                Data data;
                do {
                    int giorno = InputManager.chiediGiorno(meseDisponibilita, annoDisponibilita);
                    data = new Data(giorno, meseDisponibilita, annoDisponibilita);
                    if (!dateEscluse.isEmpty() && dateEscluse.dataPresente(data)) {
                        System.out.println("Non puoi dare disponibilità per " + data + " perché è una data esclusa.");
                    } else if (insiemePerVolontario.dataPresente(data)) {
                        System.out.println("Hai già dato disponibilità per " + data + ".");
                    }
                } while ((dateEscluse.dataPresente(data)) || insiemePerVolontario.dataPresente(data));

                if (!insiemePerVolontario.aggiungiData(data)) {
                    System.out.println("La data è già presente nell'elenco.");
                }
            } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un'altra data in cui sei disponibile?")));


//            if (this.applicazione.isDisponibilitaNext()) {
//                this.applicazione.chiediDateDisponibili(meseDisponibilita, annoDisponibilita, (Volontario) this.utente);
//            }
//            else {
//                System.out.println("La raccolta disponibilità per il mese di " + nomeMeseDisponibilita + " " + annoDisponibilita + " sono chiuse.");
//            } todo

        });

        return m;
    }

}
