package it.unibs.ingdsw.tempo;

import it.unibs.ingdsw.utenti.Volontario;
import it.unibs.ingdsw.visite.Visita;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InsiemeDate {
    private ArrayList<Data> insiemeDate;

    public InsiemeDate() {
        this.insiemeDate = new ArrayList();
    }

    public ArrayList<Data> getInsiemeDate() {
        return insiemeDate;
    }


    public boolean aggiungiData(Data data) {
        if(!dataPresente(data)) {
            this.insiemeDate.add(data);
            return true;
        }
        return false;
    }

    public InsiemeDate aggiungiDate(InsiemeDate nuove) {
        if (nuove == null || nuove.getInsiemeDate() == null) return this;

        for (Data d : nuove.getInsiemeDate()) {
            if (d != null) {
                this.aggiungiDataSeNonPresente(d);
            }
        }
        this.ordinaDateCronologicamente();
        return this;
    }


    public boolean aggiungiDataSeNonPresente(Data data) {
        if (data == null) return false;
        for (Data d : this.insiemeDate) {
            if (d.dateUguali(data)) {
                return false;
            }
        }
        this.insiemeDate.add(data);
        return true;
    }


    public void ordinaDateCronologicamente() {
        if (insiemeDate == null || insiemeDate.size() < 2) return;

        for (int i = 1; i < insiemeDate.size(); i++) {
            Data key = insiemeDate.get(i);
            int j = i - 1;
            // sposta in avanti gli elementi maggiori di key
            while (j >= 0 && confronta(insiemeDate.get(j), key) > 0) {
                insiemeDate.set(j + 1, insiemeDate.get(j));
                j--;
            }
            insiemeDate.set(j + 1, key);
        }
    }

    public InsiemeDate getDateEsclusePerMeseAnno (int mese, int anno) {
        InsiemeDate dateEscluse = new InsiemeDate();
        boolean meseConDate = false;
        for(Data d : this.getInsiemeDate()) {
            if(d.getAnno() == anno && d.getMese() == mese) {
                dateEscluse.aggiungiData(d);
            }
        }
        return dateEscluse;
    }

    // <0 se a<b, 0 se uguali, >0 se a>b (cronologico)
    private static int confronta(Data a, Data b) {
        if (a.getAnno() != b.getAnno()) return a.getAnno() - b.getAnno();
        if (a.getMese() != b.getMese()) return a.getMese() - b.getMese();
        return a.getGiorno() - b.getGiorno();
    }


    public boolean dataPresente(Data data) {
        for (Data d : insiemeDate) {
            if (d.dateUguali(data)) {
                return true;
            }
        }
        return false;
    }

    public InsiemeDate dateComune(InsiemeDate date2){
        InsiemeDate dateComuni = new InsiemeDate();

        for(Data d : this.insiemeDate){
            for(Data d2 : date2.getInsiemeDate()){
                if(d.dateUguali(d2)){
                    dateComuni.aggiungiData(d);
                }
            }
        }
        return dateComuni;
    }

    public boolean isEmpty() {
        return insiemeDate == null || insiemeDate.isEmpty();
    }

    public InsiemeDate filtraPerMeseAnno(int mese, int anno) {
        InsiemeDate res = new InsiemeDate();
        for (Data d : this.getInsiemeDate()) {
            if (d.getMese() == mese && d.getAnno() == anno) {
                res.aggiungiData(d);
            }
        }
        return res;
    }

    public static InsiemeDate dateDeiVolontari(HashMap<Volontario, InsiemeDate> dateDiTuttiVolontari, ArrayList<Volontario> voltari) {
        InsiemeDate dateDeiVolontariSelezionati = new InsiemeDate();

        for(Volontario v : voltari){
            for(Map.Entry<Volontario, InsiemeDate> v2 : dateDiTuttiVolontari.entrySet()) {
                Volontario v_iesimo = v2.getKey();
                InsiemeDate candidate = v2.getValue();

                //sono lo stesso utente?
                if(v_iesimo.ugualeA(v)){
                    dateDeiVolontariSelezionati.aggiungiDate(candidate);
                }
            }
        }
        return dateDeiVolontariSelezionati;
    }

    @Override
    public String toString() {
        return insiemeDate.toString();
    }
}
