package com.iotapp.iot.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import java.util.List;

public class Provider extends ContentProvider {

    // The constants below represent individual URI routes, as IDs. Every URI pattern recognized by
    // this ContentProvider is defined using sUriMatcher.addURI(), and associated with one of these
    // IDs.
    //
    // When a incoming URI is run through sUriMatcher, it will be tested against the defined
    // URI patterns, and the corresponding route ID will be returned.
    public static final int ROUTE_ENTRIES = 1;
    public static final int ROUTE_ENTRIES_ID = 2;
    public static final int ROUTE_LOCATION = 3;
    public static final int ROUTE_LOCATION_ID = 4;
    public static final int ROUTE_USER_ID = 5;
    public static final int ROUTE_CITYPIN_ID = 8;
    public static final int ROUTE_GEOFENCE_ID = 10;
    public static final int ROUTE_GPS_ID = 11;
    public static final int ROUTE_DISTANCE_ID = 13;
    public static final int ROUTE_SCM_ID = 14;

    /**
     * Content authority for this provider.
     */
    private static final String AUTHORITY = Schema.CONTENT_AUTHORITY;
    /**
     * UriMatcher, used to decode incoming URIs.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AUTHORITY, Schema.Sync.TABLE_NAME, ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, Schema.Sync.TABLE_NAME + "/*", ROUTE_ENTRIES_ID);

        sUriMatcher.addURI(AUTHORITY, Schema.User.TABLE_NAME, ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, Schema.User.TABLE_NAME + "/*", ROUTE_USER_ID);

        sUriMatcher.addURI(AUTHORITY, Schema.GeoFence.TABLE_NAME, ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, Schema.GeoFence.TABLE_NAME + "/*", ROUTE_GEOFENCE_ID);
        sUriMatcher.addURI(AUTHORITY, Schema.Gps.TABLE_NAME, ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, Schema.Gps.TABLE_NAME + "/*", ROUTE_GPS_ID);

        sUriMatcher.addURI(AUTHORITY, Schema.Scm.TABLE_NAME, ROUTE_ENTRIES);
        sUriMatcher.addURI(AUTHORITY, Schema.Scm.TABLE_NAME + "/*", ROUTE_SCM_ID);

    }

    DataBase mDatabaseHelper;

    @Override
    public boolean onCreate() {
        mDatabaseHelper = new DataBase(getContext());
        return true;
    }

    /**
     * Determine the mime type for entries returned by a given URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ROUTE_ENTRIES:
                return Schema.Sync.CONTENT_TYPE;
            case ROUTE_ENTRIES_ID:
                return Schema.Sync.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    /**
     * Perform a database query by URI.
     * <p/>
     * <p>Currently supports returning all entries (/entries) and individual entries by ID
     * (/entries/{ID}).
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mDatabaseHelper.getReadDbHelper();
        SelectionBuilder builder = new SelectionBuilder();
        int uriMatch = sUriMatcher.match(uri);
        String tableId = uri.getLastPathSegment();
        String[] strings;
        List<String> parameters;
        Cursor c = null;
        switch (uriMatch) {
            case ROUTE_ENTRIES_ID:
                break;
            case ROUTE_ENTRIES:
                builder.table(tableId);
                c = builder.query(db, projection, sortOrder);
                break;
            case ROUTE_LOCATION_ID:
                break;
            case ROUTE_USER_ID:
                break;
            case ROUTE_CITYPIN_ID:
                break;
            case ROUTE_GEOFENCE_ID:
                break;
            case ROUTE_DISTANCE_ID:
                break;
            case ROUTE_GPS_ID:
                builder.table(Schema.Gps.TABLE_NAME)
                        .where(Schema.Gps.GPS_USER + "!=?", tableId);
                c = builder.query(db, projection, sortOrder);
                break;
            case ROUTE_SCM_ID:
                builder.table(Schema.Scm.TABLE_NAME);
                c = builder.query(db, projection, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        return c;
    }

    /**
     * Insert a new entry into the database.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mDatabaseHelper.getWriteDbHelper();
        assert db != null;
        final int match = sUriMatcher.match(uri);
        Uri result = null;
        String tableId = uri.getLastPathSegment();

        switch (match) {
            case ROUTE_ENTRIES:
                try {
                    long id = db.insertOrThrow(tableId, null, values);
                    result = Uri.parse(uri + "/" + id);
                } catch (Exception e) {
                    if (!tableId.equalsIgnoreCase("sync")) {
                        long id = db.replace(tableId, null, values);
                        result = Uri.parse(uri + "/" + id);
                    }
                }
                break;
            case ROUTE_ENTRIES_ID:
                throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Send broadcast to registered ContentObservers, to refresh UI.
        if (tableId.equalsIgnoreCase("sync")) {
            Context ctx = getContext();
            assert ctx != null;
            ctx.getContentResolver().notifyChange(uri, null, true);
        }
        return result;
    }

    /**
     * Delete an entry by database by URI.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWriteDbHelper();
        final int match = sUriMatcher.match(uri);
        int count = 0;
        switch (match) {
            case ROUTE_ENTRIES:
                break;
            case ROUTE_ENTRIES_ID:
                break;
            case ROUTE_LOCATION_ID:
                break;
            case ROUTE_USER_ID:
                break;
            case ROUTE_GEOFENCE_ID:
                break;
            case ROUTE_GPS_ID:
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return count;
    }

    /**
     * Update an entry in the database by URI.
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SelectionBuilder builder = new SelectionBuilder();
        final SQLiteDatabase db = mDatabaseHelper.getWriteDbHelper();
        final int match = sUriMatcher.match(uri);
        int count;
        String id;
        switch (match) {
            case ROUTE_ENTRIES:
                count = builder.table(Schema.Sync.TABLE_NAME)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_ENTRIES_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Schema.Sync.TABLE_NAME)
                        .where(Schema.Sync.SYNC_JOB_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_GEOFENCE_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Schema.GeoFence.TABLE_NAME)
                        .where(Schema.GeoFence.GEO_REQ_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            case ROUTE_USER_ID:
                id = uri.getLastPathSegment();
                count = builder.table(Schema.User.TABLE_NAME)
                        .where(Schema.User.USER_ID + "=?", id)
                        .where(selection, selectionArgs)
                        .update(db, values);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return count;
    }

}
