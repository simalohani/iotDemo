package com.iotapp.iot.controller;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.iotapp.iot.database.Projection;
import com.iotapp.iot.database.Schema;
import com.iotapp.iot.modal.view.TripView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by skumari on 9/16/2017.
 */

public class TripController {

    private LinkedHashMap tripMap = new LinkedHashMap();
    public LinkedHashMap getTrip(Context context) {

        //ArrayList<TripView>  tripList= null;
        final ContentResolver resolver = context.getContentResolver();
        Cursor rows = null;
        Uri uri = Schema.Gps.CONTENT_URI.buildUpon().build();
        try{
            rows = resolver.query(uri, Projection.GPS_PROJECTION,null,null,null);
            if(rows != null && rows.getCount() >0 && rows.moveToFirst()){
               // tripList= new ArrayList<TripView>();
                do{
                    TripView tripView = new TripView();
                    String gpsUser = rows.getString(rows.getColumnIndex(Schema.Gps.GPS_USER));
                    tripView.setUser(gpsUser);
                    String gpsTripid =  rows.getString(rows.getColumnIndex(Schema.Gps.GPS_TRIP_ID));
                    tripView.setTripId(gpsTripid);
                    long gpsTime =rows.getLong(rows.getColumnIndex(Schema.Gps.GPS_TIME));
                    tripView.setTime(gpsTime);
                    double gpsLatitude = rows.getDouble( rows.getColumnIndex( Schema.Gps.GPS_LAT));
                    tripView.setLatitude(gpsLatitude);
                    double gpsLongitude = rows.getDouble(rows.getColumnIndex( Schema.Gps.GPS_LONGITUDE));
                    tripView.setLongitude(gpsLongitude);
                    float gpsDistance =rows.getFloat(rows.getColumnIndex(Schema.Gps.GPS_DIST));
                    tripView.setDistance(gpsDistance);
                    //tripList.add(tripView);
                    groupLocationByTrip(tripView);
                }while (rows.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {
            if(rows!=null){
                rows.close();
            }
        }
        return tripMap;
    }

    private void groupLocationByTrip(TripView tripView){
        String tripId = tripView.getTripId();
        ArrayList tripList;
        if(tripMap.containsKey(tripId)){
            tripList = (ArrayList)tripMap.get(tripId);
        }else{
            tripList = new ArrayList();
        }
        tripList.add(tripView);
        tripMap.put(tripId,tripList);
    }
}
