package it.unibs.ingdsw.model.tempo;

import java.util.ArrayList;

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

    public void ordinaDateCronologicamente() {
        if (insiemeDate == null || insiemeDate.size() < 2) return;

        for (int i = 1; i < insiemeDate.size(); i++) {
            Data key = insiemeDate.get(i);
            int j = i - 1;
            while (j >= 0 && confronta(insiemeDate.get(j), key) > 0) {
                insiemeDate.set(j + 1, insiemeDate.get(j));
                j--;
            }
            insiemeDate.set(j + 1, key);
        }
    }

    public InsiemeDate getDatePerMeseAnno(int mese, int anno) {
        InsiemeDate dateEscluse = new InsiemeDate();
        for(Data d : this.getInsiemeDate()) {
            if(d.getAnno() == anno && d.getMese() == mese) {
                dateEscluse.aggiungiData(d);
            }
        }
        return dateEscluse;
    }

    // forse spostare in Data e farlo non statico
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

    public InsiemeDate filtraDateDopo(Data d1) {
        ordinaDateCronologicamente();
        InsiemeDate res = new InsiemeDate();
        for(Data d : this.getInsiemeDate()) {
            if(d.segue(d1)) {
                res.aggiungiData(d);
            }
        }
        return res;
    }
}
