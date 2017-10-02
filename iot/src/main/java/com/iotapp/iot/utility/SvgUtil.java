package com.iotapp.iot.utility;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

/**
 * Created by kundankumar on 20/07/16.
 */
public class SvgUtil {
    Context ctx;
    public SvgUtil(Context context){
        this.ctx = context;
    }
    public void setIcon(int resourceId,ImageView imageView,Float opactiy){
        SVG svg = SVGParser.getSVGFromResource(ctx.getResources(), resourceId);
        Drawable drawable = svg.createPictureDrawable();
        imageView.setImageDrawable(drawable);
        imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if(opactiy!=null) {
            imageView.setAlpha(opactiy);
        }
    }

}
