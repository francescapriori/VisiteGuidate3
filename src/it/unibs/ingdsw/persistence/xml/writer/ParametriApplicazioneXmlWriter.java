package it.unibs.ingdsw.persistence.xml.writer;

import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.applicazione.StatoRichiestaDisponibilita;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import it.unibs.ingdsw.persistence.xml.XmlDomWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.time.YearMonth;

public class ParametriApplicazioneXmlWriter {
    private final File file;

    public ParametriApplicazioneXmlWriter(File file) {
        this.file = file;
    }

    public void write(String ambitoTerritoriale,
                      int numeroMaxIscrivibili,
                      boolean ambienteDaConfigurare,
                      StatoRichiestaDisponibilita stato,
                      StatoProduzioneVisite statoProduzione,
                      YearMonth nextDisponibilita) {

        try {
            Document doc = XmlDomIO.newDocument();
            Element root = doc.createElement("applicazione");
            doc.appendChild(root);

            append(doc, root, "ambienteDaConfigurare", String.valueOf(ambienteDaConfigurare));
            append(doc, root, "ambitoTerritorialeCompetenza", ambitoTerritoriale != null ? ambitoTerritoriale : "");
            append(doc, root, "numeroMaxPerIniziativa", Integer.toString(Math.max(0, numeroMaxIscrivibili)));
            append(doc, root, "stato", (stato != null ? stato : StatoRichiestaDisponibilita.DISP_CHIUSE).name());
            append(doc, root, "statoProduzione", (statoProduzione != null ? statoProduzione : StatoProduzioneVisite.NON_PRODOTTE).name());

            if (nextDisponibilita != null) {
                append(doc, root, "nextDisponibilita", nextDisponibilita.toString()); // YYYY-MM
            }

            XmlDomWriter.save(doc, file);
        } catch (Exception ex) {
            System.err.println("Errore scrittura parametriApplicazione: " + ex.getMessage());
        }
    }

    private static void append(Document doc, Element parent, String tag, String value) {
        Element e = doc.createElement(tag);
        e.setTextContent(value != null ? value : "");
        parent.appendChild(e);
    }
}
