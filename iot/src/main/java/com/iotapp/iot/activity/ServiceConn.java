package com.iotapp.iot.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.*;
import android.util.Log;
import com.iotapp.iot.controller.ChatController;
import com.iotapp.iot.mqtt.ServiceHandler;
import com.iotapp.iot.security.Authentication;
import com.iotapp.iot.service.MQTTservice;
import com.iotapp.iot.utility.Constant;
import com.iotapp.iot.utility.Util;

/**
 * Created by kundankumar on 26/09/16.
 */
public class ServiceConn implements ServiceConnection {
    private Messenger service = null;
    private Messenger serviceHandler;

    public ServiceConn(ServiceHandler handler) {
        serviceHandler = new Messenger(handler);
    }


    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = new Messenger(binder);
        Bundle data = new Bundle();
        data.putCharSequence(MQTTservice.INTENTNAME,
                "com.example.MQTT.PushReceived");
        Message msg = Message.obtain(null, MQTTservice.REGISTER);
        msg.setData(data);
        msg.replyTo = serviceHandler;
        try {
            service.send(msg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d("Service","disconnected");
    }

    public void publisgMsg(String topic, String msg) {
        Bundle data = new Bundle();
        data.putCharSequence(MQTTservice.TOPIC, topic);
        data.putCharSequence(MQTTservice.MESSAGE, msg);
        Message mesg = Message.obtain(null, MQTTservice.PUBLISH);
        mesg.setData(data);
        mesg.replyTo = serviceHandler;
        try {
            service.send(mesg);
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }
    public void subscribeMsg(String topic){
        Bundle data = new Bundle();
        data.putCharSequence(MQTTservice.TOPIC, topic);
        Message mesg = Message.obtain(null, MQTTservice.SUBSCRIBE);
        mesg.setData(data);
        mesg.replyTo = serviceHandler;
        try {
            service.send(mesg);
        } catch (RemoteException e) {
            e.printStackTrace();

        }
    }

    public void publishAck(Context context, String msg) {
        Authentication authentication = new Authentication(context);
        publisgMsg(Constant.signUpAck, authentication.userAuthentication(msg.toString()));
    }

    public void sendImage(String user,String Img) {
        String topic = "chatstart";
        String message = user + "img/" + new ChatController().getImg(Img);

        if (topic != null && topic.isEmpty() == false && Img != null
                && Img.isEmpty() == false) {

            Bundle data = new Bundle();
            data.putCharSequence(MQTTservice.TOPIC, topic);
            data.putCharSequence(MQTTservice.MESSAGE, message);
            Message msg = Message.obtain(null, MQTTservice.PUBLISH);
            msg.setData(data);
            msg.replyTo = serviceHandler;
            try {
                service.send(msg);
            } catch (RemoteException e) {
                e.printStackTrace();
                // result.setText("Publish failed with exception:" +
                // e.getMessage());
            }
        } else {
            // result.setText("Topic and message required.");
        }

    }


}
