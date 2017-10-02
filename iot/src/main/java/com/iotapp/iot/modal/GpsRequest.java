package com.iotapp.iot.modal;

/**
 * Created by kundankumar on 02/10/16.
 */
public class GpsRequest {

    public Payload pl;
    public static class Payload {
        public String tripId;
        public double latitude;
        public double longitude;
        public float speed;
        public long time;
        public double accuracy;
        public String provider;
        public double altitude;
        public String user;
        public float distance;

    }

}
