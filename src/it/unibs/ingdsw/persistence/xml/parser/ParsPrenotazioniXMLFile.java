package it.unibs.ingdsw.persistence.xml.parser;

import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.utenti.Fruitore;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.prenotazione.Prenotazione;
import it.unibs.ingdsw.model.visite.Visita;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ParsPrenotazioniXMLFile {

    private static final String DATA = "prenotazioni.xml";

    private final ArrayList<Appuntamento> appuntamenti;
    private final ArrayList<Prenotazione> prenotazioni = new ArrayList<>();

    public ParsPrenotazioniXMLFile(ArrayList<Appuntamento> appuntamenti) {
        this.appuntamenti = appuntamenti;

        try {
            parseXML();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Errore nel parsing XML prenotazioni: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ArrayList<Prenotazione> getPrenotazioni() {
        return new ArrayList<>(prenotazioni);
    }

    private void parseXML()
            throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        File xmlFile = new File(DATA);
        if (!xmlFile.exists()) {
            System.err.println("File prenotazioni XML non trovato -> " + xmlFile.getAbsolutePath());
            return; // nessun file: nessuna prenotazione
        }

        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"prenotazioni".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <prenotazioni> attesa, trovata <" + root.getTagName() + ">.");
        }

        NodeList nl = root.getElementsByTagName("prenotazione");
        for (int i = 0; i < nl.getLength(); i++) {
            Element ePren = (Element) nl.item(i);

            String codicePren = getText(ePren, "codicePrenotazione", null);

            Element eVisita = getFirst(ePren, "visita");
            String luogoID = getText(eVisita, "luogoID", null);
            String titoloVisita = getText(eVisita, "titolo", null);

            Data data = parseData(getFirst(ePren, "data"));

            Appuntamento appuntamento = resolveAppuntamento(luogoID, titoloVisita, data);
            if (appuntamento == null) {
                System.err.println("Appuntamento non trovato per prenotazione: luogoID=" +
                        luogoID + ", titolo=" + titoloVisita + ", data=" + data);
                continue; // salto questa prenotazione
            }

            Element eFruitore = getFirst(ePren, "fruitore");
            String usernameFruitore = (eFruitore != null) ? eFruitore.getAttribute("username") : null;
            Fruitore fruitore = new Fruitore(usernameFruitore, null); // oggetto minimale

            int numPersone = getInt(ePren, "numeroPersonePerPrenotazione", 1);

            Prenotazione p;
            if (codicePren != null && !codicePren.isEmpty()) {
                p = new Prenotazione(codicePren, appuntamento, fruitore, numPersone);
            } else {
                p = new Prenotazione(appuntamento, fruitore, numPersone);
            }

            prenotazioni.add(p);
        }
    }

    private Data parseData(Element e) {
        if (e == null) return new Data(1, 1, 1970);
        int g = getInt(e, "giorno", 1);
        int m = getInt(e, "mese", 1);
        int a = getInt(e, "anno", 1970);
        Data d = new Data(g, m, a);
        if (!d.dataValida()) System.err.println("Data non valida in prenotazioni: " + d);
        return d;
    }

    private Appuntamento resolveAppuntamento(String luogoID, String titoloVisita, Data data) {
        if (appuntamenti == null || data == null ||
                luogoID == null || titoloVisita == null) return null;

        for (Appuntamento app : appuntamenti) {
            Visita v = app.getVisita();
            if (v == null) continue;

            String luogoApp = v.getLuogoID();
            String titoloApp = v.getTitolo();
            Data dataApp = app.getData();

            boolean stessoLuogo = (luogoApp == null && luogoID == null)
                    || (luogoApp != null && luogoApp.equals(luogoID));
            boolean stessoTitolo = (titoloApp == null && titoloVisita == null)
                    || (titoloApp != null && titoloApp.equals(titoloVisita));
            boolean stessaData =
                    dataApp.getGiorno() == data.getGiorno() &&
                            dataApp.getMese() == data.getMese() &&
                            dataApp.getAnno() == data.getAnno();

            if (stessoLuogo && stessoTitolo && stessaData) {
                return app;
            }
        }
        return null;
    }


    public static void salvaPrenotazioni(ArrayList<Prenotazione> prenotazioni) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("prenotazioni");
            doc.appendChild(root);

            if (prenotazioni != null) {
                for (Prenotazione p : prenotazioni) {
                    appendPrenotazione(doc, root, p);
                }
            }

            File outFile = new File(DATA);
            if (outFile.getParentFile() != null) {
                outFile.getParentFile().mkdirs();
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.transform(new DOMSource(doc), new StreamResult(outFile));

        } catch (Exception e) {
            System.err.println("Errore salvataggio prenotazioni.xml: " + e.getMessage());
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
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    private static void appendText(Document doc, Element parent, String tag, String text) {
        Element e = doc.createElement(tag);
        if (text != null) e.setTextContent(text);
        parent.appendChild(e);
    }

    private static void appendData(Document doc, Element parent, String tag, Data d) {
        Element e = doc.createElement(tag);
        parent.appendChild(e);
        if (d == null) d = new Data(1, 1, 1970);
        appendText(doc, e, "giorno", String.valueOf(d.getGiorno()));
        appendText(doc, e, "mese", String.valueOf(d.getMese()));
        appendText(doc, e, "anno", String.valueOf(d.getAnno()));
    }

    private static void appendPrenotazione(Document doc, Element root, Prenotazione p) {
        Element ePren = doc.createElement("prenotazione");
        root.appendChild(ePren);

        Appuntamento app = p.getAppuntamento();
        Visita v = (app != null) ? app.getVisita() : null;
        Data d = (app != null) ? app.getData() : null;
        Fruitore f = p.getUtenteChePrenota();

        appendText(doc, ePren, "codicePrenotazione",
                p.getCodicePrenotazione() != null ? p.getCodicePrenotazione() : "");

        Element eVisita = doc.createElement("visita");
        ePren.appendChild(eVisita);
        if (v != null) {
            appendText(doc, eVisita, "luogoID",
                    v.getLuogoID() != null ? v.getLuogoID() : "");
            appendText(doc, eVisita, "titolo",
                    v.getTitolo() != null ? v.getTitolo() : "");
        }

        appendData(doc, ePren, "data", d);

        Element eFruitore = doc.createElement("fruitore");
        eFruitore.setAttribute("username",
                f != null && f.getUsername() != null ? f.getUsername() : "");
        ePren.appendChild(eFruitore);

        appendText(doc, ePren, "numeroPersonePerPrenotazione",
                String.valueOf(p.getNumeroPersonePerPrenotazione()));
    }
}
