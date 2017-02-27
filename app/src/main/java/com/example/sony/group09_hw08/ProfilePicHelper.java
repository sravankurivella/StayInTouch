package com.example.sony.group09_hw08;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Sony on 4/19/2016.
 */
public class ProfilePicHelper {
    public static String convertImageToBase64(Bitmap bmp) {
        bmp = BitmapFactory.decodeFile("/path/to/image.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bmp is the bitmap object
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }
}
