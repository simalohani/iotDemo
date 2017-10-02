package com.iotapp.iot.utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Messenger;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import com.amulyakhare.textdrawable.TextDrawable;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.iotapp.iot.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class Util {
	private static Util utility;
    private String imeiNo;
    private static Handler handler;
    private Messenger service;
	public Messenger getService() {
		return service;
	}

	public Activity getContext() {
		return context;
	}

	public void setContext(Activity context) {
		this.context = context;
	}

	private Activity context;


	public void setService(Messenger service) {
		this.service = service;
	}

	public String getImeiNo(Context ctx) {
		 TelephonyManager telephonyManager = (TelephonyManager)ctx.getSystemService(Context.TELEPHONY_SERVICE);
		 imeiNo = telephonyManager.getDeviceId();
		 return imeiNo;
	}

	public static Util getInstance() {
		if (utility == null)
			utility = new Util();
		return utility;
	}
	public  Handler getHandler() {
		return handler;
	}
	public static void setHandler(Handler handler) {
		Util.handler = handler;
	}
	public TextDrawable getTextDrawable(String text, Context ctx) {

		int randomColor = ctx.getResources().getColor(R.color.cust_circular);
		TextDrawable drawable = TextDrawable.builder()
				.beginConfig()
				.textColor(R.color.ic_font_color)
				.useFont(Typeface.DEFAULT)
				.fontSize(30) /* size in px */
				.bold()
				.toUpperCase()
				.width(120)  // width in px
				.height(120)
				.endConfig()
				.buildRound(text, randomColor);
		return drawable;
	}
	public String getDate(Long date) {
		SimpleDateFormat df2 = new SimpleDateFormat("MMM d yyyy");
		//Logger.getInstance().info("datetime:"+df2.format(date));
		return df2.format(date);

	}
	public String getDate(long milliSeconds, String dateFormat) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	public String getUniqueId() {
		UUID uniqueId = UUID.randomUUID();
		return uniqueId.toString();
	}
	public boolean checkIfGooglePlayEnabled(Context ctx) {
		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(ctx) == ConnectionResult.SUCCESS) {
			return true;
		} else {
			Toast.makeText(ctx, R.string.google_play_services_unavailable, Toast.LENGTH_LONG).show();
			//Logger.getInstance().error("CheckIfGooglePlayEnabled->"+" google_play_services_unavailable");
			return false;
		}
	}
}
