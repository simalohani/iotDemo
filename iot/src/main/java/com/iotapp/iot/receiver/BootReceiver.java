package com.iotapp.iot.receiver;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.iotapp.iot.service.MQTTservice;

public class BootReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{	
		Log.d(getClass().getCanonicalName(), "onReceive");
		context.startService(new Intent(context, MQTTservice.class));
	}
}