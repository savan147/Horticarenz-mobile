package com.chrisoft.horticare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used to display list of cuisines
 * Created by Chris on 11/08/2015.
 */
public class CuisinesFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";

    private ListView mDrawerListView;
    private ProgressDialog pd;

    //Constructor
    public CuisinesFragment() {}

    public ProgressDialog getPd() {
        return pd;
    }

    public void setPd(ProgressDialog pd) {
        this.pd = pd;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_cuisines, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity act = (MainActivity)getActivity();
                String[] cat = (String[])getCuisines().get(position);
                act.setFilterValue("CUI_" + cat[0]); //pass the category id
                act.viewRecipes(view);
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        List cursor = getList();
        mDrawerListView.setAdapter(new CuisinesAdapter(getActivity(), cursor));
        return mDrawerListView;
    }



    List cuisines = new ArrayList();

    /**
     * Function used to get cuisines list from WEB or cache
     * @return list of cuisines
     */
    public List getList() {
        try {
            if (MainActivity.checkNetwork(getActivity())) {
                OnlineAsyncTask task = new OnlineAsyncTask();
                task.execute();
            } else {
                OfflineAsyncTask task = new OfflineAsyncTask();
                task.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (cuisines==null) cuisines = new ArrayList();
        return cuisines;
    }

    public void setCuisines(List cuisines) {
        this.cuisines=cuisines;
    }

    public List getCuisines() {
        return this.cuisines;
    }

    /**
     * Task used to get information from the WEB
     */
    private class OnlineAsyncTask extends AsyncTask{

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

            //pb.setVisibility(View.GONE);
            if (result!=null) {
                List cuisines = DataManager.parseCuisines(result);
                setCuisines(cuisines);
                mDrawerListView.setAdapter(new CuisinesAdapter(getActivity(), cuisines));
            }

            if (getPd()!=null) getPd().dismiss();

        }

        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        /**
         * Function used to post http request from PHP WEB
         * @return JSONArray
         */
        public JSONArray postData() {
            //Post Data
            List<NameValuePair> postParams = new ArrayList<NameValuePair>(1);
            postParams.add(new BasicNameValuePair("option", "cuisines"));

            JSONArray result=null;
            try {

                // Execute HTTP Post Request
                String response = HttpUtil.getJSONData(Constants.URL_FUNCTIONS, postParams, 5000, 5000);

                if(response != null)
                {
                    result = new JSONArray(response);
                    boolean cached = DataManager.saveToCache(getActivity(),Constants.CACHE_CUISINES_FILENAME, response);
                    if (cached) {
                        Log.d("Http Post Response:", response);
                    }
                    return result;
                }
            }catch (Exception e) {
                Log.e("CUISINES", "OnlineAsyncTask", e);
                //e.printStackTrace();
            }
            return null;
        }

    }

    /**
     * Task used to get information from cache
     */
    private class OfflineAsyncTask extends AsyncTask{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getPd().show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            // TODO Auto-generated method stub
            return getCachedData();
        }

        protected void onPostExecute(Object result){
            super.onPostExecute(result);

            //pb.setVisibility(View.GONE);
            if (result!=null) {
                List cuisines = DataManager.parseCuisines(result);
                setCuisines(cuisines);
                mDrawerListView.setAdapter(new CuisinesAdapter(getActivity(), cuisines));
            }

            if (getPd()!=null) getPd().dismiss();

        }

        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        /**
         * Function used to read information from cache
         * @return JSONArray
         */
        public JSONArray  getCachedData() {
            JSONArray result=null;
            try {
                String data = DataManager.readFromCache(getActivity(),Constants.CACHE_CUISINES_FILENAME);
                if(data != null)
                {
                    result = new JSONArray(data);
                    Log.d("Cached Data Response:", data);
                    return result;
                }
            }catch (Exception e) {
                Log.e("CUISINES", "OfflineAsyncTask", e);
                //e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity act = ((MainActivity) activity);
        act.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
        act.setFilterValue(null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}