package it.unibs.ingdsw.persistence.xml;

import javax.xml.XMLConstants;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import java.io.File;

public final class XmlDomWriter {
    private XmlDomWriter() {}

    public static void save(Document doc, File outFile) throws Exception {
        if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();

        TransformerFactory tf = TransformerFactory.newInstance();
        try {
            tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        } catch (Exception ignore) {}

        Transformer t = tf.newTransformer();
        t.setOutputProperty(OutputKeys.INDENT, "yes");
        t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        t.transform(new DOMSource(doc), new StreamResult(outFile));
    }
}
