package it.unibs.ingdsw.tempo;

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

    @Override
    public String toString() {
        return String.format("%02d:%02d", this.ora, this.minuti);
    }
}
