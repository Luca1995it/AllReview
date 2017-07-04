package com.example.dounn.menutendina;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.dounn.menutendina.Service.RegistrationService;

public class A0_LaunchActivity extends SuperActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Service","Starting");
        Intent intent = new Intent(this, RegistrationService.class);
        startService(intent);

        Intent i;
        if(isLogged()) {
            i = new Intent(ctx, A10_HomePage.class);
        } else {
            i = new Intent(ctx, A1_LoginIniziale.class);
        }

        startActivity(i);
        finish();
    }
}
