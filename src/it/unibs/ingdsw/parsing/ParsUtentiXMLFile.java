package it.unibs.ingdsw.parsing;

import it.unibs.ingdsw.utenti.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ParsUtentiXMLFile {
    private static final String DATA = "src/it/unibs/ingdsw/parsing/file/utenti.xml";

    private final ListaUtenti listaUtenti = new ListaUtenti();

    public ParsUtentiXMLFile() {
        try {
            parseXML();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Errore nel parsing XML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ListaUtenti getListaUtenti() {
        return listaUtenti;
    }

    private void parseXML() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        DocumentBuilder db = dbf.newDocumentBuilder();
        File xmlFile = new File(DATA);
        if (!xmlFile.exists()) {
            System.err.println("Errore: file XML non trovato: " + xmlFile.getAbsolutePath());
            return;
        }

        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"utenti".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <utenti> attesa.");
        }

        NodeList children = root.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);
            if (!(n instanceof Element)) continue;
            Element e = (Element) n;

            String tag = e.getTagName();
            Utente u = null;

            if ("utente".equalsIgnoreCase(tag)) {
                String username = e.getAttribute("username").trim();
                String password = e.getAttribute("password").trim();
                String ruoloStr = e.getAttribute("ruolo").trim();
                boolean pwProv = readPwProvvisoriaAttr(e);

                if (username.isEmpty() || password.isEmpty() || ruoloStr.isEmpty()) {
                    System.err.println("Utente con dati mancanti nel file XML.");
                    continue;
                }
                u = creaUtente(ruoloStr, username, password, pwProv);

            } else if ("configuratore".equalsIgnoreCase(tag) || "volontario".equalsIgnoreCase(tag)) {
                String username = e.getAttribute("username").trim();
                String password = e.getAttribute("password").trim();
                boolean pwProv = readPwProvvisoriaAttr(e);
                String ruoloStr = tag;

                if (username.isEmpty() || password.isEmpty()) {
                    System.err.println("Utente con dati mancanti nel file XML.");
                    continue;
                }
                u = creaUtente(ruoloStr, username, password, pwProv);

            } else {
                System.err.println("Tag utente non riconosciuto: <" + tag + ">");
            }

            if (u != null) listaUtenti.aggiungiUtente(u);
        }
    }

    private static boolean readPwProvvisoriaAttr(Element e) {
        String v = e.hasAttribute("pwProvvisoria")
                ? e.getAttribute("pwProvvisoria").trim()
                : e.getAttribute("passwordProvvisoria").trim();
        return "true".equalsIgnoreCase(v);
    }

    private Utente creaUtente(String ruoloStr, String username, String password, boolean pwProv) {
        try {
            Ruolo ruolo = Ruolo.valueOf(ruoloStr.toUpperCase(Locale.ITALIAN));
            Utente u = switch (ruolo) {
                case CONFIGURATORE -> new Configuratore(username, password);
                case VOLONTARIO -> new Volontario(username, password);
                // case FRUITORE -> new Fruitore(username, password); todo Versione 3
                default -> null;
            };
            if (u != null) setPwProvvisoriaSafe(u, pwProv);
            return u;
        } catch (IllegalArgumentException ex) {
            System.err.println("Ruolo non valido: " + ruoloStr);
            return null;
        }
    }

    public static void salvaListaUtenti(ListaUtenti lista) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            Element root = doc.createElement("utenti");
            doc.appendChild(root);

            for (Utente utente : lista.getListaUtenti()) {
                String tag;
                switch (utente.getRuolo()) {
                    case CONFIGURATORE -> tag = "configuratore";
                    case VOLONTARIO -> tag = "volontario";
                    //case FRUITORE -> tag = "fruitore" todo Versione 3
                    default -> {
                        System.err.println("Ruolo non supportato in scrittura: " + utente.getRuolo());
                        continue;
                    }
                }

                Element e = doc.createElement(tag);
                e.setAttribute("username", utente.getUsername());
                e.setAttribute("password", utente.getPassword());
                e.setAttribute("pwProvvisoria", String.valueOf(getPwProvvisoriaSafe(utente)));
                root.appendChild(e);
            }

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.transform(new DOMSource(doc), new StreamResult(new File(DATA)));
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }


    private static void setPwProvvisoriaSafe(Utente u, boolean value) {
        try {
            u.getClass().getMethod("setPwProvvisoria", boolean.class).invoke(u, value);
        } catch (Exception ignore) {
            try {
                u.getClass().getMethod("setPasswordProvvisoria", boolean.class).invoke(u, value);
            } catch (Exception ignore2) {}
        }
    }

    private static boolean getPwProvvisoriaSafe(Utente u) {
        try {
            return (boolean) u.getClass().getMethod("isPwProvvisoria").invoke(u);
        } catch (Exception e1) {
            try {
                return (boolean) u.getClass().getMethod("getPwProvvisoria").invoke(u);
            } catch (Exception e2) {
                try {
                    return (boolean) u.getClass().getMethod("isPasswordProvvisoria").invoke(u);
                } catch (Exception e3) {
                    return false;
                }
            }
        }
    }
}
