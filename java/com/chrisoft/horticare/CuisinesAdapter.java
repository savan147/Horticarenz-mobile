package com.chrisoft.horticare;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chrisoft.horticare.model.Recipe;
import com.chrisoft.horticare.util.CharacterDrawable;

import java.util.List;

/**
 * Adapter used in cuisines fragment
 * Created by Chris on 11/08/2015.
 */
public class CuisinesAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List cuisines;
    public CuisinesAdapter(Activity context,
                           List cuisines) {
        super(context, R.layout.navigation_menu, cuisines);
        this.context = context;
        this.cuisines = cuisines;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_cuisines, null, true);
        String[] cuisine = (String[])cuisines.get(position);
        TextView txtName = (TextView) rowView.findViewById(R.id.name);
        txtName.setText(cuisine[1]);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.avatar);
        CharacterDrawable drawable = new CharacterDrawable(cuisine[1].toUpperCase().charAt(0), getColor() );
        imageView.setImageDrawable(drawable);

        TextView txtRecipes = (TextView) rowView.findViewById(R.id.recipes);
        txtRecipes.setText("[ " + cuisine[2] + " ]");

        return rowView;
    }

    static int cnt=-1;
    static int getColor() {
        int[] colors = new int[] {Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW};
        cnt++;
        if (cnt>=4) cnt=0;
        return colors[cnt];
    }

}