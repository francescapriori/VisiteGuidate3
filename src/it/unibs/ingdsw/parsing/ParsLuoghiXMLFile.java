package it.unibs.ingdsw.parsing;

import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.luoghi.Posizione;
import it.unibs.ingdsw.tempo.*;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.ListaVisite;
import it.unibs.ingdsw.visite.Visita;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class ParsLuoghiXMLFile {

    private static final String DATA = "src/it/unibs/ingdsw/parsing/file/luoghi.xml";

    private final ListaLuoghi listaLuoghi = new ListaLuoghi();

    public ParsLuoghiXMLFile() {
        try {
            parseXML();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Errore nel parsing XML luoghi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ListaLuoghi getListaLuoghi() {
        return listaLuoghi;
    }

    /* ===================== PARSE ===================== */

    private void parseXML() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        File xmlFile = new File(DATA);
        if (!xmlFile.exists()) {
            System.err.println("File luoghi XML non trovato -> " + xmlFile.getAbsolutePath());
            return;
        }

        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"luoghi".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <luoghi> attesa, trovata <" + root.getTagName() + ">.");
        }

        // <luogo>...
        NodeList nl = root.getElementsByTagName("luogo");
        for (int i = 0; i < nl.getLength(); i++) {
            Element eLuogo = (Element) nl.item(i);

            String id   = eLuogo.getAttribute("id");
            String nome = getText(eLuogo, "nome", "");
            String desc = getText(eLuogo, "descrizione", "");

            // posizione luogo
            Element ePos = getFirst(eLuogo, "posizione");
            Posizione pos = parsePosizione(ePos);

            // visite
            ListaVisite lv = parseVisite(getFirst(eLuogo, "visite"), id);

            listaLuoghi.aggiungiLuogo(new Luogo(id, nome, desc, pos, lv));
        }
    }

    private Posizione parsePosizione(Element ePos) {
        if (ePos == null) return new Posizione(null, null, null, 0d, 0d);
        String paese = getText(ePos, "paese", null);
        String via   = getText(ePos, "via", null);
        String cap   = getText(ePos, "cap", null);
        double lat   = getDouble(ePos, "lat", 0.0);
        double lon   = getDouble(ePos, "lon", 0.0);
        return new Posizione(paese, via, cap, lat, lon);
    }

    private ListaVisite parseVisite(Element eVisite, String luogoId) {
        ListaVisite lv = new ListaVisite();
        if (eVisite == null) return lv;

        NodeList visite = eVisite.getElementsByTagName("visita");
        for (int i = 0; i < visite.getLength(); i++) {
            Element eV = (Element) visite.item(i);

            String titolo = getText(eV, "titolo", "");
            String descr  = getText(eV, "descrizione", "");

            // posizione incontro della visita (può differire dal luogo)
            Posizione posV = parsePosizione(getFirst(eV, "posizione"));

            // giornate
            Giornate giornate = parseGiornate(getFirst(eV, "giornate"));

            // validità
            Element eVal = getFirst(eV, "validita");
            Data inizio  = parseData(getFirst(eVal, "inizio"));
            Data fine    = parseData(getFirst(eVal, "fine"));

            // orario
            Element eOra = getFirst(eV, "oraInizio");
            Orario ora   = new Orario(getInt(eOra, "ora", 0), getInt(eOra, "minuti", 0));

            int durata   = getInt(eV, "durataMinuti", 0);
            boolean bigl = getBoolean(eV, "presenzaBiglietto", false);

            // volontari
            ArrayList<Volontario> vols = parseVolontari(getFirst(eV, "volontari"));

            int minP = getInt(eV, "numeroMinimoPartecipanti", 0);
            int maxP = getInt(eV, "numeroMassimoPartecipanti", 0);

            Visita visita = new Visita(
                    titolo, descr, luogoId, posV, giornate,
                    inizio, fine, ora, durata, bigl, vols, minP, maxP
            );
            lv.aggiungiVisita(visita);
        }
        return lv;
    }

    private ArrayList<Volontario> parseVolontari(Element eVols) {
        ArrayList<Volontario> res = new ArrayList<>();
        if (eVols == null) return res;
        NodeList nl = eVols.getElementsByTagName("volontario");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            res.add(new Volontario(e.getAttribute("username"), e.getAttribute("password")));
        }
        return res;
    }

    private Giornate parseGiornate(Element eG) {
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

    private Data parseData(Element e) {
        if (e == null) return new Data(1,1,1970);
        int g = getInt(e, "giorno", 1);
        int m = getInt(e, "mese", 1);
        int a = getInt(e, "anno", 1970);
        Data d = new Data(g, m, a);
        if (!d.dataValida()) System.err.println("Data non valida: " + d);
        return d;
    }

    public static void salvaLuoghi(ListaLuoghi lista) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("luoghi");
            doc.appendChild(root);

            for (Luogo l : lista.getListaLuoghi()) {
                Element eLuogo = doc.createElement("luogo");
                eLuogo.setAttribute("id", l.getLuogoID());
                root.appendChild(eLuogo);

                appendText(doc, eLuogo, "nome", l.getNome());
                appendText(doc, eLuogo, "descrizione", l.getDescrizione());

                // posizione luogo
                Element ePos = doc.createElement("posizione");
                eLuogo.appendChild(ePos);
                Posizione p = l.getPosizione();
                if (p != null) {
                    appendText(doc, ePos, "paese", safe(p.getPaese()));
                    appendText(doc, ePos, "via",   safe(p.getVia()));
                    appendText(doc, ePos, "cap",   safe(p.getCap()));
                    appendText(doc, ePos, "lat",   String.valueOf(p.getLatitudine()));
                    appendText(doc, ePos, "lon",   String.valueOf(p.getLongitudine()));
                }

                // visite
                Element eVisite = doc.createElement("visite");
                eLuogo.appendChild(eVisite);
                ListaVisite lv = l.getInsiemeVisite();
                if (lv != null) {
                    for (Visita v : lv.getListaVisite()) {
                        Element eVisita = doc.createElement("visita");
                        eVisite.appendChild(eVisita);

                        appendText(doc, eVisita, "titolo", v.getTitolo());
                        appendText(doc, eVisita, "descrizione", v.getDescrizione());

                        // posizione incontro
                        Element ePosV = doc.createElement("posizione");
                        eVisita.appendChild(ePosV);
                        Posizione pV = v.getLuogoIncontro();
                        if (pV != null) {
                            appendText(doc, ePosV, "paese", safe(pV.getPaese()));
                            appendText(doc, ePosV, "via",   safe(pV.getVia()));
                            appendText(doc, ePosV, "cap",   safe(pV.getCap()));
                            appendText(doc, ePosV, "lat",   String.valueOf(pV.getLatitudine()));
                            appendText(doc, ePosV, "lon",   String.valueOf(pV.getLongitudine()));
                        }

                        // giornate
                        Element eGiornate = doc.createElement("giornate");
                        eVisita.appendChild(eGiornate);
                        Giornate g = v.getGiornateVisita();
                        if (g != null) {
                            for (GiornoSettimana gs : g.getGiornate()) {
                                String giorno = "GIORVEDI".equals(gs.name()) ? "GIOVEDI" : gs.name();
                                appendText(doc, eGiornate, "giorno", giorno);
                            }
                        }

                        // validità
                        Element eValidita = doc.createElement("validita");
                        eVisita.appendChild(eValidita);
                        appendData(doc, eValidita, "inizio", v.getInizioValiditaVisita());
                        appendData(doc, eValidita, "fine",   v.getFineValiditaVisita());

                        // ora
                        Element eOra = doc.createElement("oraInizio");
                        eVisita.appendChild(eOra);
                        Orario or = v.getOraInizioVisita();
                        appendText(doc, eOra, "ora",    String.valueOf(or != null ? or.getOra()    : 0));
                        appendText(doc, eOra, "minuti", String.valueOf(or != null ? or.getMinuti() : 0));

                        appendText(doc, eVisita, "durataMinuti", String.valueOf(v.getDurataMinutiVisita()));
                        appendText(doc, eVisita, "presenzaBiglietto", String.valueOf(v.isPresenzaBiglietto()));

                        // volontari
                        Element eVols = doc.createElement("volontari");
                        eVisita.appendChild(eVols);
                        var vols = v.getVolontariVisita();
                        if (vols != null) {
                            for (Volontario vol : vols) {
                                Element eV = doc.createElement("volontario");
                                eV.setAttribute("username", safe(vol.getUsername()));
                                eV.setAttribute("password", safe(vol.getPassword()));
                                eVols.appendChild(eV);
                            }
                        }

                        appendText(doc, eVisita, "numeroMinimoPartecipanti",
                                String.valueOf(v.getNumeroMinimoPartecipanti()));
                        appendText(doc, eVisita, "numeroMassimoPartecipanti",
                                String.valueOf(v.getNumeroMassimoPartecipanti()));
                    }
                }
            }

            // write file
            File out = new File(DATA);
            if (out.getParentFile() != null) out.getParentFile().mkdirs();

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.transform(new DOMSource(doc), new StreamResult(out));
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }


    private static Element getFirst(Element parent, String tag) {
        if (parent == null) return null;
        NodeList nl = parent.getElementsByTagName(tag);
        return (nl == null || nl.getLength() == 0) ? null : (Element) nl.item(0);
    }

    private static String getText(Element parent, String tag, String def) {
        Element e = getFirst(parent, tag);
        return (e == null) ? def : e.getTextContent().trim();
    }

    private static int getInt(Element parent, String tag, int def) {
        try {
            String t = getText(parent, tag, null);
            return (t == null || t.isEmpty()) ? def : Integer.parseInt(t);
        } catch (NumberFormatException ex) { return def; }
    }

    private static double getDouble(Element parent, String tag, double def) {
        try {
            String t = getText(parent, tag, null);
            if (t == null || t.isEmpty()) return def;
            return Double.parseDouble(t.replace(',', '.'));
        } catch (NumberFormatException ex) { return def; }
    }

    private static boolean getBoolean(Element parent, String tag, boolean def) {
        String t = getText(parent, tag, null);
        return (t == null || t.isEmpty()) ? def : Boolean.parseBoolean(t);
    }

    private static void appendText(Document doc, Element parent, String tag, String text) {
        Element e = doc.createElement(tag);
        if (text != null) e.setTextContent(text);
        parent.appendChild(e);
    }

    private static void appendData(Document doc, Element parent, String tag, Data d) {
        Element e = doc.createElement(tag);
        parent.appendChild(e);
        if (d == null) d = new Data(1,1,1970);
        appendText(doc, e, "giorno", String.valueOf(d.getGiorno()));
        appendText(doc, e, "mese",   String.valueOf(d.getMese()));
        appendText(doc, e, "anno",   String.valueOf(d.getAnno()));
    }

    private static String safe(String s) { return s == null ? "" : s; }
}
