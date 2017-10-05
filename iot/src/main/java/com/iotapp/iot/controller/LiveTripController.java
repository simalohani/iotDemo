package com.iotapp.iot.controller;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Map;

public class LiveTripController  {

    /**
     * This method is used for getting live user name from sharedprefrence and putting into arraylist
     */
    public ArrayList getLiveTrip(Context context){
        ArrayList liveTripList = new ArrayList();
        SharedPreferences sharedpreferences = context.getSharedPreferences("LIVETRIP", Context.MODE_PRIVATE);
        Map<String,?> keys = sharedpreferences.getAll();

        for(Map.Entry<String,?> entry : keys.entrySet()){
           // Log.d("map values",entry.getKey() + ": " + entry.getValue().toString());
            liveTripList.add(entry.getKey());
        }
        return liveTripList;
    }

}
