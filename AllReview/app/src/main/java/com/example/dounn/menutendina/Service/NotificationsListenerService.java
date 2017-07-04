package com.example.dounn.menutendina.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.dounn.menutendina.A14_Notifiche;
import com.example.dounn.menutendina.Database.Notifica;
import com.example.dounn.menutendina.Database.UtenteLogged;
import com.example.dounn.menutendina.R;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.Gson;

import org.json.JSONObject;

/**
 * Created by lucadiliello on 02/07/2017.
 */

public class NotificationsListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String s, Bundle bundle) {
        super.onMessageReceived(s, bundle);
        Log.e("Service", "message received: " + s);
        Log.e("Service", "Content: " + bundle.toString());

        try {
            String not = bundle.getString("notifica");
            Log.e("Service", "Response not: " + not);
            Notifica notifica = new Notifica(new JSONObject(not), this);

            SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
            String json = pref.getString("user", "");
            if(json.equals("")) throw new Exception("prefError");
            Gson gson = new Gson();
            UtenteLogged user = gson.fromJson(json, UtenteLogged.class);
            if(user.getId() != notifica.getIdProprietario()) throw new Exception("utente diverso");

            Intent intent = new Intent(this, A14_Notifiche.class);
            PendingIntent intent2 = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.ic_cloud_queue_black_24dp)
                            .setContentTitle("AllReview")
                            .setContentText(notifica.toString())
                            .setContentIntent(intent2)
                            .setTicker("All Review Notification")
                            .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);

            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, mBuilder.build());

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}