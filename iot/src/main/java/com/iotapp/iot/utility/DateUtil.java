package com.iotapp.iot.utility;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by kundankumar on 26/09/16.
 */
public class DateUtil {

    public static String FormatDate(String paramString) {
        SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
                "dd MMM yyyy HH:mm:ss a");
        Date localDate2 = null;
        try {
            localDate2 = localSimpleDateFormat.parse(paramString);

        } catch (Exception localException) {
            localException.printStackTrace();


        }
        return new SimpleDateFormat("dd/MM/yyyy HH:mm a").format(localDate2);
    }


    public static String getTime(Long date) {
        SimpleDateFormat df2 = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return df2.format(date);
    }

    public static String getDate(long milliSeconds, String dateFormat) {

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getDateTimeForMfst(long date) {
        String dateStr = null;
        try {
            SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            dateStr = df2.format(date);
        } catch (Exception e) {
            Log.e("getDateTimeForMfst", e.getMessage());
        }
        return dateStr;
    }

    public static String getDateTime(String datetime) {
        SimpleDateFormat df2 = new SimpleDateFormat("MMM d yyyy  hh:mm a", Locale.getDefault());
        long date = Long.parseLong(datetime);
        return df2.format(date);

    }

    public static String getDateTimeForLog(long date) {
        String dateStr = null;
        try {
            SimpleDateFormat df2 = new SimpleDateFormat("dd-MM-yy hh:mm a", Locale.getDefault());
            dateStr = df2.format(date);
        } catch (Exception e) {
            Log.e("getDateTimeForLog", e.getMessage());

        }
        return dateStr;
    }

    public static String getDateTime(Long date) {
        SimpleDateFormat df2 = new SimpleDateFormat("MMM d yyyy  hh:mm a", Locale.getDefault());
        return df2.format(date);

    }

    public static String getDate(Long date) {
        SimpleDateFormat df2 = new SimpleDateFormat("MMM d yyyy", Locale.getDefault());
        //Logger.getInstance().info("datetime:"+df2.format(date));
        return df2.format(date);

    }

    public static long addTimeToCalendar(int hours, int minutes){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        return calendar.getTime().getTime();
    }

}
