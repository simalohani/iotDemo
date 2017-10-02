package com.iotapp.iot.service;


import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.*;
import android.preference.PreferenceManager;
import android.util.Log;
import com.google.gson.Gson;
import com.iotapp.iot.controller.UserController;
import com.iotapp.iot.fragment.ChatFragment;
import com.iotapp.iot.gps.AppUtils;
import com.iotapp.iot.gps.GeofanceHandler;
import com.iotapp.iot.modal.GpsRequest;
import com.iotapp.iot.notification.NotificationUtil;
import com.iotapp.iot.utility.AppUtil;
import com.iotapp.iot.utility.Constant;
import com.iotapp.iot.utility.Util;
import org.eclipse.paho.client.mqttv3.*;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.Vector;


public class MQTTservice extends Service {
    /*
     * These are the supported messages from bound clients
     */
    public static final int REGISTER = 0;
    public static final int SUBSCRIBE = 1;
    public static final int PUBLISH = 2;

    /*
     * Fixed strings for the supported messages.
     */
    public static final String TOPIC = "topic";
    public static final String MESSAGE = "message";
    public static final String STATUS = "status";
    public static final String CLASSNAME = "classname";
    public static final String INTENTNAME = "intentname";
    private static boolean serviceRunning = false;
    private static int mid = 0;
    private static MQTTConnection connection = null;
    final String USER_REGISTERED = "isUserRegisterd";
    private final Messenger clientMessenger = new Messenger(new ClientHandler());
    public String user;
    public String userId;
    Handler chatHandler;
    SharedPreferences sharedPrefs;

    private synchronized static boolean isRunning() {
         /*
          * Only run one instance of the service.
		  */
        if (serviceRunning == false) {
            serviceRunning = true;
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        chatHandler = Util.getInstance().getHandler();
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        connection = new MQTTConnection();
        user = sharedPrefs.getString("prefUsername", "NULL");
        userId = sharedPrefs.getString("userId", "NULL");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isRunning()) {
            return START_STICKY;
        }

        super.onStartCommand(intent, flags, startId);
        /*
         * Start the MQTT Thread.
		 */

        connection.start();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        connection.end();
    }

    @Override
    public IBinder onBind(Intent intent) {
		/*
		 * Return a reference to our client handler.
		 */
        return clientMessenger.getBinder();
    }

    private void ReplytoClient(Messenger responseMessenger, int type, boolean status) {
		 /*
		  * A response can be sent back to a requester when
		  * the replyTo field is set in a Message, passed to this
		  * method as the first parameter.
		  */
        if (responseMessenger != null) {
            Bundle data = new Bundle();
            data.putBoolean(STATUS, status);
            Message reply = Message.obtain(null, type);
            reply.setData(data);

            try {
                responseMessenger.send(reply);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCurrLocationToUi(String location) {
        Intent intent = new Intent();
        intent.setAction(Constant.LOCATION_ACTION);
        // Pass the location data as an extra to the service.
        int userlimit = ((String) location).indexOf("/");
        String name = ((String) location).substring(0, userlimit);
        if (!(name.trim().equalsIgnoreCase(user))) {
            Gson gson = new Gson();
            String locationStr = ((String) location).substring(userlimit + 1, ((String) location).length());
            GpsRequest gpsRequest = gson.fromJson(locationStr, GpsRequest.class);
            Location mLocation = new Location("");
            mLocation.setLatitude(gpsRequest.pl.latitude);
            mLocation.setLongitude(gpsRequest.pl.longitude);
            intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);
            intent.putExtra("user", name);
            intent.putExtra(Constant.ID, Constant.ID_LOC);
            sendBroadcast(intent);
            new GeofanceHandler(getApplicationContext()).sendGeoToServer(locationStr, false);
        }
    }

    private void sendStartTripMsg(String location){
        Intent intent = new Intent();
        int userlimit = ((String) location).indexOf("/");
        String name = ((String) location).substring(0, userlimit);
        if (!(name.trim().equalsIgnoreCase(user))) {
            intent.putExtra("user", name);
            String locationStr = ((String) location).substring(userlimit + 1, ((String) location).length());
            intent.setAction(Constant.LOCATION_ACTION);
            intent.putExtra(Constant.ID,  Constant.ID_LOC_START);
            intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, locationStr);
            sendBroadcast(intent);
        }
        //// TODO: 9/9/2017
       // check if app is in foreground, if app is not in foreground or it is in background then notification has to be shown
        // need to check in google to know that if application is in background,
        //  if app is in background then  you need to create notification (do ggogle ,how to display notification)
        boolean isForeGround = AppUtil.isAppInForeground(getApplicationContext());
        if(isForeGround){
            Log.e("foreground",String.valueOf(isForeGround));
        }else{

            Log.e("foreground",String.valueOf(isForeGround));
           NotificationUtil notificationUtil = new NotificationUtil();
           notificationUtil.showNotification(getApplicationContext());

        }
    }



    public void sendMsgToScm(String message) {
        try {

            Intent intent = new Intent();
            intent.setAction(Constant.LOCATION_ACTION);
            intent.putExtra(Constant.ID, Constant.ID_SCM);
            intent.putExtra(Constant.SCM_FUL_FILLMENT, message);
            sendBroadcast(intent);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendMqttConnStatus(int isConnected) {
        Intent intent = new Intent();
        intent.setAction(Constant.LOCATION_ACTION);
        intent.putExtra(Constant.MQTT_CONNECTION_STATUS, isConnected);
        sendBroadcast(intent);

    }

    enum CONNECT_STATE {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    /*
     * This class handles messages sent to the service by
     * bound clients.
     */
    class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            boolean status = false;

            switch (msg.what) {
                case SUBSCRIBE:
                    connection.makeRequest(msg);
                    break;
                case PUBLISH:
           		 	/*
           		 	 * These two requests should be handled by
           		 	 * the connection thread, call makeRequest
           		 	 */
                    connection.makeRequest(msg);
                    break;
                case REGISTER: {
                    Bundle b = msg.getData();
                    if (b != null) {
                        Object target = b.getSerializable(CLASSNAME);
                        if (target != null) {
        				 /*
        				  * This request can be handled in-line
        				  * call the API
        				  */
                            connection.setPushCallback((Class<?>) target);
                            status = true;
                        }
                        CharSequence cs = b.getCharSequence(INTENTNAME);
                        if (cs != null) {
                            String name = cs.toString().trim();
                            if (name.isEmpty() == false) {
            				 /*
            				  * This request can be handled in-line
            				  * call the API
            				  */
                                connection.setIntentName(name);
                                status = true;
                            }
                        }
                    }
                    ReplytoClient(msg.replyTo, msg.what, status);
                    break;
                }
            }
        }
    }

    private class MQTTConnection extends Thread {
        private static final int STOP = PUBLISH + 1;
        private static final int CONNECT = PUBLISH + 2;
        private static final int RESETTIMER = PUBLISH + 3;
        private Class<?> launchActivity = null;
        private String intentName = null;
        private MsgHandler msgHandler = null;
        private CONNECT_STATE connState = CONNECT_STATE.DISCONNECTED;

        MQTTConnection() {
            msgHandler = new MsgHandler();
            msgHandler.sendMessage(Message.obtain(null, CONNECT));
        }

        public void end() {
            msgHandler.sendMessage(Message.obtain(null, STOP));
        }

        public void makeRequest(Message msg) {
			/*
			 * It is expected that the caller only invokes
			 * this method with valid msg.what.
			 */
            msgHandler.sendMessage(Message.obtain(msg));
        }

        public void setPushCallback(Class<?> activityClass) {
            launchActivity = activityClass;
        }

        public void setIntentName(String name) {
            intentName = name;
        }

        private class MsgHandler extends Handler implements MqttCallback {
            private final int PORT = 1883;
            private final int MINTIMEOUT = 10000;
            private final int MAXTIMEOUT = 32000;
            // private final String HOST = "iot.eclipse.org";
            // private final String HOST = "116.202.11.199";
            //  private final String HOST = "192.168.43.129";
            String user = sharedPrefs.getString("prefUsername", "NULL");
            String HOST = sharedPrefs.getString("prefIp", "NULL");
            private String uri = "tcp://" + HOST + ":" + PORT;
            private int timeout = MINTIMEOUT;
            private MqttClient client = null;
            private MqttConnectOptions options = new MqttConnectOptions();
            private Vector<String> topics = new Vector<String>();


            MsgHandler() {
                options.setCleanSession(true);
                try {
                    client = new MqttClient(uri, user, null);
                    client.setCallback(this);
                } catch (MqttException e1) {

                    e1.printStackTrace();
                }
            }

            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case STOP: {
					/*
					 * Clean up, and terminate.
					 */
                        client.setCallback(null);
                        if (client.isConnected()) {
                            try {
                                client.disconnect();
                                client.close();
                            } catch (MqttException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        getLooper().quit();
                        break;
                    }
                    case CONNECT: {
                        if (connState != CONNECT_STATE.CONNECTED) {
                            try {
                                if(user.equalsIgnoreCase("NULL") || HOST.equalsIgnoreCase("NULL")){
                                    user = sharedPrefs.getString("prefUsername", "NULL");
                                    HOST = sharedPrefs.getString("prefIp", "NULL");
                                    if((!user.equalsIgnoreCase("NULL")) && (!HOST.equalsIgnoreCase("NULL"))){
                                        uri = "tcp://" + HOST + ":" + PORT;
                                        client = new MqttClient(uri, user, null);
                                    }

                                }
                                //options.setUserName("guest");
                                //options.setPassword("guest".toCharArray());
                                client.setTimeToWait(MINTIMEOUT);
                                client.connect(options);
                                sharedPrefs.edit().putInt(Constant.MQTT_CONN_STATUS,1).apply();
                                subscribe("chatstart");
                                subscribe(Constant.TRACKING);
                                subscribe("retailor");
                                subscribe(Constant.START_TRIP);
                                /*if (Util.getInstance().getImeiNo(getBaseContext()).equalsIgnoreCase("8650720274267518") || user.equalsIgnoreCase("admin")) {
                                    subscribe(Constant.signup);
                                } else {
                                    boolean isUserRegistered = sharedPrefs.getBoolean(USER_REGISTERED, false);
                                    if (!isUserRegistered) {
                                        subscribe(Constant.signUpAck + "/" + user);
                                        userId = Util.getInstance().getUniqueId();
                                        sharedPrefs.edit().putString("userId", userId).apply();
                                        String pubMsg = new UserController().getUserJson(user, userId);
                                        subscribe(Constant.GROUP_REG +"/" + userId);
                                        publish(Constant.signup, pubMsg);
                                        sharedPrefs.edit().putBoolean(USER_REGISTERED, true).apply();
                                    }

                                }*/
                                connState = CONNECT_STATE.CONNECTED;
                                Log.d(getClass().getCanonicalName(), "Connected");
                                timeout = MINTIMEOUT;
                                sendMqttConnStatus(1);
                            } catch (MqttException e) {
                                Log.d(getClass().getCanonicalName(), "Connection attemp failed with reason code = " + e.getReasonCode() + e.getCause());
                                if (timeout < MAXTIMEOUT) {
                                    timeout *= 2;
                                }
                                this.sendMessageDelayed(Message.obtain(null, CONNECT), timeout);
                                sharedPrefs.edit().putInt(Constant.MQTT_CONN_STATUS,0).apply();
                                return;
                            }

					    /*
					     * Re-subscribe to previously subscribed topics
					     */
                            Iterator<String> i = topics.iterator();
                            while (i.hasNext()) {
                                subscribe(i.next());
                            }
                        }
                        break;
                    }
                    case RESETTIMER: {
                        timeout = MINTIMEOUT;
                        break;
                    }
                    case SUBSCRIBE: {
                        boolean status = false;
                        Bundle b = msg.getData();
                        if (b != null) {
                            CharSequence cs = b.getCharSequence(TOPIC);
                            if (cs != null) {
                                String topic = cs.toString().trim();
                                if (topic.isEmpty() == false) {
                                    status = subscribe(topic);
	        					/*
	        					 * Save this topic for re-subscription if needed.
	        					 */
                                    if (status) {
                                        topics.add(topic);
                                    }
                                }
                            }
                        }
                        ReplytoClient(msg.replyTo, msg.what, status);
                        break;
                    }
                    case PUBLISH: {
                        boolean status = false;
                        Bundle b = msg.getData();
                        if (b != null) {
                            CharSequence cs = b.getCharSequence(TOPIC);
                            if (cs != null) {
                                String topic = cs.toString().trim();
                                if (topic.isEmpty() == false) {
                                    cs = b.getCharSequence(MESSAGE);
                                    if (cs != null) {
                                        String message = cs.toString().trim();
                                        if (message.isEmpty() == false) {
                                            status = publish(topic, message);
                                        }
                                    }
                                }
                            }
                        }
                        ReplytoClient(msg.replyTo, msg.what, status);
                        break;
                    }

                }
            }

            private boolean subscribe(String topic) {
                try {
                    client.subscribe(topic);
                } catch (MqttException e) {
                    Log.d(getClass().getCanonicalName(), "Subscribe failed with reason code = " + e.getReasonCode());
                    return false;
                }
                return true;
            }

            private boolean publish(String topic, String msg) {
                try {
                    MqttMessage message = new MqttMessage();
                    message.setPayload(msg.getBytes());
                    Log.e("publish: ",topic+" "+msg);
                    if (!topic.contains("signup")) {
                        message.setRetained(true);
                    } else {
                        message.setRetained(false);
                    }
                    client.publish(topic, message);
                } catch (MqttException e) {
                    Log.e(getClass().getCanonicalName(), "Publish failed with reason code = " + e.getReasonCode());
                    return false;
                }
                return true;
            }

            @Override
            public void connectionLost(Throwable arg0) {
                Log.d(getClass().getCanonicalName(), "connectionLost");
                connState = CONNECT_STATE.DISCONNECTED;
                sendMessageDelayed(Message.obtain(null, CONNECT), timeout);
                sendMqttConnStatus(0);
                sharedPrefs.edit().putInt(Constant.MQTT_CONN_STATUS, 0).apply();
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken arg0) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.e(getClass().getCanonicalName(), topic + ":" + message.toString());
                //new NotificationAlert().showNotification(getApplicationContext(),"11",2,3,getApplicationContext().getResources().getColor(R.color.ic_green_87),false);
                Bundle data = new Bundle();
                Message msg = null;
                if (topic.equalsIgnoreCase(Constant.signup)) {
                    Log.e(topic,message.toString());
                    publish(Constant.signUpAck + "/" + message.toString(), "User Registered successfully");
                    new UserController().registration(getApplicationContext(), message.toString());
                    return;

                } else if (topic.equalsIgnoreCase(Constant.signUpAck)) {
                    Log.e(topic,message.toString());
                    sendMqttConnStatus(1);
                    return;

                } else if (topic.equalsIgnoreCase(Constant.GROUP_REG + "/" + userId)) {

                    Log.e("received msg", message.toString());
                    JSONObject jsonObject = new JSONObject(message.toString());
                    jsonObject.put(Constant.TOPIC, topic);
                    sendMsgToScm(jsonObject.toString());
                    subscribe(Constant.PO_ID + "/" + userId);
                    return;

                }else if (topic.equalsIgnoreCase(Constant.PO_ID + "/" + userId)) {

                    Log.e("received msg", message.toString());
                    JSONObject jsonObject = new JSONObject(message.toString());
                    jsonObject.put(Constant.TOPIC,topic);
                    sendMsgToScm(jsonObject.toString());
                    return;

                }else if(topic.equalsIgnoreCase(Constant.TOPIC_INVENTORY + "/" +userId)){
                    Log.e("received msg", message.toString());
                    JSONObject jsonObject = new JSONObject(message.toString());
                    jsonObject.put(Constant.TOPIC,topic);
                    sendMsgToScm(jsonObject.toString());
                    return;
                }
                else if (topic.equalsIgnoreCase(Constant.TRACKING)) {
                    sendCurrLocationToUi(message.toString());
                    return;
                } else if (topic.equalsIgnoreCase(Constant.START_TRIP)) {
                    sendStartTripMsg(message.toString());
                    return;
                }
                else if (topic.equalsIgnoreCase(Constant.ROLE_RETAILER)) {
                    try {
                        Log.e("received msg", message.toString());
                        JSONObject jsonObject = new JSONObject(message.toString());
                        jsonObject.put(Constant.TOPIC, topic);
                        sendMsgToScm(jsonObject.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return;
                } else {

                    data.putString("chat", message.toString());
                    msg = chatHandler.obtainMessage(ChatFragment.CHAT_DATA);

                }
                msg.setData(data);
                chatHandler.sendMessage(msg);
            }
        }
    }

}
