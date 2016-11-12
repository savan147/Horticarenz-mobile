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
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used in display user information in profile fragment user tab
 * Created by Chris on 19/08/2015.
 */
public class UserFragment extends Fragment {
    public static final String ARG_SECTION_NUMBER = "section_number";

    TextView txtName;
    TextView txtEmail;
    TextView txtOldPassword;
    TextView txtPassword;
    TextView txtConfirmPassword;
    User user;
    MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        //set up name text
        txtName = (TextView) view.findViewById(R.id.name);
        txtName.setText(user.getName());

        //set up email text
        txtEmail = (TextView) view.findViewById(R.id.email);
        txtEmail.setText(user.getEmail());
        txtEmail.setEnabled(false);

        //set up password text
        txtOldPassword = (TextView) view.findViewById(R.id.oldpassword);
        txtPassword = (TextView) view.findViewById(R.id.password);

        //set up password text
        txtConfirmPassword = (TextView) view.findViewById(R.id.passwordconfirm);

        txtName.requestFocus();
        Button btSave = (Button) view.findViewById(R.id.btnSave);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        return view;
    }

    public void update() {
        if (AppUtil.isEmpty(txtName.getText().toString())) {
            AppUtil.alertMessage(getActivity(),"Name should not be empty.");
            txtName.requestFocus();
        } else if (AppUtil.isEmpty(txtPassword.getText().toString())) {
            AppUtil.alertMessage(getActivity(), "Invalid password provided.");
            txtPassword.requestFocus();
        } else if (AppUtil.isEmpty(txtConfirmPassword.getText().toString())) {
            AppUtil.alertMessage(getActivity(), "Invalid password confirmation provided.");
            txtConfirmPassword.requestFocus();
        } else if (!txtPassword.getText().toString().equalsIgnoreCase(txtConfirmPassword.getText().toString())) {
            AppUtil.alertMessage(getActivity(), "Confirmation password does not match!");
            txtConfirmPassword.requestFocus();
        } else if (!txtOldPassword.getText().toString().equalsIgnoreCase(user.getPassword())) {
            AppUtil.alertMessage(getActivity(), "Old password is not valid!");
            txtOldPassword.requestFocus();
        } else {
            try {
                if (MainActivity.checkNetwork(getActivity())) {
                    OnlineAsyncTask task = new OnlineAsyncTask();
                    task.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        this.user=act.getUser();
        this.mainActivity=act;
    }
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

            //pb.setVisibility(View.GONE);
            if (result!=null && String.valueOf(result).equalsIgnoreCase("OK")) {
                if (getPd()!=null) getPd().dismiss();
                RefreshUserAsyncTask task = new RefreshUserAsyncTask();
                task.execute();

                AppUtil.alertMessage(getActivity(), "You are successfully updated the user!");
                user.setPassword(txtPassword.getText().toString());
                user.setName(txtName.getText().toString());
                mainActivity.setUser(user);

            } else  if (result!=null && String.valueOf(result).equalsIgnoreCase("FAILED")) {
                if (getPd()!=null) getPd().dismiss();
                AppUtil.alertMessage(getActivity(),"Update Failed. Please check the information you have entered.");
            } else {
                if (getPd()!=null) getPd().dismiss();
                AppUtil.alertMessage(getActivity(),"Unable to perform action. Please try again later.");
            }
        }

        public String postData() {
            //Post Data
            List<NameValuePair> postParams = new ArrayList<NameValuePair>(1);
            postParams.add(new BasicNameValuePair("option", "edituser"));
            postParams.add(new BasicNameValuePair("authorid", user.getId()));
            postParams.add(new BasicNameValuePair("name", txtName.getText().toString()));
            postParams.add(new BasicNameValuePair("password", txtPassword.getText().toString()));

            JSONArray result=null;
            try {

                // Execute HTTP Post Request
                String response = HttpUtil.getJSONData(Constants.URL_FUNCTIONS, postParams, Constants.TIMEOUT_CONN, Constants.TIMEOUT_SOCKET);

                if(response != null)
                {
                    //Log.d("Http Post Response:", response);
                    return response; //Response is OK or FAILED
                }
            }catch (Exception e) {
                Log.e("RECIPEBOOK", "USER UPDATE", e);
            }
            return null;
        }

    }

    ProgressDialog pd;
    public ProgressDialog getPd() {
        if (pd==null) {
            pd = new ProgressDialog(getActivity());
            pd.setMessage("Processing...");
        }
        return pd;
    }

    /**
     * Task used to perform http post request to WEB to authenticate user information entered
     */
    private class RefreshUserAsyncTask extends AsyncTask {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object... params) {
            return postData();
        }

        protected void onPostExecute(Object result){
            super.onPostExecute(result);
            if (result!=null) {
                Toast.makeText(getActivity(), "User Refreshed!", Toast.LENGTH_SHORT).show();
            }
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
                        Log.d("Http Post Response:", response);
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