package it.unibs.ingdsw.persistence.xml.writer;

import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import it.unibs.ingdsw.persistence.xml.XmlDomWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;
import static it.unibs.ingdsw.persistence.xml.XmlDataCodec.*;

public class DateEscluseXmlWriter {
    private final File file;

    public DateEscluseXmlWriter(File file) {
        this.file = file;
    }

    public void write(InsiemeDate lista) {
        try {
            Document doc = XmlDomIO.newDocument();
            Element root = doc.createElement("insiemeDateEscluse");
            doc.appendChild(root);

            if (lista != null) {
                for (Data d : lista.getInsiemeDate()) {
                    // qui l'XML originale usa <data><giorno>...
                    writeData(doc, root, "data", d);
                }
            }

            XmlDomWriter.save(doc, file);
        } catch (Exception ex) {
            System.err.println("Errore scrittura dateEscluse: " + ex.getMessage());
        }
    }
}
