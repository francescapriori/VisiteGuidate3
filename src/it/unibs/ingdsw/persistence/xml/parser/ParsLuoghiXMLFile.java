package it.unibs.ingdsw.persistence.xml.parser;

import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.luoghi.Posizione;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.Giornate;
import it.unibs.ingdsw.model.tempo.GiornoSettimana;
import it.unibs.ingdsw.model.tempo.Orario;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

public class ParsLuoghiXMLFile {

    private static final String DATA = "luoghi.xml";

    private final ListaLuoghi listaLuoghi = new ListaLuoghi();

    public ParsLuoghiXMLFile() {
        try {
            parseXML();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.err.println("Errore nel parsing XML luoghi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ListaLuoghi getListaLuoghi() {
        return listaLuoghi;
    }

    private void parseXML() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = XmlDocumentBuilderProvider.newSecureBuilder();
        File xmlFile = new File(DATA);
        if (!xmlFile.exists()) {
            System.err.println("File luoghi XML non trovato -> " + xmlFile.getAbsolutePath());
            return;
        }

        Document doc = db.parse(xmlFile);
        doc.getDocumentElement().normalize();

        Element root = doc.getDocumentElement();
        if (!"luoghi".equals(root.getTagName())) {
            throw new IllegalArgumentException("Root <luoghi> attesa, trovata <" + root.getTagName() + ">.");
        }

        NodeList nl = root.getElementsByTagName("luogo");
        for (int i = 0; i < nl.getLength(); i++) {
            Element eLuogo = (Element) nl.item(i);

            String id = eLuogo.getAttribute("id");
            String nome = XmlElementReader.getText(eLuogo, "nome", "");
            String desc = XmlElementReader.getText(eLuogo, "descrizione", "");

            Element ePos = XmlElementReader.getFirst(eLuogo, "posizione");
            Posizione pos = parsePosizione(ePos);

            ListaVisite lv = parseVisite(XmlElementReader.getFirst(eLuogo, "visite"), id);

            listaLuoghi.aggiungiLuogo(new Luogo(id, nome, desc, pos, lv));
        }
    }

    private Posizione parsePosizione(Element ePos) {
        if (ePos == null) return new Posizione(null, null, null, 0d, 0d);
        String paese = XmlElementReader.getText(ePos, "paese", null);
        String via = XmlElementReader.getText(ePos, "via", null);
        String cap = XmlElementReader.getText(ePos, "cap", null);
        double lat = XmlElementReader.getDouble(ePos, "lat", 0.0);
        double lon = XmlElementReader.getDouble(ePos, "lon", 0.0);
        return new Posizione(paese, via, cap, lat, lon);
    }

    private ListaVisite parseVisite(Element eVisite, String luogoId) {
        ListaVisite lv = new ListaVisite();
        if (eVisite == null) return lv;

        NodeList visite = eVisite.getElementsByTagName("visita");
        for (int i = 0; i < visite.getLength(); i++) {
            Element eV = (Element) visite.item(i);

            String titolo = XmlElementReader.getText(eV, "titolo", "");
            String descr = XmlElementReader.getText(eV, "descrizione", "");

            Posizione posV = parsePosizione(XmlElementReader.getFirst(eV, "posizione"));

            Giornate giornate = parseGiornate(XmlElementReader.getFirst(eV, "giornate"));

            Element eVal = XmlElementReader.getFirst(eV, "validita");
            Data inizio = parseData(XmlElementReader.getFirst(eVal, "inizio"));
            Data fine = parseData(XmlElementReader.getFirst(eVal, "fine"));

            Element eOra = XmlElementReader.getFirst(eV, "oraInizio");
            Orario ora = new Orario(XmlElementReader.getInt(eOra, "ora", 0),
                    XmlElementReader.getInt(eOra, "minuti", 0));

            int durata = XmlElementReader.getInt(eV, "durataMinuti", 0);
            boolean bigl = XmlElementReader.getBoolean(eV, "presenzaBiglietto", false);

            ArrayList<Volontario> vols = parseVolontari(XmlElementReader.getFirst(eV, "volontari"));

            int minP = XmlElementReader.getInt(eV, "numeroMinimoPartecipanti", 0);
            int maxP = XmlElementReader.getInt(eV, "numeroMassimoPartecipanti", 0);

            Visita visita = new Visita(
                    titolo, descr, luogoId, posV, giornate,
                    inizio, fine, ora, durata, bigl, vols, minP, maxP
            );
            lv.aggiungiVisita(visita);
        }
        return lv;
    }

    private ArrayList<Volontario> parseVolontari(Element eVols) {
        ArrayList<Volontario> res = new ArrayList<>();
        if (eVols == null) return res;
        NodeList nl = eVols.getElementsByTagName("volontario");
        for (int i = 0; i < nl.getLength(); i++) {
            Element e = (Element) nl.item(i);
            res.add(new Volontario(e.getAttribute("username"), e.getAttribute("password")));
        }
        return res;
    }

    private Giornate parseGiornate(Element eG) {
        Giornate g = new Giornate();
        if (eG == null) return g;
        NodeList giorni = eG.getElementsByTagName("giorno");
        for (int i = 0; i < giorni.getLength(); i++) {
            Node n = giorni.item(i);
            if (n.getNodeType() != Node.ELEMENT_NODE) continue;
            String raw = n.getTextContent();
            if (raw == null) continue;
            String norm = raw.trim().toUpperCase(Locale.ITALIAN).replace('Ì', 'I'); // GIOVEDÌ -> GIOVEDI
            try {
                g.aggiungiGiornoDellaSettimana(GiornoSettimana.valueOf(norm));
            } catch (IllegalArgumentException ex) {
                System.err.println("GiornoSettimana non valido nel XML: '" + raw + "'");
            }
        }
        return g;
    }

    private Data parseData(Element e) {
        if (e == null) return new Data(1,1,1970);
        int g = XmlElementReader.getInt(e, "giorno", 1);
        int m = XmlElementReader.getInt(e, "mese", 1);
        int a = XmlElementReader.getInt(e, "anno", 1970);
        Data d = new Data(g, m, a);
        if (!d.dataValida()) System.err.println("Data non valida: " + d);
        return d;
    }

    public static void salvaLuoghi(ListaLuoghi lista) {
        try {
            Document doc = XmlDocumentBuilderProvider.newDocument();

            Element root = doc.createElement("luoghi");
            doc.appendChild(root);

            for (Luogo l : lista.getListaLuoghi()) {
                Element eLuogo = doc.createElement("luogo");
                eLuogo.setAttribute("id", l.getLuogoID());
                root.appendChild(eLuogo);

                XmlElementWriter.appendText(doc, eLuogo, "nome", l.getNome());
                XmlElementWriter.appendText(doc, eLuogo, "descrizione", l.getDescrizione());

                Element ePos = doc.createElement("posizione");
                eLuogo.appendChild(ePos);
                Posizione p = l.getPosizione();
                if (p != null) {
                    XmlElementWriter.appendText(doc, ePos, "paese", XmlElementWriter.safe(p.getPaese()));
                    XmlElementWriter.appendText(doc, ePos, "via",   XmlElementWriter.safe(p.getVia()));
                    XmlElementWriter.appendText(doc, ePos, "cap",   XmlElementWriter.safe(p.getCap()));
                    XmlElementWriter.appendText(doc, ePos, "lat",   String.valueOf(p.getLatitudine()));
                    XmlElementWriter.appendText(doc, ePos, "lon",   String.valueOf(p.getLongitudine()));
                }

                Element eVisite = doc.createElement("visite");
                eLuogo.appendChild(eVisite);
                ListaVisite lv = l.getInsiemeVisite();
                if (lv != null) {
                    for (Visita v : lv.getListaVisite()) {
                        Element eVisita = doc.createElement("visita");
                        eVisite.appendChild(eVisita);

                        XmlElementWriter.appendText(doc, eVisita, "titolo", v.getTitolo());
                        XmlElementWriter.appendText(doc, eVisita, "descrizione", v.getDescrizione());

                        Element ePosV = doc.createElement("posizione");
                        eVisita.appendChild(ePosV);
                        Posizione pV = v.getLuogoIncontro();
                        if (pV != null) {
                            XmlElementWriter.appendText(doc, ePosV, "paese", XmlElementWriter.safe(pV.getPaese()));
                            XmlElementWriter.appendText(doc, ePosV, "via",   XmlElementWriter.safe(pV.getVia()));
                            XmlElementWriter.appendText(doc, ePosV, "cap",   XmlElementWriter.safe(pV.getCap()));
                            XmlElementWriter.appendText(doc, ePosV, "lat",   String.valueOf(pV.getLatitudine()));
                            XmlElementWriter.appendText(doc, ePosV, "lon",   String.valueOf(pV.getLongitudine()));
                        }

                        Element eGiornate = doc.createElement("giornate");
                        eVisita.appendChild(eGiornate);
                        Giornate g = v.getGiornateVisita();
                        if (g != null) {
                            for (GiornoSettimana gs : g.getGiornate()) {
                                String giorno = "GIORVEDI".equals(gs.name()) ? "GIOVEDI" : gs.name();
                                XmlElementWriter.appendText(doc, eGiornate, "giorno", giorno);
                            }
                        }

                        Element eValidita = doc.createElement("validita");
                        eVisita.appendChild(eValidita);
                        XmlElementWriter.appendData(doc, eValidita, "inizio", v.getInizioValiditaVisita());
                        XmlElementWriter.appendData(doc, eValidita, "fine",   v.getFineValiditaVisita());

                        Element eOra = doc.createElement("oraInizio");
                        eVisita.appendChild(eOra);
                        Orario or = v.getOraInizioVisita();
                        XmlElementWriter.appendText(doc, eOra, "ora",    String.valueOf(or != null ? or.getOra()    : 0));
                        XmlElementWriter.appendText(doc, eOra, "minuti", String.valueOf(or != null ? or.getMinuti() : 0));

                        XmlElementWriter.appendText(doc, eVisita, "durataMinuti",
                                String.valueOf(v.getDurataMinutiVisita()));
                        XmlElementWriter.appendText(doc, eVisita, "presenzaBiglietto",
                                String.valueOf(v.isPresenzaBiglietto()));

                        Element eVols = doc.createElement("volontari");
                        eVisita.appendChild(eVols);
                        var vols = v.getVolontariVisita();
                        if (vols != null) {
                            for (Volontario vol : vols) {
                                Element eV = doc.createElement("volontario");
                                eV.setAttribute("username", XmlElementWriter.safe(vol.getUsername()));
                                eV.setAttribute("password", XmlElementWriter.safe(vol.getPassword()));
                                eVols.appendChild(eV);
                            }
                        }

                        XmlElementWriter.appendText(doc, eVisita, "numeroMinimoPartecipanti",
                                String.valueOf(v.getNumeroMinimoPartecipanti()));
                        XmlElementWriter.appendText(doc, eVisita, "numeroMassimoPartecipanti",
                                String.valueOf(v.getNumeroMassimoPartecipanti()));
                    }
                }
            }

            File out = new File(DATA);
            if (out.getParentFile() != null) out.getParentFile().mkdirs();

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.setOutputProperty(OutputKeys.INDENT, "yes");
            t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            t.transform(new DOMSource(doc), new StreamResult(out));
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }


}
