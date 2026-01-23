package it.unibs.ingdsw.persistence.xml.parser;

import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.applicazione.StatoRichiestaDisponibilita;
import it.unibs.ingdsw.model.applicazione.Target;
import it.unibs.ingdsw.model.applicazione.TargetTipo;
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

    private static final String DATA = "parametriApplicazione.xml";

    private String ambitoTerritoriale;
    private int numeroMassimoIscrivibili;
    private boolean ambienteDaConfigurare;
    private StatoRichiestaDisponibilita stato; // DISP_APERTE, DISP_CHIUSE
    private StatoProduzioneVisite statoProduzione; // PRODOTTE, NON_PRODOTTE
    private YearMonth nextDisponibilita;

    public ParsParametriAppXMLFile() {
        this.ambitoTerritoriale = "";
        this.numeroMassimoIscrivibili = 0;
        this.ambienteDaConfigurare = false;
        this.stato = StatoRichiestaDisponibilita.DISP_CHIUSE;
        this.statoProduzione = StatoProduzioneVisite.NON_PRODOTTE;
        this.nextDisponibilita = null;

        boolean devoRiscrivereXML = false;

        try {
            devoRiscrivereXML = parseXML();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Errore nel parsing XML: " + e.getMessage());
            e.printStackTrace();
            devoRiscrivereXML = true;
        }

        if (this.nextDisponibilita == null) {
            this.nextDisponibilita = calcolaNextDisponibilitaDiDefault();
            devoRiscrivereXML = true;
        }

        if (this.stato == null) this.stato = StatoRichiestaDisponibilita.DISP_CHIUSE;
        if (this.statoProduzione == null) this.statoProduzione = StatoProduzioneVisite.NON_PRODOTTE;

        if (devoRiscrivereXML) {
            salvaParametri(
                    this.ambitoTerritoriale,
                    this.numeroMassimoIscrivibili,
                    this.ambienteDaConfigurare,
                    this.stato,
                    this.statoProduzione,
                    this.nextDisponibilita
            );
        }
    }

    private boolean parseXML() throws ParserConfigurationException, SAXException, IOException {
        boolean nextCorrettaDaSalvare = false;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        File xmlFile = new File(DATA);

        if (!xmlFile.exists()) {
            System.err.println("File XML non trovato: " + xmlFile.getAbsolutePath() + " (uso default e lo ricreo)");
            return true;
        }

        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"applicazione".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <applicazione> attesa.");
        }

        // ambienteDaConfigurare
        if (root.getElementsByTagName("ambienteDaConfigurare").getLength() > 0 &&
                root.getElementsByTagName("ambienteDaConfigurare").item(0) != null) {
            String txt = root.getElementsByTagName("ambienteDaConfigurare").item(0).getTextContent().trim();
            this.ambienteDaConfigurare = Boolean.parseBoolean(txt);
        }

        // ambitoTerritoriale
        if (root.getElementsByTagName("ambitoTerritorialeCompetenza").getLength() > 0 &&
                root.getElementsByTagName("ambitoTerritorialeCompetenza").item(0) != null) {
            this.ambitoTerritoriale =
                    root.getElementsByTagName("ambitoTerritorialeCompetenza").item(0).getTextContent().trim();
        }

        // numero max
        if (root.getElementsByTagName("numeroMaxPerIniziativa").getLength() > 0 &&
                root.getElementsByTagName("numeroMaxPerIniziativa").item(0) != null) {
            String maxText = root.getElementsByTagName("numeroMaxPerIniziativa").item(0).getTextContent().trim();
            if (!maxText.isEmpty()) {
                try {
                    this.numeroMassimoIscrivibili = Integer.parseInt(maxText);
                } catch (NumberFormatException ex) {
                    System.err.println("Valore di <numeroMaxPerIniziativa> non valido: " + maxText + ". Uso 0.");
                    this.numeroMassimoIscrivibili = 0;
                }
            }
        }

        // stato
        if (root.getElementsByTagName("stato").getLength() > 0 &&
                root.getElementsByTagName("stato").item(0) != null) {
            String statoText = root.getElementsByTagName("stato").item(0).getTextContent().trim();
            if (!statoText.isEmpty()) {
                try {
                    this.stato = StatoRichiestaDisponibilita.valueOf(statoText);
                } catch (IllegalArgumentException ex) {
                    System.err.println("Valore di <stato> non valido: " + statoText + ". Uso DISP_CHIUSE.");
                    this.stato = StatoRichiestaDisponibilita.DISP_CHIUSE;
                }
            }
        }

        // statoProduzione
        if (root.getElementsByTagName("statoProduzione").getLength() > 0 &&
                root.getElementsByTagName("statoProduzione").item(0) != null) {
            String statoProdText = root.getElementsByTagName("statoProduzione").item(0).getTextContent().trim();
            if (!statoProdText.isEmpty()) {
                try {
                    this.statoProduzione = StatoProduzioneVisite.valueOf(statoProdText);
                } catch (IllegalArgumentException ex) {
                    System.err.println("Valore di <statoProduzione> non valido: " + statoProdText + ". Uso NON_PRODOTTE.");
                    this.statoProduzione = StatoProduzioneVisite.NON_PRODOTTE;
                }
            }
        }

        // nextDisponibilita
        if (root.getElementsByTagName("nextDisponibilita").getLength() > 0 &&
                root.getElementsByTagName("nextDisponibilita").item(0) != null) {

            String nextText = root.getElementsByTagName("nextDisponibilita").item(0).getTextContent().trim();

            if (!nextText.isEmpty()) {
                try {
                    this.nextDisponibilita = YearMonth.parse(nextText); // "YYYY-MM"
                } catch (Exception ex) {
                    System.err.println("Valore di <nextDisponibilita> non valido: " + nextText + ". Calcolo il default.");
                    this.nextDisponibilita = calcolaNextDisponibilitaDiDefault();
                    nextCorrettaDaSalvare = true;
                }
            } else {
                // tag presente ma vuoto
                this.nextDisponibilita = calcolaNextDisponibilitaDiDefault();
                nextCorrettaDaSalvare = true;
            }

        } else {
            // tag assente
            this.nextDisponibilita = calcolaNextDisponibilitaDiDefault();
            nextCorrettaDaSalvare = true;
        }

        return nextCorrettaDaSalvare;
    }

    private YearMonth calcolaNextDisponibilitaDiDefault() {
        try {
            Target targetApplicazione = new Target();
            return targetApplicazione.calcolaDataTarget(TargetTipo.DISPONIBILITA);
        } catch (Exception ex) {
            System.err.println("Errore nel calcolo della data target: " + ex.getMessage());
            return null;
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
                                      boolean ambienteDaConfigurare,
                                      StatoRichiestaDisponibilita stato,
                                      StatoProduzioneVisite statoProduzione,
                                      YearMonth nextDisponibilita) {
        try {
            if (nextDisponibilita == null) {
                try {
                    Target targetApplicazione = new Target();
                    nextDisponibilita = targetApplicazione.calcolaDataTarget(TargetTipo.DISPONIBILITA);
                } catch (Exception ex) {
                    System.err.println("Errore nel calcolo della data target: " + ex.getMessage());
                    nextDisponibilita = null;
                }
            }

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
            eStato.setTextContent((stato != null ? stato : StatoRichiestaDisponibilita.DISP_CHIUSE).name());
            root.appendChild(eStato);

            Element eStatoProd = doc.createElement("statoProduzione");
            eStatoProd.setTextContent((statoProduzione != null ? statoProduzione : StatoProduzioneVisite.NON_PRODOTTE).name());
            root.appendChild(eStatoProd);

            if (nextDisponibilita != null) {
                Element eNext = doc.createElement("nextDisponibilita");
                eNext.setTextContent(nextDisponibilita.toString()); // "YYYY-MM"
                root.appendChild(eNext);
            }

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
    public static void salvaParametri(String ambitoTerritoriale, int numeroMaxIscrivibili,
                                      StatoRichiestaDisponibilita statoDisp,
                                      StatoProduzioneVisite statoProduzione,
                                      YearMonth nextDisponibilita) {

        salvaParametri(
                ambitoTerritoriale,
                numeroMaxIscrivibili,
                false,
                statoDisp != null ? statoDisp : StatoRichiestaDisponibilita.DISP_CHIUSE,
                statoProduzione != null ? statoProduzione : StatoProduzioneVisite.NON_PRODOTTE,
                nextDisponibilita
        );
    }

}
