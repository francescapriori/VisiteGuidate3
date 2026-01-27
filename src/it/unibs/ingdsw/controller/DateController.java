package it.unibs.ingdsw.controller;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.service.ServiceDate;
import it.unibs.ingdsw.view.cli.io.InputManager;
import it.unibs.ingdsw.view.cli.io.OutputManager;

public class DateController {
    
    private final ServiceDate serviceDate;
    private final Applicazione applicazione;

    public DateController() {
        applicazione = Applicazione.getApplicazione();
        this.serviceDate = new ServiceDate(Applicazione.getApplicazione().getDateEscluse());
    }
    
    public void indicaDateDaEscludere(int mesePerEsclusione, int annoPerEsclusione) {
        InsiemeDate dateEscluse = applicazione.getDateEscluse().getDatePerMeseAnno(mesePerEsclusione, annoPerEsclusione);
        OutputManager.visualizzaDatePerMeseAnno(dateEscluse, mesePerEsclusione, annoPerEsclusione, OutputManager.TipoRichiestaData.ESCLUSIONE);
        do {
            Data dataDaEscludere = new Data(chiediGiorno(mesePerEsclusione, annoPerEsclusione), mesePerEsclusione, annoPerEsclusione);
            if (!applicazione.getDateEscluse().aggiungiData(dataDaEscludere)) {
                OutputManager.visualizzaMessaggio("La data è già presente nell'elenco.");
            }
        } while ("sì".equals(InputManager.chiediSiNo("Vuoi aggiungere un'altra data da escludere?")));
        dateEscluse = applicazione.getDateEscluse().getDatePerMeseAnno(mesePerEsclusione, annoPerEsclusione);
        OutputManager.visualizzaDatePerMeseAnno(dateEscluse, mesePerEsclusione, annoPerEsclusione, OutputManager.TipoRichiestaData.ESCLUSIONE);
    }

    public static Data chiediData() {
        Data d = new Data();
        do {
            d.setGiorno(InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 31));
            d.setMese(InputManager.leggiInteroConMinMax("Inserisci il mese: ", 1, 12));
            d.setAnno(InputManager.leggiInteroConMin("Inserisci l'anno: ", 1970));
            if(!d.dataValida()) {
                OutputManager.visualizzaMessaggio("Errore: data invalida.");
            }
        } while(!d.dataValida());

        return d;
    }

    public static int chiediGiorno(int mese, int anno) {
        int giorno = 1;
        if (mese == 1 || mese == 3 || mese == 5 || mese == 7 || mese == 8 || mese == 10 || mese == 12) {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 31);
        }
        else if (Data.isBisestile(anno) && mese == 2) {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 29);
        }
        else if (mese == 2) {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 28);
        }
        else {
            giorno = InputManager.leggiInteroConMinMax("Inserisci il giorno: ", 1, 30);
        }
        return giorno;
    }
}
