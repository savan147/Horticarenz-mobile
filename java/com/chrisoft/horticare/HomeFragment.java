package com.chrisoft.horticare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.chrisoft.horticare.model.MenuItem;
import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used to display application landing page
 * Created by Dahye on 11/08/2015.
 */
public class HomeFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // 1. pass context and data to the custom adapter
        MainMenuAdapter adapter = new MainMenuAdapter(view.getContext(), generateData());

        // if extending Activity 2. Get ListView from activity_main.xml
        ListView listView = (ListView) view.findViewById(R.id.listview);

        // 3. setListAdapter
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        return view;
    }

    private void selectItem(int position) {
        //Toast.makeText(getActivity(), "You selected" + position, Toast.LENGTH_SHORT).show();
        if (position==3) {
            ((MainActivity) getActivity()).onNavigationDrawerItemSelected(6);
        }else if (position == 1){
            ((MainActivity) getActivity()).onNavigationDrawerItemSelected(2);
        }
        else if (position == 2){
            ((MainActivity) getActivity()).onNavigationDrawerItemSelected(5);
        }
        else {
            ((MainActivity) getActivity()).onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity act = ((MainActivity) activity);
        if (getArguments()!=null) {
            act.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    private ArrayList<MenuItem> generateData(){
        ArrayList<MenuItem> models = new ArrayList<MenuItem>();
        models.add(new MenuItem(R.mipmap.ic_cuisine,"Library",""));
        models.add(new MenuItem(R.mipmap.ic_cuisine,"When to Plant?",""));
        models.add(new MenuItem(R.mipmap.ic_cuisine, "Frequently Asked Questions", ""));
        models.add(new MenuItem(R.mipmap.ic_cuisine, "Contact Us", ""));
        return models;
    }
}