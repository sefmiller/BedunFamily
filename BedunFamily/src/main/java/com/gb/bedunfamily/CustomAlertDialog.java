package com.gb.bedunfamily;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.gb.bedunfamily.R;

/**
 * Custom Dialog for displaying alert dialog with positive  button in the house style,
 * rather then the android dialog style
 */
class CustomAlertDialog extends Dialog {

    public CustomAlertDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        Activity c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_alert);
        TextView tx = findViewById(R.id.dialog_alert_text_title);
        Typeface titleFont = Typeface.
                createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
        tx.setTypeface(titleFont);


    }



}