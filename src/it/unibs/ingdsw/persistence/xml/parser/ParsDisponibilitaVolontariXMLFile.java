package it.unibs.ingdsw.persistence.xml.parser;

import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.model.utenti.Volontario;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ParsDisponibilitaVolontariXMLFile {

    private static final String DATA = "disponibilitaVolontari.xml";

    private final HashMap<Volontario, InsiemeDate> disponibilitaPerVol = new HashMap<>();

    public ParsDisponibilitaVolontariXMLFile(ListaUtenti listaUtenti) {
        try {
            parseXML(listaUtenti);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Errore nel parsing XML disponibilitaVolontari: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public HashMap<Volontario, InsiemeDate> getDisponibilitaPerVol() {
        return disponibilitaPerVol;
    }

    private void parseXML(ListaUtenti listaUtenti)
            throws ParserConfigurationException, SAXException, IOException {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        File xmlFile = new File(DATA);
        if (!xmlFile.exists()) {
            System.err.println("File XML non trovato -> " + xmlFile.getAbsolutePath());
            return;
        }

        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"disponibilitaVolontari".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <disponibilitaVolontari> attesa, trovata <" + root.getTagName() + ">.");
        }

        NodeList nl = root.getElementsByTagName("volontario");
        for (int i = 0; i < nl.getLength(); i++) {
            Element eVol = (Element) nl.item(i);
            String username = eVol.getAttribute("username");
            if (username == null || username.isBlank()) continue;

            Volontario v = resolveVolontario(listaUtenti, username);
            InsiemeDate insieme = parseInsiemeDate(getFirst(eVol, "disponibilita"));
            disponibilitaPerVol.put(v, insieme);
        }
    }

    private Volontario resolveVolontario(ListaUtenti lista, String username) {
        if (lista != null) {
            for (Utente u : lista.getListaUtenti()) {
                if (u instanceof Volontario v &&
                        v.getUsername() != null &&
                        v.getUsername().equalsIgnoreCase(username)) {
                    return v;
                }
            }
        }
        return new Volontario(username, null);
    }

    private InsiemeDate parseInsiemeDate(Element eIns) {
        InsiemeDate ins = new InsiemeDate();
        if (eIns == null) return ins;
        NodeList dates = eIns.getElementsByTagName("data");
        for (int i = 0; i < dates.getLength(); i++) {
            Element e = (Element) dates.item(i);
            if(!ins.aggiungiData(parseData(e))){
                System.out.println("La data è già presente nell'elenco.");
            }
        }
        ins.ordinaDateCronologicamente();
        return ins;
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

    public static void salvaDisponibilitaVolontari(HashMap<Volontario, InsiemeDate> mappa) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("disponibilitaVolontari");
            doc.appendChild(root);

            if (mappa != null) {
                for (var entry : mappa.entrySet()) {
                    Volontario vol = entry.getKey();
                    InsiemeDate ins = entry.getValue();

                    Element eVol = doc.createElement("volontario");
                    eVol.setAttribute("username", vol != null && vol.getUsername() != null ? vol.getUsername() : "");
                    root.appendChild(eVol);

                    Element eDisp = doc.createElement("disponibilita");
                    eVol.appendChild(eDisp);

                    if (ins != null) {
                        for (Data d : ins.getInsiemeDate()) {
                            Element eData = doc.createElement("data");
                            eDisp.appendChild(eData);
                            appendText(doc, eData, "giorno", String.valueOf(d.getGiorno()));
                            appendText(doc, eData, "mese",  String.valueOf(d.getMese()));
                            appendText(doc, eData, "anno",  String.valueOf(d.getAnno()));
                        }
                    }
                }
            }

            File outFile = new File(DATA);
            if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.transform(new DOMSource(doc), new StreamResult(outFile));
        } catch (Exception e) {
            System.err.println("Errore salvataggio disponibilitaVolontari.xml: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Element getFirst(Element parent, String tag) {
        if (parent == null) return null;
        NodeList nl = parent.getElementsByTagName(tag);
        return (nl == null || nl.getLength() == 0) ? null : (Element) nl.item(0);
    }

    private static int getInt(Element parent, String tag, int def) {
        try {
            Element e = getFirst(parent, tag);
            String t = (e == null) ? null : e.getTextContent().trim();
            return (t == null || t.isEmpty()) ? def : Integer.parseInt(t);
        } catch (NumberFormatException ex) { return def; }
    }

    private static void appendText(Document doc, Element parent, String tag, String text) {
        Element e = doc.createElement(tag);
        if (text != null) e.setTextContent(text);
        parent.appendChild(e);
    }
}
