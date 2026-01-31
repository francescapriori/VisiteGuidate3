package it.unibs.ingdsw.persistence.xml.reader;

import it.unibs.ingdsw.model.applicazione.StatoProduzioneVisite;
import it.unibs.ingdsw.model.applicazione.StatoRichiestaDisponibilita;
import it.unibs.ingdsw.model.applicazione.Target;
import it.unibs.ingdsw.model.applicazione.TargetTipo;
import it.unibs.ingdsw.persistence.xml.XmlDomIO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.time.YearMonth;

public class ParametriApplicazioneXmlReader {
    private final File file;

    public ParametriApplicazioneXmlReader(File file) {
        this.file = file;
    }

    public Result readOrDefault() {
        Result r = new Result();
        r.ambitoTerritoriale = "";
        r.numeroMax = 0;
        r.ambienteDaConfigurare = false;
        r.statoDisp = StatoRichiestaDisponibilita.DISP_CHIUSE;
        r.statoProduzione = StatoProduzioneVisite.NON_PRODOTTE;
        r.nextDisponibilita = null;
        r.shouldRewrite = false;

        if (!file.exists()) {
            r.nextDisponibilita = calcolaDefaultNextDisponibilita();
            r.shouldRewrite = true;
            return r;
        }

        try {
            Document doc = XmlDomIO.load(file, "applicazione");
            Element root = doc.getDocumentElement();

            r.ambienteDaConfigurare = Boolean.parseBoolean(get(root, "ambienteDaConfigurare", "false"));
            r.ambitoTerritoriale = get(root, "ambitoTerritorialeCompetenza", "");
            r.numeroMax = parseInt(get(root, "numeroMaxPerIniziativa", "0"), 0);

            r.statoDisp = parseEnum(get(root, "stato", "DISP_CHIUSE"), StatoRichiestaDisponibilita.DISP_CHIUSE, StatoRichiestaDisponibilita.class);
            r.statoProduzione = parseEnum(get(root, "statoProduzione", "NON_PRODOTTE"), StatoProduzioneVisite.NON_PRODOTTE, StatoProduzioneVisite.class);

            String nextTxt = get(root, "nextDisponibilita", "");
            if (nextTxt.isBlank()) {
                r.nextDisponibilita = calcolaDefaultNextDisponibilita();
                r.shouldRewrite = true;
            } else {
                try {
                    r.nextDisponibilita = YearMonth.parse(nextTxt.trim());
                } catch (Exception ex) {
                    r.nextDisponibilita = calcolaDefaultNextDisponibilita();
                    r.shouldRewrite = true;
                }
            }

            if (r.nextDisponibilita == null) {
                r.nextDisponibilita = calcolaDefaultNextDisponibilita();
                r.shouldRewrite = true;
            }

        } catch (Exception ex) {
            r.nextDisponibilita = calcolaDefaultNextDisponibilita();
            r.shouldRewrite = true;
        }

        return r;
    }

    private static String get(Element root, String tag, String def) {
        var nl = root.getElementsByTagName(tag);
        if (nl.getLength() == 0 || nl.item(0) == null) return def;
        String t = nl.item(0).getTextContent();
        return t == null ? def : t.trim();
    }

    private static int parseInt(String s, int def) {
        try { return Integer.parseInt(s.trim()); }
        catch (Exception ex) { return def; }
    }

    private static <E extends Enum<E>> E parseEnum(String s, E def, Class<E> clazz) {
        try { return Enum.valueOf(clazz, s.trim()); }
        catch (Exception ex) { return def; }
    }

    private static YearMonth calcolaDefaultNextDisponibilita() {
        try {
            Target targetApplicazione = new Target();
            return targetApplicazione.calcolaDataTarget(TargetTipo.DISPONIBILITA);
        } catch (Exception ex) {
            return null;
        }
    }

    public static class Result {
        public String ambitoTerritoriale;
        public int numeroMax;
        public boolean ambienteDaConfigurare;
        public StatoRichiestaDisponibilita statoDisp;
        public StatoProduzioneVisite statoProduzione;
        public YearMonth nextDisponibilita;
        public boolean shouldRewrite;
    }
}
