package it.unibs.ingdsw.model.applicazione;

/*
    La classe TargetTipo modella i diversi tipi di target, per ciascuno dei quali viene incapsulata
    la regola che associa un numero di mesi da aggiungere in base alla posizione temporale corrente
    rispetto alla soglia definita.
    Tali informazioni vengono utilizzate dalla classe Target per il calcolo della data target.
 */

public enum TargetTipo {
    DISPONIBILITA(2, 3),
    ESCLUSIONE(3, 4),
    PRODUZIONE(1, 2);

    private final int primaSoglia;
    private final int dopoSoglia;

    TargetTipo(int primaSoglia, int dopoSoglia) {
        this.primaSoglia = primaSoglia;
        this.dopoSoglia = dopoSoglia;
    }

    public int mesiPrimaSoglia() {return primaSoglia;}
    public int mesiDopoSoglia()  { return dopoSoglia;}
}
