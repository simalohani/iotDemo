package com.iotapp.iot.custom;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.Settings;
import android.widget.RemoteViews;
import com.iotapp.iot.R;
import com.iotapp.iot.activity.HomeActivity;
import com.iotapp.iot.utility.Util;

/**
 * Created by kundankumar on 22/07/16.
 */
public class NotificationAlert {

    /*private Notification createNotification() {
        Notification notification = new Notification();

        notification.icon = R.drawable.ic_launcher;
        notification.when = System.currentTimeMillis();

        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;

        notification.defaults |= Notification.DEFAULT_VIBRATE;
        notification.defaults |= Notification.DEFAULT_LIGHTS;

        notification.ledARGB = Color.WHITE;
        notification.ledOnMS = 1500;
        notification.ledOffMS = 1500;

        return notification;
    }*/

    public void showNotification(Context context, String message, int id, int scrNo,int ledColor,boolean isCustomSound) {

        Notification notification = new Notification(R.mipmap.ic_launcher, "Iot Notification", System.currentTimeMillis());

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);

        contentView.setImageViewResource(R.id.image, R.mipmap.ic_launcher);
        contentView.setImageViewResource(R.id.auc_img, R.mipmap.ic_launcher);

        contentView.setTextViewText(R.id.time, String.valueOf(Util.getInstance().getDate(System.currentTimeMillis(), "hh:mm a")));
        //contentView.setTextViewText(R.id.message, message+ ", Count:" + String.valueOf(length));
        contentView.setTextViewText(R.id.message, message);
        notification.contentView = contentView;

        Intent notificationIntent = new Intent(context, HomeActivity.class);
        notificationIntent.putExtra("scrNo", scrNo);

        PendingIntent contentIntent = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_ONE_SHOT);
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        notification.contentIntent = contentIntent;
        notification.flags |= Notification.FLAG_AUTO_CANCEL; // Do not clear the
        // notification

        //notification.defaults |= Notification.DEFAULT_LIGHTS; // LED
        // notification.defaults |= Notification.DEFAULT_VIBRATE; //Vibration

        if(isCustomSound) {
            //notification.sound = Uri.parse("android.resource://"
            //        + context.getPackageName() + "/" + R.raw.ring_tone);
        }else{
            notification.defaults |= Notification.DEFAULT_SOUND; // Sound
        }
        //notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE;
        //notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.defaults |= Notification.DEFAULT_VIBRATE;
        //notification.ledARGB = ledColor;
        notification.ledARGB = 0xff0000ff; //blue color
        notification.ledOnMS = 1500;
        notification.ledOffMS = 1500;
        mNotificationManager.notify(id, notification);
    }

    public void clearNotification(Context ctx,int id){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(id);
    }
    public void clearAllNotification(Context ctx){
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancelAll();
    }

    public void soundNotification(Context context,boolean isCustomSound){
        //Define Notification Manager
        /*NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       //Define sound URI
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.icon)
                .setContentTitle("Tusker Notification")
                .setContentText("sound")
                .setSound(soundUri) //This sets the sound to play
                .setAutoCancel(true);
        //Display notification
        notificationManager.notify(id, mBuilder.build());*/
        try {
            Uri notification = null;
            if (isCustomSound){
               // notification = Uri.parse("android.resource://"
                   //     + context.getPackageName() + "/" + R.raw.ring_tone);

            }else{
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
             }
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void ledNotification(Context ctx,int id)
    {
        NotificationManager nm = ( NotificationManager ) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notif = new Notification();
        notif.defaults = 0;
        notif.defaults |= Notification.DEFAULT_LIGHTS;
        notif.ledARGB = 0xff0000ff;
        notif.flags = Notification.FLAG_SHOW_LIGHTS;
        notif.ledOnMS = 1500;
        notif.ledOffMS = 1500;
        nm.notify(5, notif);
    }
    public void isNotificationEnabled(Context ctx){
        try {

            String notifName = Settings.Secure.getString(ctx.getContentResolver(), "enabled_notification_listeners");
            if (notifName.contains(ctx.getPackageName())) {
                //service is enabled do something
            } else {
                //service is not enabled try to enabled by calling...
                ctx.startActivity(new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
            }
        }catch (Exception e){
            //Logger.getInstance().error(e);
        }
    }
   /* private boolean isNotificationVisible(Context context, int id) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent test = PendingIntent.getActivity(context, id, notificationIntent, PendingIntent.FLAG_NO_CREATE);
        return test != null;

        // [BEGIN get_active_notifications]
        // Query the currently displayed notifications.

        /*final StatusBarNotification[] activeNotifications = mNotificationManager
                .getActiveNotifications();

        // [END get_active_notifications]
        final int numberOfNotifications = activeNotifications.length;
        for(int i=0;i<numberOfNotifications;i++){
            if(id== activeNotifications[i].getId()){
                return true;
            }
        }
        return false;
    }*/

    /*final static int NOTIFICATION_ID = 100;
    Notification.Builder builder2;

    private void showConnectionErrorNotification(String status){
        int defaults = 0;
        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(
                this.getResources(),
                R.mipmap.ion_icon);
        PendingIntent pi;
        builder2 = new Notification.Builder(this);
        if(status.equalsIgnoreCase(getString(R.string.connection_error_message))){
            pi = PendingIntent.getActivity(this, IonService.NOTIFICATION_ID, new Intent(Settings.ACTION_WIFI_SETTINGS), 0);
            builder2.setStyle(new Notification.BigTextStyle().bigText(status));
        }else if(status.equalsIgnoreCase(getString(R.string.ion_wifi_connection_error_message))){
            pi = PendingIntent.getActivity(this, IonService.NOTIFICATION_ID, new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK), 0);
            builder2.setStyle(new Notification.BigTextStyle().bigText(status));
        }else {
            Intent intent = new Intent(this, SplashScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            pi = PendingIntent.getActivity(this, IonService.NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            builder2.setContentText(status);
        }



        builder2.setContentTitle(getString(R.string.app_name));


        builder2.setContentIntent(pi);
        builder2.setLargeIcon(notificationLargeIconBitmap);
        builder2.setSmallIcon(R.drawable.notification_icon);
        builder2.setAutoCancel(true);
        builder2.setOngoing(false);
        builder2.setOnlyAlertOnce(true);
        builder2.setDefaults(defaults);
        Notification notification = builder2.build();
        mNotificationManager.notify(NOTIFICATION_ID, notification);

    }


    NotificationCompat.Builder builder;
    private void showOnGoingNotification(String remainingRefreshTime){
        try {

                Intent intent = new Intent(this, SplashScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                int defaults = 0;
                defaults |= Notification.DEFAULT_LIGHTS;
                defaults |= Notification.DEFAULT_VIBRATE;
                defaults |= Notification.DEFAULT_SOUND;
                defaults |= Notification.FLAG_ONLY_ALERT_ONCE;
                defaults |= Notification.FLAG_AUTO_CANCEL;
                defaults |= Notification.DEFAULT_LIGHTS ;
                Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(
                        this.getResources(),
                        R.mipmap.ion_icon);
                PendingIntent pi = PendingIntent.getActivity(this, IonService.NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                PendingIntent refreshButtonPendingIntent = PendingIntent.getBroadcast(this, 0,
                        new Intent("RefreshBroadcast"), 0);
                PendingIntent disconnectBtnPendingIntent = PendingIntent.getBroadcast(this, 0,
                        new Intent("DisconnectIONWiFiBroadcast"), 0);
                builder.setContentTitle(getString(R.string.app_name));
                builder.setContentIntent(pi);
                builder.setLargeIcon(notificationLargeIconBitmap);
                builder.setSmallIcon(R.drawable.notification_icon);
                builder.setAutoCancel(true);
                builder.setOngoing(false);
                builder.setOnlyAlertOnce(true);
                builder.setDefaults(defaults);
                builder.addAction(R.drawable.refresh_icon, getString(R.string.refresh), refreshButtonPendingIntent);
                builder.addAction(R.drawable.log_out, getString(R.string.disconnect), disconnectBtnPendingIntent);
                builder.setContentText(getString(R.string.app_require_refresh) + remainingRefreshTime);
                Notification notification = builder.build();
                mNotificationManager.notify(NOTIFICATION_ID, notification);
            } else {
                builder.setSmallIcon(R.drawable.notification_icon);
                builder.setDefaults(0);
                builder.setContentText(getString(R.string.app_require_refresh) + remainingRefreshTime);
                Notification notification = builder.build();
                mNotificationManager.notify(NOTIFICATION_ID, notification);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/

}
