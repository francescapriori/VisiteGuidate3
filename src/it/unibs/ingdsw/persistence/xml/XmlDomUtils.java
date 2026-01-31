package it.unibs.ingdsw.persistence.xml;

import org.w3c.dom.*;

public final class XmlDomUtils {
    private XmlDomUtils() {}

    public static Element firstChild(Element parent, String tag) {
        if (parent == null) return null;
        NodeList nl = parent.getElementsByTagName(tag);
        return (nl.getLength() == 0) ? null : (Element) nl.item(0);
    }

    public static String text(Element parent, String tag, String def) {
        Element e = firstChild(parent, tag);
        if (e == null) return def;
        String t = e.getTextContent();
        return (t == null) ? def : t.trim();
    }

    public static int intValue(Element parent, String tag, int def) {
        try {
            String t = text(parent, tag, null);
            return (t == null || t.isBlank()) ? def : Integer.parseInt(t.trim());
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    public static double doubleValue(Element parent, String tag, double def) {
        try {
            String t = text(parent, tag, null);
            if (t == null || t.isBlank()) return def;
            return Double.parseDouble(t.trim().replace(',', '.'));
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    public static boolean boolValue(Element parent, String tag, boolean def) {
        String t = text(parent, tag, null);
        return (t == null || t.isBlank()) ? def : Boolean.parseBoolean(t.trim());
    }

    public static Element appendElement(Document doc, Element parent, String tag) {
        Element e = doc.createElement(tag);
        parent.appendChild(e);
        return e;
    }

    public static void appendText(Document doc, Element parent, String tag, String text) {
        Element e = doc.createElement(tag);
        e.setTextContent(text != null ? text : "");
        parent.appendChild(e);
    }

    public static void removeWhitespaceTextNodes(Node parent) {
        Node child = parent.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                parent.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceTextNodes(child);
            }
            child = next;
        }
    }
}
