package it.unibs.ingdsw.parsing;

import it.unibs.ingdsw.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.applicazione.StatoRichiestaDisponibilita;
import it.unibs.ingdsw.applicazione.Target;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.YearMonth;

public class ParsParametriAppXMLFile {

    private static final String DATA = "src/it/unibs/ingdsw/parsing/file/parametriApplicazione.xml";

    private String ambitoTerritoriale;
    private int numeroMassimoIscrivibili;
    private boolean ambienteDaConfigurare;
    private StatoRichiestaDisponibilita stato;     // DISP_APERTE, DISP_CHIUSE
    private StatoProduzioneVisite statoProduzione; // PRODOTTE, NON_PRODOTTE
    private YearMonth nextDisponibilita;

    public ParsParametriAppXMLFile() {
        try {
            parseXML();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Errore nel parsing XML: " + e.getMessage());
            e.printStackTrace();
            this.stato = StatoRichiestaDisponibilita.DISP_CHIUSE;
            this.statoProduzione = StatoProduzioneVisite.NON_PRODOTTE;
            this.nextDisponibilita = null;
        }

        if (this.stato == null) {
            this.stato = StatoRichiestaDisponibilita.DISP_CHIUSE;
        }
        if (this.statoProduzione == null) {
            this.statoProduzione = StatoProduzioneVisite.NON_PRODOTTE;
        }
        if (this.nextDisponibilita == null) {
            this.nextDisponibilita = YearMonth.now();
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
                    stato = StatoRichiestaDisponibilita.valueOf(statoText);
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
                    statoProduzione = StatoProduzioneVisite.valueOf(statoProdText);
                } catch (IllegalArgumentException ex) {
                    System.err.println("Valore di <statoProduzione> non valido: " + statoProdText
                            + ". Imposto default NON_PRODOTTE.");
                    statoProduzione = StatoProduzioneVisite.NON_PRODOTTE;
                }
            }
        }

        if (root.getElementsByTagName("nextDisponibilita").getLength() > 0 &&
                root.getElementsByTagName("nextDisponibilita").item(0) != null) {

            String nextText = root.getElementsByTagName("nextDisponibilita")
                    .item(0)
                    .getTextContent()
                    .trim();

            if (!nextText.isEmpty()) {
                try {
                    nextDisponibilita = YearMonth.parse(nextText);
                } catch (Exception ex) {
                    System.err.println("Valore di <nextDisponibilita> non valido: " + nextText
                            + ". Uso la data calcolata da Target.");
                    try {
                        Target targetApplicazione = new Target();
                        nextDisponibilita = targetApplicazione.calcolaDataTargetDisponibilita();
                    } catch (Exception ex2) {
                        System.err.println("Errore nel calcolo della data target: " + ex2.getMessage());
                        nextDisponibilita = null;
                    }
                }
            } else {
                try {
                    Target targetApplicazione = new Target();
                    nextDisponibilita = targetApplicazione.calcolaDataTargetDisponibilita();
                } catch (Exception ex2) {
                    System.err.println("Errore nel calcolo della data target: " + ex2.getMessage());
                    nextDisponibilita = null;
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

    public YearMonth getNextDisponibilita() {
        return nextDisponibilita;
    }


    public static void salvaParametri(String ambitoTerritoriale, int numeroMaxIscrivibili,
                                      StatoRichiestaDisponibilita statoDisp,
                                      StatoProduzioneVisite statoProduzione) {

        salvaParametri(
                ambitoTerritoriale,
                numeroMaxIscrivibili,
                false,  // ambienteDaConfigurare
                statoDisp != null ? statoDisp : StatoRichiestaDisponibilita.DISP_CHIUSE,
                statoProduzione != null ? statoProduzione : StatoProduzioneVisite.NON_PRODOTTE,
                YearMonth.now()
        );
    }

    public static void salvaParametri(String ambitoTerritoriale, int numeroMaxIscrivibili,
                                      boolean ambienteDaConfigurare,
                                      StatoRichiestaDisponibilita stato,
                                      StatoProduzioneVisite statoProduzione,
                                      YearMonth nextDisponibilita) {
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

            Element eNext = doc.createElement("nextDisponibilita");
            YearMonth effNext = (nextDisponibilita != null)
                    ? nextDisponibilita
                    : YearMonth.now();   // ulteriore safety net
            eNext.setTextContent(effNext.toString());   // "YYYY-MM"
            root.appendChild(eNext);

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
