package it.unibs.ingdsw.persistence.xml.reader;

import it.unibs.ingdsw.model.luoghi.*;
import it.unibs.ingdsw.model.tempo.*;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import org.w3c.dom.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;
import static it.unibs.ingdsw.persistence.xml.XmlDataCodec.*;

public class LuoghiXmlReader {
    private final File file;

    public LuoghiXmlReader(File file) {
        this.file = file;
    }

    public ListaLuoghi read() {
        ListaLuoghi listaLuoghi = new ListaLuoghi();
        if (!file.exists()) return listaLuoghi;

        try {
            Document doc = XmlDomIO.load(file, "luoghi");
            Element root = doc.getDocumentElement();

            NodeList nl = root.getElementsByTagName("luogo");
            for (int i = 0; i < nl.getLength(); i++) {
                Element eLuogo = (Element) nl.item(i);
                String id = eLuogo.getAttribute("id");
                String nome = text(eLuogo, "nome", "");
                String desc = text(eLuogo, "descrizione", "");

                Posizione pos = parsePosizione(firstChild(eLuogo, "posizione"));
                ListaVisite lv = parseVisite(firstChild(eLuogo, "visite"), id);

                listaLuoghi.aggiungiLuogo(new Luogo(id, nome, desc, pos, lv));
            }
        } catch (Exception ex) {
            System.err.println("Errore lettura luoghi: " + ex.getMessage());
        }

        return listaLuoghi;
    }

    private static Posizione parsePosizione(Element ePos) {
        if (ePos == null) return new Posizione(null, null, null, 0d, 0d);
        String paese = text(ePos, "paese", null);
        String via = text(ePos, "via", null);
        String cap = text(ePos, "cap", null);
        double lat = doubleValue(ePos, "lat", 0.0);
        double lon = doubleValue(ePos, "lon", 0.0);
        return new Posizione(paese, via, cap, lat, lon);
    }

    private static ListaVisite parseVisite(Element eVisite, String luogoId) {
        ListaVisite lv = new ListaVisite();
        if (eVisite == null) return lv;

        NodeList visite = eVisite.getElementsByTagName("visita");
        for (int i = 0; i < visite.getLength(); i++) {
            Element eV = (Element) visite.item(i);

            String titolo = text(eV, "titolo", "");
            String descr = text(eV, "descrizione", "");
            Posizione posV = parsePosizione(firstChild(eV, "posizione"));

            Giornate giornate = parseGiornate(firstChild(eV, "giornate"));

            Element eVal = firstChild(eV, "validita");
            Data inizio = readData(firstChild(eVal, "inizio"), new Data(1,1,1970));
            Data fine = readData(firstChild(eVal, "fine"), new Data(1,1,1970));

            Element eOra = firstChild(eV, "oraInizio");
            Orario ora = new Orario(intValue(eOra, "ora", 0), intValue(eOra, "minuti", 0));

            int durata = intValue(eV, "durataMinuti", 0);
            boolean bigl = boolValue(eV, "presenzaBiglietto", false);

            ArrayList<Volontario> vols = parseVolontari(firstChild(eV, "volontari"));

            int minP = intValue(eV, "numeroMinimoPartecipanti", 0);
            int maxP = intValue(eV, "numeroMassimoPartecipanti", 0);

            Visita visita = new Visita(titolo, descr, luogoId, posV, giornate, inizio, fine, ora, durata, bigl, vols, minP, maxP);
            lv.aggiungiVisita(visita);
        }

        return lv;
    }

    private static ArrayList<Volontario> parseVolontari(Element eVols) {
        ArrayList<Volontario> res = new ArrayList<>();
        if (eVols == null) return res;

        NodeList nl = eVols.getElementsByTagName("volontario");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            res.add(new Volontario(e.getAttribute("username"), e.getAttribute("password")));
        }
        return res;
    }

    private static Giornate parseGiornate(Element eG) {
        Giornate g = new Giornate();
        if (eG == null) return g;

        NodeList giorni = eG.getElementsByTagName("giorno");
        for (int i = 0; i < giorni.getLength(); i++) {
            Node n = giorni.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;

            String raw = n.getTextContent();
            if (raw == null) continue;

            String norm = raw.trim().toUpperCase(Locale.ITALIAN).replace('Ì', 'I'); // GIOVEDÌ -> GIOVEDI
            try {
                g.aggiungiGiornoDellaSettimana(GiornoSettimana.valueOf(norm));
            } catch (IllegalArgumentException ex) {
                System.err.println("GiornoSettimana non valido nel XML: '" + raw + "'");
            }
        }
        return g;
    }
}
