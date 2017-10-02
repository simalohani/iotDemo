package com.iotapp.iot.custom;

import android.net.Uri;
import com.iotapp.iot.modal.view.FragmentHandler;

/**
 * Created by kundankumar on 26/09/16.
 */
public interface FragmentInteractionListener {
    public void onFragmentInteraction(FragmentHandler fragmentHandler);
    public void sendData(Object object);
}
