package it.unibs.ingdsw.persistence.xml.parser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

final class XmlElementReader {
    private XmlElementReader() {
    }

    static Element getFirst(Element parent, String tag) {
        if (parent == null) return null;
        NodeList nl = parent.getElementsByTagName(tag);
        return (nl == null || nl.getLength() == 0) ? null : (Element) nl.item(0);
    }

    static String getText(Element parent, String tag, String def) {
        Element e = getFirst(parent, tag);
        return (e == null) ? def : e.getTextContent().trim();
    }

    static int getInt(Element parent, String tag, int def) {
        try {
            String t = getText(parent, tag, null);
            return (t == null || t.isEmpty()) ? def : Integer.parseInt(t);
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    static double getDouble(Element parent, String tag, double def) {
        try {
            String t = getText(parent, tag, null);
            if (t == null || t.isEmpty()) return def;
            return Double.parseDouble(t.replace(',', '.'));
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    static boolean getBoolean(Element parent, String tag, boolean def) {
        String t = getText(parent, tag, null);
        return (t == null || t.isEmpty()) ? def : Boolean.parseBoolean(t);
    }

    static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
