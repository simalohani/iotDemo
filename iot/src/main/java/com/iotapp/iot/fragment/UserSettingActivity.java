package com.iotapp.iot.fragment;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.iotapp.iot.R;

public class UserSettingActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);

	}
}
