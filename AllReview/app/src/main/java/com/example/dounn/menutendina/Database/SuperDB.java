package com.example.dounn.menutendina.Database;

import com.example.dounn.menutendina.SuperActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lucadiliello on 03/07/2017.
 * Modified by lucadiliello on 03/07/2017
 */

public class SuperDB {

    int data;

    SuperDB(int data) {
        this.data = data;
    }

    public String reduceData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(new Date(data * 1000L));
    }

    public String completeData() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        return formatter.format(new Date(data * 1000L));
    }

}
