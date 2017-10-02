package com.iotapp.iot.custom;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.EditTextPreference;
import android.util.AttributeSet;

public class CustomEditTextPreference extends EditTextPreference implements OnSharedPreferenceChangeListener {
    String user;
    Context ctx;

    public CustomEditTextPreference(Context context) {
        super(context);
        ctx = context;
    }

    public CustomEditTextPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        ctx = context;
    }

    public CustomEditTextPreference(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        ctx = context;
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == DialogInterface.BUTTON_POSITIVE) {
           /* MainActivity userReg = new MainActivity();
            user = getEditText().getText().toString();
            if (user != null && !(Utility.getInstance().getImeiNo(ctx).equalsIgnoreCase("865072027426751"))) {
                userReg.publisgMsg(Constant.signup, user + Utility.getInstance().getImeiNo(ctx));

            }*/

        }
        super.onClick(dialog, which);

    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        user = key;
        System.out.println("string key=" + user);

    }


}
