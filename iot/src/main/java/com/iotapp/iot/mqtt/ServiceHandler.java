package com.iotapp.iot.mqtt;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.iotapp.iot.service.MQTTservice;

/**
 * Created by kundankumar on 01/10/16.
 */
public class ServiceHandler extends Handler {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case MQTTservice.SUBSCRIBE:
                break;
            case MQTTservice.PUBLISH:
                break;
            case MQTTservice.REGISTER:
                break;
            case 6:
                break;
            default:
                super.handleMessage(msg);
                return;
        }

        /*Bundle b = msg.getData();
			if (b != null) {
				TextView result = (TextView) findViewById(R.id.textResultStatus);
				Boolean status = b.getBoolean(MQTTservice.STATUS);
				if (status == false) {
					result.setText("Fail");
				} else {
					result.setText("Success");
				}

			}*/

    }

}
