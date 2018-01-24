package com.gb.bedunfamily;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;

import static com.gb.bedunfamily.SplashscreenActivity.lang;

/**
        * //Activity for selecting the language of the application. The wheelpicker presents a scrollable wheel
        * for the user to select a language and set the locale of the app
 * */
public class LanguageActivity extends AppCompatActivity implements WheelPicker.OnItemSelectedListener {
    private TextView tx;
    private Button but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        tx = findViewById(R.id.lang_title);
        but = findViewById(R.id.langbutton);

        // set item selected listener for language select wheel
        WheelPicker wheel = findViewById(R.id.main_wheel);
        wheel.setOnItemSelectedListener(this);

        //sets font for title
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        tx.setTypeface(custom_font);
    }

    /**
     * sends intent to start login activity. passes over selected language/locale to the login activity.
     * @param view View of the Button
     */
    public void sendMessage(View view)
    {
        SharedPreferences.Editor editor = getSharedPreferences("com.gb.bedunfamily.preferences", MODE_PRIVATE).edit();
        editor.putString("lang", lang).commit();
        Intent intent = new Intent(LanguageActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * on an item being selected by the scrollable wheel spinner, the lang string is set to the locale abbreviation for the selected country (e.g 'en')
     * sets the title view to display 'select language' in the selected language (e.g French -  Choisir la langue)
     * sets the button view to display 'Sign in or register' in the selected language (e.g French -  Connexion ou Registre)
     * @param picker   Wheel picker, a custom wheel spinner
     * @param item an item in the spinner, in this case a language string
     * @param position the index position of the item in the wheel spinner
     */
    @Override
    public void onItemSelected(WheelPicker picker, Object item, int position) {

        switch (picker.getCurrentItemPosition()) {
            case 0:
                lang = "en";
                tx.setText(R.string.language_title);
                but.setText(R.string.action_sign_in);
                break;
            case 1:
                lang = "ar";
                tx.setText(R.string.arabic_title);
                but.setText(R.string.arabic_sign_in);
                break;
            case 2:
                lang = "fr";
                tx.setText(R.string.french_title);
                but.setText(R.string.french_sign_in);
                break;
            case 3:
                lang = "ps";
                tx.setText(R.string.french_title);
                but.setText(R.string.french_sign_in);
                break;
            case 4:
                lang = "ur";
                tx.setText(R.string.urdu_title);
                but.setText(R.string.urdu_sign_in);
                break;
            case 5:
                lang = "it";
                tx.setText(R.string.italian_title);
                but.setText(R.string.italian_sign_in);
                break;
            case 6:
                lang = "ku";
                tx.setText(R.string.kurdish_title);
                but.setText(R.string.kurdish_sign_in);
                break;
            case 7:
                lang = "pt";
                tx.setText(R.string.portugese_title);
                but.setText(R.string.portugese_sign_in);
                break;
            case 8:
                lang = "de";
                tx.setText(R.string.german_title);
                but.setText(R.string.german_sign_in);
                break;
            case 9:
                lang = "sr";
                tx.setText(R.string.serbian_title);
                but.setText(R.string.serbian_sign_in);
                break;
            case 10:
                lang = "so";
                tx.setText(R.string.somali_title);
                but.setText(R.string.somali_sign_in);
                break;
            case 11:
                lang = "es";
                tx.setText(R.string.spanish_title);
                but.setText(R.string.spanish_sign_in);
                break;
            case 12:
                lang = "el";
                tx.setText(R.string.greek_title);
                but.setText(R.string.greek_sign_in);
                break;
            case 13:
                lang = "pa";
                tx.setText(R.string.pashto_title);
                but.setText(R.string.pashto_sign_in);
                break;
            case 14:
                lang = "tr";
                tx.setText(R.string.turkish_title);
                but.setText(R.string.turkish_title);
                break;
            case 15:
                lang = "ru";
                tx.setText(R.string.russian_title);
                but.setText(R.string.russian_sign_in);
                break;
            case 16:
                lang = "sq";
                tx.setText(R.string.albanian_title);
                but.setText(R.string.albanian_sign_in);
                break;
        }
    }

}
