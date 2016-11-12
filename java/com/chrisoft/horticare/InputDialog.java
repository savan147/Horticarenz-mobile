package com.chrisoft.horticare;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chrisoft.horticare.model.Recipe;
import com.chrisoft.horticare.util.Constants;
import com.chrisoft.horticare.util.DataManager;
import com.chrisoft.horticare.util.HttpUtil;

import java.io.File;

/**
 * Customized input dialog for text view or edit text components
 * Created by Chris on 13/08/2015.
 */
public class InputDialog extends Dialog {

    EditText txtContents;
    Object target;

    //Constructor
    public InputDialog(Context context) {
        super(context);
    }

    /**
     * Setup the layout and components of the dialog screen
     * @param target
     * @param maxLength
     * @param title
     */
    public void setUp(final Object target, int maxLength, String title) {
        setContentView(R.layout.input_dialog);
        setTitle(title);
        setCancelable(true);

        //set up text
        txtContents = (EditText) this.findViewById(R.id.txtContent);

        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter.LengthFilter(maxLength); //Filter to maxLength characters
        txtContents.setFilters(filters);

        //set up text
        Button btOk = (Button) this.findViewById(R.id.btnOk);

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtContents.getText().length() > 0) {
                    if (target instanceof EditText) {
                        EditText txt = (EditText) target;
                        txt.setText(txtContents.getText());
                    } else if (target instanceof TextView) {
                        TextView txt = (TextView) target;
                        txt.setText(txtContents.getText());
                    }
                    dismiss();
                }
            }
        });
    }

    public void setContents(String contents) {
        txtContents.setText(contents);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
    }

}
