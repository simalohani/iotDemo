package com.iotapp.iot.controller;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.util.Log;
import com.iotapp.iot.database.Schema;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by kundankumar on 16/11/16.
 */
public class UserController {

    public String getUserJson(String user, String userId) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Schema.User.USER_F_NAME, user);
            jsonObject.put(Schema.User.USER_ID, userId);
            return jsonObject.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void registration(Context ctx, String userJson) {
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        try {
            JSONObject jsonObject = new JSONObject(userJson);

            batch.add(ContentProviderOperation.newInsert(Schema.User.CONTENT_URI)
                    .withValue(Schema.User.USER_ID, jsonObject.get(Schema.User.USER_ID))
                    .withValue(Schema.User.USER_F_NAME, jsonObject.get(Schema.User.USER_F_NAME))
                    .build());
            addDummyData(ctx);
            ctx.getContentResolver().applyBatch(Schema.CONTENT_AUTHORITY, batch);
            Log.e("registration", "success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addDummyData(Context ctx) {
        ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

        try {

            batch.add(ContentProviderOperation.newInsert(Schema.User.CONTENT_URI)
                    .withValue(Schema.User.USER_ID, "readme")
                    .withValue(Schema.User.USER_F_NAME, "readme")
                    .build());

            batch.add(ContentProviderOperation.newInsert(Schema.User.CONTENT_URI)
                    .withValue(Schema.User.USER_ID, "samsung")
                    .withValue(Schema.User.USER_F_NAME, "samsung")
                    .build());
            ctx.getContentResolver().applyBatch(Schema.CONTENT_AUTHORITY, batch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
