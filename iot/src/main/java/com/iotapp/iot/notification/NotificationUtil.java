package com.iotapp.iot.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import com.iotapp.iot.R;

/**
 * Created by skumari on 9/10/2017.
 */

public class NotificationUtil {
    public void soundNotification(Context context, boolean isCustomSound){

        try {
            Uri notification;
            if (isCustomSound){
                notification = Uri.parse("android.resource://"
                        + context.getPackageName() + "/" + R.raw.ring_tone);

            }else{
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            }
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showNotification(Context context){
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        // the addAction re-use the same intent to keep the example short
        Notification n  = new Notification.Builder(context)
                .setContentTitle("trip started")
                .setContentText("need to track")
                .setSmallIcon(R.mipmap.ic_launcher)
                //.setContentIntent(pIntent)
                .setAutoCancel(true).build();
        //.addAction(R.drawable.icon, "Call", pIntent)
        //.addAction(R.drawable.icon, "More", pIntent)
        //.addAction(R.drawable.icon, "And more", pIntent).build();




        notificationManager.notify(0, n);
    }
}
