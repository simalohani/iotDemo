package com.iotapp.iot.database;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Field and table name constants for
 * {@link Provider}.
 */
public class Schema {
    /**
     * Content provider authority.
     */
    public static final String CONTENT_AUTHORITY = "com.iotapp.iot";
    /**
     * Base URI. (content://com.logistimo.tusker)
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    private Schema() {
    }

    /**
     * Columns supported by "entries" records.
     */
    public static class Sync implements BaseColumns {
        /**
         * MIME type for lists of entries.
         */
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.tusker.syncs";
        /**
         * MIME type for individual entries.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.iotapp.sync";

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "sync";
        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();


        /*Sync column name*/
        public static final String SYNC_JOB_REQ_ID = "reqId";
        public static final String SYNC_JOB_TYPE = "jobType";
        public static final String SYNC_METHOD_TYPE = "method";
        public static final String SYNC_PAYLOAD = "payload";
        public static final String SYNC_URL = "url";
        public static final String SYNC_HEADER = "header";
        public static final String SYNC_JOB_ID = "jobId";
        public static final String SYNC_JOB_STATUS = "status";
        public static final String SYNC_REQ_ID_HEADER = "LM.REQ_ID";

    }


    public static class User implements BaseColumns {

       /* public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.tusker.users";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.tusker.user";*/

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "user";
        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        /* user column name */
        public static final String USER_ID = "id";
        public static final String USER_P_MOB = "p_mob";
        public static final String USER_F_NAME = "f_name";
        public static final String USER_IMG = "img";
        public static final String USER_SEX = "sex";
        public static final String USER_LANGUAGE = "lang";
        public static final String USER_ROLE = "role";
        public static final String USER_GROUP = "groupName";
        public static final String USER_LOC_ID = "locId";
        public static final String USER_LOC_AVL = "isLocAvail";


    }

    public static class Scm implements BaseColumns {
        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "scm";
        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        /**
         * Column name
         */
        public static final String TEAM_NAME = "name";
        public static final String INVENTORY = "inv";
        public static final String BACKLOG = "backlog";
        public static final String SCM_DATE = "date";
        public static final String WEEK_NO = "week";
        public static final String SCM_ROLE = "role";
        public static final String SCM_PO = "po";

    }

    public static class GeoFence implements BaseColumns {

        /*public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.tusker.geofences";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.tusker.geofence";*/

        /**
         * Table name where records are stored for "entry" resources.
         */
        public static final String TABLE_NAME = "geofence";
        /**
         * Fully qualified URI for "entry" resources.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        /* column Info*/
        public static final String GEO_LAT = "lat";
        public static final String GEO_LONG = "long";
        public static final String GEO_ID = "id";
        public static final String GEO_REQ_ID = "reqId";
        public static final String GEO_STATUS = "status";
        public static final String GEO_RADIUS = "radius";

    }

    public static class Gps implements BaseColumns {

        public static final String TABLE_NAME = "gps";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();

        /* column name */
        //todo - add trip id(primary key) column
        public static final String GPS_TRIP_ID =  "tripid" ;
        public static final String GPS_USER = "user";
        public static final String GPS_LAT = "lat";
        public static final String GPS_LONGITUDE = "long";
        public static final String GPS_SPEED = "speed";
        public static final String GPS_TIME = "time";
        public static final String GPS_ACCR = "accuracy";
        public static final String GPS_SRC = "source";
        public static final String GPS_ALT = "alt";
        public static final String GPS_DIST ="distance";


    }
    public static class Chat implements BaseColumns {

        public static final String TABLE_NAME = "chat";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(TABLE_NAME).build();
         /* column name */
        //todo - add chat(primary key) column

        public static final String CHAT_Trip_ID = "tripid";
        public static final String CHAT_USER = "user";
        public static final String CHAT_MSG = "msg";
        public static final String CHAT_TIME = "time";

    }


    }