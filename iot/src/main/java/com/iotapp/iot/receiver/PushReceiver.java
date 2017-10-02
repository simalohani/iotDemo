package com.iotapp.iot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.iotapp.iot.service.MQTTservice;

/**
 * Created by kundankumar on 01/10/16.
 */
/*public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {
        String topic = i.getStringExtra(MQTTservice.TOPIC);
        String message = i.getStringExtra(MQTTservice.MESSAGE);
        Toast.makeText(context,
                "Push message received - " + topic + ":" + message,
                Toast.LENGTH_LONG).show();
    }
}*/
