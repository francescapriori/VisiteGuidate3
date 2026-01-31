package it.unibs.ingdsw.view.format;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.luoghi.Posizione;
import it.unibs.ingdsw.model.prenotazione.Prenotazione;
import it.unibs.ingdsw.model.tempo.*;
import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class Formatters {

    private static <T> String join(Collection<T> items, String separatore, Function<T, String> formatter) {
        if (items == null || items.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (T item : items) {
            if (i++ > 0) sb.append(separatore);
            sb.append(formatter.apply(item));
        }
        return sb.toString();
    }

    public static String data(Data data) {
        return String.format("%02d/%02d/%04d", data.getGiorno(), data.getMese(), data.getAnno());
    }

    public static String insiemeDate(InsiemeDate insieme) {
        return join(insieme.getInsiemeDate(), ", ", Formatters::data);
    }

    public static String giornoSettimana(GiornoSettimana giornoSettimana) {
        String nome = giornoSettimana.name().toLowerCase();
        nome = nome.substring(0, 1).toUpperCase() + nome.substring(1);
        return String.format("%s", nome);
    }

    public static String insiemeGiorni(Giornate giornate) {
        return join(giornate.getGiornate(), ", ", Formatters::giornoSettimana);
    }

    public static String orario(Orario orario) {
        return String.format("%02d:%02d", orario.getOra(), orario.getMinuti());
    }

    public static String utente(Utente utente) {
        return String.format("%s", utente.getUsername());
    }

    public static String listaUtenti(ListaUtenti listaUtenti) {
        return join(listaUtenti.getListaUtenti(), ", ", Formatters::utente);
    }

    public static String volontariVisita(List<Volontario> volontari) {
        return join(volontari, ", ", Formatters::utente);
    }

    public static String visita(Visita visita) {
        StringBuilder sb = new StringBuilder();
        sb.append("\tTitolo della visita: ").append(visita.getTitolo())
                .append("\n\tDescrizione della visita: ").append(visita.getDescrizione())
                .append("\n\tLuogoID: ").append(visita.getLuogoID())
                .append("\n\tGiornate di visita: ").append(insiemeGiorni(visita.getGiornateVisita()))
                .append("\n\tData inizio validità visita: ").append(data(visita.getInizioValiditaVisita()))
                .append("\tData fine validità visita: ").append(data(visita.getFineValiditaVisita()))
                .append("\n\tOrario inizio visita: ").append(orario(visita.getOraInizioVisita()))
                .append("\tDurata in minuti: ").append(visita.getDurataMinutiVisita())
                .append("\n\tPresenza di un biglietto di ingresso: ").append(visita.isPresenzaBiglietto())
                .append("\n\tVolontari disponibili per la visita: ").append(volontariVisita(visita.getVolontariVisita()))
                .append("\n\tNumero minimo partecipanti: ").append(visita.getNumeroMinimoPartecipanti())
                .append("\tNumero massimo partecipanti: ").append(visita.getNumeroMassimoPartecipanti())
                .append("\n\tStato: ").append(visita.getStatoVisita());//
        return sb.toString();
    }

    public static String visitaBase(Visita visita) {
        return String.format("%s - %s", visita.getTitolo(), visita.getDescrizione());
    }

    public static String listaVisite(ListaVisite listaVisite) {
        if (listaVisite == null) {
            return "";
        }
        return join(listaVisite.getListaVisite(), "\n", Formatters::visita);
    }

    public static String appuntamento(Appuntamento appuntamento) {
        return String.format(appuntamento.getVisita().getTitolo() + "\t-\t"
                + data(appuntamento.getData()) + " alle ore "
                + orario(appuntamento.getVisita().getOraInizioVisita())
                + "\t-\tguida: " + utente(appuntamento.getGuida()) + "\t-\tstato: "
                + appuntamento.getStatoVisita());
    }

    public static String calendarioAppuntamenti(InsiemeAppuntamenti calendarioVisite) {
        if (calendarioVisite == null || calendarioVisite.getAppuntamenti().isEmpty()) {
            return "Nessun appuntamento disponibile in questo stato";
        }
        return join(calendarioVisite.getAppuntamenti(), ", ", Formatters::appuntamento);
    }

    public static String creaPrenotazione(String data, int contatore) {
        return String.format("PR-%s-%04d", data, contatore);
    }

    public static String prenotazione(Prenotazione prenotazione) {
        return String.format("Codice: " + prenotazione.getCodicePrenotazione() +
                " - Numero persone: " + prenotazione.getNumeroPersonePerPrenotazione());
    }

    public static String applicazione(Applicazione applicazione) {
        return "Ambito territoriale di competenza: " + applicazione.getAmbitoTerritoriale() +
                "\nNumero massimo di persone iscrivibili per ogni prenotazione: " + applicazione.getNumeroMassimoIscrivibili() +
                "\nLuoghi Visitabili: " + listaLuoghi(applicazione.getListaLuoghi());
    }

    public static String posizione(Posizione posizione) {
        return String.format("%s, via %s - %s (LAT %f, LONG %f)", posizione.getPaese(), posizione.getVia(), posizione.getCap(), posizione.getLatitudine(), posizione.getLongitudine());
    }

    public static String luogo(Luogo luogo) {
        return String.format("[%s] %s - \n%s", luogo.getLuogoID(), luogo.getNome(), luogo.getDescrizione()) +
                "\n" + posizione(luogo.getPosizione()) +
                "\nVisite associate: \n" + listaVisite(luogo.getInsiemeVisite());
    }

    public static String listaLuoghi(ListaLuoghi listaLuoghi) {
        if (listaLuoghi == null || listaLuoghi.getListaLuoghi().isEmpty()) {
            return "";
        }
        return join(listaLuoghi.getListaLuoghi(), "\n", Formatters::luogo);
    }

    public static String soloLuogo(Luogo luogo){
        return String.format("%s - %s", luogo.getNome(), luogo.getDescrizione()) +
                "\n" + posizione(luogo.getPosizione());
    }

    public static String luogoBase(Luogo luogo){
        return String.format("%s - %s", luogo.getLuogoID(), luogo.getNome());
    }
}
