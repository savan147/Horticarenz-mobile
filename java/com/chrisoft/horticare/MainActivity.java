package com.chrisoft.horticare;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.ActionMenuItemView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chrisoft.horticare.callback.RecipeCallBack;
import com.chrisoft.horticare.model.User;
import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Main Activity used in the application
 */
public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, RecipeCallBack {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Used to store filter value
     */
    private String filterValue=null;

    /**
     * Used to store flag if the previous screen is from registration
     */
    private boolean fromRegister=false;

    /**
     * Used to store number of stacks when back button is pressed
     */
    private static int STACK=0;

    /**
     * Used to hold user information
     */
    private User user=null;

    /**
     * Used to hold image selected from image gallery
     */
    private ImageView targetImage; //used to store small image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        //Check if there is an existing user logged in to the application
        String jsonUser = DataManager.readFromCache(getApplicationContext(), Constants.CACHE_USER_FILENAME);
        if (jsonUser!=null) {
            try {
                User user1 = DataManager.parseUser(jsonUser);
                setUser(user1);
            } catch (Exception e) {
                e.printStackTrace();
                setUser(null);
            }
        } else {
            setUser(null);
        }

        targetImage=null;

        //System.out.println("MAIN_ON_CREATE");
        if (MainActivity.checkNetwork(this)) {
            OnlineAsyncTask task = new OnlineAsyncTask();
            task.execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Get the flag if the last page is from registration
     * @return
     */
    public boolean isFromRegister() {
        return fromRegister;
    }

    /**
     * Set the flag as registration page
     * @param fromRegister
     */
    public void setFromRegister(boolean fromRegister) {
        this.fromRegister = fromRegister;
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        onSectionAttached(position + 1);
        restoreActionBar();
        /*if (position==0) {
            viewProfile(null);
        } else */if (position == 7) { //Signup Page
            ProgressDialog pd;
            pd = new ProgressDialog(this);
            pd.setMessage("Processing...");
            RegisterView dialog = new RegisterView(this);
            dialog.setUp(this);
            dialog.setPd(pd);
            dialog.show();
        } else if (position==8) {
            clearStacks();
            finish();
        } else {
            // update the main content by replacing fragments
            FragmentUtil util= new FragmentUtil();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();

            if (position==0 || position== 1) {
                clearStacks();
                STACK=0;
            }

            transaction.add(R.id.container, util.getFragment(position + 1, this));

            transaction.addToBackStack(null);
            transaction.commit();
            STACK++;
        }
    }

    @Override
    public void onBackPressed() {
        //Log.d("FRAG_STACK", "" + STACK);
        if (STACK>0) {
            STACK--;
            super.onBackPressed();
        } else {
            finish();
        }
    }

    @Override
    public boolean hasWindowFocus() {
        if (isFromRegister()) {
            onNavigationDrawerItemSelected(-1);
            clearStacks();
        }
        setFromRegister(false);
        return super.hasWindowFocus();
    }

    /**
     * Method used to change the title name in action bar
     * @param number
     */
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
               mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = "Frequently Asked Question";
                break;
            case 6:
                mTitle = "Contact us";
                break;
            default:
                mTitle = getString(R.string.app_name);
                STACK=0;
                break;
        }
    }


    /**
     * Free all fragment stacks
     */
    public void clearStacks() {
        final FragmentManager fm = getSupportFragmentManager();
        //Log.d("FM_STACKS", "" + fm.getBackStackEntryCount());
        while (fm.getBackStackEntryCount() > 1) {
            fm.popBackStackImmediate();
        }
    }

    /**
     * Restore action bar to default state
     */
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_home) {
            onNavigationDrawerItemSelected(-1);
            clearStacks();
            return true;
        } else  if (id == R.id.action_search) {
            SearchDialog search = new SearchDialog(this);
            search.setCancelable(true);
            search.setTitle("Search Recipe");
            search.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    final int SELECT_PHOTO = 2;
    private Bitmap selectedBitmap;
    private String selectedImageName;
    @Override
    public void loadImageFromGallery(ImageView small) {
        this.targetImage=small;
        this.selectedImageName=null;
        Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    InputStream imageStream=null;
                    try {
                        imageStream = getContentResolver().openInputStream(selectedImage);
                        selectedImageName= selectedImage.getEncodedPath();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    targetImage.setImageBitmap(yourSelectedImage);
                    selectedBitmap = yourSelectedImage;
                }
        }
    }

    @Override
    public Bitmap getSelectedBitmap() {
        return selectedBitmap;
    }

    @Override
    public String getImageFileName() {
        return selectedImageName;
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    class FragmentUtil {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public Fragment getFragment(int sectionNumber, Activity act) {
/*            if (sectionNumber==1) { //Profile menu is accessed
                Fragment fragment=null;
                if (getUser()!=null) {
                    fragment = new ProfileFragment();
                    Bundle args = new Bundle();
                    args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                    fragment.setArguments(args);
                } else {
                    fragment = new LoginFragment();
                    Bundle args = new Bundle();
                    args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                    fragment.setArguments(args);
                }
                return fragment;*/
            if (sectionNumber==1) { //Profile menu is accessed
                Fragment fragment=  new LibraryFragment();
                Bundle args = new Bundle();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                return fragment;
            } else if (sectionNumber == 2) { //Recipes menu is accessed
                ProgressDialog pd;
                pd = new ProgressDialog(act);
                pd.setMessage("Processing...");

                RecipesFragment fragment = new RecipesFragment();
                fragment.setPd(pd);

                Bundle args = new Bundle();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                if (getFilterValue()!=null) args.putString(RecipesFragment.ARG_RECIPE_FILTER, getFilterValue());
                fragment.setArguments(args);
                return fragment;
            } else if (sectionNumber == 3) { //Categories menu is accessed
                ProgressDialog pd;
                pd = new ProgressDialog(act);
                pd.setMessage("Processing...");

                CategoryFragment fragment = new CategoryFragment();
                fragment.setPd(pd);

                Bundle args = new Bundle();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                return fragment;
            } else if (sectionNumber == 4) { //Cuisines menu is accessed
                ProgressDialog pd;
                pd = new ProgressDialog(act);
                pd.setMessage("Processing...");

                CuisinesFragment fragment = new CuisinesFragment();
                fragment.setPd(pd);

                Bundle args = new Bundle();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                return fragment;
            } else if (sectionNumber == 5) { //Cuisines menu is accessed
                    ProgressDialog pd;
                    pd = new ProgressDialog(act);
                    pd.setMessage("Processing...");

                    FaqFragment  fragment = new FaqFragment();
                    fragment.setPd(pd);

                    Bundle args = new Bundle();
                    args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                    fragment.setArguments(args);
                    return fragment;
            } else {
                Fragment fragment = new HomeFragment();
                Bundle args = new Bundle();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                return fragment;
            }
        }
    }

    /**
     * Function called to start profile fragment
     * @param v
     */
    public void viewProfile(View v) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_LOGIN));
        startActivity(browserIntent);
    }

    /**
     * Function used to start recipe fragment
     * @param v
     */
    public void viewRecipes(View v) {
        boolean doSearch = (getFilterValue()!=null && getFilterValue().startsWith("SEARCH"));
        if (doSearch) {
            clearStacks();
            STACK=0;
        }
        onNavigationDrawerItemSelected(1);
    }

    /**
     * Function used to return to home fragment
     * @param v
     */
    public void viewHome(View v) {
        onNavigationDrawerItemSelected(-1);
    }

    public void logout(View v) {
        DataManager.deleteFromCache(this,Constants.CACHE_USER_FILENAME);
        setUser(null);
        onNavigationDrawerItemSelected(-1);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

    /**
     * Common method used to check network availability
     * @param act
     * @return
     */
    public static boolean checkNetwork(Context act) {
        ConnectivityManager con = (ConnectivityManager)act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = con.getActiveNetworkInfo();
        if (info!=null && info.isConnectedOrConnecting()) {
            //Toast.makeText(act,"Connected",Toast.LENGTH_LONG).show();
            return true;
        } else {
            Toast.makeText(act,"Offline",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    /**
     * Used to store filter value like cuisine or category id
     * @return
     */
    public String getFilterValue() {
        return filterValue;
    }

    /**
     * Setting of filter
     * @param filterValue
     */
    public void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    /**
     * Used to get logged in user
     * @return
     */
    public User getUser() {
        return user;
    }

    /**
     * Used to set user information from login page
     * @param user
     */
    public void setUser(User user) {
        this.user = user;
    }


    ProgressDialog pd;
    public ProgressDialog getPd() {
        if (pd==null) {
            pd = new ProgressDialog(this);
            pd.setMessage("Loading...");
        }
        return pd;
    }

    /**
     * Async Task called to retrieve the information from the PHP service
     */
    private class OnlineAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getPd().show();
        }

        @Override
        protected Object doInBackground(Object... params) {
            refreshCuisines();
            refreshCategories();
            refreshRecipes();
            return null;
        }

        protected void onPostExecute(Object result){
            super.onPostExecute(result);
            if (getPd()!=null) getPd().dismiss();

        }

        protected void onProgressUpdate(Integer... progress){
            //pb.setProgress(progress[0]);
        }

        /**
         * Execute the http post request to get the list of categories from WEB
         * @return JSONArray
         */
        public JSONArray refreshCategories() {
            //Post Data
            List<NameValuePair> postParams = new ArrayList<NameValuePair>(1);
            postParams.add(new BasicNameValuePair("option", "categories"));

            JSONArray result=null;
            try {

                // Execute HTTP Post Request
                String response = HttpUtil.getJSONData(Constants.URL_FUNCTIONS, postParams, 5000, 5000);

                if(response != null)
                {
                    result = new JSONArray(response);
                    boolean cached = DataManager.saveToCache(getApplicationContext(),Constants.CACHE_CATEGORY_FILENAME, response);
                    if (cached) {
                        //Log.d("CATEGORIES:", response);
                    }
                    return result;
                }
            }catch (Exception e) {
                Log.e("CATEGORIES", "OnlineAsyncTask", e);
            }
            return null;
        }

        /**
         * Execute the http post request to get the list of categories from WEB
         * @return JSONArray
         */
        public JSONArray refreshCuisines() {
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
                    boolean cached = DataManager.saveToCache(getApplicationContext(),Constants.CACHE_CUISINES_FILENAME, response);
                    if (cached) {
                        //Log.d("CUISINES:", response);
                    }
                    return result;
                }
            }catch (Exception e) {
                Log.e("CUISINES", "OnlineAsyncTask", e);
            }
            return null;
        }

        public JSONArray refreshRecipes() {
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
                    boolean cached = DataManager.saveToCache(getApplicationContext(),Constants.CACHE_RECIPES_FILENAME, response);
                    if (cached) {
                        //Log.d("RECIPES:", response);
                    }
                    return result;
                }
            }catch (Exception e) {
                Log.e("RECIPEBOOK", "OnlineAsyncTask", e);
                //e.printStackTrace();
            }
            return null;
        }
    }
}
