package com.iotapp.iot.gps;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kundankumar on 23/06/16.
 */
public class ConfigPreference {
    //config key for geo
    public static final String CURRENTLY_TRACKING = "currentlyTracking";
    public static final String GPS_GPS_CALC_TIME = "geo_gps_calc_time";
    public static final String GPS_LOC_DET_FREQ = "geo_loc_det_freq";
    public static final String GPS_LOC_DIST_FREQ = "geo_loc_dist_freq";
    public static final String GPS_FREQ_TO_SERVER_COUNTER = "geo_freq_to_server_counter";
    public static final String LAT = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String GEOFENCE_STATUS = "status";
    public static final String ACCURACY = "accuracy";
    public static final String GPS_COUNT = "gpsCount";
    public static final String FST_FIX = "firstTimeGettingPosition";
    public static final String PRV_LAT = "previousLatitude";
    public static final String PRV_LONG = "previousLongitude";
    public static final String GPS_STATUS = "gpsStatus";
    private static final String CONFIG_PREF = "config_setting";
    public static final String DISTANCE = "distance";
    public static final String START_TIME = "startTime";
    private static ConfigPreference configPreference;
    private static SharedPreferences sharedPreferences;
    private static float DEFAULT_FLOAT = 0f;
    private static long DEFALUT_DWTIMESTAMP = 1;


    private ConfigPreference() {

    }

    public static ConfigPreference getInstance() {
        if (configPreference == null) {
            configPreference = new ConfigPreference();
        }
        return configPreference;
    }
    /*public SharedPreferences getConfig(Context context){
        if (sharedPreferences != null) {
            sharedPreferences = context.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
            return sharedPreferences;
        }
        return null;
    }*/

    public Long getLongValue(Context ctx, String key) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        long lastDownloadTime = sharedPreferences.getLong(key, DEFALUT_DWTIMESTAMP);
        return lastDownloadTime;
    }

    public void saveLongValue(Context ctx, String key, long dwTime) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putLong(key, dwTime).apply();
    }

    public void saveBoolValue(Context ctx, String key, boolean value) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putBoolean(key, value).apply();
    }

    public boolean getBoolValue(Context ctx, String key) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        boolean value = sharedPreferences.getBoolean(key, false);
        return value;
    }

    public boolean getBoolValue(Context ctx, String key, boolean defaultFlag) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        boolean value = sharedPreferences.getBoolean(key, defaultFlag);
        return value;
    }

    public int getIntValue(Context ctx, String key) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        int value = sharedPreferences.getInt(key, 0);
        return value;
    }

    public void saveIntValue(Context ctx, String key, int value) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putInt(key, value).apply();
    }

    public float getFloatValue(Context ctx, String key) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        float value = sharedPreferences.getFloat(key, DEFAULT_FLOAT);
        return value;
    }

    public void saveFloatValue(Context ctx, String key, float value) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putFloat(key, value).apply();
    }

    public void saveStringValue(Context ctx, String key, String value) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().putString(key, value).apply();
    }

    public String getStringValue(Context ctx, String key) {
        if (sharedPreferences == null) {
            sharedPreferences = ctx.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        String value = sharedPreferences.getString(key, "");
        return value;
    }

    public void finish(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(CONFIG_PREF, Context.MODE_PRIVATE);
        }
        sharedPreferences.edit().clear().apply();
        sharedPreferences = null;
        configPreference = null;

    }

}
