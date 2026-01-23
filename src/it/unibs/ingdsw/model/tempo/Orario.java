package it.unibs.ingdsw.model.tempo;

public class Orario {

    private int ora;
    private int minuti;


    public Orario(int ora, int minuti) {
        this.ora = ora;
        this.minuti = minuti;
    }

    public int getOra() {
        return ora;
    }
    public int getMinuti() {
        return minuti;
    }
}
