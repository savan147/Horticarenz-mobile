package com.chrisoft.horticare;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.chrisoft.horticare.callback.RecipeCallBack;
import com.chrisoft.horticare.model.Recipe;
import com.chrisoft.horticare.model.User;
import com.chrisoft.horticare.util.AppUtil;
import com.chrisoft.horticare.util.CharacterDrawable;
import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A customized dialog view used to display recipe information and allowed user to update details of the recipe
 * Created by Chris on 13/08/2015.
 */
public class RecipeEdit extends Dialog {

    ImageView img;
    ProgressBar pb;
    EditText txtName;
    TextView txtDesc;
    TextView txtIng;
    TextView txtSteps;
    Spinner cuisines;
    Spinner categories;
    String recipeId;
    ImageButton btnLoadImage;
    ImageButton btnSave;

    RecipeCallBack callback;
    String[] catIDs;
    Recipe recipe;
    MainActivity mainActivity;

    public RecipeEdit(Activity context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    public void setUp(MainActivity mainActivity, Object data) {
        recipe = (Recipe) data;
        setContentView(R.layout.editrecipe);
        setTitle(recipe.getName());
        setCancelable(true);

        recipeId = recipe.getId();

        //set up text
        txtName = (EditText) this.findViewById(R.id.txtName);
        txtName.setText(recipe.getName());

        //set up text
        txtDesc = (TextView) this.findViewById(R.id.txtDesc);
        txtDesc.setText(recipe.getDescription());

        //set up text
        txtIng = (TextView) this.findViewById(R.id.txtIngredients);
        txtIng.setText(recipe.getIngredients());

        //set up text
        txtSteps = (TextView) this.findViewById(R.id.txtSteps);
        txtSteps.setText(recipe.getProcedure());

        callback = (RecipeCallBack)mainActivity;
        this.mainActivity=mainActivity;

        //imageFile=null;

        btnLoadImage = (ImageButton)this.findViewById(R.id.btnUpload);
        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.loadImageFromGallery(img);
            }
        });

        btnSave = (ImageButton)this.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        ImageView btnDesc = (ImageView)this.findViewById(R.id.btDesc);
        ImageView btnIng = (ImageView)this.findViewById(R.id.btIng);
        ImageView btnSteps = (ImageView)this.findViewById(R.id.btSteps);

        btnDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(txtDesc, 250, "Recipe Description");
            }
        });

        btnIng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(txtIng, 1500, "Ingredients");
            }
        });

        btnSteps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(txtSteps, 1500, "Procedure");
            }
        });

        //set up image view
        img = (ImageView) this.findViewById(R.id.ImageView01);

        pb = (ProgressBar)this.findViewById(R.id.progressBar);

        cuisines = (Spinner)this.findViewById(R.id.cuisines);
        categories = (Spinner)this.findViewById(R.id.categories);

        txtName.requestFocus();


        catIDs = mainActivity.getResources().getStringArray(R.array.cuisines_id);

        System.out.println("recipe.getCuisine()=" + recipe.getCuisine() + " :::: recipe.getCategory() = " + recipe.getCategory());
        cuisines.setSelection(Integer.parseInt(recipe.getCuisine()) - 1);
        categories.setSelection(Integer.parseInt(recipe.getCategory())-1);

        try {
            if (MainActivity.checkNetwork(getContext())) {
                BitmapAsyncTask task = new BitmapAsyncTask(recipe.getId());
                task.execute();
            } else {
                BitmapOfflineAsyncTask task = new BitmapOfflineAsyncTask(recipe.getId());
                task.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void reset() {
        txtName.setText("");
        txtDesc.setText("");
        txtIng.setText("");
        txtSteps.setText("");
    }

    public void save() {
        if (AppUtil.isEmpty(txtName.getText().toString())) {
            AppUtil.alertMessage(getContext(),"Please enter valid recipe name.");
            txtName.requestFocus();
        } else if (AppUtil.isEmpty(txtDesc.getText().toString())) {
            AppUtil.alertMessage(getContext(), "Please enter recipe description.");
            txtDesc.requestFocus();
        } else if (AppUtil.isEmpty(txtIng.getText().toString())) {
            AppUtil.alertMessage(getContext(), "Please enter ingredient/s.");
            txtIng.requestFocus();
        } else if (AppUtil.isEmpty(txtSteps.getText().toString())) {
            AppUtil.alertMessage(getContext(), "Please enter preparation steps.");
            txtSteps.requestFocus();
        } else {
            try {
                if (MainActivity.checkNetwork(getContext())) {
                    SaveAsyncTask task = new SaveAsyncTask();
                    task.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showInputDialog(TextView target, int length, String title) {
        InputDialog dialog = new InputDialog(getContext());
        dialog.setUp(target, length, title);
        dialog.setContents(target.getText().toString());
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);

    }

    private class BitmapAsyncTask extends AsyncTask {

        private String id;

        public BitmapAsyncTask(String id) {
            this.id=id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute(); pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                return HttpUtil.getBitmapFromURL(Constants.URL_RECIPE_IMAGES + File.separator + this.id + ".jpg");
            } catch (Exception e) {
                return null;
            }
        }

        private void hideProgress() {
            if (pb.getVisibility()==ProgressBar.VISIBLE) {
                pb.setVisibility(ProgressBar.GONE);
            }
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);

            if (result != null) {
                img.setImageBitmap((Bitmap) result);
                try {
                    BitmapSaveAsyncTask task = new BitmapSaveAsyncTask(this.id,(Bitmap)result);
                    task.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getContext(), "Unable to load image.",Toast.LENGTH_SHORT).show();
            }

            hideProgress();
        }

        protected void onProgressUpdate(Integer... progress) {
        }
    }

    private class BitmapOfflineAsyncTask extends AsyncTask {

        private String id;

        public BitmapOfflineAsyncTask(String id) {
            this.id=id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute(); pb.setVisibility(ProgressBar.VISIBLE);
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                return DataManager.getCachedImage(getContext(), "RECIPE_" + this.id);
            } catch (Exception e) {
                return null;
            }
        }

        private void hideProgress() {
            if (pb.getVisibility()==ProgressBar.VISIBLE) {
                pb.setVisibility(ProgressBar.GONE);
            }
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result!=null) {
                img.setImageBitmap((Bitmap) result);
            } else {
                Toast.makeText(getContext(), "Unable to load image.", Toast.LENGTH_SHORT).show();
            }
            hideProgress();
        }


    }

    private class BitmapSaveAsyncTask extends AsyncTask {

        private Bitmap image;
        private String id;

        public BitmapSaveAsyncTask(String id, Bitmap image) {
            this.image=image;
            this.id=id;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                DataManager.saveImageCache(getContext(),"RECIPE_" + this.id,this.image);
                return true;
            } catch (Exception e) {
                return false;
            }
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            boolean b = (boolean)result;
            if (b==true) {
                //Toast.makeText(getContext(), "Image saved offline to " + getContext().getCacheDir(),Toast.LENGTH_SHORT).show();
            }
        }

        protected void onProgressUpdate(Integer... progress) {
        }
    }

    private class UploadImageAsyncTask extends AsyncTask {

        private String recipeId;

        public UploadImageAsyncTask(String recipeId) {
            this.recipeId=recipeId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                //return HttpUtil.uploadFile(recipeId, AppUtil.getBitmapFromImageView(imgUpload));
                return HttpUtil.uploadFile(recipeId, callback.getSelectedBitmap());
            } catch (Exception e) {
                return false;
            }
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            boolean b = (boolean)result;
            if (b==true) {
                Toast.makeText(getContext(), "Successfully uploaded.",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Upload failed.",Toast.LENGTH_SHORT).show();
            }
        }

        protected void onProgressUpdate(Integer... progress) {
        }
    }

    private class SaveAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getPd().show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            return postData();
        }

        protected void onPostExecute(Object result){
            super.onPostExecute(result);

            if (result!=null) {
                try {
                    String code=(String)result;
                    if (code.equalsIgnoreCase("OK")) {
                        if (callback.getImageFileName()!=null && MainActivity.checkNetwork(getContext())) {
                            UploadImageAsyncTask task = new UploadImageAsyncTask(recipeId);
                            task.execute();
                        }
                        //AppUtil.alertMessage(getContext(),"Recipe updated successfully.");

                        //Refresh the recipe in cache
                        RefreshAsyncTask task2 = new RefreshAsyncTask();
                        task2.execute();

                        mainActivity.onNavigationDrawerItemSelected(0);

                    } else {
                        AppUtil.alertMessage(getContext(), "Unable to update the recipe information!");
                    }
                } catch (Exception e) {
                    AppUtil.alertMessage(getContext(), "Unable to update the recipe information!");
                    e.printStackTrace();
                }
            } else {
                AppUtil.alertMessage(getContext(), "Unable to update the recipe information!");
            }
            if (getPd()!=null) getPd().dismiss();
        }

        /**
         *$recipename = (string) $_POST["recipename"];
         $description = (string) $_POST["description"];
         $catid = (string) $_POST["catid"];
         $catid = (string) $_POST["cuisineid"];
         $ingredients = (string) $_POST["ingredients"];
         $procedure = (string) $_POST["procedure"];
         * @return
         */
        public String postData() {
            //Post Data
            List<NameValuePair> postParams = new ArrayList<NameValuePair>(1);
            postParams.add(new BasicNameValuePair("option", "updaterecipe"));
            postParams.add(new BasicNameValuePair("recipename", txtName.getText().toString()));
            postParams.add(new BasicNameValuePair("description", txtDesc.getText().toString()));
            postParams.add(new BasicNameValuePair("catid", catIDs[categories.getSelectedItemPosition()]));
            postParams.add(new BasicNameValuePair("cuisineid", String.valueOf(cuisines.getSelectedItemPosition()+1)));
            postParams.add(new BasicNameValuePair("ingredients", txtIng.getText().toString()));
            postParams.add(new BasicNameValuePair("procedure", txtSteps.getText().toString()));
            postParams.add(new BasicNameValuePair("recipeid", recipeId));

            try {

                // Execute HTTP Post Request
                String response = HttpUtil.getJSONData(Constants.URL_FUNCTIONS, postParams, Constants.TIMEOUT_CONN, Constants.TIMEOUT_SOCKET);

                if(response != null)
                {
                    //Log.d("Http Post Response:", response);
                    return response;
                }
            }catch (Exception e) {
                Log.e("RECIPEBOOK", "update recipe", e);
            }
            return null;
        }

    }

    ProgressDialog pd;
    public ProgressDialog getPd() {
        if (pd==null) {
            pd = new ProgressDialog(getContext());
            pd.setMessage("Processing...");
        }
        return pd;
    }

    private class RefreshAsyncTask extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            return postData();
        }

        protected void onPostExecute(Object result){
            super.onPostExecute(result);
        }

        public JSONArray postData() {
            //Post Data
            List<NameValuePair> postParams = new ArrayList<NameValuePair>(1);
            postParams.add(new BasicNameValuePair("option", "recipes"));

            JSONArray result=null;
            try {

                // Execute HTTP Post Request
                String response = HttpUtil.getJSONData(Constants.URL_FUNCTIONS, postParams, Constants.TIMEOUT_CONN , Constants.TIMEOUT_SOCKET);

                if(response != null)
                {
                    result = new JSONArray(response);
                    boolean cached = DataManager.saveToCache(getContext(),Constants.CACHE_RECIPES_FILENAME, response);
                    return result;
                }
            }catch (Exception e) {
                Log.e("RECIPEBOOK", "Refresh Recipe List", e);
                //e.printStackTrace();
            }
            return null;
        }

    }

}
