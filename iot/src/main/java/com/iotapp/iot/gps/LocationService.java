package com.iotapp.iot.gps;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.*;
import com.google.gson.Gson;
import com.iotapp.iot.R;
import com.iotapp.iot.activity.HomeActivity;
import com.iotapp.iot.database.Schema;
import com.iotapp.iot.modal.GpsRequest;
import com.iotapp.iot.utility.Constant;
import com.iotapp.iot.utility.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, ResultCallback<Status> {

    final static int REQUEST_LOCATION = 199;
    private static final String TAG = "LocationService";
    private static final String GEOFENCE_REMOVE = "geoRemove";
    String geofenceRemoveId = null;
    // use the websmithing defaultUploadWebsite for testing and then check your
    // location with your browser here: https://www.websmithing.com/gpstracker/displaymap.php
    private String defaultUploadWebsite;
    private boolean currentlyProcessingLocation = false;
    private LocationRequest locationRequest;
    private GoogleApiClient googleApiClient;
    private PendingIntent mGeofencePendingIntent;

    @Override
    public void onCreate() {
        super.onCreate();

        //defaultUploadWebsite = getString(R.string.default_upload_website);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // if we are currently trying to get a location and the alarm manager has called this again,
        // no need to start processing a new location.
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            startTracking();
        }
        geofenceRemoveId = intent.getStringExtra(GEOFENCE_REMOVE);
        if (null != geofenceRemoveId) {
            removeGeofence(geofenceRemoveId);
        }
        return START_NOT_STICKY;
    }

    private void startTracking() {
        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS) {

            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        } else {
            Log.e(TAG, "unable to connect to google play services.");
            // Logger.getInstance().error(TAG + ": unable to connect to google play services.");
        }
    }

    protected void sendLocationDataToServer(Location location) {
        ConfigPreference configPreference = ConfigPreference.getInstance();

        boolean firstTimeGettingPosition = configPreference.getBoolValue(getApplicationContext(), ConfigPreference.FST_FIX);
        float distance = 0;
        if (!firstTimeGettingPosition) {
            configPreference.saveBoolValue(getApplicationContext(), ConfigPreference.FST_FIX, true);
        } else {
            Location previousLocation = new Location("");
            previousLocation.setLatitude(configPreference.getFloatValue(getApplicationContext(), ConfigPreference.PRV_LAT));
            previousLocation.setLongitude(configPreference.getFloatValue(getApplicationContext(), ConfigPreference.PRV_LONG));
            distance = location.distanceTo(previousLocation);

        }
        configPreference.saveFloatValue(getApplicationContext(), ConfigPreference.PRV_LAT, (float) location.getLatitude());
        configPreference.saveFloatValue(getApplicationContext(), ConfigPreference.PRV_LONG, (float) location.getLongitude());
        configPreference.saveFloatValue(getApplicationContext(), ConfigPreference.ACCURACY, location.getAccuracy());
        configPreference.saveLongValue(getApplicationContext(), ConfigPreference.LAT, Double.doubleToLongBits(location.getLatitude()));
        configPreference.saveLongValue(getApplicationContext(), ConfigPreference.LONGITUDE, Double.doubleToLongBits(location.getLongitude()));
        float prvDistance = configPreference.getFloatValue(getApplicationContext(),ConfigPreference.DISTANCE);
        configPreference.saveFloatValue(getApplicationContext(), ConfigPreference.DISTANCE, distance+prvDistance);

        //if it is depot or franchisee dont send to location
        SharedPreferences loginpref = getApplicationContext().getSharedPreferences("login", 0);
        if ((distance != 0) || (!firstTimeGettingPosition)) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String user = sharedPrefs.getString("prefUsername", "NULL");
            String tripId = sharedPrefs.getString("tripId", "");
            GpsRequest gpsRequest = new GpsRequest();
            GpsRequest.Payload pl = new GpsRequest.Payload();
            pl.latitude = location.getLatitude();
            pl.longitude = location.getLongitude();
            pl.speed = location.getSpeed();
            pl.time = location.getTime();
            pl.accuracy = location.getAccuracy();
            pl.provider = location.getProvider();
            pl.altitude = location.getAltitude();
            pl.distance = distance+prvDistance;
            pl.user = user;
            pl.tripId =tripId;
            gpsRequest.pl = pl;
            String gpsStr = new Gson().toJson(gpsRequest);

            String topic = Constant.TRACKING;// t.getText().toString().trim();

            HashMap data = new HashMap();
            data.put(Constant.ACTION,Constant.ACTION_PUBLISH_TRACKING);
            data.put("topic",topic);
            data.put("message", gpsStr);
            HomeActivity.serviceConnection.publisgMsg(topic, user + "/" + gpsStr);
 //           sendCurrLocationToUi(location, user);
 //           new GeofanceHandler(getApplicationContext()).sendGeoToServer(gpsStr, firstTimeGettingPosition);
            Log.e(TAG, "Location service gps data found ");
        }

        stopSelf();

    }
    private void sendCurrLocationToUi(Location location,String user){
        Intent intent = new Intent();
        intent.setAction(Constant.LOCATION_ACTION);
        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, location);
        intent.putExtra("user",user);
        sendBroadcast(intent);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            if (new GeofanceHandler(getApplicationContext()).checkGeofenceStatus()) {
                addGeofence();
            }
            // we have our desired accuracy of 500 meters so lets quit this service,
            // onDestroy will be called and stop our location uodates
            if (location.getAccuracy() < 20.0f) {
                //Log.e(TAG, "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy() + " gps speed:" + location.getSpeed() + " alt:" + location.getAltitude());
                //Logger.getInstance().info(TAG + ": " + "position: " + location.getLatitude() + ", " + location.getLongitude() + " accuracy: " + location.getAccuracy() + " gps speed:" + location.getSpeed());

                stopLocationUpdates();
                sendLocationDataToServer(location);
            }
        } else {
            Log.e(TAG, "location null: ");
        }
    }

    private void stopLocationUpdates() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    /**
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
        //Log.d(TAG, "onConnected");

        locationRequest = LocationRequest.create();
        int gps_calc_time = ConfigPreference.getInstance().getIntValue(getApplicationContext(), ConfigPreference.GPS_GPS_CALC_TIME);
        locationRequest.setInterval(5 * 6 * 1000); // milliseconds//2
        locationRequest.setFastestInterval(4 * 6 * 1000); // 0,the fastest rate in milliseconds at which your app can handle location updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (!grantLocation()) {
            return;
        }


        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
        if (new GeofanceHandler(getApplicationContext()).checkGeofenceStatus()) {
            addGeofence();
        }
        if (geofenceRemoveId != null) {
            removeGeofence(geofenceRemoveId);
        }
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result;
        result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            Activity act = (HomeActivity) Util.getInstance().getContext();
                            if (act == null) {
                                stopLocationUpdates();
                                stopSelf();
                                return;
                            }
                            status.startResolutionForResult(
                                    act,
                                    REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            stopLocationUpdates();
                            stopSelf();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed");

        stopLocationUpdates();
        stopSelf();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "GoogleApiClient connection has been suspend");
    }

    private void addGeofence() {
        if (!googleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            GeofanceHandler geofanceHandler = new GeofanceHandler(getApplicationContext());
            ArrayList geofenceList = geofanceHandler.getGeofences();
            if (geofenceList != null && geofenceList.size() > 0) {
                LocationServices.GeofencingApi.addGeofences(
                        googleApiClient,
                        // The GeofenceRequest object.
                        getGeofencingRequest(geofenceList),
                        // A pending intent that that is reused when calling removeGeofences(). This
                        // pending intent is used to generate an intent when a matched geofence
                        // transition is observed.
                        getGeofencePendingIntent()
                ).setResultCallback(this); // Result processed in onResult().
            }
            geofanceHandler.setGeofenceStatus(Constant.GEOFENCE_STATUS_INACTIVE);
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            // logSecurityException(securityException);
            Log.e(TAG, securityException.toString());
            //Logger.getInstance().error(securityException);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest(ArrayList mGeofenceList) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }

    @Override
    public void onResult(Status status) {

    }

    private void removeGeofence(String reqId) {
        if (!googleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            List<String> removeIds = new ArrayList<String>();
            removeIds.add(reqId);
            LocationServices.GeofencingApi.removeGeofences(googleApiClient, removeIds);
            geofenceRemoveId = null;
        } catch (Exception e) {
            //Logger.getInstance().error(e);
        }
    }

    private boolean grantLocation() {
        //for 6.0
        //Log.e(TAG, String.valueOf(Build.VERSION.SDK_INT));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                if (Util.getInstance().getContext() != null) {
                    int permissionCheck = ContextCompat.checkSelfPermission(Util.getInstance().getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION);
                    if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((HomeActivity) Util.getInstance().getContext(),
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                Constant.MY_PERMISSIONS_REQUEST);
                        return false;
                    }
                }

            } catch (Exception e) {
                //Logger.getInstance().error(e);
            }
        }
        return true;
    }

    public double getDistance(double lat1, double lon1, double lat2, double lon2)
    {
        double latA = Math.toRadians(lat1);
        double lonA = Math.toRadians(lon1);
        double latB = Math.toRadians(lat2);
        double lonB = Math.toRadians(lon2);
        double cosAng = (Math.cos(latA) * Math.cos(latB) * Math.cos(lonB-lonA)) +
                (Math.sin(latA) * Math.sin(latB));
        double ang = Math.acos(cosAng);
        double dist = ang *6371;
        return dist;
    }
}
