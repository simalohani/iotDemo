package com.iotapp.iot.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.gson.internal.ObjectConstructor;
import com.iotapp.iot.R;
import com.iotapp.iot.activity.HomeActivity;
import com.iotapp.iot.custom.FragmentInteractionListener;
import com.iotapp.iot.gps.AppUtils;
import com.iotapp.iot.gps.ConfigPreference;
import com.iotapp.iot.gps.FetchAddressIntentService;
import com.iotapp.iot.gps.GeofanceHandler;
import com.iotapp.iot.modal.GpsRequest;
import com.iotapp.iot.modal.view.FragmentHandler;
import com.iotapp.iot.modal.view.TripView;
import com.iotapp.iot.notification.NotificationUtil;
import com.iotapp.iot.utility.Constant;
import com.iotapp.iot.utility.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class MapFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final int REQUEST_CODE_AUTOCOMPLETE = 1;
    private static String TAG = "MAP LOCATION";
    /**
     * The formatted location address.
     */
    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    Context mContext;
    TextView mLocationMarkerText;
    MapView mMapView;
    EditText mLocationAddress;
    TextView mLocationText;
    private String mParam1;
    private ArrayList mParam2;
    private FragmentInteractionListener mListener;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LatLng mCenterLatLong;
    Double pLati;
    Double plongi;
    ArrayList<LatLng> arrayList = new ArrayList<LatLng>();
    ArrayList<LatLng> otherarrayList = new ArrayList<LatLng>();
    ImageView startTrip;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;
    String user;
    SharedPreferences sharedPrefs;
    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String param1, ArrayList param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        if(param2!=null) {
            args.putParcelableArrayList(ARG_PARAM2, param2);
        }
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getParcelableArrayList(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        user = sharedPrefs.getString("prefUsername", "NULL");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        //SupportMapFragment mapFragment = (SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map);
        MapsInitializer.initialize(this.getActivity());
        mContext = getContext();
        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        mLocationMarkerText = (TextView) rootView.findViewById(R.id.locationMarkertext);
        mLocationAddress = (EditText) rootView.findViewById(R.id.Address);
        mLocationText = (TextView) rootView.findViewById(R.id.Locality);
        mLocationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                openAutocompleteActivity();

            }


        });
        mResultReceiver = new AddressResultReceiver(new Handler());
        final ImageView chat = (ImageView) rootView.findViewById(R.id.iv_chat);
        final Button startTrip = (Button) rootView.findViewById(R.id.startTrip);
        final String tripId = sharedPrefs.getString("tripId","");
        if(tripId.equalsIgnoreCase("")){

        }else{
            startTrip.setEnabled(false);
            chat.setVisibility(View.VISIBLE );
        }
        startTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTrip.setEnabled(false);
                Util util = new Util();
                String tripID = util.getUniqueId();
                sharedPrefs.edit().putString("tripId",tripID).apply();

                ConfigPreference.getInstance().saveFloatValue(getContext(), ConfigPreference.DISTANCE, 0);

                HashMap data = new HashMap();
                data.put(Constant.ACTION,Constant.ACTION_PUBLISH_MSG);
                data.put("topic",Constant.START_TRIP);
                data.put("message", user + "/"+tripID+"/start trip ");
                mListener.sendData(data);

                GeofanceHandler geofanceHandler = new GeofanceHandler(getActivity().getApplicationContext());
                geofanceHandler.checkTrackingStatus();

                chat.setVisibility(View.VISIBLE);
            }
        });

        final TextView distance = (TextView) rootView.findViewById(R.id.distance);
        final Button endTrip = (Button) rootView.findViewById(R.id.endTrip);

        endTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTrip.setEnabled(true);
                distance.setText(String.valueOf(ConfigPreference.getInstance().getFloatValue(getContext(), ConfigPreference.DISTANCE)));
                ConfigPreference.getInstance().saveLongValue(getContext(), ConfigPreference.START_TIME, new Date().getTime());
                sharedPrefs.edit().putString("tripId","").apply();
                GeofanceHandler geofanceHandler = new GeofanceHandler( getActivity().getApplicationContext());
                geofanceHandler.endTrip();
                chat.setVisibility(View.GONE);


            }
        });
        final Button history =(Button) rootView.findViewById(R.id.history);
        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TripHistoryActivity.class);
                startActivity(intent);
            }
        });


        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentHandler fragmentHandler = FragmentHandler.getInstance();
                fragmentHandler.setCurrFragment(this);
                fragmentHandler.setScrNo(Constant.CHAT_FRAGMENT);
                fragmentHandler.setData(null);
                fragmentHandler.setIsBackFragmentMove(false);
                fragmentHandler.setIsNextFragmentMove(true);
                fragmentHandler.setTitle("Chat");
                mListener.onFragmentInteraction(fragmentHandler);

               // HomeActivity.serviceConnection.subscribeMsg(topic);
            }
        });

        return rootView;



    }

    @Override
    public void onStop() {
        super.onStop();
        try {

        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


    private void setCurrFragment() {
        FragmentHandler fragmentHandler = FragmentHandler.getInstance();
        fragmentHandler.setCurrFragment(this);
        fragmentHandler.setScrNo(Constant.MAP_FRAGMENT);
        fragmentHandler.setData(null);
        fragmentHandler.setIsBackFragmentMove(false);
        fragmentHandler.setIsNextFragmentMove(false);
        fragmentHandler.setTitle("Tracking");
        mListener.onFragmentInteraction(fragmentHandler);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.d("Camera postion change" + "", cameraPosition + "");
                mCenterLatLong = cameraPosition.target;


               // mMap.clear();

                try {

                    Location mLocation = new Location("");
                    mLocation.setLatitude(mCenterLatLong.latitude);
                    mLocation.setLongitude(mCenterLatLong.longitude);

                    startIntentService(mLocation);
                    mLocationMarkerText.setText("Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);
                    if(mParam2!=null){
                        int size = mParam2.size();
                        for(int i=0;i<size;i++){
                            TripView tripView = (TripView) mParam2.get(i);
                            drawmap(tripView.getLatitude(),tripView.getLongitude(), false,tripView.getUser());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }


//        // Add a marker in Sydney and move the camera
          ConfigPreference configPreference = ConfigPreference.getInstance();
          Double lat = Double.longBitsToDouble(configPreference.getLongValue(getContext(), ConfigPreference.LAT));
          Double longitude =  Double.longBitsToDouble(configPreference.getLongValue(getContext(), ConfigPreference.LONGITUDE));
          if(lat!=null) {
              LatLng sydney = new LatLng(lat, longitude);
              mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
              Location location = new Location("");
              location.setLatitude(lat);
              location.setLongitude(longitude);
              changeMap(location);
              /*ArrayList<GpsRequest.Payload> gpsRequests = new GeofanceHandler(getContext()).getGPSDataFromDB();
              if(gpsRequests!=null){
                  addMarker(gpsRequests);
              }*/
          }

    }

    private void addMarker(ArrayList<GpsRequest.Payload> gpsRequests){
        int length = gpsRequests.size();
        for(int i =0;i<length;i++){
            GpsRequest.Payload pl = gpsRequests.get(i);
            LatLng latLng = new LatLng(pl.latitude, pl.longitude);
            mMap.addMarker(new MarkerOptions().position(latLng).title(pl.user));
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(my));
            otherarrayList.add(latLng);
        }
        Polyline line = mMap.addPolyline(new PolylineOptions().addAll(otherarrayList)
                .width(5).color(Color.RED));

    }
    public void changeMap(Location location) {

        Log.d(TAG, "Reaching map" + mMap);


        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        // check if map is created successfully or not
        if (mMap != null) {
            mMap.getUiSettings().setZoomControlsEnabled(false);
            LatLng latLong;


            latLong = new LatLng(location.getLatitude(), location.getLongitude());

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLong).zoom(19f).tilt(70).build();

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

            mLocationMarkerText.setText("Lat : " + location.getLatitude() + "," + "Long : " + location.getLongitude());
            startIntentService(location);
            drawmap(location.getLatitude(),location.getLongitude(),true,"Me");


        } else {
            Toast.makeText(getContext(),
                    "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    /**
     * Updates the address in the UI.
     */
    protected void displayAddressOutput() {
        //  mLocationAddressTextView.setText(mAddressOutput);
        try {
            if (mAreaOutput != null)
                // mLocationText.setText(mAreaOutput+ "");

                mLocationAddress.setText(mAddressOutput);
            //mLocationText.setText(mAreaOutput);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(getContext(), FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
        getContext().startService(intent);
    }

    private void openAutocompleteActivity() {
        try {
            // The autocomplete activity requires Google Play Services to be available. The intent
            // builder checks this and throws an exception if it is not the case.
            Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                    .build(getActivity());
            startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE);
        } catch (GooglePlayServicesRepairableException e) {
            // Indicates that Google Play Services is either not installed or not up to date. Prompt
            // the user to correct the issue.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), e.getConnectionStatusCode(),
                    0 /* requestCode */).show();
        } catch (GooglePlayServicesNotAvailableException e) {
            // Indicates that Google Play Services is not available and the problem is not easily
            // resolvable.
            String message = "Google Play Services is not available: " +
                    GoogleApiAvailability.getInstance().getErrorString(e.errorCode);

            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //mMapView.onSaveInstanceState(outState);
    }

    /**
     * Called after the autocomplete activity has finished to return its result.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check that the result was from the autocomplete widget.
        if (requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            if (resultCode == getActivity().RESULT_OK) {
                // Get the user's selected place from the Intent.
                Place place = PlaceAutocomplete.getPlace(mContext, data);

                // TODO call location based filter


                LatLng latLong;


                latLong = place.getLatLng();

                mLocationText.setText(place.getName() + "");

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLong).zoom(19f).tilt(70).build();

                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mMap.setMyLocationEnabled(true);
                mMap.animateCamera(CameraUpdateFactory
                        .newCameraPosition(cameraPosition));


            }


        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
            Status status = PlaceAutocomplete.getStatus(mContext, data);
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            // Indicates that the activity closed before a selection was made. For example if
            // the user pressed the back button.
        }


    }

    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    class AddressResultReceiver extends ResultReceiver {
        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        /**
         * Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string or an error message sent from the intent service.
            mAddressOutput = resultData.getString(AppUtils.LocationConstants.RESULT_DATA_KEY);

            mAreaOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_AREA);

            mCityOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_CITY);
            mStateOutput = resultData.getString(AppUtils.LocationConstants.LOCATION_DATA_STREET);

            displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));


            }


        }

    }

    public void drawmap(double latid, double longid,boolean isSelf,String user) {
        // draw on map here
        // draw line from intial to final location and draw tracker location map
        if(pLati != null) {
            Log.i("Tag", "map");

            // add line b/w current and prev location.
            ConfigPreference configPreference = ConfigPreference.getInstance();

            LatLng prev = new LatLng(pLati, plongi);
            LatLng my = new LatLng(latid, longid);
            //arrayList.add(prev);
            if(isSelf) {
                arrayList.add(my);
                mMap.addMarker(new MarkerOptions().position(my).title(user));
                Polyline line = mMap.addPolyline(new PolylineOptions().addAll(arrayList)
                        .width(5).color(Color.BLUE));
            }else{

                mMap.addMarker(new MarkerOptions().position(my).title(user));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(my));
                otherarrayList.add(my);
                Polyline line = mMap.addPolyline(new PolylineOptions().addAll(otherarrayList)
                        .width(10).color(Color.RED));
            }
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(my, 15));
            //mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);


        }
        pLati = latid;
        plongi = longid;

    }
    public String getLocationData() {
        String locationStr = null;
        /*try {
            ConfigPreference configPreference = ConfigPreference.getInstance();
            JSONObject jsonObj = new JSONObject();
            if (Double.longBitsToDouble(configPreference.getLongValue(ctx,ConfigPreference.LAT)) != 1) {
                jsonObj.put("lat", Double.longBitsToDouble(configPreference.getLongValue(ctx,ConfigPreference.LAT)));
                jsonObj.put("long", Double.longBitsToDouble(configPreference.getLongValue(ctx, ConfigPreference.LONGITUDE)));
                jsonObj.put("accr", configPreference.getFloatValue(ctx, ConfigPreference.ACCURACY));
                jsonObj.put("src", "googleMap");
                locationStr = jsonObj.toString();
                return locationStr;
            }

        } catch (Exception e) {
            //Logger.getInstance().error(e);
        }*/
        return locationStr;
    }




}
