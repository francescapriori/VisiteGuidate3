package it.unibs.ingdsw.persistence.xml.reader;

import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.persistence.dto.DisponibilitaVolontarioDTO;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import org.w3c.dom.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;
import static it.unibs.ingdsw.persistence.xml.XmlDataCodec.*;

public class DisponibilitaVolontariXmlReader {
    private final File file;

    public DisponibilitaVolontariXmlReader(File file) {
        this.file = file;
    }

    public List<DisponibilitaVolontarioDTO> readAll() {
        List<DisponibilitaVolontarioDTO> out = new ArrayList<>();
        if (!file.exists()) return out;

        try {
            Document doc = XmlDomIO.load(file, "disponibilitaVolontari");
            Element root = doc.getDocumentElement();

            NodeList nl = root.getElementsByTagName("volontario");
            for (int i = 0; i < nl.getLength(); i++) {
                Element eVol = (Element) nl.item(i);
                String username = eVol.getAttribute("username");
                if (username == null || username.isBlank()) continue;

                InsiemeDate ins = new InsiemeDate();
                Element eDisp = firstChild(eVol, "disponibilita");
                if (eDisp != null) {
                    NodeList dates = eDisp.getElementsByTagName("data");
                    for (int j = 0; j < dates.getLength(); j++) {
                        Element eData = (Element) dates.item(j);
                        Data d = readData(eData, new Data(1, 1, 1970));
                        if (!ins.aggiungiData(d)) {
                            System.out.println("La data è già presente nell'elenco.");
                        }
                    }
                    ins.ordinaDateCronologicamente();
                }

                out.add(new DisponibilitaVolontarioDTO(username, ins));
            }
        } catch (Exception ex) {
            System.err.println("Errore lettura disponibilitaVolontari: " + ex.getMessage());
        }

        return out;
    }
}
