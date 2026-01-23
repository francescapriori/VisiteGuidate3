package it.unibs.ingdsw.service;

import it.unibs.ingdsw.model.applicazione.Applicazione;
import it.unibs.ingdsw.model.applicazione.Target;
import it.unibs.ingdsw.model.tempo.Data;
import it.unibs.ingdsw.model.tempo.InsiemeDate;
import it.unibs.ingdsw.model.utenti.Volontario;
import it.unibs.ingdsw.model.appuntamenti.Appuntamento;
import it.unibs.ingdsw.model.appuntamenti.InsiemeAppuntamenti;
import it.unibs.ingdsw.model.visite.Visita;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServiceAppuntamenti {

    private final Applicazione applicazione;

    public ServiceAppuntamenti(Applicazione applicazione) {
        this.applicazione = applicazione;
    }

    public void aggiornaStati(InsiemeAppuntamenti calendario, LocalDate oggi) {
        for (Appuntamento a : calendario.getAppuntamenti()) {
            a.aggiornaStato(oggi);
        }
    }

    public boolean volontarioGiaPresenteInData(Data data, Volontario vol) {
        for (Appuntamento a : this.applicazione.getCalendarioAppuntamenti().getAppuntamenti()) {
            if (a.getData().dateUguali(data) && a.getGuida() != null && a.getGuida().utenteUguale(vol)) {
                return true;
            }
        }
        return false;
    }

    public InsiemeAppuntamenti getAppuntamentiDelMeseTarget(int mese, int anno) {
        InsiemeAppuntamenti result = new InsiemeAppuntamenti();

        LocalDate start = LocalDate.of(anno, mese, Target.SOGLIA_CAMBIO_MESE);

        int meseSuccessivo = (mese == 12) ? 1 : mese + 1;
        int annoSuccessivo = (mese == 12) ? anno + 1 : anno;

        LocalDate endExclusive = LocalDate.of(annoSuccessivo, meseSuccessivo, Target.SOGLIA_CAMBIO_MESE);

        for (Appuntamento a : this.applicazione.getCalendarioAppuntamenti().getAppuntamenti()) {
            LocalDate d = a.getData().toLocalDate();
            if (!d.isBefore(start) && d.isBefore(endExclusive)) {
                result.getAppuntamenti().add(a);
            }
        }

        return result;
    }

    public void salvaCalendario(InsiemeAppuntamenti calendario) {
        this.applicazione.setCalendarioAppuntamenti(calendario);
    }

    public ArrayList<Appuntamento> getAppuntamentiDellUtente(Volontario volontario) {
        ArrayList<Appuntamento> appuntamentoDellUtente = new ArrayList<>();
        for (Appuntamento a : this.applicazione.getCalendarioAppuntamenti().getAppuntamenti()) {
            if (a.getGuida().utenteUguale(volontario)) {
                appuntamentoDellUtente.add(a);
            }
        }
        return appuntamentoDellUtente;
    }

    public InsiemeAppuntamenti produciVisitePerIlMese(int meseTargetV, int annoTargetV) {
        ServiceVisite serviceVisite = new ServiceVisite(this.applicazione);
        HashMap<Visita, InsiemeDate> calendarioProvvisorio =
                serviceVisite.calendarioProvvisiorioVisiteDelMese(meseTargetV, annoTargetV);

        InsiemeAppuntamenti calendarioAppuntamenti = new InsiemeAppuntamenti();
        HashMap<Volontario, InsiemeDate> volontariConDate = this.applicazione.getDisponibilitaPerVol();

        for (Map.Entry<Visita, InsiemeDate> entry : calendarioProvvisorio.entrySet()) {
            Visita visita = entry.getKey();
            InsiemeDate dateCalendarioProvvisorio = entry.getValue();

            for (Data d1 : dateCalendarioProvvisorio.getInsiemeDate()) {

                boolean assegnato = false;

                for (Map.Entry<Volontario, InsiemeDate> entry2 : volontariConDate.entrySet()) {
                    Volontario volontario = entry2.getKey();
                    InsiemeDate dateDisponibilitaVolontario = entry2.getValue();

                    if (dateDisponibilitaVolontario.dataPresente(d1) &&
                            !volontarioGiaPresenteInData(d1, volontario)) {

                        calendarioAppuntamenti.getAppuntamenti()
                                .add(new Appuntamento(visita, d1, volontario));

                        assegnato = true;
                        break;
                    }
                }

            }
        }

        return calendarioAppuntamenti;
    }
}
