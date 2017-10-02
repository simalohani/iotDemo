package com.iotapp.iot.controller;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import com.iotapp.iot.database.Projection;
import com.iotapp.iot.database.Schema;
import com.iotapp.iot.modal.ScmUsers;
import com.iotapp.iot.modal.ScmView;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kundankumar on 11/11/16.
 */
public class ScmController {

    public ArrayList<ScmView> getScmData(Context ctx) {
        Cursor rows = null;
        ArrayList<ScmView> scmRequests = new ArrayList<ScmView>();
        try {

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            String user = sharedPrefs.getString("prefUsername", "NULL");
            Uri uri = Schema.Scm.CONTENT_URI.buildUpon().appendPath("retailor")
                    .build();
            rows = ctx.getContentResolver().query(uri, Projection.SCM_PROJECTION, null, null, null);

            if (rows != null && rows.getCount() > 0 && rows.moveToFirst()) {
                do {
                    ScmView scmView = new ScmView();
                    scmView.setBacklog(Integer.parseInt(rows.getString(rows.getColumnIndex(Schema.Scm.BACKLOG))));
                    scmView.setInventory(Integer.parseInt(rows.getString(rows.getColumnIndex(Schema.Scm.INVENTORY))));
                    scmView.setName(rows.getString(rows.getColumnIndex(Schema.Scm.TEAM_NAME)));
                    scmView.setPo(Integer.parseInt(rows.getString(rows.getColumnIndex(Schema.Scm.SCM_PO))));
                    scmView.setWeekNo(Integer.parseInt(rows.getString(rows.getColumnIndex(Schema.Scm.WEEK_NO))));
                    scmView.setRole(rows.getString(rows.getColumnIndex(Schema.Scm.SCM_ROLE)));
                    scmRequests.add(scmView);
                } while (rows.moveToNext());

                return scmRequests;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Logger.getInstance().error(e);
        } finally {
            if (rows != null)
                rows.close();
        }
        return scmRequests;

    }

    public ArrayList<ScmUsers> getScmAdminView(Context ctx) {
        Cursor rows = null;
        try {

            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
            String user = sharedPrefs.getString("prefUsername", "NULL");
            Uri uri = Schema.User.CONTENT_URI.buildUpon()
                    .build();
            rows = ctx.getContentResolver().query(uri, Projection.USER_PROJECTION, null, null, null);
            ArrayList<ScmUsers> scmRequests = new ArrayList<ScmUsers>();
            if (rows != null && rows.getCount() > 0 && rows.moveToFirst()) {
                do {
                    ScmUsers scmView = new ScmUsers();
                    scmView.setUser(rows.getString(rows.getColumnIndex(Schema.User.USER_F_NAME)));
                    scmView.setRole(rows.getString(rows.getColumnIndex(Schema.User.USER_ROLE)));
                    scmView.setId(rows.getString(rows.getColumnIndex(Schema.User.USER_ID)));
                    scmRequests.add(scmView);
                    Log.e("getScmAdminView", "users available");
                } while (rows.moveToNext());

                return scmRequests;
            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (rows != null)
                rows.close();
        }
        return null;
    }

    public void saveScmRow(Context ctx, String role, int weekNo, int inventory, int backLog, int po, String groupName) {
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
        try {
            batch.add(ContentProviderOperation.newInsert(Schema.Scm.CONTENT_URI)
                    .withValue(Schema.Scm.INVENTORY, inventory)
                    .withValue(Schema.Scm.SCM_DATE, new Date().getTime())
                    .withValue(Schema.Scm.SCM_ROLE, role)
                    .withValue(Schema.Scm.WEEK_NO, weekNo)
                    .withValue(Schema.Scm.BACKLOG, backLog)
                    .withValue(Schema.Scm.SCM_PO, po)
                    .withValue(Schema.Scm.TEAM_NAME, groupName)
                    .build());

            ctx.getContentResolver().applyBatch(Schema.CONTENT_AUTHORITY, batch);
            Log.e("saveScmRow", "success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
