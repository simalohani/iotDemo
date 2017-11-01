package com.iotapp.iot.fragment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iotapp.iot.R;
import com.iotapp.iot.activity.HomeActivity;
import com.iotapp.iot.adapter.LiveTripAdapter;
import com.iotapp.iot.controller.LiveTripController;
import com.iotapp.iot.controller.TripController;

import java.util.ArrayList;


public class LiveTripActivity extends AppCompatActivity {
    RecyclerView rvTrip;
    LiveTripAdapter liveTripAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_trip);
        rvTrip = (RecyclerView) findViewById(R.id.rv_livetrip);
        rvTrip.addOnItemTouchListener(new RecyclerItemClickListener(rvTrip,new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                /*ArrayList tripList =  (ArrayList)liveTripAdapter.getItem(position);
                Intent intent = new Intent(LiveTripActivity.this,HomeActivity.class);
                intent.putExtra("data",tripList);
                startActivity(intent);*/
            }
        }));



    }

    @Override
    protected void onResume() {
        super.onResume();
        LiveTripController LiveTripController = new LiveTripController();
        ArrayList tripList =  LiveTripController.getLiveTrip(getApplicationContext());

       liveTripAdapter = new LiveTripAdapter(getApplicationContext(),tripList);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvTrip.setLayoutManager(llm);
        rvTrip.setHasFixedSize(true);
        rvTrip.setAdapter(liveTripAdapter);
    }



}

