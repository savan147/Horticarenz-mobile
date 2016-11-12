package com.chrisoft.horticare;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chrisoft.horticare.util.CharacterDrawable;

import java.util.List;

/**
 * Adapter for FAQ fragment
 *
 */
public class FaqAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final List faqs;
    public FaqAdapter(Activity context,
                           List faqs) {
        super(context, R.layout.navigation_menu, faqs);
        this.context = context;
        this.faqs = faqs;
    }

    /**
     * Alter the default layout of the listview item
     * @param position
     * @param view
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.list_faqs, null, true);
        String[] cuisine = (String[])faqs.get(position);
        TextView txtQuestion = (TextView) rowView.findViewById(R.id.question);
        txtQuestion.setText(cuisine[1]);


        TextView txtAnswer = (TextView) rowView.findViewById(R.id.answer);
        txtAnswer.setText("" + cuisine[2] + "");

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