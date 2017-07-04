package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.dounn.menutendina.Adapters.ElementoAdapter;
import com.example.dounn.menutendina.Database.Elemento;
import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class A26_RisultatiRicerca extends SuperActivity {

    RecyclerView recyclerView;
    private ArrayList<Elemento> elementos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a26_layout);
        Log.e("Successo", "Entro nella ricerca");
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        String tmp = extras.getString("query");

        elementos = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.rv_ris_ricerca);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        recyclerView.setVisibility(View.VISIBLE);

        chiamaDatabaseRicercaNomeElemento(tmp);
    }

    public void chiamaDatabaseRicercaNomeElemento(String query) {
        startCaricamento(0, getResources().getString(R.string.Load_search));
        JSONObject req = new JSONObject();
        try {
            req.put("token", getToken());
            req.put("nome", query);
            req.put("path", "search_elemento");
            new Request(new RequestCallback() {
                @Override
                public void inTheEnd(JSONObject a) {
                    //controllo lo stato della richiesta
                    try {
                        if(!a.getString("status").equals("ERROR")) {
                            Log.i("Successo ", "Trovato elemento" + a.getJSONArray("result"));
                            int lenght = a.getJSONArray("result").length();
                            Log.i("Successo", "" + lenght);
                            for(int i = 0; i < lenght; i++) {
                                //NUMERO ELEMENTI ANALIZZATO
                                Elemento elemento = new Elemento(a.getJSONArray("result").getJSONObject(i));
                                elementos.add(elemento);
                            }
                            Log.e("Successo", "Aggiunti " + elementos.size());
                            Intent i = new Intent(ctx, A27_PaginaElemento.class);
                            ElementoAdapter adapter = new ElementoAdapter(ctx, elementos, i);
                            recyclerView.setAdapter(adapter);
                            stopCaricamento(200);
                        } else {
                            Log.e("Successo", "Errore:" + a.getString("status"));
                        }
                    } catch(JSONException e) {
                        Log.e("Successo", " Oggetto json:" + a.toString() + "\nErrore:\n" + e.toString());
                    }
                }

                @Override
                public void noInternetConnection() {
                    noInternetErrorBar();
                }
            }).execute(req);
        } catch(JSONException e) {
            Log.e("Successo", "Errore:\n" + e);
        }
    }
}