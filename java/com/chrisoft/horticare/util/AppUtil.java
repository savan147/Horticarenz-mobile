package com.chrisoft.horticare.util;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Common class used to store generic functions
 * Created by Lester on 18/08/2015.
 */
public class AppUtil {

    /**
     * Validate email format
     * @param target
     * @return true/false
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    /**
     * Check if string is empty or not
     * @param txt
     * @return true/false
     */
    public final static boolean isEmpty(String txt) {
        if (txt != null && txt.trim().length() > 0)
            return false;
        else return true;
    }

    /**
     * Function used to display dialog message
     * @param context
     * @param message
     */
    public static void alertMessage(Context context, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set title
        alertDialogBuilder.setTitle("HortiCare");

        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(true)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    /**
     * Function used to encode string to base 64; used in uploading image
     * @param bitmap
     * @return encoded string
     */
    public static String encodeTobase64(Bitmap image) {
        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        //Log.d("ENCODED_BASE64", imageEncoded);
        return imageEncoded;
    }


    /**
     * Function used to encode string to base 64; used in uploading image
     * @param byte[]
     * @return encoded string
     */
    public static String encodeTobase64(byte[] bytes) {
        byte[] b = bytes;
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        return imageEncoded;
    }

    /**
     * Used to decode base64 string
     * @param input
     * @return Bitmap
     */
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        //Log.d("DECODED_BASE64", new String(decodedByte));
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    /**
     * Read bitmap from the ImageView
     * @param photo
     * @return Bitmap
     */
    public static Bitmap getBitmapFromImageView(ImageView photo) {
        try {
            BitmapDrawable drawable = (BitmapDrawable) photo.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            bitmap = Bitmap.createScaledBitmap(bitmap, 70, 70, true);
            return bitmap;
        } catch (Exception e) { }
        return  null;
    }

    public static String toDateTime(long timeinmillis) {
        return new Date(timeinmillis).toString();
    }
}
