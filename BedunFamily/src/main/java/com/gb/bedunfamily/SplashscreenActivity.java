package com.gb.bedunfamily;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * //Splashscreen Activity. First screen of the application. runs the animation for the application logo (to be implemented) then exits to language activity
 * */
public class SplashscreenActivity extends Activity {

    private Handler mHandler;
    static String lang = "en";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashcreen);
        ImageView img1 = findViewById(R.id.splashImage);
        mHandler = new Handler();

        //set font for title
        TextView tx = findViewById(R.id.splashTitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        tx.setTypeface(custom_font);
        mHandler.postDelayed(mUpdateTimeTask, 5000);

    }

    //starts language activity after 5 second delay
    private final Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            Intent intent = new Intent(SplashscreenActivity.this, LanguageActivity.class);
            startActivity(intent);
        }

    };


    //starts language activity after 5 second delay - on resumption of activity e.g back button pressed
    @Override
    protected  void onResume(){
        super.onResume();
        mHandler.postDelayed(mUpdateTimeTask, 5000);
    }
}
