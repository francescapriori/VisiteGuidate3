package it.unibs.ingdsw.persistence.xml.parser;

import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

final class XmlDocumentBuilderProvider {
    private XmlDocumentBuilderProvider() {
    }

    static DocumentBuilder newSecureBuilder() throws ParserConfigurationException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        return dbf.newDocumentBuilder();
    }

    static Document newDocument() throws ParserConfigurationException {
        return newSecureBuilder().newDocument();
    }
}
