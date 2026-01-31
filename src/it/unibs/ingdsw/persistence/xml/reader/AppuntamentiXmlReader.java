package it.unibs.ingdsw.persistence.xml.reader;

import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.persistence.dto.AppuntamentoDTO;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import org.w3c.dom.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;
import static it.unibs.ingdsw.persistence.xml.XmlDataCodec.*;

public class AppuntamentiXmlReader {
    private final File file;

    public AppuntamentiXmlReader(File file) {
        this.file = file;
    }

    public List<AppuntamentoDTO> readAll() {
        List<AppuntamentoDTO> out = new ArrayList<>();
        if (!file.exists()) return out;

        try {
            Document doc = XmlDomIO.load(file, "appuntamenti");
            Element root = doc.getDocumentElement();

            NodeList nl = root.getElementsByTagName("appuntamento");
            for (int i = 0; i < nl.getLength(); i++) {
                Element eApp = (Element) nl.item(i);

                Element eVisita = firstChild(eApp, "visita");
                String luogoID = text(eVisita, "luogoID", null);
                String titolo = text(eVisita, "titolo", null);

                Data data = readData(firstChild(eApp, "data"), new Data(1,1,1970));

                Element eGuida = firstChild(eApp, "guida");
                String username = (eGuida != null) ? eGuida.getAttribute("username") : null;

                String stato = text(eApp, "statoVisita", "PROPOSTA");
                int pren = intValue(eApp, "numeroPersonePrenotate", 0);

                out.add(new AppuntamentoDTO(luogoID, titolo, data, username, stato, pren));
            }
        } catch (Exception ex) {
            System.err.println("Errore lettura appuntamenti: " + ex.getMessage());
        }

        return out;
    }
}
