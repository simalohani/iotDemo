package com.iotapp.iot.modal.view;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by skumari on 9/16/2017.
 */

public class TripView implements Parcelable{

    public static final Parcelable.Creator<TripView> CREATOR = new Creator<TripView>() {
        @Override
        public TripView createFromParcel(Parcel source) {
            return new TripView(source);
        }

        @Override
        public TripView[] newArray(int size) {
            return new TripView[size];
        }
    };
    public TripView(){

    }
    private String user;
    private String tripId;
    private  long time;
    private String address;
    private double latitude;
    private  double longitude;

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    private  float distance;
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }



    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }




    public String getTripId() {
        return tripId;

    }
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    public TripView(Parcel in){
        this.tripId = in.readString();
        this.user = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.time = in.readLong();
        this.address = in.readString();
        this.distance= in.readFloat();
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tripId);
        dest.writeString(user);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeLong(time);
        dest.writeString(address);
        dest.writeFloat(distance);
    }

}
