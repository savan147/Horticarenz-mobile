package com.chrisoft.horticare;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Adapter used to list menu item in navigation drawer
 * Created by Chris on 11/08/2015.
 */
public class MenuAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] menu;
    private final Integer[] imageId = new Integer[]{
            R.mipmap.ic_profile,
            R.mipmap.ic_recipes,
            R.mipmap.ic_category,
            R.mipmap.ic_cuisine,
            R.mipmap.ic_signup,
            R.mipmap.ic_close
    };
    public MenuAdapter(Activity context,
                      String[] menu) {
        super(context, R.layout.navigation_menu, menu);
        this.context = context;
        this.menu = menu;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_single, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(menu[position]);
        imageView.setImageResource(imageId[position]);
        return rowView;
    }
}