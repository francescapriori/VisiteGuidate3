package it.unibs.ingdsw.persistence.xml.writer;

import it.unibs.ingdsw.model.luoghi.ListaLuoghi;
import it.unibs.ingdsw.model.luoghi.Luogo;
import it.unibs.ingdsw.model.luoghi.Posizione;
import it.unibs.ingdsw.model.tempo.*;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.visite.ListaVisite;
import it.unibs.ingdsw.model.visite.Visita;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import it.unibs.ingdsw.persistence.xml.XmlDomWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

import static it.unibs.ingdsw.persistence.xml.XmlDomUtils.*;
import static it.unibs.ingdsw.persistence.xml.XmlDataCodec.*;

public class LuoghiXmlWriter {
    private final File file;

    public LuoghiXmlWriter(File file) {
        this.file = file;
    }

    public void write(ListaLuoghi lista) {
        try {
            Document doc = XmlDomIO.newDocument();
            Element root = doc.createElement("luoghi");
            doc.appendChild(root);

            if (lista != null) {
                for (Luogo l : lista.getListaLuoghi()) {
                    Element eLuogo = appendElement(doc, root, "luogo");
                    eLuogo.setAttribute("id", l.getLuogoID());

                    appendText(doc, eLuogo, "nome", safe(l.getNome()));
                    appendText(doc, eLuogo, "descrizione", safe(l.getDescrizione()));

                    Element ePos = appendElement(doc, eLuogo, "posizione");
                    Posizione p = l.getPosizione();
                    appendText(doc, ePos, "paese", p != null ? safe(p.getPaese()) : "");
                    appendText(doc, ePos, "via", p != null ? safe(p.getVia()) : "");
                    appendText(doc, ePos, "cap", p != null ? safe(p.getCap()) : "");
                    appendText(doc, ePos, "lat", p != null ? String.valueOf(p.getLatitudine()) : "0.0");
                    appendText(doc, ePos, "lon", p != null ? String.valueOf(p.getLongitudine()) : "0.0");

                    Element eVisite = appendElement(doc, eLuogo, "visite");
                    ListaVisite lv = l.getInsiemeVisite();
                    if (lv != null) {
                        for (Visita v : lv.getListaVisite()) {
                            Element eVisita = appendElement(doc, eVisite, "visita");

                            appendText(doc, eVisita, "titolo", safe(v.getTitolo()));
                            appendText(doc, eVisita, "descrizione", safe(v.getDescrizione()));

                            Element ePosV = appendElement(doc, eVisita, "posizione");
                            Posizione pV = v.getLuogoIncontro();
                            appendText(doc, ePosV, "paese", pV != null ? safe(pV.getPaese()) : "");
                            appendText(doc, ePosV, "via", pV != null ? safe(pV.getVia()) : "");
                            appendText(doc, ePosV, "cap", pV != null ? safe(pV.getCap()) : "");
                            appendText(doc, ePosV, "lat", pV != null ? String.valueOf(pV.getLatitudine()) : "0.0");
                            appendText(doc, ePosV, "lon", pV != null ? String.valueOf(pV.getLongitudine()) : "0.0");

                            Element eGiornate = appendElement(doc, eVisita, "giornate");
                            Giornate g = v.getGiornateVisita();
                            if (g != null) {
                                for (GiornoSettimana gs : g.getGiornate()) {
                                    appendText(doc, eGiornate, "giorno", gs.name()); // nel tuo writer originale avevi un if inutile
                                }
                            }

                            Element eValidita = appendElement(doc, eVisita, "validita");
                            writeData(doc, eValidita, "inizio", v.getInizioValiditaVisita());
                            writeData(doc, eValidita, "fine", v.getFineValiditaVisita());

                            Element eOra = appendElement(doc, eVisita, "oraInizio");
                            Orario or = v.getOraInizioVisita();
                            appendText(doc, eOra, "ora", String.valueOf(or != null ? or.getOra() : 0));
                            appendText(doc, eOra, "minuti", String.valueOf(or != null ? or.getMinuti() : 0));

                            appendText(doc, eVisita, "durataMinuti", String.valueOf(v.getDurataMinutiVisita()));
                            appendText(doc, eVisita, "presenzaBiglietto", String.valueOf(v.isPresenzaBiglietto()));

                            Element eVols = appendElement(doc, eVisita, "volontari");
                            var vols = v.getVolontariVisita();
                            if (vols != null) {
                                for (Volontario vol : vols) {
                                    Element eVol = appendElement(doc, eVols, "volontario");
                                    eVol.setAttribute("username", vol != null ? safe(vol.getUsername()) : "");
                                    eVol.setAttribute("password", vol != null ? safe(vol.getPassword()) : "");
                                }
                            }

                            appendText(doc, eVisita, "numeroMinimoPartecipanti", String.valueOf(v.getNumeroMinimoPartecipanti()));
                            appendText(doc, eVisita, "numeroMassimoPartecipanti", String.valueOf(v.getNumeroMassimoPartecipanti()));
                        }
                    }
                }
            }

            XmlDomWriter.save(doc, file);
        } catch (Exception ex) {
            System.err.println("Errore scrittura luoghi: " + ex.getMessage());
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }
}
