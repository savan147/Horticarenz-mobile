package com.chrisoft.horticare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisoft.horticare.callback.RecipeCallBack;
import com.chrisoft.horticare.model.Recipe;
import com.chrisoft.horticare.util.AppUtil;
import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used to display/manage add recipe screen
 * Created by Chris on 19/08/2015.
 */
public class RecipeAddFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";

    ImageView img; //Handle image of the recipe
    ProgressBar pb; //Progress bar used when save button is clicked
    EditText txtName; //Hold recipe name component
    TextView txtDesc; //Hold recipe decripton component
    TextView txtIng; //Hold recipe ingredients component
    TextView txtSteps; //Hold recipe procedure component
    Spinner cuisines; //Hold recipe cuisines component
    Spinner categories; //Hold recipe categories component
    String recipeId; //Hold recipe recipe id value
    ImageButton btnLoadImage; //Button for upload image
    ImageButton btnSave; //Button for saving recipe info

    RecipeCallBack callback; //Callback interface implemented by MainActivity
    String[] catIDs; //Store category ids
    MainActivity mainActivity; //Hold reference from main activity

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity act = ((MainActivity) activity);
        if (getArguments()!=null) {
            act.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
            act.setFilterValue(null);
        }
        this.mainActivity=act;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.editrecipe, container, false);
        //set up text
        txtName = (EditText) view.findViewById(R.id.txtName);

        //set up text
        txtDesc = (TextView) view.findViewById(R.id.txtDesc);

        //set up text
        txtIng = (TextView) view.findViewById(R.id.txtIngredients);

        //set up text
        txtSteps = (TextView) view.findViewById(R.id.txtSteps);

        callback = (RecipeCallBack)mainActivity;

        //set up image view
        img = (ImageView) view.findViewById(R.id.ImageView01);

        btnLoadImage = (ImageButton)view.findViewById(R.id.btnUpload);
        btnLoadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.loadImageFromGallery(img);
            }
        });

        btnSave = (ImageButton)view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        txtName.requestFocus();

        ImageView btnDesc = (ImageView)view.findViewById(R.id.btDesc);
        ImageView btnIng = (ImageView)view.findViewById(R.id.btIng);
        ImageView btnSteps = (ImageView)view.findViewById(R.id.btSteps);

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
        img = (ImageView) view.findViewById(R.id.ImageView01);

        pb = (ProgressBar)view.findViewById(R.id.progressBar);

        cuisines = (Spinner)view.findViewById(R.id.cuisines);
        categories = (Spinner)view.findViewById(R.id.categories);

        txtName.requestFocus();

        catIDs = mainActivity.getResources().getStringArray(R.array.cuisines_id);

        return view;
    }

    /**
     * Function called when save button is clicked
     */
    public void save() {
        if (AppUtil.isEmpty(txtName.getText().toString())) {
            AppUtil.alertMessage(mainActivity,"Please enter valid recipe name.");
            txtName.requestFocus();
        } else if (AppUtil.isEmpty(txtDesc.getText().toString())) {
            AppUtil.alertMessage(mainActivity, "Please enter recipe description.");
            txtDesc.requestFocus();
        } else if (AppUtil.isEmpty(txtIng.getText().toString())) {
            AppUtil.alertMessage(mainActivity, "Please enter ingredient/s.");
            txtIng.requestFocus();
        } else if (AppUtil.isEmpty(txtSteps.getText().toString())) {
            AppUtil.alertMessage(mainActivity, "Please enter preparation steps.");
            txtSteps.requestFocus();
        } else {
            try {
                if (MainActivity.checkNetwork(mainActivity)) {
                    SaveAsyncTask task = new SaveAsyncTask();
                    task.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Called when editing a text view
     * @param target
     * @param length
     * @param title
     */
    private void showInputDialog(TextView target, int length, String title) {
        InputDialog dialog = new InputDialog(mainActivity);
        dialog.setUp(target, length, title);
        dialog.setContents(target.getText().toString());
        dialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * Task used to load image from WEB
     */
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
                Toast.makeText(mainActivity, "Unable to load image.", Toast.LENGTH_SHORT).show();
            }

            hideProgress();
        }

        protected void onProgressUpdate(Integer... progress) {
        }
    }

    /**
     * Task used to load image from cache
     */
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
                return DataManager.getCachedImage(mainActivity, "RECIPE_" + this.id);
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
                Toast.makeText(mainActivity, "Unable to load image.", Toast.LENGTH_SHORT).show();
            }
            hideProgress();
        }


    }

    /**
     * Task used to save changes in the WEB database
     */
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
                DataManager.saveImageCache(mainActivity,"RECIPE_" + this.id,this.image);
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

    /**
     * Task used to upload image to WEB
     */
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
                Toast.makeText(mainActivity, "Successfully uploaded.",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mainActivity, "Upload failed.",Toast.LENGTH_SHORT).show();
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
                    if (code.startsWith("OK")) {
                        if (callback.getImageFileName()!=null && MainActivity.checkNetwork(mainActivity)) {
                            recipeId = code.substring(code.indexOf("#")+1);
                            UploadImageAsyncTask task = new UploadImageAsyncTask(recipeId);
                            task.execute();
                        }

                        //AppUtil.alertMessage(mainActivity,"Recipe added successfully.");

                        //Refresh the recipe in cache
                        RefreshAsyncTask task2 = new RefreshAsyncTask();
                        task2.execute();

                        mainActivity.onNavigationDrawerItemSelected(0);

                    } else {
                        AppUtil.alertMessage(mainActivity, "Unable to add the recipe information!");
                    }
                } catch (Exception e) {
                    AppUtil.alertMessage(mainActivity, "Unable to add the recipe information!");
                    e.printStackTrace();
                }
            } else {
                AppUtil.alertMessage(mainActivity, "Unable to add the recipe information!");
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
            postParams.add(new BasicNameValuePair("option", "addrecipe"));
            postParams.add(new BasicNameValuePair("recipename", txtName.getText().toString()));
            postParams.add(new BasicNameValuePair("description", txtDesc.getText().toString()));
            postParams.add(new BasicNameValuePair("catid", catIDs[categories.getSelectedItemPosition()]));
            postParams.add(new BasicNameValuePair("cuisineid", String.valueOf(cuisines.getSelectedItemPosition()+1)));
            postParams.add(new BasicNameValuePair("ingredients", txtIng.getText().toString()));
            postParams.add(new BasicNameValuePair("procedure", txtSteps.getText().toString()));
            postParams.add(new BasicNameValuePair("author", mainActivity.getUser().getId()));

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
            pd = new ProgressDialog(mainActivity);
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
                    boolean cached = DataManager.saveToCache(mainActivity,Constants.CACHE_RECIPES_FILENAME, response);
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