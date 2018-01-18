package com.gb.globalfamily;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.gb.globalfamily.Model.DatabaseHelper;

/**
 * Activity for Choosing User Type. Either Refugee or Aid Worker
 * */
public class Choose_User_Type_Activity extends AppCompatActivity implements
        View.OnClickListener {
    private String username;
    private String loginId;
    private static DatabaseHelper dbHelper;
    private String password;
    private String lang;
//sets activity language
    //protected void attachBaseContext(Context newBase) {
    //    SharedPreferences shared = getSharedPreferences("com.gb.globalfamily.preferences", MODE_PRIVATE);
    //   lang = shared.getString("lang", null);
    //super.attachBaseContext(LanguageContextWrapper.wrap(newBase, lang));
    //}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_user_type);
        //set title font
        TextView tx = findViewById(R.id.user_type_title);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        tx.setTypeface(custom_font);

        //gets username, password passed from previous activity
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
            username =(String) b.get("name");
            password =(String) b.get("password");

        }

        //set input listeners
        findViewById(R.id.org_button).setOnClickListener(this);
        findViewById(R.id.register_button).setOnClickListener(this);

        //gets the user login details from the database table for the current user on creation of activity
        getUserLogin();

    }

    /**
     * Performs different behaviour depending on which view is clicked
     * @param v View with onClicklListener
     */
    @Override
    public void onClick(View v) {
        //gets resource id of the View
        int i = v.getId();
        //on click of button to register as Aid Worker, sends setAidWorkerType()
        // to set an entry for the UserType as  'AidWorker' in the backend DB
        if (i == R.id.org_button) {
            //sets the user type to 'AidWorker' in the database table for the current user.
            setAidWorkerType();
        }
        //on click of button to register as a Refugee, sends setRefugeeType()
        // to set an entry for the UserType as  'Refugee' in the backend DB
        if (i == R.id.register_button) {
            //sets the user type to 'Refugee' in the database table for the current user.
            setRefugeeType();
        }

    }

    /**
     * starts Registration_Refugee_Activity. Passes over username,
     * loginId, password
     */
    private void registerSignUp() {
        loginId = dbHelper.getLoginId();
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putString("name", username);
        b.putString("loginId", loginId);
        b.putString("password", password);
        i.putExtras(b);
        i.setClass(this, Registration_Refugee_Activity.class);
        startActivityForResult(i, 10);
    }

    /**
     * starts Registration_Aid_Worker_Activity. passes over username,
     * loginId, password
     */
    private void orgSignUp() {
        loginId = dbHelper.getLoginId();
        Intent i = new Intent();
        Bundle b = new Bundle();
        b.putString("name", username);
        b.putString("loginId", loginId);
        b.putString("password", password);
        i.putExtras(b);
        i.setClass(this, Registration_Aid_Worker_Activity.class);
        startActivityForResult(i, 10);

    }

    /**
     * Asynchronous task.
     * called in onCreate.
     * task creates new database object with constructor dbHelper = new DatabaseHelper(username);
     * constructor sets the username in dbHelper to the current username/phone number
     * task forwards to database helper method getUserLogin. passes in url which points to the php script to get the current login details
     * from the backend database.
     */
    private void getUserLogin() {
        class GetUserLog extends AsyncTask<Object, Object, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Object... params) {
                dbHelper = new DatabaseHelper(username);
                dbHelper.getUserLogin();
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                super.onPostExecute(o);

            }
        }
        GetUserLog gul = new GetUserLog();
        gul.execute();
    }

    /**
     * Asynchronous task.
     * task forwards to database helper method setRefugeeType. passes in url which points to php script to add the Refugee user type
     * to the backend database.
     * post execution and the db helper method being invoked, registerSignUp() message is sent to move to next activity.
     */
    private void setRefugeeType(){
        class SetLoginDb extends AsyncTask<Object, Object, Object> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Object... params) {
                dbHelper.setRefugeeType();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                registerSignUp();
            }
        }
        SetLoginDb sld = new SetLoginDb();
        sld.execute();
    }

    /**
     * Asynchronous task.
     * task forwards to database helper method setAidWorkerType. passes in url which points to php script to add the AidWorker user type.
     * to the backend database.
     * post execution and the db helper method being invoked,  orgSignUp() message is sent to move to next activity.
     */     private void setAidWorkerType(){
        class SetLoginDb extends AsyncTask<Object, Object, Object> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Object... params) {
                dbHelper.setAidWorkerType();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                orgSignUp();
            }
        }
        SetLoginDb sld = new SetLoginDb();
        sld.execute();
    }
}
