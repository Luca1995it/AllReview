package com.example.dounn.menutendina.Utility;

/**
 * Created by Enrico on 12/06/2017.
 */

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by lucadiliello on 08/06/2017.
 */

public class  CheckConnection extends AsyncTask<Void,Void,Boolean> {
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            HttpURLConnection urlc = (HttpURLConnection)
                    (new URL("http://clients3.google.com/generate_204")
                            .openConnection());
            urlc.setRequestProperty("User-Agent", "Android");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == 204 &&
                    urlc.getContentLength() == 0);
        } catch (IOException e) {
            Log.e("Prova connessione errore " , "Errore nel tentativo di connessione al server");
        }
        return false;
    }
}