package it.unibs.ingdsw.persistence.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;


public final class XmlDomIO {
    private XmlDomIO() {}

    public static Document load(File file, String expectedRootTag) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(file);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (expectedRootTag != null && !expectedRootTag.equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <" + expectedRootTag + "> attesa, trovata <" + root.getTagName() + ">.");
        }
        return doc;
    }

    public static Document newDocument() throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.newDocument();
    }
}
