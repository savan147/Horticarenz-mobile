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
 * Fragment used to display FAQ
 *
 */
public class FaqFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";
   // public static final String ARG_RECIPE_FILTER = "filter";

    private ListView mDrawerListView;
    private ProgressDialog pd;

    List faqs = new ArrayList();

    public FaqFragment() {}

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
                R.layout.fragment_faq, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity act = (MainActivity)getActivity();
                String[] faq = (String[])getFaqs().get(position);
                act.setFilterValue("FAQ_" + faq[0]); //pass the category id
                //act.viewRecipes(view);
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        List cursor = getList();
        mDrawerListView.setAdapter(new FaqAdapter(getActivity(), cursor));
        return mDrawerListView;
    }

    /**
     * Function used to get the list of items to be populated in the list view
     * @return
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
        if (faqs==null) faqs = new ArrayList();
        return faqs;
    }

    public void setFaqs(List faqs) {
        this.faqs=faqs;
    }

    public List getFaqs() {
        return this.faqs;
    }

    /**
     * Async Task called to retrieve the information from the PHP service
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
                List faqs = DataManager.parseFaqs(result);
                setFaqs(faqs);
                mDrawerListView.setAdapter(new FaqAdapter(getActivity(), faqs));
            }

            if (getPd()!=null) getPd().dismiss();

        }

        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        /**
         * Execute the http post request to get the list of categories from WEB
         * @return JSONArray
         */
        public JSONArray postData() {
            //Post Data
            List<NameValuePair> postParams = new ArrayList<NameValuePair>(1);
            postParams.add(new BasicNameValuePair("option", "faqs"));

            JSONArray result=null;
            try {

                // Execute HTTP Post Request
                String response = HttpUtil.GetData(Constants.URL_API + "/faqs");


                if(response != null)
                {
                    result = new JSONArray(response);
                    boolean cached = DataManager.saveToCache(getActivity(),Constants.CACHE_FAQS_FILENAME, response);
                    if (cached) {
                        Log.d("Http Post Response:", response);
                    }
                    return result;
                }
            }catch (Exception e) {
                Log.e("FAQS", "OnlineAsyncTask", e);
                //e.printStackTrace();
            }
            return null;
        }

    }

    /**
     * Async Task called to retrieve the information from the cache
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
                List faqs = DataManager.parseFaqs(result);
                setFaqs(faqs);
                mDrawerListView.setAdapter(new FaqAdapter(getActivity(), faqs));
            }

            if (getPd()!=null) getPd().dismiss();

        }

        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        /**
         * Method used to read file from cache
         * @return JSONArray
         */
        public JSONArray  getCachedData() {
            JSONArray result=null;
            try {
                String data = DataManager.readFromCache(getActivity(),Constants.CACHE_FAQS_FILENAME);
                if(data != null)
                {
                    result = new JSONArray(data);
                    Log.d("Cached Data Response:", data);
                    return result;
                }
            }catch (Exception e) {
                Log.e("FAQS", "OfflineAsyncTask", e);
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