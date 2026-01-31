package it.unibs.ingdsw.persistence.xml.reader;

import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.persistence.dto.PrenotazioneDTO;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import org.w3c.dom.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;
import static it.unibs.ingdsw.persistence.xml.XmlDataCodec.*;

public class PrenotazioniXmlReader {
    private final File file;

    public PrenotazioniXmlReader(File file) {
        this.file = file;
    }

    public List<PrenotazioneDTO> readAll() {
        List<PrenotazioneDTO> out = new ArrayList<>();
        if (!file.exists()) return out;

        try {
            Document doc = XmlDomIO.load(file, "prenotazioni");
            Element root = doc.getDocumentElement();

            NodeList nl = root.getElementsByTagName("prenotazione");
            for (int i = 0; i < nl.getLength(); i++) {
                Element ePren = (Element) nl.item(i);

                String codice = text(ePren, "codicePrenotazione", null);

                Element eVisita = firstChild(ePren, "visita");
                String luogoID = text(eVisita, "luogoID", null);
                String titolo = text(eVisita, "titolo", null);

                Data data = readData(firstChild(ePren, "data"), new Data(1,1,1970));

                Element eFruitore = firstChild(ePren, "fruitore");
                String usernameFruitore = (eFruitore != null) ? eFruitore.getAttribute("username") : null;

                int numPers = intValue(ePren, "numeroPersonePerPrenotazione", 1);

                out.add(new PrenotazioneDTO(codice, luogoID, titolo, data, usernameFruitore, numPers));
            }
        } catch (Exception ex) {
            System.err.println("Errore lettura prenotazioni: " + ex.getMessage());
        }

        return out;
    }
}
