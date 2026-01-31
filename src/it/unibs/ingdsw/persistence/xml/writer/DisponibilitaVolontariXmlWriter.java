package it.unibs.ingdsw.persistence.xml.writer;

import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import it.unibs.ingdsw.persistence.xml.XmlDomWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;
import static it.unibs.ingdsw.persistence.xml.XmlDataCodec.*;

public class DisponibilitaVolontariXmlWriter {
    private final File file;

    public DisponibilitaVolontariXmlWriter(File file) {
        this.file = file;
    }

    public void write(HashMap<Volontario, InsiemeDate> mappa) {
        try {
            Document doc = XmlDomIO.newDocument();
            Element root = doc.createElement("disponibilitaVolontari");
            doc.appendChild(root);

            if (mappa != null) {
                for (Map.Entry<Volontario, InsiemeDate> entry : mappa.entrySet()) {
                    Volontario vol = entry.getKey();
                    InsiemeDate ins = entry.getValue();

                    Element eVol = appendElement(doc, root, "volontario");
                    eVol.setAttribute("username", vol != null && vol.getUsername() != null ? vol.getUsername() : "");

                    Element eDisp = appendElement(doc, eVol, "disponibilita");

                    if (ins != null) {
                        for (Data d : ins.getInsiemeDate()) {
                            writeData(doc, eDisp, "data", d);
                        }
                    }
                }
            }

            XmlDomWriter.save(doc, file);
        } catch (Exception ex) {
            System.err.println("Errore scrittura disponibilitaVolontari: " + ex.getMessage());
        }
    }
}
