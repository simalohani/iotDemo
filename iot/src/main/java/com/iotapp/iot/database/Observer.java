package com.iotapp.iot.database;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

/**
 * Created by kundankumar on 24/11/15.
 */
public class Observer extends ContentObserver {
    Handler mHandler;

    public Observer(Handler handler) {
        super(handler);
        this.mHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);
        /*Log.e("onChange", uri.getLastPathSegment());
        Logger.getInstance().error("onChange :"+uri.getLastPathSegment());
        if (uri.getLastPathSegment().contains(Schema.Sync.TABLE_NAME)) {
            //SyncUtils.TriggerRefresh();
        }  else if (uri.getLastPathSegment().contains(Schema.Configuration.TABLE_NAME)) {
            Message msg = mHandler.obtainMessage(Const.DW_UPDATE);
            Bundle bundle = new Bundle();
            bundle.putInt(SyncUtils.SYNC_TYPE, SyncUtils.CONFIG_DW_ID);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        } else if (uri.getLastPathSegment().contains(Schema.GeoFence.TABLE_NAME)) {
            Message msg = mHandler.obtainMessage(Const.DW_UPDATE);
            Bundle bundle = new Bundle();
            bundle.putInt(SyncUtils.SYNC_TYPE, Const.KEY_GEOFENCE);
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }*/


    }

}
