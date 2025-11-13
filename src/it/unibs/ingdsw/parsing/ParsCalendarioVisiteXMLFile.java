package it.unibs.ingdsw.parsing;

import it.unibs.ingdsw.luoghi.Luogo;
import it.unibs.ingdsw.luoghi.ListaLuoghi;
import it.unibs.ingdsw.tempo.Data;
import it.unibs.ingdsw.tempo.InsiemeDate;
import it.unibs.ingdsw.visite.Visita;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.HashMap;


public class ParsCalendarioVisiteXMLFile {

    private static final String DATA = "src/it/unibs/ingdsw/parsing/file/calendarioVisite.xml";

    private String mese;
    private final HashMap<Visita, InsiemeDate> calendarioVisite = new HashMap<>();

    public HashMap<Visita, InsiemeDate> getCalendarioVisite() {return this.calendarioVisite;}

    private void parseXML(ListaLuoghi listaLuoghi) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        File xmlFile = new File(DATA);
        if (!xmlFile.exists()) {
            // file assente: lascia mappa vuota
            return;
        }

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"calendarioVisite".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <calendarioVisite> attesa, trovata <" + root.getTagName() + ">.");
        }

        NodeList nVisite = root.getElementsByTagName("visita");
        for (int i = 0; i < nVisite.getLength(); i++) {
            Element eVis = (Element) nVisite.item(i);
            String titolo = eVis.getAttribute("titolo");
            String luogoID = eVis.getAttribute("luogoID");

            if (titolo == null || titolo.isBlank() || luogoID == null || luogoID.isBlank()) continue;

            Visita visita = resolveVisita(listaLuoghi, titolo, luogoID);
            if (visita == null) {
                System.err.println("Avviso: visita non trovata nell'app (titolo=\"" + titolo + "\", luogoID=\"" + luogoID + "\").");
                continue;
            }

            InsiemeDate ins = parseDateBlock(getFirst(eVis, "date"));
            calendarioVisite.put(visita, ins);
        }
    }

    private InsiemeDate parseDateBlock(Element eDate) {
        InsiemeDate ins = new InsiemeDate();
        if (eDate == null) return ins;
        NodeList datas = eDate.getElementsByTagName("data");
        for (int j = 0; j < datas.getLength(); j++) {
            Element e = (Element) datas.item(j);
            Data d = parseData(e);
            if (!ins.aggiungiData(d)) {
                System.out.println("La data " + d + " è già presente per la visita.");
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

    /**
     * Cerca la Visita in base a titolo + luogoID nella lista luoghi dell'app.
     */
    private Visita resolveVisita(ListaLuoghi listaLuoghi, String titolo, String luogoID) {
        if (listaLuoghi == null) return null;
        for (Luogo l : listaLuoghi.getListaLuoghi()) {
            if (!luogoID.equalsIgnoreCase(l.getLuogoID())) continue;
            for (Visita v : l.getInsiemeVisite().getListaVisite()) {
                if (v.getTitolo() != null && v.getTitolo().equalsIgnoreCase(titolo)) {
                    return v;
                }
            }
        }
        return null;
    }


    public static void salvaCalendarioVisite(HashMap<Visita, InsiemeDate> mappa) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("calendarioVisite");
            doc.appendChild(root);

            if (mappa != null) {
                for (var entry : mappa.entrySet()) {
                    Visita visita = entry.getKey();
                    InsiemeDate ins = entry.getValue();

                    Element eVis = doc.createElement("visita");
                    eVis.setAttribute("titolo", visita != null && visita.getTitolo() != null ? visita.getTitolo() : "");
                    eVis.setAttribute("luogoID", visita != null && visita.getLuogoID() != null ? visita.getLuogoID() : "");
                    root.appendChild(eVis);

                    Element eDate = doc.createElement("date");
                    eVis.appendChild(eDate);

                    if (ins != null) {
                        for (Data d : ins.getInsiemeDate()) {
                            Element eData = doc.createElement("data");
                            appendText(doc, eData, "giorno", String.valueOf(d.getGiorno()));
                            appendText(doc, eData, "mese",  String.valueOf(d.getMese()));
                            appendText(doc, eData, "anno",  String.valueOf(d.getAnno()));
                            eDate.appendChild(eData);
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
            System.err.println("Errore salvataggio calendarioVisite.xml: " + e.getMessage());
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
