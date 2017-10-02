package com.iotapp.iot.utility;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by kundankumar on 30/03/16.
 */
public class FontUtil {
    private Typeface medium,regular,light,bold;
    public static final int MEDIUM = 0;
    public static final int REGULAR = 1;
    public static final int LIGHT = 2;
    public static final int BOLD = 3;
    static FontUtil fontUtil;
    public static FontUtil getInstance(){
        if(fontUtil == null) {
            fontUtil = new FontUtil();
        }
        return fontUtil;
    }
    private FontUtil(){

    }
    public void setFont(Context ctx) {
        regular = Typeface.createFromAsset(ctx.getAssets(), "fonts/ubuntu/Ubuntu-R.ttf");
        bold = Typeface.createFromAsset(ctx.getAssets(), "fonts/ubuntu/Ubuntu-B.ttf");
        light = Typeface.createFromAsset(ctx.getAssets(), "fonts/ubuntu/Ubuntu-L.ttf");
        medium = Typeface.createFromAsset(ctx.getAssets(), "fonts/ubuntu/Ubuntu-M.ttf");
    }
   public Typeface getFont(int fontType){
       Typeface typeface =null;
       switch (fontType){
           case MEDIUM:
               typeface = medium;
               break;
           case REGULAR:
               typeface =regular;
               break;
           case LIGHT:
               typeface =light;
               break;
           case BOLD:
               typeface = bold;
               break;
       }
       return typeface;
   }

}
