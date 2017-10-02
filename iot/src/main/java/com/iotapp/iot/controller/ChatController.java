package com.iotapp.iot.controller;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import java.io.*;

/**
 * Created by kundankumar on 26/09/16.
 */
public class ChatController {

    public String getImg(String img) {
        InputStream inputStream;
        String encodedString = "";
        try {
            inputStream = new FileInputStream(img);
            byte[] bytes;
            byte[] buffer = new byte[8192];
            int bytesRead;
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            try {
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            bytes = output.toByteArray();
            encodedString = Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }

        return encodedString;
    }

    public static Bitmap decodeToImage(String imageString) {
        byte[] bytarray = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap bmimage = BitmapFactory.decodeByteArray(bytarray, 0,
                bytarray.length);
        return bmimage;

    }

    public void setPic(String imagePath, ImageView destination) {
        int targetW = 530;// destination.getWidth();
        int targetH = 400;// destination.getHeight();
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        byte[] bytarray = Base64.decode(imagePath, Base64.DEFAULT);
        BitmapFactory.decodeByteArray(bytarray, 0, bytarray.length, bmOptions);

        // BitmapFactory.decodeFile(imagePath,bmOptions);
        // BitmapFactory.de
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytarray, 0,
                bytarray.length, bmOptions);
        destination.setImageBitmap(bitmap);
    }
}
