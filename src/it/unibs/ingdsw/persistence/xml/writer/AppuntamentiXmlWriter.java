package it.unibs.ingdsw.persistence.xml.writer;

import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.StatoAppuntamento;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import it.unibs.ingdsw.persistence.xml.XmlDomWriter;
import org.w3c.dom.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;
import static it.unibs.ingdsw.persistence.xml.XmlDataCodec.*;

public class AppuntamentiXmlWriter {
    private final File file;

    public AppuntamentiXmlWriter(File file) {
        this.file = file;
    }

    public void upsertAll(List<Appuntamento> nuoviAppuntamenti) {
        try {
            Document doc;
            Element root;

            if (file.exists()) {
                doc = XmlDomIO.load(file, null);
                root = doc.getDocumentElement();
                if (!"appuntamenti".equals(root.getTagName())) {
                    doc = XmlDomIO.newDocument();
                    root = doc.createElement("appuntamenti");
                    doc.appendChild(root);
                }
            } else {
                doc = XmlDomIO.newDocument();
                root = doc.createElement("appuntamenti");
                doc.appendChild(root);
            }

            removeWhitespaceTextNodes(root);

            if (nuoviAppuntamenti != null) {
                for (Appuntamento app : nuoviAppuntamenti) {
                    Element existing = findAppuntamentoElement(root, app);
                    if (existing != null) existing.getParentNode().removeChild(existing);
                    appendAppuntamento(doc, root, app);
                }
            }

            XmlDomWriter.save(doc, file);
        } catch (Exception ex) {
            System.err.println("Errore scrittura appuntamenti: " + ex.getMessage());
        }
    }

    private static Element findAppuntamentoElement(Element root, Appuntamento app) {
        if (app == null || app.getVisita() == null || app.getData() == null) return null;

        NodeList nl = root.getElementsByTagName("appuntamento");
        for (int i = 0; i < nl.getLength(); i++) {
            Element eApp = (Element) nl.item(i);

            Element eVisita = firstChild(eApp, "visita");
            String luogoID = text(eVisita, "luogoID", null);
            String titolo = text(eVisita, "titolo", null);

            Element eData = firstChild(eApp, "data");
            int g = intValue(eData, "giorno", -1);
            int m = intValue(eData, "mese", -1);
            int a = intValue(eData, "anno", -1);

            if (stessaChiave(app, luogoID, titolo, g, m, a)) return eApp;
        }
        return null;
    }

    private static boolean stessaChiave(Appuntamento app, String luogoIDXml, String titoloXml, int gXml, int mXml, int aXml) {
        Visita v = app.getVisita();
        Data d = app.getData();
        if (v == null || d == null) return false;

        String luogoIDApp = v.getLuogoID();
        String titoloApp = v.getTitolo();

        boolean stessoLuogo = (luogoIDApp == null && luogoIDXml == null) || (luogoIDApp != null && luogoIDApp.equals(luogoIDXml));
        boolean stessoTitolo = (titoloApp == null && titoloXml == null) || (titoloApp != null && titoloApp.equals(titoloXml));
        boolean stessaData = d.getGiorno() == gXml && d.getMese() == mXml && d.getAnno() == aXml;

        return stessoLuogo && stessoTitolo && stessaData;
    }

    private static void appendAppuntamento(Document doc, Element root, Appuntamento app) {
        Element eApp = appendElement(doc, root, "appuntamento");

        Element eVisita = appendElement(doc, eApp, "visita");
        Visita v = app.getVisita();
        appendText(doc, eVisita, "luogoID", v != null && v.getLuogoID() != null ? v.getLuogoID() : "");
        appendText(doc, eVisita, "titolo", v != null && v.getTitolo() != null ? v.getTitolo() : "");

        writeData(doc, eApp, "data", app.getData());

        Element eGuida = appendElement(doc, eApp, "guida");
        Volontario guida = app.getGuida();
        eGuida.setAttribute("username", guida != null && guida.getUsername() != null ? guida.getUsername() : "");

        StatoAppuntamento stato = app.getStatoVisita();
        appendText(doc, eApp, "statoVisita", stato != null ? stato.name() : StatoAppuntamento.PROPOSTA.name());
        appendText(doc, eApp, "numeroPersonePrenotate", String.valueOf(app.getNumeroPersonePrenotate()));
    }
}
