package com.chrisoft.horticare;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisoft.horticare.model.Recipe;
import com.chrisoft.horticare.util.AppUtil;
import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A customized dialog used to register user information
 * Created by Chris on 13/08/2015.
 */
public class RegisterView extends Dialog {

    ImageView img;
    ProgressBar pb;
    TextView txtName;
    TextView txtEmail;
    TextView txtPassword;
    TextView txtConfirmPassword;
    MainActivity act;

    private ProgressDialog pd;

    public RegisterView(Activity context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.act=(MainActivity)context;
    }

    public ProgressDialog getPd() {
        return pd;
    }

    public void setPd(ProgressDialog pd) {
        this.pd = pd;
    }

    public void register() {
        if (AppUtil.isEmpty(txtName.getText().toString())) {
            AppUtil.alertMessage(getContext(),"Name should not be empty.");
            txtName.requestFocus();
        } else if (!AppUtil.isValidEmail(txtEmail.getText().toString())) {
            AppUtil.alertMessage(getContext(), "Invalid email provided.");
            txtEmail.requestFocus();
        } else if (AppUtil.isEmpty(txtPassword.getText().toString())) {
            AppUtil.alertMessage(getContext(), "Invalid password provided.");
            txtPassword.requestFocus();
        } else if (AppUtil.isEmpty(txtConfirmPassword.getText().toString())) {
            AppUtil.alertMessage(getContext(), "Invalid password confirmation provided.");
            txtConfirmPassword.requestFocus();
        } else if (!txtPassword.getText().toString().equalsIgnoreCase(txtConfirmPassword.getText().toString())) {
            AppUtil.alertMessage(getContext(), "Confirmation password does not match!");
            txtConfirmPassword.requestFocus();
        } else {
           try {
                if (MainActivity.checkNetwork(getContext())) {
                    OnlineAsyncTask task = new OnlineAsyncTask();
                    task.execute();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setUp(final Activity dialog) {
        //Recipe recipe = (Recipe) data;
        setContentView(R.layout.fragment_register);
        setTitle("Register");
        setCancelable(true);

        //there are a lot of settings, for dialog, check them all out!

        //set up name text
        txtName = (TextView) this.findViewById(R.id.name);

        //set up email text
        txtEmail = (TextView) this.findViewById(R.id.email);

        //set up password text
        txtPassword = (TextView) this.findViewById(R.id.password);

        //set up password text
        txtConfirmPassword = (TextView) this.findViewById(R.id.passwordconfirm);

        Button btRegister = (Button) this.findViewById(R.id.btnRegister);
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });
    }

    public void reset() {
        txtName.setText("");
        txtEmail.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
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
            if (result!=null && String.valueOf(result).equalsIgnoreCase("OK")) {
                if (getPd()!=null) getPd().dismiss();
                AppUtil.alertMessage(getContext(), "You are successfully registered. Thank you!");
                act.setFromRegister(true);
                act.hasWindowFocus();
                reset();
                dismiss();
            } else  if (result!=null && String.valueOf(result).equalsIgnoreCase("FAILED")) {
                if (getPd()!=null) getPd().dismiss();
                AppUtil.alertMessage(getContext(),"Registration Failed. Please check the information you have entered.");
            } else {
                if (getPd()!=null) getPd().dismiss();
                AppUtil.alertMessage(getContext(),"Unable to perform action. Please try again later.");
            }
        }

        public String postData() {
            //Post Data
            List<NameValuePair> postParams = new ArrayList<NameValuePair>(1);
            postParams.add(new BasicNameValuePair("option", "register"));
            postParams.add(new BasicNameValuePair("name", txtName.getText().toString()));
            postParams.add(new BasicNameValuePair("email", txtEmail.getText().toString()));
            postParams.add(new BasicNameValuePair("password", txtPassword.getText().toString()));

            JSONArray result=null;
            try {

                // Execute HTTP Post Request
                String response = HttpUtil.getJSONData(Constants.URL_FUNCTIONS, postParams, Constants.TIMEOUT_CONN , Constants.TIMEOUT_SOCKET);

                if(response != null)
                {
                    //Log.d("Http Post Response:", response);
                    return response; //Response is OK or FAILED
                }
            }catch (Exception e) {
                Log.e("RECIPEBOOK", "REGISTERVIEW", e);
            }
            return null;
        }

    }
}
