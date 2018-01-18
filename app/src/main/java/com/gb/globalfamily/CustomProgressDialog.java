package com.gb.globalfamily;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Custom Dialog for displaying progress bar in custom progress dialog layout xmls rather then  android progressDialog layout
 */
class CustomProgressDialog extends Dialog {


    public CustomProgressDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        Activity c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_progress);
        TextView tx = findViewById(R.id.dialog_progress);
        Typeface titleFont = Typeface.
                createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
        tx.setTypeface(titleFont);


    }



}