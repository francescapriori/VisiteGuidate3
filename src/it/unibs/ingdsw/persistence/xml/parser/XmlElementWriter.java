package it.unibs.ingdsw.persistence.xml.parser;

import it.unibs.ingdsw.model.tempo.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

final class XmlElementWriter {
    private XmlElementWriter() {
    }

    static void appendText(Document doc, Element parent, String tag, String text) {
        Element e = doc.createElement(tag);
        if (text != null) e.setTextContent(text);
        parent.appendChild(e);
    }

    static void appendData(Document doc, Element parent, String tag, Data d) {
        Element e = doc.createElement(tag);
        parent.appendChild(e);
        if (d == null) d = new Data(1, 1, 1970);
        appendText(doc, e, "giorno", String.valueOf(d.getGiorno()));
        appendText(doc, e, "mese", String.valueOf(d.getMese()));
        appendText(doc, e, "anno", String.valueOf(d.getAnno()));
    }

    static String safe(String s) {
        return s == null ? "" : s;
    }
}
