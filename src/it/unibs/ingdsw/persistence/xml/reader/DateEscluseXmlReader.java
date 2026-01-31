package it.unibs.ingdsw.persistence.xml.reader;

import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import org.w3c.dom.*;

import java.io.File;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;
import static it.unibs.ingdsw.persistence.xml.XmlDataCodec.*;

public class DateEscluseXmlReader {
    private final File file;

    public DateEscluseXmlReader(File file) {
        this.file = file;
    }

    public InsiemeDate read() {
        InsiemeDate dateEscluse = new InsiemeDate();
        if (!file.exists()) return dateEscluse;

        try {
            Document doc = XmlDomIO.load(file, "insiemeDateEscluse");
            Element root = doc.getDocumentElement();

            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);
                if (!(n instanceof Element e)) continue;
                if (!"data".equalsIgnoreCase(e.getTagName())) continue;

                Data d = readData(e, new Data(1, 1, 1970));
                if (!dateEscluse.aggiungiData(d)) {
                    System.out.println("La data è già presente nell'elenco.");
                }
            }
            dateEscluse.ordinaDateCronologicamente();
        } catch (Exception ex) {
            System.err.println("Errore lettura dateEscluse: " + ex.getMessage());
        }

        return dateEscluse;
    }
}
