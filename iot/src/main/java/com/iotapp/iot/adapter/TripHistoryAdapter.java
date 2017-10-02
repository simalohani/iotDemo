package com.iotapp.iot.adapter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.iotapp.iot.R;
import com.iotapp.iot.fragment.MapFragment;
import com.iotapp.iot.gps.AppUtils;
import com.iotapp.iot.gps.FetchAddressIntentService;
import com.iotapp.iot.modal.view.TripView;
import com.iotapp.iot.utility.DateUtil;
import com.iotapp.iot.utility.FontUtil;
import com.iotapp.iot.utility.TextUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by skumari on 6/10/2017.
 */

public class TripHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_ITEM =1;
    public Context context;
    LinkedHashMap  tripList = new LinkedHashMap();

    protected String mAddressOutput;
    protected String mAreaOutput;
    protected String mCityOutput;
    protected String mStateOutput;
    protected  String tripId;
    protected  double lat;

    public TripHistoryAdapter(Context ctx, LinkedHashMap trips){
        if(null != trips){
            this.tripList = trips;
        }
        this.context = ctx;
        mResultReceiver = new AddressResultReceiver(new Handler());
    }

    public Object getItem(int position){
        return tripList.get( (tripList.keySet().toArray())[ position ] );

    }
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //if(viewType == TYPE_ITEM){
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip_history,parent,false);
            return new ViewHolder(v);
        //}
        //return null;
    }


    public static int getTypeItem() {
        return TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
         ArrayList<TripView> list = (ArrayList) getItem(position);
          TripView strtLoc = list.get(0);
          TripView endLoc =  list.get(list.size()-1);

          String user = TextUtil.toFirstLtrUprCase(strtLoc.getUser());
          String strtLocTime = DateUtil.getDateTimeForLog(strtLoc.getTime());
          String endLocTime =  DateUtil.getDateTimeForLog(endLoc.getTime());

          ViewHolder viewHolder = (ViewHolder)holder;
          Log.e("id",user);
          viewHolder.tvUser.setText(user);
          // viewHolder.tvTripLoc.setText(trip.getTripId());
          viewHolder.tvStrtTime.setText(strtLocTime);
        viewHolder.tvEndTime.setText(endLocTime);
        if(strtLoc.getAddress()==null || strtLoc.getAddress().equalsIgnoreCase("")) {
            Location mLocation = new Location("");
            mLocation.setLatitude(strtLoc.getLatitude());
            mLocation.setLongitude(strtLoc.getLongitude());
            startIntentService(mLocation, strtLoc.getTripId());
        }else{
            viewHolder.tvStrtLoc.setText(strtLoc.getAddress());
        }
        if(endLoc.getAddress()== null || endLoc.getAddress().equalsIgnoreCase("")) {
            Location endLocation = new Location("");
            endLocation.setLatitude(endLoc.getLatitude());
            endLocation.setLongitude(endLoc.getLongitude());
            startIntentService(endLocation, endLoc.getTripId());
        }else{
            viewHolder.tvEndLoc.setText(endLoc.getAddress());
        }
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvStrtTime;
        TextView tvEndTime;
        TextView tvStrtLoc;
        TextView tvEndLoc;
        TextView tvUser;
        TextView tvDistance;




        public ViewHolder(View itemView) {
            super(itemView);
            this.tvStrtTime =(TextView )itemView.findViewById(R.id.tv_strt_time);
            this.tvEndTime =(TextView) itemView.findViewById( R.id.tv_end_time);
            this.tvStrtLoc =(TextView) itemView.findViewById( R.id.tv_strt_loc);
            this.tvEndLoc =(TextView) itemView.findViewById( R.id.tv_end_loc);
            this.tvUser = (TextView) itemView.findViewById(R.id.tv_user);
            this.tvDistance  = (TextView) itemView.findViewById(R.id.tv_distance) ;

            tvStrtTime.setTypeface(FontUtil.getInstance().getFont(FontUtil.BOLD));
            tvEndTime.setTypeface(FontUtil.getInstance().getFont(FontUtil.BOLD));
            tvStrtLoc.setTypeface(FontUtil.getInstance().getFont(FontUtil.REGULAR));
            tvEndLoc.setTypeface(FontUtil.getInstance().getFont(FontUtil.REGULAR));
            tvUser.setTypeface(FontUtil.getInstance().getFont(FontUtil.REGULAR));
            tvDistance.setTypeface(FontUtil.getInstance().getFont(FontUtil.BOLD));
        }
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    protected void startIntentService(Location mLocation,String tripId) {
        // Create an intent for passing to the intent service responsible for fetching the address.
        Intent intent = new Intent(context, FetchAddressIntentService.class);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.RECEIVER, mResultReceiver);

        // Pass the location data as an extra to the service.
        intent.putExtra(AppUtils.LocationConstants.LOCATION_DATA_EXTRA, mLocation);


        intent.putExtra(AppUtils.LocationConstants.TRIP_ID, tripId);
        intent.putExtra(AppUtils.LocationConstants.TRIP_LAT, mLocation.getLatitude());

        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.
       context.startService(intent);
    }

    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

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
            tripId = resultData.getString(AppUtils.LocationConstants.TRIP_ID);
            lat = resultData.getDouble(AppUtils.LocationConstants.TRIP_LAT);
            ArrayList list = (ArrayList)tripList.get(tripId);
            TripView startTrip = (TripView)list.get(0);
            TripView endTrip = (TripView)list.get(list.size()-1);
            if(lat == startTrip.getLatitude()) {
                startTrip.setAddress(mAreaOutput + "\n" + mCityOutput);
            }else{
                endTrip.setAddress(mAreaOutput + "\n" + mCityOutput);
            }
            updateUI();
            //displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == AppUtils.LocationConstants.SUCCESS_RESULT) {
                //  showToast(getString(R.string.address_found));


            }


        }

    }

    private void updateUI(){
        this.notifyDataSetChanged();
    }
}
