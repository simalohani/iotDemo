package com.iotapp.iot.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iotapp.iot.R;
import com.iotapp.iot.activity.HomeActivity;
import com.iotapp.iot.adapter.TripHistoryAdapter;
import com.iotapp.iot.controller.TripController;
import com.iotapp.iot.gps.AppUtils;
import com.iotapp.iot.gps.FetchAddressIntentService;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class TripHistoryActivity extends AppCompatActivity {

    RecyclerView rvTrip;
    TripHistoryAdapter tripHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        rvTrip = (RecyclerView) findViewById(R.id.rv_trip);
        rvTrip.addOnItemTouchListener(new RecyclerItemClickListener(rvTrip,new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ArrayList tripList =  (ArrayList)tripHistoryAdapter.getItem(position);
                Intent intent = new Intent(TripHistoryActivity.this,HomeActivity.class);
                intent.putExtra("data",tripList);
                startActivity(intent);
            }
        }));
    }

    @Override
    protected void onResume() {
        super.onResume();
        TripController tripController= new TripController();
        LinkedHashMap tripMap = tripController.getTrip(getApplicationContext());

        tripHistoryAdapter = new TripHistoryAdapter(getApplicationContext(),tripMap);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvTrip.setLayoutManager(llm);
        rvTrip.setHasFixedSize(true);
        rvTrip.setAdapter(tripHistoryAdapter);
    }





}
