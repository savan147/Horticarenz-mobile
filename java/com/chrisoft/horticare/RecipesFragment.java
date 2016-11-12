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

import com.chrisoft.horticare.model.User;
import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used to display list of recipes
 * Created by Chris on 11/08/2015.
 */
public class RecipesFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";
    public static final String ARG_RECIPE_FILTER = "filter";

    private ListView mDrawerListView;
    private ProgressDialog pd;
    private User user;
    private MainActivity activity;

    public RecipesFragment() {}

    public ProgressDialog getPd() {
        if (pd==null) {
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Processing...");
        }
        return pd;
    }

    public void setPd(ProgressDialog pd) {
        this.pd = pd;
    }

    private String filterValue=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_recipes, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!fromUser) {
                    RecipeView dialog = new RecipeView(getActivity());
                    dialog.setUp(getActivity(), getRecipes().get(position));
                    dialog.show();
                } else {
                    RecipeEdit dialog = new RecipeEdit(getActivity());
                    dialog.setUp(activity, getRecipes().get(position));
                    dialog.show();
                }
            }
        });

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //System.out.println("RECIPES FILTER ::: " + getFilterValue());
        List cursor = getRecipes(getFilterValue());
        mDrawerListView.setAdapter(new RecipesAdapter(getActivity(), cursor));
        return mDrawerListView;
    }

    List recipes = new ArrayList();
    public List getRecipes(String filter) {
        boolean doSearch = (filter!=null && filter.startsWith("SEARCH"));
        try {
            if (!doSearch && MainActivity.checkNetwork(getActivity())) {
                OnlineAsyncTask task = new OnlineAsyncTask();
                task.execute();
            } else {
                OfflineAsyncTask task = new OfflineAsyncTask();
                task.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (recipes==null) recipes = new ArrayList();
        return recipes;
    }

    public void setRecipes(List recipes) {
        this.recipes=recipes;
    }

    public List getRecipes() {
        return this.recipes;
    }

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
                List recipes = DataManager.parseRecipes(result, getFilterValue());
                setRecipes(recipes);
                mDrawerListView.setAdapter(new RecipesAdapter(getActivity(), recipes));
            }

            if (getPd()!=null) getPd().dismiss();

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
                    boolean cached = DataManager.saveToCache(getActivity(),Constants.CACHE_RECIPES_FILENAME, response);
                    /*if (cached) {
                        Log.d("Http Post Response:", response);
                    }*/
                    return result;
                }
            }catch (Exception e) {
                Log.e("RECIPEBOOK", "OnlineAsyncTask", e);
                //e.printStackTrace();
            }
            return null;
        }

    }

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
                List recipes = DataManager.parseRecipes(result, getFilterValue());
                setRecipes(recipes);
                mDrawerListView.setAdapter(new RecipesAdapter(getActivity(), recipes));
            }

            if (getPd()!=null) getPd().dismiss();

        }

        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        public JSONArray  getCachedData() {
            JSONArray result=null;
            try {
                String data = DataManager.readFromCache(getActivity(),Constants.CACHE_RECIPES_FILENAME);
                if(data != null)
                {
                    result = new JSONArray(data);
                    //Log.d("Cached Data Response:", data);
                    return result;
                }
            }catch (Exception e) {
                Log.e("RECIPEBOOK", "OfflineAsyncTask", e);
                //e.printStackTrace();
            }
            return null;
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity act = ((MainActivity) activity);
        setFilterValue(null);
        setUser(act.getUser());
        if (getArguments()!=null) {
            act.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
            if (getArguments().getString(ARG_RECIPE_FILTER) != null) {
                setFilterValue(getArguments().getString(ARG_RECIPE_FILTER));
            }
        } else {
            if (getUser()!=null) {
                setFilterValue("USER_" + getUser().getId());
                fromUser=true;
            }
        }
        act.setFilterValue(null);
        this.activity=act;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public String getFilterValue() {
        return filterValue;
    }

    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private boolean fromUser=false;
}