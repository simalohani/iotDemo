package com.iotapp.iot.database;

/**
 * Created by kundankumar on 14/12/15.
 */
public class Projection {
    public static final String[] SYNC_PROJECTION = new String[]{
            Schema.Sync.SYNC_JOB_TYPE,
            Schema.Sync.SYNC_JOB_ID,
            Schema.Sync.SYNC_PAYLOAD,
            Schema.Sync.SYNC_HEADER,
            Schema.Sync.SYNC_URL,
            Schema.Sync.SYNC_METHOD_TYPE,
            Schema.Sync.SYNC_JOB_STATUS};



    public static final String[] USER_PROJECTION = new String[]{
            Schema.User.USER_ID,
            Schema.User.USER_F_NAME,
            Schema.User.USER_ROLE,
            Schema.User.USER_P_MOB,
            Schema.User.USER_LOC_ID,
            Schema.User.USER_LOC_AVL

    };


    public static final String[] GEOFENCE_PROJECTION = new String[]{
            Schema.GeoFence.GEO_ID,
            Schema.GeoFence.GEO_LAT,
            Schema.GeoFence.GEO_LONG,
            Schema.GeoFence.GEO_REQ_ID,
            Schema.GeoFence.GEO_STATUS,
            Schema.GeoFence.GEO_RADIUS
    };
    public static final String[] GPS_PROJECTION = new String[]{
            Schema.Gps.GPS_LAT,
            Schema.Gps.GPS_LONGITUDE,
            Schema.Gps.GPS_SPEED,
            Schema.Gps.GPS_ACCR,
            Schema.Gps.GPS_TIME,
            Schema.Gps.GPS_SRC,
            Schema.Gps.GPS_ALT,
            Schema.Gps.GPS_USER,
            Schema.Gps.GPS_TRIP_ID,
            Schema.Gps.GPS_DIST,


    };

    public static final String[] SCM_PROJECTION = new String[]{
            Schema.Scm.INVENTORY,
            Schema.Scm.BACKLOG,
            Schema.Scm.SCM_ROLE,
            Schema.Scm.SCM_DATE,
            Schema.Scm.WEEK_NO,
            Schema.Scm.TEAM_NAME,
            Schema.Scm.SCM_PO
    };
}
