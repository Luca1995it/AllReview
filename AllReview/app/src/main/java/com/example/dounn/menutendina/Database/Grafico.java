package com.example.dounn.menutendina.Database;

import com.example.dounn.menutendina.Utility.Utility;
import com.jjoe64.graphview.series.DataPoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by lucadiliello on 12/06/2017.
 */

public class Grafico {

    private DataPoint[] punti;
    private int[] date;

    Grafico(JSONArray result) throws JSONException {

        punti = new DataPoint[result.length() > 2 ? result.length() : 3];
        date = new int[result.length() > 2 ? result.length() : 3];


        if(result.length() > 2) {
            for(int i = 0; i < result.length(); i++) {
                JSONObject o = result.getJSONObject(i);
                punti[i] = new DataPoint(i, o.getInt("points"));
                date[i] = o.getInt("data");
            }
        } else if(result.length() > 0) {
            int i;
            JSONObject o = result.getJSONObject(0);
            for(i = 0; i < result.length(); i++) {
                o = result.getJSONObject(i);
                punti[i] = new DataPoint(i, o.getInt("points"));
                date[i] = o.getInt("data");
            }
            for(; i < punti.length; i++) {
                punti[i] = new DataPoint(i, o.getInt("points"));
                date[i] = o.getInt("data");
            }
        } else {
            for(int i = 0; i < 3; i++) {
                punti[i] = new DataPoint(i, 0);
                date[i] = (int) (System.currentTimeMillis() / 1000);
            }
        }
    }


    public DataPoint[] getPunti() {
        return punti;
    }

    public String[] getDates() {
        String[] res = new String[punti.length];
        if(res.length < 5) {

            for(int j = 0; j < res.length; j++) res[j] = "";

            res[0] = getDate(date[0]);
            res[punti.length / 2] = getDate(date[punti.length / 2]);
            res[punti.length - 1] = getDate(date[punti.length - 1]);

            return res;
        } else {

            for(int j = 0; j < res.length; j++) res[j] = "";

            res[1] = getDate(date[0]);
            res[punti.length / 2] = getDate(date[punti.length / 2]);
            res[punti.length - 2] = getDate(date[punti.length - 1]);

            return res;
        }
    }

    private String getDate(int a) {
        return Utility.format_grafico.format(new Date(a).getTime());
    }
}
