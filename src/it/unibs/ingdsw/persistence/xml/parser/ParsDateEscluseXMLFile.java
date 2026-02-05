package it.unibs.ingdsw.persistence.xml.parser;

import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class ParsDateEscluseXMLFile {

    private static final String DATA = "dateEscluse.xml";

    private final InsiemeDate dateEscluse = new InsiemeDate();

    public ParsDateEscluseXMLFile() {
        try {
            parseXML();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Errore nel parsing XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public InsiemeDate getInsiemeDate() {
        return this.dateEscluse;
    }

    private void parseXML() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = XmlDocumentBuilderProvider.newSecureBuilder();
        File xmlFile = new File(DATA);
        if (!xmlFile.exists()) {
            System.err.println("Errore: file XML non trovato: " + xmlFile.getAbsolutePath());
            return;
        }

        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"insiemeDateEscluse".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <insiemeDateEscluse> attesa.");
        }

        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (!(n instanceof Element)) continue;
            Element e = (Element) n;
            if (!"data".equalsIgnoreCase(e.getTagName())) {
                System.err.println("Tag non riconosciuto sotto <insiemeDateEscluse>: <" + e.getTagName() + ">");
                continue;
            }

            Data d = parseDataElement(e);
            if (d != null) {
                if(!dateEscluse.aggiungiData(d)){
                    System.out.println("La data è già presente nell'elenco.");
                }
            }
        }

        dateEscluse.ordinaDateCronologicamente();
    }

    private static Data parseDataElement(Element eData) {
        String gTxt = XmlElementReader.getText(eData, "giorno", null);
        String mTxt = XmlElementReader.getText(eData, "mese", null);
        String aTxt = XmlElementReader.getText(eData, "anno", null);

        if (gTxt == null) gTxt = eData.getAttribute("giorno");
        if (mTxt == null) mTxt = eData.getAttribute("mese");
        if (aTxt == null) aTxt = eData.getAttribute("anno");

        if (XmlElementReader.isBlank(gTxt) ||
                XmlElementReader.isBlank(mTxt) ||
                XmlElementReader.isBlank(aTxt)) {
            System.err.println("Data con elementi mancanti: giorno='" + gTxt + "', mese='" + mTxt + "', anno='" + aTxt + "'");
            return null;
        }

        try {
            int g = Integer.parseInt(gTxt.trim());
            int m = Integer.parseInt(mTxt.trim());
            int a = Integer.parseInt(aTxt.trim());
            return new Data(g, m, a);
        } catch (NumberFormatException ex) {
            System.err.println("Numeri data non validi: g='" + gTxt + "', m='" + mTxt + "', a='" + aTxt + "'");
            return null;
        }
    }

    public static void salvaListaDate(InsiemeDate lista) {
        try {
            Document doc = XmlDocumentBuilderProvider.newDocument();

            Element root = doc.createElement("insiemeDateEscluse");
            doc.appendChild(root);

            if (lista != null) {
                for (Data d : lista.getInsiemeDate()) {
                    Element eData = doc.createElement("data");
                    root.appendChild(eData);

                    XmlElementWriter.appendText(doc, eData, "giorno", Integer.toString(d.getGiorno()));
                    XmlElementWriter.appendText(doc, eData, "mese",  Integer.toString(d.getMese()));
                    XmlElementWriter.appendText(doc, eData, "anno",  Integer.toString(d.getAnno()));
                }
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.transform(new DOMSource(doc), new StreamResult(new File(DATA)));
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

}
