package it.unibs.ingdsw.parsing;

import it.unibs.ingdsw.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.applicazione.StatoRichiestaDisponibilita;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class ParsParametriAppXMLFile {

    private static final String DATA = "src/it/unibs/ingdsw/parsing/file/parametriApplicazione.xml";

    private String ambitoTerritoriale;
    private int numeroMassimoIscrivibili;
    private boolean ambienteDaConfigurare;
    private StatoRichiestaDisponibilita stato;          // DISP_APERTE, DISP_CHIUSE
    private StatoProduzioneVisite statoProduzione;      // PRODOTTE, NON_PRODOTTE

    public ParsParametriAppXMLFile() {
        try {
            parseXML();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Errore nel parsing XML: " + e.getMessage());
            e.printStackTrace();
            // fallback di sicurezza
            this.stato = StatoRichiestaDisponibilita.DISP_CHIUSE;
            this.statoProduzione = StatoProduzioneVisite.NON_PRODOTTE;
        }
        // default se non trovato a XML valido
        if (this.stato == null) {
            this.stato = StatoRichiestaDisponibilita.DISP_CHIUSE;
        }
        if (this.statoProduzione == null) {
            this.statoProduzione = StatoProduzioneVisite.NON_PRODOTTE;
        }
    }

    private void parseXML() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        File xmlFile = new File(DATA);
        if (!xmlFile.exists()) {
            System.err.println("Errore: file XML non trovato: " + xmlFile.getAbsolutePath());
            return;
        }

        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"applicazione".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <applicazione> attesa.");
        }

        ambienteDaConfigurare = Boolean.parseBoolean(
                root.getElementsByTagName("ambienteDaConfigurare").item(0).getTextContent().trim()
        );

        ambitoTerritoriale =
                root.getElementsByTagName("ambitoTerritorialeCompetenza").item(0).getTextContent().trim();

        String maxText = root.getElementsByTagName("numeroMaxPerIniziativa").item(0).getTextContent().trim();
        numeroMassimoIscrivibili = maxText.isEmpty() ? 0 : Integer.parseInt(maxText);


        if (root.getElementsByTagName("stato").getLength() > 0 &&
                root.getElementsByTagName("stato").item(0) != null) {

            String statoText = root.getElementsByTagName("stato").item(0).getTextContent().trim();

            if (!statoText.isEmpty()) {
                try {
                    stato = StatoRichiestaDisponibilita.valueOf(statoText);   // es. "DISP_APERTE"
                } catch (IllegalArgumentException ex) {
                    System.err.println("Valore di <stato> non valido: " + statoText
                            + ". Imposto default DISP_CHIUSE.");
                    stato = StatoRichiestaDisponibilita.DISP_CHIUSE;
                }
            }
        }

        if (root.getElementsByTagName("statoProduzione").getLength() > 0 &&
                root.getElementsByTagName("statoProduzione").item(0) != null) {

            String statoProdText = root.getElementsByTagName("statoProduzione").item(0).getTextContent().trim();

            if (!statoProdText.isEmpty()) {
                try {
                    statoProduzione = StatoProduzioneVisite.valueOf(statoProdText); // es. "PRODOTTE"
                } catch (IllegalArgumentException ex) {
                    System.err.println("Valore di <statoProduzione> non valido: " + statoProdText
                            + ". Imposto default NON_PRODOTTE.");
                    statoProduzione = StatoProduzioneVisite.NON_PRODOTTE;
                }
            }
        }

    }

    public String getAmbitoTerritoriale() {
        return ambitoTerritoriale;
    }

    public int getNumeroMassimoIscrivibili() {
        return numeroMassimoIscrivibili;
    }

    public boolean isAmbienteDaConfigurare() {
        return ambienteDaConfigurare;
    }

    public StatoRichiestaDisponibilita getStato() {
        return stato;
    }

    public StatoProduzioneVisite getStatoProduzione() {
        return statoProduzione;
    }

    public static void salvaParametri(String ambitoTerritoriale, int numeroMaxIscrivibili,
                                      StatoRichiestaDisponibilita statoDisp,
                                      StatoProduzioneVisite statoProduzione) {
        salvaParametri(ambitoTerritoriale, numeroMaxIscrivibili,
                false,                       // ambienteDaConfigurare
                statoDisp != null ? statoDisp : StatoRichiestaDisponibilita.DISP_CHIUSE,
                statoProduzione != null ? statoProduzione : StatoProduzioneVisite.NON_PRODOTTE);
    }

    public static void salvaParametri(String ambitoTerritoriale, int numeroMaxIscrivibili,
                                      boolean ambienteDaConfigurare,
                                      StatoRichiestaDisponibilita stato,
                                      StatoProduzioneVisite statoProduzione) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("applicazione");
            doc.appendChild(root);

            Element eConf = doc.createElement("ambienteDaConfigurare");
            eConf.setTextContent(String.valueOf(ambienteDaConfigurare));
            root.appendChild(eConf);

            Element eAmb = doc.createElement("ambitoTerritorialeCompetenza");
            eAmb.setTextContent(ambitoTerritoriale != null ? ambitoTerritoriale : "");
            root.appendChild(eAmb);

            Element eMax = doc.createElement("numeroMaxPerIniziativa");
            eMax.setTextContent(Integer.toString(Math.max(0, numeroMaxIscrivibili)));
            root.appendChild(eMax);

            Element eStato = doc.createElement("stato");
            eStato.setTextContent((stato != null
                    ? stato
                    : StatoRichiestaDisponibilita.DISP_CHIUSE).name());
            root.appendChild(eStato);

            Element eStatoProd = doc.createElement("statoProduzione");
            eStatoProd.setTextContent((statoProduzione != null
                    ? statoProduzione
                    : StatoProduzioneVisite.NON_PRODOTTE).name());
            root.appendChild(eStatoProd);

            File outFile = new File(DATA);
            File parent = outFile.getParentFile();
            if (parent != null) parent.mkdirs();

            javax.xml.transform.TransformerFactory tf = javax.xml.transform.TransformerFactory.newInstance();
            javax.xml.transform.Transformer t = tf.newTransformer();
            t.setOutputProperty(javax.xml.transform.OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.setOutputProperty(javax.xml.transform.OutputKeys.ENCODING, "UTF-8");
            t.transform(new javax.xml.transform.dom.DOMSource(doc),
                    new javax.xml.transform.stream.StreamResult(outFile));
        } catch (Exception e) {
            System.err.println("Errore durante il salvataggio dei parametri: " + e.getMessage());
            e.printStackTrace();
        }
    }


}
