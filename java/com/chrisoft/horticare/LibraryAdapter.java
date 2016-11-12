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
import android.widget.RatingBar;
import android.widget.TextView;

import com.chrisoft.horticare.model.PlantDetail;
import com.chrisoft.horticare.util.AppUtil;
import com.chrisoft.horticare.util.CharacterDrawable;
import com.chrisoft.horticare.util.DataManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Adapter used to display list of recipes in the recipe list fragment
 * Created by Chris on 11/08/2015.
 */
public class LibraryAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List plants;
    public LibraryAdapter(Activity context,
                          List plants) {
        super(context, R.layout.navigation_menu, plants);
        this.context = context;
        this.plants = plants;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_plants, null, true);
        JSONObject obj = (JSONObject)plants.get(position);
        TextView txtName = (TextView) rowView.findViewById(R.id.name);
        TextView txtDesc = (TextView) rowView.findViewById(R.id.desc);
        TextView txtAuthor = (TextView) rowView.findViewById(R.id.author);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.avatar);

        try {
            String desc=limitDisplay(obj.getString("description"));
            txtName.setText(obj.getString("name"));
            txtDesc.setText(desc);
            txtAuthor.setText("by: " + obj.getString("author") + " on "
                    + AppUtil.toDateTime(Long.valueOf(obj.getString("modifiedon"))));

            CharacterDrawable drawable = new CharacterDrawable(obj.getString("name").toUpperCase().charAt(0), getColor() );
            imageView.setImageDrawable(drawable);
        } catch (Exception e) {}

        try {
            BitmapOfflineAsyncTask task = new BitmapOfflineAsyncTask(obj.getString("pid"), imageView);
            task.execute();
        } catch (Exception e) {}

        return rowView;
    }

    public String limitDisplay(String desc) {
        if (desc.length()>100) {
            return desc.substring(0,100) + "...";
        } else {
            return desc;
        }
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
                return DataManager.getCachedImage(getContext(), "PLANT_" + this.id);
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