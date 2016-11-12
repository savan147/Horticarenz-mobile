package com.chrisoft.horticare;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisoft.horticare.model.Recipe;
import com.chrisoft.horticare.util.CharacterDrawable;
import com.chrisoft.horticare.util.DataManager;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Adapter used to display list of recipes in the recipe list fragment
 * Created by Chris on 11/08/2015.
 */
public class RecipesAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List recipes;
    public RecipesAdapter(Activity context,
                          List recipes) {
        super(context, R.layout.navigation_menu, recipes);
        this.context = context;
        this.recipes = recipes;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_recipes, null, true);
        Recipe recipe = (Recipe)recipes.get(position);
        TextView txtName = (TextView) rowView.findViewById(R.id.name);
        TextView txtDesc = (TextView) rowView.findViewById(R.id.desc);
        TextView txtAuthor = (TextView) rowView.findViewById(R.id.author);
        txtName.setText(recipe.getName());
        txtDesc.setText(recipe.getDescription());
        txtAuthor.setText(recipe.getAuthor());

        ImageView imageView = (ImageView) rowView.findViewById(R.id.avatar);
        CharacterDrawable drawable = new CharacterDrawable(recipe.getName().toUpperCase().charAt(0), getColor() );
        imageView.setImageDrawable(drawable);

        try {
            BitmapOfflineAsyncTask task = new BitmapOfflineAsyncTask(recipe.getId(),imageView);
            task.execute();
        } catch (Exception e) {}

        RatingBar rating = (RatingBar) rowView.findViewById(R.id.ratingBar);
        String rate = recipe.getRating();
        if (rate!=null) {
            rating.setRating(Float.parseFloat(rate));
        }

        return rowView;
    }

    static int cnt=-1;
    static int getColor() {
        int[] colors = new int[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        cnt++;
        if (cnt>=4) cnt=0;
        return colors[cnt];
    }

    private class BitmapOfflineAsyncTask extends AsyncTask {

        private String id;
        private ImageView img;

        public BitmapOfflineAsyncTask(String id, ImageView img) {
            this.id=id;
            this.img = img;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                return DataManager.getCachedImage(getContext(), "RECIPE_" + this.id);
            } catch (Exception e) {
                return null;
            }
        }

        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (result!=null) {
                img.setImageBitmap((Bitmap) result);
            }
            img=null;
        }

    }

}