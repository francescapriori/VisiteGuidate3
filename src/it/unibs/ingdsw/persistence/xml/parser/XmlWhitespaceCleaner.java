package it.unibs.ingdsw.persistence.xml.parser;

import org.w3c.dom.Node;

final class XmlWhitespaceCleaner {
    private XmlWhitespaceCleaner() {
    }

    static void removeWhitespaceTextNodes(Node parent) {
        Node child = parent.getFirstChild();
        while (child != null) {
            Node next = child.getNextSibling();
            if (child.getNodeType() == Node.TEXT_NODE &&
                    child.getTextContent().trim().isEmpty()) {
                parent.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceTextNodes(child);
            }
            child = next;
        }
    }
}
