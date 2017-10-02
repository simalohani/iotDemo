package com.iotapp.iot.activity;

import android.content.*;
import android.content.res.TypedArray;
import android.location.Location;
import android.os.Bundle;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.iotapp.iot.R;
import com.iotapp.iot.adapter.HomeAdapter;
import com.iotapp.iot.adapter.NavigationDrawerAdapter;
import com.iotapp.iot.controller.AlarmHandler;
import com.iotapp.iot.custom.FragmentInteractionListener;
import com.iotapp.iot.fragment.ChatFragment;
import com.iotapp.iot.fragment.MapFragment;
import com.iotapp.iot.fragment.ScmFragment;
import com.iotapp.iot.gps.AppUtils;
import com.iotapp.iot.gps.ConfigPreference;

import com.iotapp.iot.modal.GpsRequest;
import com.iotapp.iot.modal.view.FragmentHandler;
import com.iotapp.iot.mqtt.ServiceHandler;

import com.iotapp.iot.notification.NotificationUtil;
import com.iotapp.iot.service.MQTTservice;
import com.iotapp.iot.utility.Constant;
import com.iotapp.iot.utility.Util;

import java.util.ArrayList;
import java.util.HashMap;

//https://www.mkyong.com/android/android-gridview-example/
public class HomeActivity extends AppCompatActivity
        implements FragmentInteractionListener {

    public DrawerLayout drawerLayout;
    RecyclerView recyclerView;
    String navTitles[];
    TypedArray navIcons;
    RecyclerView.Adapter recyclerViewAdapter;
    ActionBarDrawerToggle drawerToggle;
    Toolbar toolbar;
    ActionBar actionBar;
    public static ServiceConn serviceConnection;
    private Messenger service = null;
    private IntentFilter intentFilter = null;
    private PushReceiver pushReceiver;
    static FragmentHandler fragmentStatus;
    String user;
    private Menu mymenu;
    SharedPreferences sharedPrefs;
    GridView gridView;
    static final String[] homeArray = new String[] {
            "Live", "History","Start Trip", "Chat" };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        user = sharedPrefs.getString("prefUsername", "NULL");

        // Creating The Toolbar and setting it as the Toolbar for the activity
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        //Adding Action bar
        actionBar = getSupportActionBar();
        //actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
        actionBar.setTitle("Home");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        setupNavigation();

        serviceConnection = new ServiceConn(new ServiceHandler());

        intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.LOCATION_ACTION);
        pushReceiver = new PushReceiver();
        registerReceiver(pushReceiver, intentFilter, null, null);

        startService(new Intent(this, MQTTservice.class));
        //addPublishButtonListener();
        //showDefaultScreen();
        Util.getInstance().setContext(this);

        gridView = (GridView) findViewById(R.id.gridView1);

        gridView.setAdapter(new HomeAdapter(this, homeArray));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Fragment fragment = null;
                if(position ==0){
                    //live
                     fragment= MapFragment.newInstance("live",null);
                }else if(position ==1){
                    //history
                }else if(position == 2){
                    //start trip
                }else{
                    gridView.setVisibility(View.GONE);
                    fragment= ChatFragment.newInstance("chat",null);
                    //chat
                }
                goToNextFragment(fragment);
            }
        });

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        mymenu = menu;
        int isMqttConnStatus = sharedPrefs.getInt(Constant.MQTT_CONN_STATUS, 0);
        setMqttStatus(isMqttConnStatus);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        /*int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(FragmentHandler fragmentHandler) {
        fragmentStatus = fragmentHandler;
        if(actionBar!= null) {
            actionBar.setTitle(fragmentHandler.getTitle());
        }
        Fragment fragment = null;
        if(fragmentHandler.isNextFragmentMove()) {
            switch (fragmentHandler.getScrNo()) {
                case Constant.CHAT_FRAGMENT:
                    fragment = ChatFragment.newInstance("chat", "chat");
                    break;
                case Constant.MAP_FRAGMENT:
                    fragment = MapFragment.newInstance("map", null);
                    break;


            }
            goToNextFragment(fragment);
        }
    }

    @Override
    public void sendData(Object object) {
        HashMap actionHashMap = (HashMap) object;
        Integer action = (Integer) actionHashMap.get(Constant.ACTION);
        switch (action) {
            case Constant.ACTION_PUBLISH_ACK:
                String data = (String) actionHashMap.get("data");
                serviceConnection.publishAck(this, data);
                break;
            case Constant.ACTION_PUBLISH_IMG:
                String user = (String) actionHashMap.get("user");
                String picturePath = (String) actionHashMap.get("picturePath");
                serviceConnection.sendImage(user, picturePath);
                break;
            case Constant.ACTION_PUBLISH_MSG:
                String topic = (String) actionHashMap.get("topic");
                String message = (String) actionHashMap.get("message");
                serviceConnection.publisgMsg(topic, message);
                break;
            default:
                break;

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MQTTservice.class), serviceConnection, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(pushReceiver, intentFilter);
        //checkTrackingStatus();
        int isMqttConnStatus = sharedPrefs.getInt(Constant.MQTT_CONN_STATUS, 0);
        setMqttStatus(isMqttConnStatus);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(serviceConnection);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(pushReceiver);
    }

    private void setupNavigation() {
        //Initialize Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Setup Titles and Icons of Navigation Drawer
        navTitles = getResources().getStringArray(R.array.navDrawerItems);
        navIcons = getResources().obtainTypedArray(R.array.navDrawerIcons);
        recyclerViewAdapter = new NavigationDrawerAdapter(navTitles, navIcons, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //Finally setup ActionBarDrawerToggle
        setupDrawerToggle();

    }

    void setupDrawerToggle() {
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            //This is necessary to change the icon of the Drawer Toggle upon state change.
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }

    private void showDefaultScreen() {
        Intent intent = getIntent();
        Fragment fragment;
        if(null!= intent){
            ArrayList tripList = intent.getParcelableArrayListExtra("data");
            if(tripList!=null){
                fragment = MapFragment.newInstance("Chat", tripList);
            }else{
                fragment = ChatFragment.newInstance("Chat", "");
            }
        }else{
            fragment = ChatFragment.newInstance("Chat", "");
        }

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, null);
        fragmentTransaction.commit();
    }

    public void goToNextFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment, null);
        fragmentTransaction.commit();
    }

    public class PushReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int isConnected = intent.getIntExtra(Constant.MQTT_CONNECTION_STATUS, 2);
            if(isConnected!=2){
                setMqttStatus(isConnected);
            }else {
                // Get the location passed to this service through an extra.
                int id = intent.getIntExtra(Constant.ID, 0);
                if (id == Constant.ID_LOC) {
                    Location location = intent.getParcelableExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA);
                    String isSelf = intent.getStringExtra("user");

                    if (fragmentStatus != null) {
                        if (fragmentStatus.getScrNo() == 2) {
                            if (fragmentStatus.getCurrFragment() instanceof MapFragment) {
                                if (isSelf.trim().equalsIgnoreCase(user.trim())) {
                                    ((MapFragment) fragmentStatus.getCurrFragment()).changeMap((Location) location);
                                } else {
                                    ((MapFragment) fragmentStatus.getCurrFragment()).drawmap(location.getLatitude(), location.getLongitude(), false, isSelf.trim());
                                }
                            }
                        }
                    }

                }else if(id==Constant.ID_LOC_START){
                    String isSelf = intent.getStringExtra("user");
                    if (!isSelf.trim().equalsIgnoreCase(user.trim())) {
                        String  location = intent.getStringExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA);
                        startTrip(isSelf,location);
                    }
                }
                else if(id==Constant.ID_SCM){
                    String scmInput = intent.getStringExtra(Constant.SCM_FUL_FILLMENT);
                    if(fragmentStatus != null){
                        if(fragmentStatus.getScrNo()==4){
                            if(fragmentStatus.getCurrFragment() instanceof ScmFragment){
                                ((ScmFragment) fragmentStatus.getCurrFragment()).incomingMsg(scmInput);
                            }
                        }
                    }
                }
            }

        }
    }

    private void setMqttStatus(int isConnected){
        if(mymenu!=null) {
            MenuItem m = mymenu.findItem(R.id.mqtt);
            if (m != null) {
                if (isConnected == 1) {
                    m.setIcon(R.mipmap.ic_sync_white_24dp);
                } else {
                    m.setIcon(R.mipmap.ic_sync_problem_white_24dp);
                }
            }
        }
    }

    public void startTrip(String user, String msg){

        String[] tripStr = msg.split("/");
        String tripId = tripStr[0];
        String tripMsg = tripStr[1];
        sharedPrefs.edit().putString(user,tripId).apply();
        Toast.makeText(getApplicationContext(),
                tripMsg, Toast.LENGTH_SHORT)
                .show();
        NotificationUtil notification= new NotificationUtil();
        notification.soundNotification(getApplicationContext(),true);

    }
}
