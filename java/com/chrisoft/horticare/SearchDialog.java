package com.chrisoft.horticare;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Customized input dialog for text view or edit text components
 * Created by Chris on 13/08/2015.
 */
public class SearchDialog extends Dialog {

    EditText txtContents;
    Object target;
    MainActivity activity;
    ImageButton btSearch;

    //Constructor
    public SearchDialog(Context context) {
        super(context);
        this.activity = (MainActivity)context;
        setContentView(R.layout.search_layout);
        final EditText txtSearch = (EditText)this.findViewById(R.id.txt_search);
        btSearch = (ImageButton)this.findViewById(R.id.btSearch);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtSearch.getText().toString().trim().length() > 0) {
                    activity.setFilterValue("SEARCH#" + txtSearch.getText().toString());
                    activity.viewRecipes(null);
                    dismiss();
                }
            }
        });

        txtSearch.requestFocus();

        txtSearch.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_ENTER:
                        /* This is a sample for handling the Enter button */
                        btSearch.callOnClick();
                        return true;
                }
                return false;            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
    }

}
