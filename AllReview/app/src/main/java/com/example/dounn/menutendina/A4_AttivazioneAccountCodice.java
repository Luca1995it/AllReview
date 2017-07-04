package com.example.dounn.menutendina;

/**
 * Created by dounn on 31/05/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dounn.menutendina.Utility.Request;
import com.example.dounn.menutendina.Utility.RequestCallback;

import org.json.JSONException;
import org.json.JSONObject;


public class A4_AttivazioneAccountCodice extends SuperActivity {

    private EditText inserimentoCodice;
    private TextView erroreInvioCodice;
    private TextView erroreReinviaEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a4_layout);

        inserimentoCodice = (EditText) findViewById(R.id.inserisci_codice);
        Button inviaCodice = (Button) findViewById(R.id.bottone_invia_codice);
        TextView inviaNuovamenteMail = (TextView) findViewById(R.id.testo_reinvia_email);


        //listener per l'invio del codice
        inviaCodice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLogged()) {
                    //creo oggetto JSON con dentro il codice inserito dall'utente
                    JSONObject req = new JSONObject();
                    try {
                        //inserisco nella richiesta il token in sessione e il codice inviatomi per e-mail
                        req.put("code", inserimentoCodice.getText());
                        req.put("token", getToken());
                        req.put("path", "attiva");

                        new Request(new RequestCallback() {

                            @Override
                            public void inTheEnd(JSONObject a) {
                                try {
                                    if(a != null) {
                                        Log.d("Risultati conferma codice:", a.toString());
                                        if(!a.getString("status").equals("ERROR")) {
                                            //inserisco il token nella sessione
                                            getUser().setAttivato(true);
                                            Intent intent = new Intent(ctx, A10_HomePage.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            //TODO messaggio di avvenuta registrazione con successo
                                        } else {
                                            //se ci sono stati problemi mostro la stringa di errore
                                            erroreInvioCodice = (TextView) findViewById(R.id.errore_conferma_codice);
                                            erroreInvioCodice.setVisibility(View.VISIBLE);
                                            //attivo il listener per cancellare l'errore
                                            cancellaMessaggioErroreCodice();
                                        }
                                    } else {
                                        Log.e("Risultati accesso errore", "Errore nella connessione al server nella Conferma codice");
                                    }
                                } catch(JSONException e) {
                                    Log.e("errore post invio dati server attiva account", "\nErrore:\n" + e.toString());
                                }

                            }


                            @Override
                            public void noInternetConnection() {
                                noInternetErrorBar();
                            }
                        }).execute(req);
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    //se ci sono stati problemi con il token mostro un errore
                    erroreInvioCodice = (TextView) findViewById(R.id.errore_conferma_codice);
                    erroreInvioCodice.setVisibility(View.VISIBLE);
                    //attivo il listener per cancellare l'errore
                    cancellaMessaggioErroreCodice();
                }
            }
        });

        //richiesta reinvio email
        inviaNuovamenteMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject req = new JSONObject();
                try {
                    //inserisco nella richiesta il token in sessione
                    req.put("token", getToken());
                    req.put("path", "reinvio_email");
                } catch(JSONException e) {
                    Log.e("errore nella creazione rerquest reinvio email della email:/n", e.toString());
                }

                new Request(new RequestCallback() {
                    @Override
                    public void inTheEnd(JSONObject a) {
                        try {
                            if(a != null) {
                                //Log.d("Risultati reinviao email:", a.toString());
                                //se non ci sono stati errori mostro un toast
                                if(!a.getString("status").equals("ERROR")) {
                                    successBar(getResources().getString(R.string.Success_email), 3000);
                                } else {
                                    //altrimenti faccio comparire messaggio di errore
                                    erroreReinviaEmail = (TextView) findViewById(R.id.errore_reinvio_email);
                                    erroreReinviaEmail.setVisibility(View.VISIBLE);
                                }
                            } else {
                                Log.e("Risultati accesso errore", "Errore nella connessione al server nella Registrazione");
                            }
                        } catch(JSONException e) {
                            Log.e("errore nella risposta per il reinvio della email:/n", e.toString());
                        }
                    }

                    @Override
                    public void noInternetConnection() {
                        noInternetErrorBar();
                    }
                }).execute(req);
            }
        });
    }


    public void cancellaMessaggioErroreCodice() {

        //listener che ascoltano il cambio del testo
        inserimentoCodice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                erroreInvioCodice.setVisibility(View.GONE);
            }
        });
    }
}