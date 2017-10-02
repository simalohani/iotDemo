package com.iotapp.iot.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.iotapp.iot.R;
import com.iotapp.iot.activity.HomeActivity;
import com.iotapp.iot.modal.ScmUsers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kundankumar on 14/11/16.
 */
public class ScmAdminAdapter extends BaseAdapter {

    String user;
    SharedPreferences sharedPrefs;
    private Context mContext;
    private ArrayList<ScmUsers> scmList;
    static int count =1;
    boolean isGroupForm = false;
    RelativeLayout groupLayout;
    boolean defaultSpinner = false;
    public static HashMap roleMaps = new HashMap();
    public ScmAdminAdapter(Context ctx, ArrayList<ScmUsers> scmList,RelativeLayout groupLayout) {
        mContext = ctx;
        this.scmList = scmList;
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        user = sharedPrefs.getString("prefUsername", "NULL");
        this.groupLayout = groupLayout;
    }

    @Override
    public int getCount() {
        return scmList.size();
    }

    @Override
    public Object getItem(int position) {
        return scmList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0L;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;
        if (view == null) {
            LayoutInflater in = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = in.inflate(R.layout.fragment_scm_admin_row, null);
            holder = new ViewHolder();
            assert view != null;
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.checkBoxEmg = ((CheckBox) view.findViewById(R.id.checkBoxEmg));
        holder.userTxtLbl = ((TextView) view.findViewById(R.id.userTxtLbl));
        holder.roleSpinner = ((Spinner) view.findViewById(R.id.roleSpinner));
        holder.roleSpinner.setTag(position);
        holder.editImg = ((ImageView) view.findViewById(R.id.editImg));

        holder.userTxtLbl.setText(scmList.get(position).getUser());
        //holder.roleSpinner.setText(scmList.get(paramInt).getBacklog());
        holder.checkBoxEmg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(count==4){
                        count=1;
                        isGroupForm = true;
                        groupLayout.setVisibility(View.VISIBLE);
                    }else{
                        groupLayout.setVisibility(View.GONE);
                    }
                    count++;
                }else{
                    count--;
                    isGroupForm = false;
                    groupLayout.setVisibility(View.GONE);
                }
            }
        });
        holder.roleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
               /* Toast.makeText(parent.getContext(),
                        "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                        Toast.LENGTH_SHORT).show();*/
                if(defaultSpinner) {
                    int index = (Integer) holder.roleSpinner.getTag();
                    String userId = scmList.get(index).getId();
                    //HomeActivity.serviceConnection.publisgMsg("role/"+userId,parent.getItemAtPosition(position).toString());
                    if (roleMaps.containsKey(parent.getItemAtPosition(position).toString())) {
                        Toast.makeText(mContext, "role is already selected", Toast.LENGTH_LONG).show();
                        return;
                    }
                    roleMaps.put(parent.getItemAtPosition(position).toString(), userId);
                }else{
                    defaultSpinner =true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return view;
    }

    static class ViewHolder {
        CheckBox checkBoxEmg;
        TextView userTxtLbl;
        Spinner roleSpinner;
        ImageView editImg;
    }
}
