package com.iotapp.iot.gps;

import android.content.*;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.gson.Gson;
import com.iotapp.iot.controller.AlarmHandler;
import com.iotapp.iot.database.Projection;
import com.iotapp.iot.database.Schema;
import com.iotapp.iot.modal.GpsRequest;
import com.iotapp.iot.utility.Constant;
import com.iotapp.iot.utility.Util;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kundankumar on 17/02/16.
 */
public class GeofanceHandler {
    protected ArrayList<Geofence> mGeofenceList;
    HashMap config;
    private String TAG = "GeofanceHandler";
    private HashMap geoTrackerMap = new HashMap();
    private Context ctx;
    //config key for geofence and gps
    private static final String GEOFENCE_NOTIF_RAD = "geo_fence_notif_radius";
    private static final String GEOFENCE_DWELL_RAD = "geo_fence_dwell_radius";
    private static final String GEOFENCE_DWELL_ID = "geo_fence_dwell_id";
    private static final String GEOFENCE_NOTIF_ID = "geo_fence_notif_id";
    private static final String GEOFENCE_STATUS = "status";
    /* Constant for geofence */
    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    private static final long GEOFENCE_EXPIRATION_IN_HOURS = 48;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    private static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;

    private static final int GPS_FREQ_SERVER = 10;

    public GeofanceHandler(Context context) {
        this.ctx = context;
    }

    public void insertGeofence(double lat, double longitude, String id) {
        try {
            ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
            JSONArray reqIds = getGeoRgstsToServer(id);
            if (reqIds == null) {
                //if radius is more for the same loc id then insertion of geofence depend upon radius count
                batch.add(ContentProviderOperation.newInsert(Schema.GeoFence.CONTENT_URI)
                        .withValue(Schema.GeoFence.GEO_ID, id)
                        .withValue(Schema.GeoFence.GEO_LAT, lat)
                        .withValue(Schema.GeoFence.GEO_LONG, longitude)
                        .withValue(Schema.GeoFence.GEO_STATUS, Constant.GEO_STATUS_INACTIVE)
                        .withValue(Schema.GeoFence.GEO_RADIUS, config.get(GEOFENCE_DWELL_RAD))
                        .withValue(Schema.GeoFence.GEO_REQ_ID, Util.getInstance().getUniqueId())
                        .build());
                batch.add(ContentProviderOperation.newInsert(Schema.GeoFence.CONTENT_URI)
                        .withValue(Schema.GeoFence.GEO_ID, id)
                        .withValue(Schema.GeoFence.GEO_LAT, lat)
                        .withValue(Schema.GeoFence.GEO_LONG, longitude)
                        .withValue(Schema.GeoFence.GEO_STATUS, Constant.GEO_STATUS_INACTIVE)
                        .withValue(Schema.GeoFence.GEO_RADIUS, config.get(GEOFENCE_NOTIF_RAD))
                        .withValue(Schema.GeoFence.GEO_REQ_ID, Util.getInstance().getUniqueId())
                        .build());
            } else if (reqIds.length() == 0) {

            } else {
                int length = reqIds.length();
                for (int i = 0; i < length; i++) {
                    Uri updateUri = Schema.GeoFence.CONTENT_URI.buildUpon()
                            .appendPath(reqIds.getString(i)).build();
                    batch.add(ContentProviderOperation.newUpdate(updateUri)
                            .withValue(Schema.GeoFence.GEO_LAT, lat)
                            .withValue(Schema.GeoFence.GEO_LONG, longitude)
                            .build());
                }
            }
            ctx.getContentResolver().applyBatch(Schema.CONTENT_AUTHORITY, batch);
        } catch (Exception e) {
            //Logger.getInstance().error(e);
        }
    }

    private JSONArray getGeoRgstsToServer(String locId) {
        Cursor rows = null;
        JSONArray ids = null;
        try {
            Uri uri = Schema.GeoFence.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(locId)).build();
            rows = ctx.getContentResolver().query(uri, Projection.GEOFENCE_PROJECTION, null, null, null);

            if (rows != null && rows.getCount() > 0 && rows.moveToFirst()) {
                ids = new JSONArray();
                int status = Integer.parseInt(rows.getString(rows.getColumnIndex(Schema.GeoFence.GEO_STATUS)));
                String reqId = rows.getString(rows.getColumnIndex(Schema.GeoFence.GEO_REQ_ID));
                if (status == Constant.GEO_STATUS_ACTIVE) {
                    ids.put(reqId);
                    return ids;
                } else if (status == Constant.GEO_STATUS_INACTIVE) {
                    //delInActiveGeofence(reqId);
                }
            }
        } catch (Exception e) {
           // Logger.getInstance().error(e);
        } finally {
            if (rows != null)
                rows.close();
        }
        return ids;
    }

    public ArrayList getGeofences() {
        Cursor rows = null;
        mGeofenceList = null;
        mGeofenceList = new ArrayList<Geofence>();
        try {
            final ContentResolver contentResolver = ctx.getContentResolver();
            Uri uri = Schema.GeoFence.CONTENT_URI.buildUpon()
                    .build();
            rows = contentResolver.query(uri, Projection.GEOFENCE_PROJECTION, null, null, null);
            if (rows != null && rows.getCount() > 0 && rows.moveToFirst()) {
                ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
                do {
                    double lat = Double.parseDouble(rows.getString(rows.getColumnIndex(Schema.GeoFence.GEO_LAT)));
                    double longitude = Double.parseDouble(rows.getString(rows.getColumnIndex(Schema.GeoFence.GEO_LONG)));
                    String reqId = rows.getString(rows.getColumnIndex(Schema.GeoFence.GEO_REQ_ID));
                    int radius = Integer.parseInt(rows.getString(rows.getColumnIndex(Schema.GeoFence.GEO_RADIUS)));
                    addGeofance(lat, longitude, reqId, radius);
                    //updating geo status active in geofence table
                    Uri updateUri = Schema.GeoFence.CONTENT_URI.buildUpon()
                            .appendPath(reqId).build();
                    batch.add(ContentProviderOperation.newUpdate(updateUri)
                            .withValue(Schema.GeoFence.GEO_STATUS, Constant.GEO_STATUS_ACTIVE)
                            .build());
                } while (rows.moveToNext());
                ctx.getContentResolver().applyBatch(Schema.CONTENT_AUTHORITY, batch);
            }
        } catch (Exception e) {
            //Logger.getInstance().error(e);
        } finally {
            if (rows != null)
                rows.close();
        }
        return mGeofenceList;
    }

    private void addGeofance(double lat, double longitude, String id, int radius) {
        try {
            if (!geoTrackerMap.containsKey(id)) {
                Geofence geofence = new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId(id)

                                // Set the circular region of this geofence.
                        .setCircularRegion(
                                lat,
                                longitude,
                                radius
                        )

                                // Set the expiration duration of the geofence. This geofence gets automatically
                                // removed after this period of time.
                        .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                                // Set the transition types of interest. Alerts are only generated for these
                                // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)

                                // Create the geofence.
                        .build();
                mGeofenceList.add(geofence);
                geoTrackerMap.put(id, geofence);
            }
        } catch (Exception e) {
           // Logger.getInstance().error(e);
        }
    }

    private void delInActiveGeofence(String reqId) {
        try {

            ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
            Uri deleteUri = Schema.GeoFence.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf(reqId)).build();
            batch.add(ContentProviderOperation.newDelete(deleteUri).build());
            ctx.getContentResolver().applyBatch(Schema.CONTENT_AUTHORITY, batch);
        } catch (Exception e) {
           // Logger.getInstance().error(e);
        }
    }

    public void setGeofenceStatus(int status) {
         ConfigPreference.getInstance().saveIntValue(ctx,GEOFENCE_STATUS, status);

    }

    public boolean checkGeofenceStatus() {
        int geofenceStatus = ConfigPreference.getInstance().getIntValue(ctx, ConfigPreference.GEOFENCE_STATUS);
        if (geofenceStatus == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void getTasksIDs(String locId) {

    }

    public void sendGeoToServer(String gpsStr,boolean firstTimeGpsFix) {
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        GpsRequest gpsRequest = new Gson().fromJson(gpsStr, GpsRequest.class);
        GpsRequest.Payload pl = gpsRequest.pl;
        //Toast.makeText(ctx,gpsStr,Toast.LENGTH_LONG);
        try {
            batch.add(ContentProviderOperation.newInsert(Schema.Gps.CONTENT_URI)
                    .withValue(Schema.Gps.GPS_USER, pl.user)
                    .withValue(Schema.Gps.GPS_LAT, pl.latitude)
                    .withValue(Schema.Gps.GPS_LONGITUDE, pl.longitude)
                    .withValue(Schema.Gps.GPS_SPEED, pl.speed)
                    .withValue(Schema.Gps.GPS_TIME, pl.time)
                    .withValue(Schema.Gps.GPS_ACCR, pl.accuracy)
                    .withValue(Schema.Gps.GPS_SRC, pl.provider)
                    .withValue(Schema.Gps.GPS_ALT, pl.altitude)
                    .withValue(Schema.Gps.GPS_TRIP_ID, pl.tripId)
                    .withValue(Schema.Gps.GPS_DIST,pl.distance)
                    .build());
            ctx.getContentResolver().applyBatch(Schema.CONTENT_AUTHORITY, batch);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    private void addGpsDataToSyncDb(String payload) {

       /* if (payload != null) {
            try {
               // SyncUtils.registerSyncObserver(ctx);
                ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
                batch.add(ContentProviderOperation.newInsert(Schema.Sync.CONTENT_URI)
                        .withValue(Schema.Sync.SYNC_JOB_TYPE, Schema.Sync.SYNC_JOB_GEO_LOCATION)
                        .withValue(Schema.Sync.SYNC_JOB_ID, Util.getInstance().getUniqueId())
                        .withValue(Schema.Sync.SYNC_PAYLOAD, payload)
                        .withValue(Schema.Sync.SYNC_HEADER, addHeader().toString())
                        .withValue(Schema.Sync.SYNC_URL, WebServiceUrls.BASE_URL + WebServiceUrls.GEO)
                        .withValue(Schema.Sync.SYNC_METHOD_TYPE, "PUT")
                        .withValue(Schema.Sync.SYNC_JOB_STATUS, Schema.Sync.SYNC_JOB_STATUS_START)
                        .build());
                ctx.getContentResolver().applyBatch(Schema.CONTENT_AUTHORITY, batch);


            } catch (Exception e) {
                //Logger.getInstance().error(e);
            }
        }*/
    }

    private void delGeoData(long time) {
        try {
            ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
            Uri deleteUri = Schema.Gps.CONTENT_URI.buildUpon().appendPath(String.valueOf(time))
                    .build();
            batch.add(ContentProviderOperation.newDelete(deleteUri).build());
            ctx.getContentResolver().applyBatch(Schema.CONTENT_AUTHORITY, batch);
        } catch (Exception e) {
            //Logger.getInstance().error(e);
        }
    }

    public ArrayList getGPSDataFromDB() {
        Cursor rows = null;
        try {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            String user = sharedPrefs.getString("prefUsername", "NULL");

            Uri uri = Schema.Gps.CONTENT_URI.buildUpon().appendPath(user)
                    .build();
            rows = ctx.getContentResolver().query(uri, Projection.GPS_PROJECTION, null, null, null);

            ArrayList<GpsRequest.Payload> gpsRequests = new ArrayList<GpsRequest.Payload>();

            if (rows != null && rows.getCount() > 0 && rows.moveToFirst()) {
                do {
                    GpsRequest.Payload pl = new GpsRequest.Payload();
                    pl.latitude = Double.parseDouble(rows.getString(rows.getColumnIndex(Schema.Gps.GPS_LAT)));
                    pl.longitude = Double.parseDouble(rows.getString(rows.getColumnIndex(Schema.Gps.GPS_LONGITUDE)));
                    pl.speed = Float.parseFloat(rows.getString(rows.getColumnIndex(Schema.Gps.GPS_SPEED)));
                    pl.accuracy = Float.parseFloat(rows.getString(rows.getColumnIndex(Schema.Gps.GPS_ACCR)));
                    pl.time = Long.parseLong(rows.getString(rows.getColumnIndex(Schema.Gps.GPS_TIME)));
                    pl.provider = rows.getString(rows.getColumnIndex(Schema.Gps.GPS_SRC));
                    pl.altitude = Double.parseDouble(rows.getString(rows.getColumnIndex(Schema.Gps.GPS_ALT)));
                    pl.user = rows.getString(rows.getColumnIndex(Schema.Gps.GPS_USER));
                    gpsRequests.add(pl);
                } while (rows.moveToNext());

                return gpsRequests;
            }
        } catch (Exception e) {
            e.printStackTrace();
           // Logger.getInstance().error(e);
        } finally {
            if (rows != null)
                rows.close();
        }
        return null;
    }

    private JSONObject addHeader() {
        JSONObject map = new JSONObject();
        /*try {
            map.put("LM.LVL", String.valueOf(Const.LM_LVL));
            map.put("Content-type", "application/json");
            map.put("LM.TOKEN", new LoginHandler(ctx).getLoginInfo().getToken());
            String versionName = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionName;
            int versionCode = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0).versionCode;
            map.put("User-Agent", System.getProperty("http.agent") + "/app" + versionName + "_" + versionCode);

        } catch (Exception e) {
            //Logger.getInstance().error("error in addHeader");
        }*/
        return map;
    }

    public void sendGeoFenceToServer(int f_ntf, String reqId) {
        Cursor rows = null;
        /*try {
            Uri uri = Schema.GeoFence.CONTENT_URI.buildUpon()
                    .appendPath(String.valueOf("ab" + reqId)).build();
            rows = ctx.getContentResolver().query(uri, Projection.GEOFENCE_PROJECTION, null, null, null);

            if (rows != null && rows.getCount() > 0 && rows.moveToFirst()) {
                Geo geo = new Geo();
                Geo.Pl pl = new Geo.Pl();
                Geo.CRT crt = new Geo.CRT();
                Geo.Msgs[] msgses = new Geo.Msgs[1];
                int count = 0;
                do {
                    String locId = rows.getString(rows.getColumnIndex(Schema.GeoFence.GEO_ID));
                    int radius = Integer.parseInt(rows.getString(rows.getColumnIndex(Schema.GeoFence.GEO_RADIUS)));
                    Geo.Msgs msgs = new Geo.Msgs();
                    msgs.m_dt = new Date().getTime();
                    msgs.type = 8;

                    if (config == null)
                        config = getGeoConfig();

                    if (radius == Integer.parseInt((String) config.get(GEOFENCE_NOTIF_RAD))) {
                        msgs.f_id = (String) config.get(GEOFENCE_NOTIF_ID);//"e39cd5af-1a53-4373-b0c8-783f0b9f0dc5"; //id for notification fence
                    } else {
                        msgs.f_id = (String) config.get(GEOFENCE_DWELL_ID);
                    }
                    msgs.l_id = locId;
                    msgs.f_ntf = f_ntf;
                    msgses[count] = msgs;
                    pl.msgs = msgses;
                    count++;
                    if (f_ntf == 4) {
                        if (!reqId.startsWith("abcd")) {
                            if (!isPendingTask(locId)) {
                                delInActiveGeofence(reqId);
                                Intent intent = new Intent(ctx, LocationService.class);
                                intent.putExtra(Const.GEOFENCE_REMOVE, reqId);
                                ctx.startService(intent);
                            }

                        }
                    }
                } while (rows.moveToNext());
                crt.app = null;
                LoginHandler loginHandler = new LoginHandler(ctx);
                if (loginHandler != null && loginHandler.getLoginInfo() != null) {
                    if (loginHandler.getLoginInfo().getTranspotrId() != null && loginHandler.getLoginInfo().getTranspotrId().length() > 1) {
                        crt.o_id = loginHandler.getLoginInfo().getTranspotrId();
                        crt.o_ty = loginHandler.getLoginInfo().getUserType();
                    } else {
                        crt = getCrt(crt);
                    }
                } else {
                    crt = getCrt(crt);
                }
                if (crt.o_id.equalsIgnoreCase("")) {
                    return;
                }
                pl.crt = crt;
                geo.pl = pl;
                String str = new Gson().toJson(geo);
                addGpsDataToSyncDb(str);

            }
        } catch (Exception e) {
            Logger.getInstance().error(e);
        } finally {
            if (rows != null)
                rows.close();
        }*/

    }





   /* private Geo.CRT getCrt(Geo.CRT crt) {
        try {
            SharedPreferences loginpref = ctx.getSharedPreferences("login", 0);
            crt.o_id = loginpref.getString("transPotrId", "");
            crt.o_ty = loginpref.getString("userType", "");
        } catch (Exception e) {
            Logger.getInstance().error(e);
        }
        return crt;
    }*/

    //For tracking
    public void checkTrackingStatus() {
        try {
            if (!Util.getInstance().checkIfGooglePlayEnabled(ctx)) {
                return;
            }
            AlarmHandler.removeAlarmSync(ctx, Constant.GPS_ALARM_ID);
            AlarmHandler.start(ctx, Constant.GPS_ALARM_ID, 30);

        }catch (Exception e){
            //Logger.getInstance().error(e);
        }
    }

    public  void endTrip() {
     AlarmHandler.removeAlarmSync(ctx,Constant.GPS_ALARM_ID);
    }

}
