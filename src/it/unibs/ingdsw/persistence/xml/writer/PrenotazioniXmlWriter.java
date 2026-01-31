package it.unibs.ingdsw.persistence.xml.writer;

import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.prenotazione.Prenotazione;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.utenti.Fruitore;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import it.unibs.ingdsw.persistence.xml.XmlDomWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;
import static it.unibs.ingdsw.persistence.xml.XmlDataCodec.*;

public class PrenotazioniXmlWriter {
    private final File file;

    public PrenotazioniXmlWriter(File file) {
        this.file = file;
    }

    public void writeAll(List<Prenotazione> prenotazioni) {
        try {
            Document doc = XmlDomIO.newDocument();
            Element root = doc.createElement("prenotazioni");
            doc.appendChild(root);

            if (prenotazioni != null) {
                for (Prenotazione p : prenotazioni) {
                    appendPrenotazione(doc, root, p);
                }
            }

            XmlDomWriter.save(doc, file);
        } catch (Exception ex) {
            System.err.println("Errore scrittura prenotazioni: " + ex.getMessage());
        }
    }

    private static void appendPrenotazione(Document doc, Element root, Prenotazione p) {
        Element ePren = appendElement(doc, root, "prenotazione");

        Appuntamento app = p.getAppuntamento();
        Visita v = (app != null) ? app.getVisita() : null;
        Data d = (app != null) ? app.getData() : null;
        Fruitore f = p.getUtenteChePrenota();

        appendText(doc, ePren, "codicePrenotazione", p.getCodicePrenotazione() != null ? p.getCodicePrenotazione() : "");

        Element eVisita = appendElement(doc, ePren, "visita");
        appendText(doc, eVisita, "luogoID", v != null && v.getLuogoID() != null ? v.getLuogoID() : "");
        appendText(doc, eVisita, "titolo", v != null && v.getTitolo() != null ? v.getTitolo() : "");

        writeData(doc, ePren, "data", d);

        Element eFruitore = appendElement(doc, ePren, "fruitore");
        eFruitore.setAttribute("username", f != null && f.getUsername() != null ? f.getUsername() : "");

        appendText(doc, ePren, "numeroPersonePerPrenotazione", String.valueOf(p.getNumeroPersonePerPrenotazione()));
    }
}
