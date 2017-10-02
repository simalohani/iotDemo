package com.iotapp.iot.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iotapp.iot.R;
import com.iotapp.iot.utility.FontUtil;


public class HomeAdapter extends BaseAdapter {
    private Context context;
    private final String[] mobileValues;

    public HomeAdapter(Context context, String[] mobileValues) {
        this.context = context;
        this.mobileValues = mobileValues;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {

            gridView = new View(context);

            // get layout from mobile.xml
            gridView = inflater.inflate(R.layout.item_home, null);

            // set value into textview
            TextView textView = (TextView) gridView
                    .findViewById(R.id.tv_home);
            textView.setTypeface(FontUtil.getInstance().getFont(FontUtil.BOLD));
            textView.setText(mobileValues[position]);

            // set image based on selected text
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.iv_icon);

            String mobile = mobileValues[position];

            if (mobile.equals("Live")) {
                imageView.setImageResource(R.drawable.ic_live_tracking);
            } else if (mobile.equals("History")) {
                imageView.setImageResource(R.drawable.ic_history_tracking);
            } else if (mobile.equals("Start Trip")) {
                imageView.setImageResource(R.drawable.ic_start_trip);
            } else {
                imageView.setImageResource(R.drawable.ic_chat_msg);
            }

        } else {
            gridView = (View) convertView;
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return mobileValues.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
