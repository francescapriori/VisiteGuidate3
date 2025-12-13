package it.unibs.ingdsw.parsing;

import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.utenti.ListaUtenti;
import it.unibs.ingdsw.utenti.Utente;
import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.*;
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

public class ParsAppuntamentiXMLFile {

    private static final String DATA = "src/it/unibs/ingdsw/parsing/file/appuntamenti.xml";

    private final ListaLuoghi listaLuoghi;
    private final ListaUtenti listaUtenti;

    private final ArrayList<Appuntamento> appuntamenti = new ArrayList<>();

    public ParsAppuntamentiXMLFile(ListaLuoghi listaLuoghi, ListaUtenti listaUtenti) {
        this.listaLuoghi = listaLuoghi;
        this.listaUtenti = listaUtenti;

        try {
            parseXML();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Errore nel parsing XML appuntamenti: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public CalendarioAppuntamenti getAppuntamenti() {
        return new CalendarioAppuntamenti(new ArrayList<>(appuntamenti));
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
            System.err.println("File appuntamenti XML non trovato -> " + xmlFile.getAbsolutePath());
            return; // nessun file: nessun appuntamento
        }

        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"appuntamenti".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <appuntamenti> attesa, trovata <" + root.getTagName() + ">.");
        }

        NodeList nl = root.getElementsByTagName("appuntamento");
        for (int i = 0; i < nl.getLength(); i++) {
            Element eApp = (Element) nl.item(i);

            Element eVisita = getFirst(eApp, "visita");
            String luogoID = getText(eVisita, "luogoID", null);
            String titoloVisita = getText(eVisita, "titolo", null);

            Visita visita = resolveVisita(luogoID, titoloVisita);
            if (visita == null) {
                System.err.println("Visita non trovata per appuntamento: luogoID=" +
                        luogoID + ", titolo=" + titoloVisita);
                continue;
            }

            Data data = parseData(getFirst(eApp, "data"));

            Element eGuida = getFirst(eApp, "guida");
            String usernameGuida = eGuida != null ? eGuida.getAttribute("username") : null;
            Volontario guida = resolveVolontario(usernameGuida);

            String statoStr = getText(eApp, "statoVisita", null);
            StatoVisita stato = StatoVisita.PROPOSTA;
            if (statoStr != null && !statoStr.isBlank()) {
                try {
                    stato = StatoVisita.valueOf(statoStr.trim().toUpperCase());
                } catch (IllegalArgumentException ex) {
                    System.err.println("StatoVisita non valido nel XML: '" + statoStr +
                            "', uso PROPOSTA come default.");
                }
            }

            int numeroPersonePrenotate = getInt(eApp, "numeroPersonePrenotate", 0);

            Appuntamento app = new Appuntamento(visita, data, guida);
            app.setStatoVisita(stato);
            app.setNumeroPersonePrenotate(numeroPersonePrenotate);

            appuntamenti.add(app);
        }
    }

    private Visita resolveVisita(String luogoID, String titoloVisita) {
        if (listaLuoghi == null || luogoID == null || titoloVisita == null) return null;

        for (Luogo l : listaLuoghi.getListaLuoghi()) {
            if (luogoID.equals(l.getLuogoID())) {
                ListaVisite lv = l.getInsiemeVisite();
                if (lv == null) continue;
                for (Visita v : lv.getListaVisite()) {
                    if (v.getTitolo() != null && v.getTitolo().equalsIgnoreCase(titoloVisita)) {
                        return v;
                    }
                }
            }
        }
        return null;
    }

    private Volontario resolveVolontario(String username) {
        if (username == null || username.isBlank()) return null;
        if (listaUtenti != null) {
            for (Utente u : listaUtenti.getListaUtenti()) {
                if (u instanceof Volontario v &&
                        v.getUsername() != null &&
                        v.getUsername().equalsIgnoreCase(username)) {
                    return v;
                }
            }
        }
        return new Volontario(username, null);
    }

    private Data parseData(Element e) {
        if (e == null) return new Data(1, 1, 1970);
        int g = getInt(e, "giorno", 1);
        int m = getInt(e, "mese", 1);
        int a = getInt(e, "anno", 1970);
        Data d = new Data(g, m, a);
        if (!d.dataValida()) System.err.println("Data non valida: " + d);
        return d;
    }

    public static void salvaAppuntamenti(ArrayList<Appuntamento> nuoviAppuntamenti) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            File outFile = new File(DATA);
            Document doc;
            Element root;

            if (outFile.exists()) {
                doc = db.parse(outFile);
                doc.getDocumentElement().normalize();
                root = doc.getDocumentElement();
                if (!"appuntamenti".equals(root.getTagName())) {
                    System.err.println("Root non valida in appuntamenti.xml, ricreo il file.");
                    doc = db.newDocument();
                    root = doc.createElement("appuntamenti");
                    doc.appendChild(root);
                }
            } else {
                doc = db.newDocument();
                root = doc.createElement("appuntamenti");
                doc.appendChild(root);
            }

            removeWhitespaceTextNodes(root);

            if (nuoviAppuntamenti != null) {
                for (Appuntamento app : nuoviAppuntamenti) {
                    Element esistente = findAppuntamentoElement(root, app);
                    if (esistente != null) {
                        Node parent = esistente.getParentNode();
                        parent.removeChild(esistente);
                        appendAppuntamento(doc, (Element) parent, app);
                    } else {
                        appendAppuntamento(doc, root, app);
                    }
                }
            }

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
            System.err.println("Errore salvataggio appuntamenti.xml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Element findAppuntamentoElement(Element root, Appuntamento app) {
        if (app == null || app.getVisita() == null || app.getData() == null) return null;

        NodeList nl = root.getElementsByTagName("appuntamento");
        for (int i = 0; i < nl.getLength(); i++) {
            Element eApp = (Element) nl.item(i);

            Element eVisita = getFirst(eApp, "visita");
            String luogoID = getText(eVisita, "luogoID", null);
            String titoloVisita = getText(eVisita, "titolo", null);

            Element eData = getFirst(eApp, "data");
            int g = getInt(eData, "giorno", -1);
            int m = getInt(eData, "mese", -1);
            int a = getInt(eData, "anno", -1);

            if (stessaChiave(app, luogoID, titoloVisita, g, m, a)) {
                return eApp;
            }
        }
        return null;
    }

    private static boolean stessaChiave(Appuntamento app,
                                        String luogoIDXml, String titoloXml,
                                        int gXml, int mXml, int aXml) {

        Visita v = app.getVisita();
        if (v == null || app.getData() == null) return false;

        String luogoIDApp = v.getLuogoID();
        String titoloApp = v.getTitolo();
        Data d = app.getData();

        boolean stessoLuogo = (luogoIDApp == null && luogoIDXml == null)
                || (luogoIDApp != null && luogoIDApp.equals(luogoIDXml));

        boolean stessoTitolo = (titoloApp == null && titoloXml == null)
                || (titoloApp != null && titoloApp.equals(titoloXml));

        boolean stessaData =
                d.getGiorno() == gXml &&
                        d.getMese() == mXml &&
                        d.getAnno() == aXml;

        return stessoLuogo && stessoTitolo && stessaData;
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


    private static void appendAppuntamento(Document doc, Element root, Appuntamento app) {
        Element eApp = doc.createElement("appuntamento");
        root.appendChild(eApp);

        Element eVisita = doc.createElement("visita");
        eApp.appendChild(eVisita);
        Visita v = app.getVisita();
        if (v != null) {
            appendText(doc, eVisita, "luogoID",
                    v.getLuogoID() != null ? v.getLuogoID() : "");
            appendText(doc, eVisita, "titolo",
                    v.getTitolo() != null ? v.getTitolo() : "");
        }

        appendData(doc, eApp, "data", app.getData());

        Element eGuida = doc.createElement("guida");
        Volontario guida = app.getGuida();
        eGuida.setAttribute("username",
                guida != null && guida.getUsername() != null
                        ? guida.getUsername()
                        : "");
        eApp.appendChild(eGuida);

        StatoVisita stato = app.getStatoVisita();
        appendText(doc, eApp, "statoVisita",
                stato != null ? stato.name() : StatoVisita.PROPOSTA.name());

        appendText(doc, eApp, "numeroPersonePrenotate",
                String.valueOf(app.getNumeroPersonePrenotate()));
    }

    private static void removeWhitespaceTextNodes(Node parent) {
        Node child = parent.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();
            if (child.getNodeType() == Node.TEXT_NODE &&
                    child.getTextContent().trim().isEmpty()) {
                parent.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceTextNodes(child);
            }
            child = next;
        }
    }
}
