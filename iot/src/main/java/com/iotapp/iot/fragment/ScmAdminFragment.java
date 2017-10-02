package com.iotapp.iot.fragment;

import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.iotapp.iot.R;
import com.iotapp.iot.activity.HomeActivity;
import com.iotapp.iot.adapter.ScmAdminAdapter;
import com.iotapp.iot.controller.ScmController;
import com.iotapp.iot.custom.FragmentInteractionListener;
import com.iotapp.iot.database.Schema;
import com.iotapp.iot.modal.ScmUsers;
import com.iotapp.iot.modal.view.FragmentHandler;
import com.iotapp.iot.utility.Constant;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ScmAdminFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String user;
    SharedPreferences sharedPrefs;
    LinearLayout parentLayout;
    ArrayList<ScmUsers> scmList = new ArrayList();
    ScmAdminAdapter adapter;
    ListView listView;
    RelativeLayout groupLayout;
    EditText groupName;
    Button strtGameBtn;
    private FragmentInteractionListener mListener;

    public ScmAdminFragment() {
        // Required empty public constructor
    }

    public static ScmAdminFragment newInstance(String param1, String param2) {
        ScmAdminFragment fragment = new ScmAdminFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_scm_admin, container, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        user = sharedPrefs.getString("prefUsername", "NULL");
        parentLayout = (LinearLayout) rootView.findViewById(R.id.lv_prnt);
        addView();
        groupLayout = (RelativeLayout) rootView.findViewById(R.id.groupLayout);
        groupName = (EditText) rootView.findViewById(R.id.groupName);
        strtGameBtn = (Button) rootView.findViewById(R.id.strtGameBtn);
        this.listView = ((ListView) rootView.findViewById(R.id.scmAdminListView));
        this.adapter = new ScmAdminAdapter(getActivity().getApplicationContext(), scmList,groupLayout);
        this.listView.setAdapter(this.adapter);

        initHandler();

        return rootView;
    }

    private void initHandler(){
        strtGameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name = groupName.getText().toString();
                    String placeOrderId = "";
                    HashMap<String, String> userRoleMaps = ScmAdminAdapter.roleMaps;
                    ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();
                    for (HashMap.Entry<String, String> entry : userRoleMaps.entrySet()) {
                        String role = entry.getKey();
                        String userId = entry.getValue();
                        Uri updateUri = Schema.User.CONTENT_URI.buildUpon()
                                .appendPath(userId).build();
                        batch.add(ContentProviderOperation.newUpdate(updateUri)
                                .withValue(Schema.User.USER_GROUP, name)
                                .withValue(Schema.User.USER_ROLE, role).build());
                        if(role.equalsIgnoreCase(Constant.ROLE_RETAILER)){
                            placeOrderId =userRoleMaps.get(Constant.ROLE_WHOLESALER);
                        }else if(role.equalsIgnoreCase(Constant.ROLE_WHOLESALER)){
                            placeOrderId =userRoleMaps.get(Constant.ROLE_DISTRIBUTOR);
                        }else if(role.equalsIgnoreCase(Constant.ROLE_DISTRIBUTOR)){
                            placeOrderId =userRoleMaps.get(Constant.ROLE_FACTORY);
                        }
                    pubMsgToUsers(userId,name,role,placeOrderId);
                    }

                    getActivity().getApplicationContext().getContentResolver().applyBatch(Schema.CONTENT_AUTHORITY, batch);

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void pubMsgToUsers(String userId,String groupName,String role,String poId){
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Schema.User.USER_GROUP, groupName);
            jsonObject.put(Schema.User.USER_ROLE, role);
            jsonObject.put(Constant.PO_ID,poId);
            HomeActivity.serviceConnection.publisgMsg(Constant.GROUP_REG + "/" + userId, jsonObject.toString());
            Log.e("pubMsgToUsers",jsonObject.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListener) activity;
            setCurrFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        displayData();
    }

    private void setCurrFragment() {
        FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.setCurrFragment(this);
        fragmentHandler.setScrNo(Constant.SCM_ADMIN_FRAGMENT);
        fragmentHandler.setData(null);
        fragmentHandler.setIsBackFragmentMove(false);
        fragmentHandler.setIsNextFragmentMove(false);
        fragmentHandler.setTitle("Beer Game");
        mListener.onFragmentInteraction(fragmentHandler);
    }

    private void addView() {

    }

    public void incomingMsg(String data) {
        fulFillOrder(data);
    }

    private void fulFillOrder(String data) {

    }

    public void updateScmData(ScmUsers scmView) {
        scmList.add(scmView);
        this.adapter = new ScmAdminAdapter(getActivity().getApplicationContext(), scmList,groupLayout);
        this.listView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
    }

    public void displayData() {
        try {
            scmList = new ScmController().getScmAdminView(getActivity().getApplicationContext());
            if (scmList != null) {
                this.adapter = new ScmAdminAdapter(getActivity().getApplicationContext(), scmList,groupLayout);
                this.listView.setAdapter(this.adapter);
                this.adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
