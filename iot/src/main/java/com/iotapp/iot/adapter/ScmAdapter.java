package com.iotapp.iot.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import com.iotapp.iot.R;
import com.iotapp.iot.modal.ScmView;

import java.util.ArrayList;

/**
 * Created by kundankumar on 26/09/16.
 */
public class ScmAdapter extends BaseAdapter {

    String user;
    SharedPreferences sharedPrefs;
    private Context mContext;
    private ArrayList<ScmView> scmList;

    public ScmAdapter(Context ctx, ArrayList<ScmView> scmList) {
        mContext = ctx;
        this.scmList = scmList;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        user = sharedPrefs.getString("prefUsername", "NULL");
    }

    public int getCount() {
        return scmList.size();
    }

    public Object getItem(int paramInt) {
        return scmList.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return 0L;
    }

    public View getView(int paramInt, View paramView,
                        ViewGroup paramViewGroup) {
        final ViewHolder holder;
        View view = paramView;
        if (view == null) {
            LayoutInflater in = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = in.inflate(R.layout.fragment_scm_row, null);
            holder = new ViewHolder();
            assert view != null;
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.invTxtVal = ((EditText) view.findViewById(R.id.invTxtVal));
        holder.BackLogTxtVal = ((EditText) view.findViewById(R.id.BackLogTxtVal));
        holder.poTxtVal = ((EditText) view.findViewById(R.id.poTxtVal));
        holder.editImg = ((ImageView) view.findViewById(R.id.editImg));
        String invTxt = String.valueOf(scmList.get(paramInt).getInventory());
        String backLog = String.valueOf(scmList.get(paramInt).getBacklog());
        String po = String.valueOf(scmList.get(paramInt).getPo());
        holder.invTxtVal.setText(invTxt);
        holder.BackLogTxtVal.setText(backLog);
        holder.poTxtVal.setText(po);
        if (paramInt == scmList.size() - 1) {
            holder.editImg.setVisibility(View.VISIBLE);
            holder.invTxtVal.setEnabled(true);
            holder.BackLogTxtVal.setEnabled(true);
            holder.poTxtVal.setEnabled(true);
        } else {
            holder.editImg.setVisibility(View.GONE);
            holder.invTxtVal.setEnabled(false);
            holder.BackLogTxtVal.setEnabled(false);
            holder.poTxtVal.setEnabled(false);
        }

        holder.editImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = scmList.size() - 1;

            }
        });

        return view;

    }

    static class ViewHolder {


        EditText invTxtVal;
        EditText BackLogTxtVal;
        EditText poTxtVal;
        ImageView editImg;
    }

}
