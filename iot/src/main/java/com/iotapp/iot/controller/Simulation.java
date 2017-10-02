package com.iotapp.iot.controller;

import com.iotapp.iot.activity.HomeActivity;
import com.iotapp.iot.database.Schema;
import com.iotapp.iot.utility.Constant;
import org.json.JSONObject;

/**
 * Created by kundankumar on 11/11/16.
 */
public class Simulation {
    static int i = 4;
    String topic = Constant.ROLE_RETAILER;
    int count = 0;

    public void simulate() {
        try {
            count++;
            if (count == 4) {
                count = 0;
                i = i + 4;
            }
            HomeActivity.serviceConnection.subscribeMsg(topic);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Schema.Scm.INVENTORY, String.valueOf(i));
            HomeActivity.serviceConnection.publisgMsg(topic, jsonObject.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
