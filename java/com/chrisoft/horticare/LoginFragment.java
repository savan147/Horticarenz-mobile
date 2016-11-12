package com.chrisoft.horticare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisoft.horticare.model.User;
import com.chrisoft.horticare.util.AppUtil;
import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;


/**
 * Fragment used to display login page
 * Created by Chris on 19/08/2015.
 */
public class LoginFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";
    private EditText txtEmail;
    private EditText txtPassword;
    private Button btLogin;
    private ProgressDialog pd;
    private MainActivity parent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        txtEmail = (EditText) view.findViewById(R.id.email);
        txtPassword = (EditText) view.findViewById(R.id.password);
        btLogin = (Button) view.findViewById(R.id.btnLogin);
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtEmail.getText().toString().length()>0 && txtPassword.getText().length()>0) checkLogin();
            }
        });
        return view;
    }

    /**
     * Initialize the progress dialog
     * @return ProgressDialog
     */
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

    /**
     * Function called to check login details
     */
    private void checkLogin() {
        if (MainActivity.checkNetwork(getActivity())) {
            OnlineAsyncTask task = new OnlineAsyncTask();
            task.execute();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity act = ((MainActivity) activity);
        if (getArguments()!=null) {
            act.onSectionAttached(getArguments().getInt(ARG_SECTION_NUMBER));
            act.setFilterValue(null);
        }
        parent=act;
    }

    /**
     * Task used to perform http post request to WEB to authenticate user information entered
     */
    private class OnlineAsyncTask extends AsyncTask {

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

            if (result!=null) {
                try {
                    User user = DataManager.parseUser((String)result);
                    if (user.getPassword().equals(txtPassword.getText().toString())) {
                        parent.setUser(user);
                        parent.onNavigationDrawerItemSelected(0);
                    } else {
                        AppUtil.alertMessage(getActivity(), "Invalid user details entered!");
                    }
                } catch (Exception e) {
                    AppUtil.alertMessage(getActivity(), "Invalid user details entered!");
                    e.printStackTrace();
                }
            } else {
                AppUtil.alertMessage(getActivity(), "Invalid user details entered!");
            }

            if (getPd()!=null) getPd().dismiss();

        }

        public String postData() {
            //Post Data
            List<NameValuePair> postParams = new ArrayList<NameValuePair>(1);
            postParams.add(new BasicNameValuePair("option", "getuser"));
            postParams.add(new BasicNameValuePair("email", txtEmail.getText().toString()));

            try {

                // Execute HTTP Post Request
                String response = HttpUtil.getJSONData(Constants.URL_FUNCTIONS, postParams, Constants.TIMEOUT_CONN, Constants.TIMEOUT_SOCKET);

                if(response != null)
                {
                    boolean cached = DataManager.saveToCache(getActivity(),Constants.CACHE_USER_FILENAME, response);
                    if (cached) {
                        //Log.d("Http Post Response:", response);
                    }
                    return response;
                }
            }catch (Exception e) {
                Log.e("RECIPEBOOK", "getUser", e);
            }
            return null;
        }

    }
}