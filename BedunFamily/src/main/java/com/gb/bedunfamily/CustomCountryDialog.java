package com.gb.bedunfamily;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;


/**
 * Custom Dialog for displaying list Views in custom dialog layout xmls rather then standard android spinner layout
 */
class CustomCountryDialog extends Dialog {


    public CustomCountryDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        Activity c = a;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_country);
        TextView tx = findViewById(R.id.dialog_tit);
        Typeface titleFont = Typeface.
                createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
        tx.setTypeface(titleFont);


    }



}