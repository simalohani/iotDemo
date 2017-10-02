package com.iotapp.iot.receiver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import com.iotapp.iot.controller.AlarmHandler;
import com.iotapp.iot.gps.LocationService;
import com.iotapp.iot.utility.Constant;
import com.iotapp.iot.utility.Util;

/**
 * Created by kundankumar on 22/02/16.
 */
// make sure we use a WakefulBroadcastReceiver so that we acquire a partial wakelock
public class AlarmReceiver extends WakefulBroadcastReceiver {
    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {


        try {
            Bundle extras = intent.getExtras();
            int header = extras.getInt(AlarmHandler.SYNC_HEADER);
            int type = extras.getInt(AlarmHandler.SYNC_TYPE);
           // Util.getInstance().setContext(context);
            if (header == AlarmHandler.DW_HEADER) {
                switch (type) {
                    case Constant.GPS_ID:
                        context.startService(new Intent(context, LocationService.class));
                        break;
                }
            }
        } catch (Exception e) {
            // Logger.getInstance().error(e);
        }

    }

}
