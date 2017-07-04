package com.example.dounn.menutendina.Database;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lucadiliello on 12/06/2017.
 */


public class Impostazioni {
    private boolean NuovaRecensioneUtenteCheSeguo;
    private boolean NuovaRecensioneOggettoCheSeguo;
    private boolean NuovoVotoMiaRecensione;
    private boolean NuovaRispostaMiaDomanda;
    private boolean MiaMigliorRisposta;

    Impostazioni(JSONObject impostazioni) throws JSONException {
        NuovaRecensioneUtenteCheSeguo = Boolean.valueOf(impostazioni.getString("NuovaRecensioneUtenteCheSeguo").toLowerCase());
        NuovaRecensioneOggettoCheSeguo = Boolean.valueOf(impostazioni.getString("NuovaRecensioneOggettoCheSeguo").toLowerCase());
        NuovoVotoMiaRecensione = Boolean.valueOf(impostazioni.getString("NuovoVotoMiaRecensione").toLowerCase());
        NuovaRispostaMiaDomanda = Boolean.valueOf(impostazioni.getString("NuovaRispostaMiaDomanda").toLowerCase());
        MiaMigliorRisposta = Boolean.valueOf(impostazioni.getString("MiaMigliorRisposta").toLowerCase());
    }

    public boolean isNuovaRecensioneUtenteCheSeguo() {
        return NuovaRecensioneUtenteCheSeguo;
    }

    public boolean isNuovaRecensioneOggettoCheSeguo() {
        return NuovaRecensioneOggettoCheSeguo;
    }

    public boolean isNuovoVotoMiaRecensione() {
        return NuovoVotoMiaRecensione;
    }

    public boolean isNuovaRispostaMiaDomanda() {
        return NuovaRispostaMiaDomanda;
    }

    public boolean isMiaMigliorRisposta() {
        return MiaMigliorRisposta;
    }
}
