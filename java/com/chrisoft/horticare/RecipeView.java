package com.chrisoft.horticare;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisoft.horticare.model.Recipe;
import com.chrisoft.horticare.util.CharacterDrawable;
import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import java.io.File;

/**
 * A customized dialog used to display recipe information in view mode
 * Created by Chris on 13/08/2015.
 */
public class RecipeView extends Dialog {

    ImageView img;
    ProgressBar pb;

    public RecipeView(Activity context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }



    public void setUp(final Activity dialog, Object data) {
        Recipe recipe = (Recipe) data;
        setContentView(R.layout.viewrecipe);
        setTitle(recipe.getName());
        setCancelable(true);

        //there are a lot of settings, for dialog, check them all out!

        //set up text
        TextView text1 = (TextView) this.findViewById(R.id.txtDesc);
        TextView text4 = (TextView) this.findViewById(R.id.txtIngredients);
        TextView text5 = (TextView) this.findViewById(R.id.txtSteps);

        text1.setText(recipe.getDescription());
        text4.setText(recipe.getIngredients());
        text5.setText(recipe.getProcedure());

        //set up text
        TextView text2 = (TextView) this.findViewById(R.id.txtName);
        text2.setText(recipe.getName());


        //set up text
        TextView text3 = (TextView) this.findViewById(R.id.txtAuthor);
        text3.setText(recipe.getAuthor());

        //set up image view
        img = (ImageView) this.findViewById(R.id.ImageView01);

        pb = (ProgressBar)this.findViewById(R.id.progressBar);

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

            hideProgress();;
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

}
