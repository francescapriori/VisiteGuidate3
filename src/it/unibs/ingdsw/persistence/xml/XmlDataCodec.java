package it.unibs.ingdsw.persistence.xml;

import it.unibs.ingdsw.model.tempo.Data;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;

public final class XmlDataCodec {
    private XmlDataCodec() {}

    public static Data readData(Element eData, Data def) {
        if (eData == null) return def;
        int g = intValue(eData, "giorno", def.getGiorno());
        int m = intValue(eData, "mese", def.getMese());
        int a = intValue(eData, "anno", def.getAnno());
        Data d = new Data(g, m, a);
        return d;
    }

    public static void writeData(Document doc, Element parent, String tag, Data d) {
        if (d == null) d = new Data(1, 1, 1970);
        Element e = appendElement(doc, parent, tag);
        appendText(doc, e, "giorno", String.valueOf(d.getGiorno()));
        appendText(doc, e, "mese", String.valueOf(d.getMese()));
        appendText(doc, e, "anno", String.valueOf(d.getAnno()));
    }
}
