package com.iotapp.iot.fragment;


import android.content.Context;
import android.content.SharedPreferences;
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
import com.iotapp.iot.adapter.ScmAdapter;
import com.iotapp.iot.controller.ScmController;
import com.iotapp.iot.controller.Simulation;
import com.iotapp.iot.custom.FragmentInteractionListener;
import com.iotapp.iot.database.Schema;
import com.iotapp.iot.modal.ScmView;
import com.iotapp.iot.modal.view.FragmentHandler;
import com.iotapp.iot.utility.Constant;
import org.json.JSONObject;

import java.util.ArrayList;

public class ScmFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    ImageView inventoryImg;
    TextView invTxtLbl, invTxtVal, orderFullFillmentLbl, orderFullFillmentVal;
    EditText poTxtVal;
    ArrayList<ScmView> scmList = new ArrayList();
    ScmAdapter adapter;
    ListView listView;
    SharedPreferences sharedPrefs;
    private FragmentInteractionListener mListener;
    public String user,userId;
    public String groupName,role,poId;
    int inventory;
    int orderFullFillMent,backLog;
    Button save;
    int weekNo;
    public ScmFragment() {
        // Required empty public constructor
    }

    public static ScmFragment newInstance(String param1, String param2) {
        ScmFragment fragment = new ScmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_scm, container, false);
        inventoryImg = (ImageView) rootView.findViewById(R.id.inventoryImg);
        invTxtLbl = (TextView) rootView.findViewById(R.id.invTxtLbl);
        invTxtVal = (TextView) rootView.findViewById(R.id.invTxtVal);
        poTxtVal = (EditText) rootView.findViewById(R.id.poTxtVal);
        orderFullFillmentLbl = (TextView) rootView.findViewById(R.id.orderFullFillmentLbl);
        orderFullFillmentVal = (TextView) rootView.findViewById(R.id.orderFullFillmentVal);
        this.listView = ((ListView) rootView.findViewById(R.id.scmListView));
        save = (Button) rootView.findViewById(R.id.save);
        this.adapter = new ScmAdapter(getContext(), scmList);
        this.listView.setAdapter(this.adapter);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        user = sharedPrefs.getString("prefUsername", "NULL");
        userId = sharedPrefs.getString("userId", "NULL");
        groupName = sharedPrefs.getString(Schema.User.USER_GROUP, "");
        role = sharedPrefs.getString(Schema.User.USER_ROLE, "");
        poId = sharedPrefs.getString(Constant.PO_ID,"");
        inventory = sharedPrefs.getInt(Schema.Scm.INVENTORY,-1);

        if(role.equalsIgnoreCase("")) {
            setCurrFragment("Beer game");
        }else{
            setCurrFragment(groupName +" "+role);
        }
        inventoryImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(role.equalsIgnoreCase(Constant.ROLE_RETAILER)) {
                    new Simulation().simulate();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishOrder();
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mListener = (FragmentInteractionListener) activity;
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

    private void setCurrFragment(String title) {
        FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.setCurrFragment(this);
        fragmentHandler.setScrNo(Constant.SCM_FRAGMENT);
        fragmentHandler.setData(null);
        fragmentHandler.setIsBackFragmentMove(false);
        fragmentHandler.setIsNextFragmentMove(false);
        fragmentHandler.setTitle(title);
        mListener.onFragmentInteraction(fragmentHandler);
    }

    public void incomingMsg(String data) {
        fulFillOrder(data);
    }

    private void fulFillOrder(String data) {
        Log.e("ScmFragment", data);
        try {
            JSONObject jsonObject = new JSONObject(data);
            String topic = jsonObject.optString(Constant.TOPIC);
            if(topic.contains(Constant.GROUP_REG)){
                groupName = jsonObject.optString(Schema.User.USER_GROUP);
                role = jsonObject.optString(Schema.User.USER_ROLE);
                poId = jsonObject.optString(Constant.PO_ID);
                inventory = 20;
                sharedPrefs.edit().putString(Schema.User.USER_GROUP,groupName).apply();
                sharedPrefs.edit().putString(Schema.User.USER_ROLE,role).apply();
                sharedPrefs.edit().putString(Constant.PO_ID,poId).apply();
                sharedPrefs.edit().putInt(Schema.Scm.INVENTORY, 20).apply();
                setCurrFragment(groupName + "  " + role);
                HomeActivity.serviceConnection.subscribeMsg(Constant.TOPIC_INVENTORY + "/" + poId);

            }else if(topic.contains(Constant.PO_ID)){

                orderFullFillMent = Integer.parseInt(jsonObject.optString(Schema.Scm.SCM_PO));
                int fullFilled = 0;
                fullFilled = inventory - orderFullFillMent;
                if(fullFilled>=0){
                    invTxtVal.setText(String.valueOf(fullFilled));
                    if(backLog>0) {
                        backLog = fullFilled - backLog;
                    }
                    HomeActivity.serviceConnection.publisgMsg(Constant.TOPIC_INVENTORY+"/"+ userId, jsonObject.optString(Schema.Scm.SCM_PO));
                    inventory = fullFilled;
                }else{
                    backLog = backLog + (-fullFilled);
                    HomeActivity.serviceConnection.publisgMsg(Constant.TOPIC_INVENTORY+"/"+ userId, String.valueOf(inventory));
                    inventory = 0;
                }
                orderFullFillmentVal.setText(String.valueOf(orderFullFillMent));


            }else if(topic.contains(Constant.TOPIC_INVENTORY)){

                String incomInventory = jsonObject.optString(Schema.Scm.INVENTORY);
                inventory = inventory + Integer.parseInt(incomInventory);

            }
            else if(topic.contains(Constant.ROLE_RETAILER)){
                orderFullFillMent = Integer.parseInt(jsonObject.optString(Schema.Scm.INVENTORY));
                int fullFilled = 0;
                fullFilled = inventory - orderFullFillMent;
                if(fullFilled>=0){
                    invTxtVal.setText(String.valueOf(fullFilled));
                    backLog = fullFilled - backLog;
                    inventory = fullFilled;
                }else{
                    backLog = backLog + (-fullFilled);
                    inventory = 0;
                }
                orderFullFillmentVal.setText(String.valueOf(orderFullFillMent));

            }
        }catch (Exception e){

        }
    }

    public void updateScmData(ScmView scmView) {
        scmList.add(scmView);
        this.adapter = new ScmAdapter(getContext(), scmList);
        this.listView.setAdapter(this.adapter);
        this.adapter.notifyDataSetChanged();
    }

    public void displayData() {
        try {
            scmList = new ScmController().getScmData(getContext());
            if (scmList != null) {
                this.adapter = new ScmAdapter(getContext(), scmList);
                this.listView.setAdapter(this.adapter);
                this.adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void publishOrder(){

        try {

            String poVal = poTxtVal.getText().toString();
            weekNo = weekNo+1;
            new ScmController().saveScmRow(getActivity().getApplicationContext(), role, weekNo, inventory, backLog, Integer.parseInt(poVal), groupName);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(Schema.Scm.SCM_PO, poVal);
            HomeActivity.serviceConnection.publisgMsg(Constant.PO_ID + "/" + poId, jsonObject.toString());
            Log.e("pubMsgToUsers", jsonObject.toString());
            ScmView scmView = new ScmView();
            scmView.setPo(Integer.parseInt(poVal));
            scmView.setInventory(inventory);
            scmView.setBacklog(backLog);
            updateScmData(scmView);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
