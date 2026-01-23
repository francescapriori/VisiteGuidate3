package it.unibs.ingdsw.model.luoghi;

public class Posizione {
    public static final int LUNGHEZZA_CAP = 5;
    public static final double LAT_MIN = -90.0;
    public static final double LAT_MAX = 90.0;
    public static final double LON_MIN = -180.0;
    public static final double LON_MAX = 180.0;

    public String paese;
    public String via;
    public String cap;
    public double latitudine;
    public double longitudine;

    public Posizione(String paese, String via, String cap, double latitudine, double longitudine) {
        this.paese = paese;
        this.via = via;
        this.cap = cap;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
    }

    public String getPaese() {
        return paese;
    }

    public String getVia() {
        return via;
    }

    public String getCap() {
        return cap;
    }

    public double getLatitudine() {
        return latitudine;
    }

    public double getLongitudine() {
        return longitudine;
    }
}
