package com.chrisoft.horticare.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.chrisoft.horticare.model.Recipe;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dahye on 14/08/2015.
 */
public class HttpUtil {

    /**
     * Generic method to read JSON data from WEB Service
     * @param url
     * @param parameters
     * @param conn_timeout
     * @param so_timeout
     * @return JSON String
     */
    public static String getJSONData(String url, List<NameValuePair> parameters, int conn_timeout, int so_timeout) {

        // Create a new HttpClient and Post Header
        HttpClient httpClient = null;
        // replace with your url
        HttpPost httpPost = null;

        try {
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, conn_timeout);
            HttpConnectionParams.setSoTimeout(params, so_timeout);
            httpClient = new DefaultHttpClient(params);

            httpPost = new HttpPost(Constants.URL_FUNCTIONS);

            httpPost.setEntity(new UrlEncodedFormEntity(parameters));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httpPost);

            if(response != null)
            {
                //Read
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                return sb.toString();
            }
        }catch (Exception e) {
            Log.e("HORTICARE", "HttpUtil.getJSONData", e);
        } finally {
            httpPost=null;
            httpClient=null;
        }
        return null;
    }

    /**
     * Generic method used to read bitmap from WEBSITE
     * @param bitmapURL
     * @return Bitmap
     * @throws Exception
     */
    public static Bitmap getBitmapFromURL(String bitmapURL) throws Exception{
        URL url = new URL(bitmapURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(Constants.TIMEOUT_CONN);
        connection.setReadTimeout(Constants.TIMEOUT_SOCKET);
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        Bitmap myBitmap = BitmapFactory.decodeStream(input);
        return myBitmap;
    }

    /**
     * Generic method used to read bitmap from WEBSITE
     * @param api
     * @return Bitmap
     * @throws Exception
     */
    public static String GetData(String api) throws Exception{
        URL url = new URL(api);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(Constants.TIMEOUT_CONN);
        connection.setReadTimeout(Constants.TIMEOUT_SOCKET);
        connection.setDoInput(true);
        connection.connect();
        InputStream input = connection.getInputStream();
        if(input != null)
        {
            //Read
            BufferedReader br = new BufferedReader(new InputStreamReader(input,"UTF-8"));

            String line = null;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            br.close();
            return sb.toString();
        }
        return null;
    }

    /**
     * Function used to upload bitmap to web server
     * @param recipeId
     * @param image
     * @return true/false
     */
    public static boolean uploadFile(String recipeId, Bitmap image) {
        // Create a new HttpClient and Post Header
        HttpClient httpClient = null;
        // replace with your url
        HttpPost httpPost = null;

        try {
            HttpParams params = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(params, 30000);
            HttpConnectionParams.setSoTimeout(params, 30000);
            httpClient = new DefaultHttpClient(params);

            httpPost = new HttpPost(Constants.URL_UPLOAD);

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, stream); //compress to which format you want.
            byte [] byte_arr = stream.toByteArray();
            String image_str = AppUtil.encodeTobase64(byte_arr);

            ArrayList<NameValuePair> parameters = new  ArrayList<NameValuePair>();
            parameters.add(new BasicNameValuePair("image",image_str));
            parameters.add(new BasicNameValuePair("recipeId",recipeId));
            httpPost.setEntity(new UrlEncodedFormEntity(parameters));

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null && response.getEntity()!=null)
            {
                //Read
                BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(),"UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                br.close();
                if (sb.toString().equalsIgnoreCase("success")) return true;
            }
        }catch (Exception e) {
           e.printStackTrace();
        } finally {
            httpPost=null;
            httpClient=null;
        }
        return false;
    }


    /**
     * Functio used to extract path from media store
     * @param context
     * @param contentUri
     * @return path
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
