package it.unibs.ingdsw.persistence.xml.reader;

import it.unibs.ingdsw.model.utenti.*;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import org.w3c.dom.*;

import java.io.File;
import java.util.Locale;

public class UtentiXmlReader {
    private final File file;

    public UtentiXmlReader(File file) {
        this.file = file;
    }

    public ListaUtenti read() {
        ListaUtenti listaUtenti = new ListaUtenti();
        if (!file.exists()) return listaUtenti;

        try {
            Document doc = XmlDomIO.load(file, "utenti");
            Element root = doc.getDocumentElement();

            NodeList children = root.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node n = children.item(i);
                if (!(n instanceof Element e)) continue;

                String tag = e.getTagName();
                Utente u = null;

                if ("utente".equalsIgnoreCase(tag)) {
                    String username = e.getAttribute("username").trim();
                    String password = e.getAttribute("password").trim();
                    String ruoloStr = e.getAttribute("ruolo").trim();
                    boolean pwProv = readPwProvvisoriaAttr(e);

                    if (username.isEmpty() || password.isEmpty() || ruoloStr.isEmpty()) continue;
                    u = creaUtente(ruoloStr, username, password, pwProv);

                } else if ("configuratore".equalsIgnoreCase(tag) || "volontario".equalsIgnoreCase(tag) || "fruitore".equalsIgnoreCase(tag)) {
                    String username = e.getAttribute("username").trim();
                    String password = e.getAttribute("password").trim();
                    boolean pwProv = readPwProvvisoriaAttr(e);

                    if (username.isEmpty() || password.isEmpty()) continue;
                    u = creaUtente(tag, username, password, pwProv);
                }

                if (u != null) listaUtenti.aggiungiUtente(u);
            }
        } catch (Exception ex) {
            System.err.println("Errore lettura utenti: " + ex.getMessage());
        }

        return listaUtenti;
    }

    private static boolean readPwProvvisoriaAttr(Element e) {
        String v = e.hasAttribute("pwProvvisoria") ? e.getAttribute("pwProvvisoria").trim() : e.getAttribute("passwordProvvisoria").trim();
        return "true".equalsIgnoreCase(v);
    }

    private static Utente creaUtente(String ruoloStr, String username, String password, boolean pwProv) {
        try {
            Ruolo ruolo = Ruolo.valueOf(ruoloStr.toUpperCase(Locale.ITALIAN));
            Utente u = switch (ruolo) {
                case CONFIGURATORE -> new Configuratore(username, password);
                case VOLONTARIO -> new Volontario(username, password);
                case FRUITORE -> new Fruitore(username, password);
            };
            setPwProvvisoriaSafe(u, pwProv);
            return u;
        } catch (Exception ex) {
            return null;
        }
    }

    private static void setPwProvvisoriaSafe(Utente u, boolean value) {
        try {
            u.getClass().getMethod("setPwProvvisoria", boolean.class).invoke(u, value);
        } catch (Exception ignore) {
            try { u.getClass().getMethod("setPasswordProvvisoria", boolean.class).invoke(u, value); }
            catch (Exception ignore2) {}
        }
    }
}
