package com.iotapp.iot.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Simple reminder database access helper class. Defines the basic CRUD
 * operations (Create, Read, Update, Delete) for the example, and gives the
 * ability to list all reminders as well as retrieve or modify a specific
 * reminder.
 */
public class DataBase {
    // Databsae Related Constants

    private static final String DATABASE_NAME = "tusker";
    private static final int DATABASE_VERSION = 10;
    private static final String TAG = "DataBase";
    /**
     * Database creation SQL statement
     */

    private static final String TABLE_CREATE_SYNC = "create table " + Schema.Sync.TABLE_NAME +
            "(" + Schema.Sync.SYNC_JOB_TYPE + " text not null," + Schema.Sync.SYNC_JOB_ID + " text," + Schema.Sync.SYNC_JOB_REQ_ID + " text," + Schema.Sync.SYNC_PAYLOAD + " text,"
            + Schema.Sync.SYNC_HEADER + " text," + Schema.Sync.SYNC_URL + " text," + Schema.Sync.SYNC_METHOD_TYPE + " text," + Schema.Sync.SYNC_JOB_STATUS + " integer)";


    private static final String TABLE_CREATE_USER = "create table " + Schema.User.TABLE_NAME +
            "(" + Schema.User.USER_ID + " text PRIMARY KEY not null," + Schema.User.USER_LOC_ID + " text ," + Schema.User.USER_ROLE + " text ," + Schema.User.USER_F_NAME + " text," + Schema.User.USER_P_MOB + " text,"
            + Schema.User.USER_LANGUAGE + " text," + Schema.User.USER_SEX + " text," + Schema.User.USER_IMG + " text," + Schema.User.USER_GROUP + " text," + Schema.User.USER_LOC_AVL + " boolean default 1)";

    private static final String TABLE_CREATE_GEOFENCE = "create table " + Schema.GeoFence.TABLE_NAME +
            "(" + Schema.GeoFence.GEO_REQ_ID + " text PRIMARY KEY not null," + Schema.GeoFence.GEO_ID + " text not null," + Schema.GeoFence.GEO_RADIUS + " integer not null," + Schema.GeoFence.GEO_LAT + " text," + Schema.GeoFence.GEO_LONG + " text," + Schema.GeoFence.GEO_STATUS + " int)";

    private static final String TABLE_CREATE_GPS = "create table " + Schema.Gps.TABLE_NAME +
            "("+ Schema.Gps.GPS_TRIP_ID + " text not null,"  + Schema.Gps.GPS_USER + " text not null," + Schema.Gps.GPS_LAT
            + " double not null," + Schema.Gps.GPS_LONGITUDE + " double not null," + Schema.Gps.GPS_SPEED + " float,"
            + Schema.Gps.GPS_ACCR + " double," + Schema.Gps.GPS_SRC + " text," + Schema.Gps.GPS_TIME + " long,"
            + Schema.Gps.GPS_ALT + " double," + Schema.Gps.GPS_DIST + " float)" ;

    private static final String TABLE_CREATE_CHAT = "create table " + Schema.Chat.TABLE_NAME +
            "("+ Schema.Chat.CHAT_Trip_ID+"text not null," + Schema.Chat.CHAT_USER+" text not null,"
               +Schema.Chat.CHAT_MSG+ "text not null,"+ Schema.Chat.CHAT_TIME+ " long)";







    /**
     * SQL statement to drop "entry" table.
     */
    private static final String SQL_SYNC = "DROP TABLE IF EXISTS " + Schema.Sync.TABLE_NAME;

    private static final String SQL_USER = "DROP TABLE IF EXISTS " + Schema.User.TABLE_NAME;

    private static final String SQL_GEOFENCE = "DROP TABLE IF EXISTS " + Schema.GeoFence.TABLE_NAME;
    private static final String SQL_GPS = "DROP TABLE IF EXISTS " + Schema.Gps.TABLE_NAME;
    private static  final String SQL_CHAT =" DROP TABLE IF EXISTS" + Schema.Chat.TABLE_NAME;
    private static Context mCtx = null;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;


    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     *
     * @param ctx the Context within which to work
     */
    public DataBase(Context ctx) {
        mCtx = ctx;
        if (mDbHelper == null) {
            mDbHelper = new DatabaseHelper(mCtx);
        }
        mDb = mDbHelper.getWritableDatabase();
    }


    public SQLiteDatabase getWriteDbHelper() {
        mDb = mDbHelper.getWritableDatabase();
        return mDb;
    }

    public SQLiteDatabase getReadDbHelper() {
        mDb = mDbHelper.getReadableDatabase();
        return mDb;
    }

    public void insertRows(String tableName, ContentValues[] serverData) {
        try {
            if (mDb != null) {
                mDb.beginTransaction();
                for (ContentValues values : serverData) {
                    try {
                        mDb.insertOrThrow(tableName, null, values);
                    } catch (Exception e) {
                        mDb.replace(tableName, null, values);
                        values.clear();
                        //Logger.getInstance().error("error in orders data insertRows:" + e.getMessage());
                    }
                }
                mDb.setTransactionSuccessful();
            }
        } catch (Exception e) {
            //Logger.getInstance().error("error in all data insertRows :" + e.getMessage());
        } finally {
            if (mDb != null && mDb.inTransaction()) {
                mDb.endTransaction();
            }
        }
    }

    /**
     * Open the database. If it cannot be opened, try to create a new instance
     * of the database. If it cannot be created, throw an exception to signal
     * the failure
     *
     * @return this (self reference, allowing this to be chained in an
     * initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public DataBase open() throws SQLException {
        mDbHelper = new DatabaseHelper(mCtx);
        mDb = mDbHelper.getWritableDatabase();

        return this;
    }

    public void close() {

        mDbHelper.close();
    }

    /**
     * @param tablename
     * @param selection
     * @param selectionargs
     * @param orderby
     * @return cursor
     */
    public Cursor query(String tablename, String selection, String[] selectionargs, String orderby) {

        //return mDb.query(tablename, null, selection, selectionargs, null, null, orderby);
        return mDb.rawQuery(tablename, null);
    }

    public void update(String table, ContentValues values, String whereClause, String[] whereArgs) {

        mDb.update(table, values, whereClause, whereArgs);
    }

    public void resetDataBase() {
        try {
            if (mDb != null) {

                mDb.execSQL(SQL_SYNC);

                mDb.execSQL(SQL_USER);



                mDb.execSQL(SQL_GEOFENCE);
                mDb.execSQL(SQL_GPS);


                //on Create
                mDb.execSQL(TABLE_CREATE_SYNC);

                mDb.execSQL(TABLE_CREATE_USER);

                mDb.execSQL(TABLE_CREATE_GEOFENCE);
                mDb.execSQL(TABLE_CREATE_GPS);

                Log.e(TAG, ":resetDataBase->DB creating");

            }
            if (mDbHelper != null) {
                mDbHelper = null;
                mDb = null;
            }
        } catch (Exception e) {
            //Logger.getInstance().error(e);
        }
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {

            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try {
                db.execSQL(TABLE_CREATE_SYNC);
                db.execSQL(TABLE_CREATE_USER);
                db.execSQL(TABLE_CREATE_GEOFENCE);
                db.execSQL(TABLE_CREATE_GPS);
                db.execSQL(TABLE_CREATE_CHAT);
                //Logger.getInstance().info(TAG + ":DB creating");
                Log.e(TAG, ":DB creating");
            } catch (Exception e) {
                e.printStackTrace();
                //Logger.getInstance().error(e);
            }

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //Logger.getInstance().info("Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            Log.e(TAG, ":onUpgrade");
            db.execSQL(SQL_SYNC);
            db.execSQL(SQL_USER);
            db.execSQL(SQL_GEOFENCE);
            db.execSQL(SQL_GPS);
            db.execSQL( SQL_CHAT);

            onCreate(db);
            // upgradeConfig();
            for (int i = oldVersion; i < newVersion; ++i) {
                //file name shall be in assets folder from_old_to_new.sql
                String migrationName = String.format("from_%d_to_%d.sql", i, (i + 1));
                Log.d(TAG, "Looking for migration file: " + migrationName);
                readAndExecuteSQLScript(db, mCtx, migrationName);
            }
        }
        /**
         * This method is used for finding all sql files which has upgraded in released version
         * @param db is database
         * @param ctx is context
         * @param fileName reading every version file from asset folder
         */
        private void readAndExecuteSQLScript(SQLiteDatabase db, Context ctx, String fileName) {
            if (TextUtils.isEmpty(fileName)) {
                Log.d(TAG, "SQL db_script file name is empty");
                return;
            }

            Log.d(TAG, "Script found. Executing...");
            AssetManager assetManager = ctx.getAssets();
            BufferedReader reader = null;

            try {
                InputStream is = assetManager.open("db_script/"+fileName);
                InputStreamReader isr = new InputStreamReader(is);
                reader = new BufferedReader(isr);
                executeSQLScript(db, reader);
            } catch (IOException e) {
                Log.e(TAG, "IOException:", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(TAG, "IOException:", e);
                    }
                }
            }

        }

        /**
         * This method is used for executing sql query in file to make change which is released with every version
         * @param db
         * @param reader
         * @throws IOException
         */
        private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
            String line;
            StringBuilder statement = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                statement.append(line);
                statement.append("\n");
                if (line.endsWith(";")) {
                    db.execSQL(statement.toString());
                    statement = new StringBuilder();
                }
            }

        }
    }





}

