package com.iotapp.iot.controller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import com.iotapp.iot.receiver.AlarmReceiver;
import com.iotapp.iot.receiver.BootReceiver;
import com.iotapp.iot.utility.Constant;

/**
 * Created by kundankumar on 02/10/16.
 */
public class AlarmHandler {
    /**
     * creating alarm receiver for the device which does not support sync download
     */
    public static final int DW_HEADER = 1;
    public static final String SYNC_TYPE = "type";
    public static final String SYNC_HEADER = "header";

    public static void start(Context ctx, int alarmType, int gpsInterval) {

        AlarmManager manager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (alarmType == Constant.GPS_ALARM_ID) {//gpsInterval
            manager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 1000 * gpsInterval, setPeriodicAlarm(ctx, DW_HEADER, Constant.GPS_ID));
        }

        ComponentName receiver = new ComponentName(ctx, BootReceiver.class);
        PackageManager pm = ctx.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private static Bundle getPeriodicSyncBundle(int header, int syncId) {
        Bundle b = new Bundle();
        b.putInt(SYNC_HEADER, header);
        b.putInt(SYNC_TYPE, syncId);
        return b;
    }


    private static PendingIntent setPeriodicAlarm(Context ctx, int header, int syncId) {
        Intent alarmIntent = new Intent(ctx, AlarmReceiver.class);
        alarmIntent.putExtras(getPeriodicSyncBundle(header, syncId));
        //alarmIntent.putExtra(String.valueOf(syncId),getPeriodicSyncBundle(header,syncId));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, syncId, alarmIntent, 0);
        return pendingIntent;

    }

    public static void removeAlarmSync(Context ctx, int alarmType) {
        try {
            AlarmManager manager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
            if (manager == null)
                return;
            if (alarmType == Constant.GPS_ALARM_ID) {
                manager.cancel(setPeriodicAlarm(ctx, DW_HEADER, Constant.GPS_ID));
            }

            // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
            // alarm when the device is rebooted.

            /*ComponentName receiver = new ComponentName(ctx, BootReceiver.class);
            PackageManager pm = ctx.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);*/
        } catch (Exception e) {
            //Logger.getInstance().error(e);
        }


    }
}
