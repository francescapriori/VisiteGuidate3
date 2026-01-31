package it.unibs.ingdsw.persistence.xml.writer;

import it.unibs.ingdsw.model.utenti.ListaUtenti;
import it.unibs.ingdsw.model.utenti.Utente;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import it.unibs.ingdsw.persistence.xml.XmlDomWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

public class UtentiXmlWriter {
    private final File file;

    public UtentiXmlWriter(File file) {
        this.file = file;
    }

    public void write(ListaUtenti lista) {
        try {
            Document doc = XmlDomIO.newDocument();
            Element root = doc.createElement("utenti");
            doc.appendChild(root);

            if (lista != null) {
                for (Utente utente : lista.getListaUtenti()) {
                    String tag = switch (utente.getRuolo()) {
                        case CONFIGURATORE -> "configuratore";
                        case VOLONTARIO -> "volontario";
                        case FRUITORE -> "fruitore";
                    };

                    Element e = doc.createElement(tag);
                    e.setAttribute("username", utente.getUsername());
                    e.setAttribute("password", utente.getPassword());
                    e.setAttribute("pwProvvisoria", String.valueOf(getPwProvvisoriaSafe(utente)));
                    root.appendChild(e);
                }
            }

            XmlDomWriter.save(doc, file);
        } catch (Exception ex) {
            System.err.println("Errore scrittura utenti: " + ex.getMessage());
        }
    }

    private static boolean getPwProvvisoriaSafe(Utente u) {
        try { return (boolean) u.getClass().getMethod("isPwProvvisoria").invoke(u); }
        catch (Exception e1) {
            try { return (boolean) u.getClass().getMethod("getPwProvvisoria").invoke(u); }
            catch (Exception e2) {
                try { return (boolean) u.getClass().getMethod("isPasswordProvvisoria").invoke(u); }
                catch (Exception e3) { return false; }
            }
        }
    }
}
