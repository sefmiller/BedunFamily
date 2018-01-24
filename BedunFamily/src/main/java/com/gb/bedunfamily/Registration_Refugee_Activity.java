package com.gb.bedunfamily;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;

import com.gb.bedunfamily.Model.DatabaseHelper;
import com.gb.bedunfamily.Model.Refugee;
import com.gb.bedunfamily.Presenter.RefugeePresenter;


/**
 * Activity for recording user details and registering as a Refugee
 * */
public class Registration_Refugee_Activity extends AppCompatActivity implements
        View.OnClickListener {

    private HashMap<Integer, ArrayList<String>> userTypeMap;

    private static final String TAG = "EmailPassword";
    private String loginId;
    private String password;
    private Button but;

    private RefugeePresenter refugeePresenter;
    private Refugee refugee;
    private DatabaseHelper dbHelper;
    private String username;
    private String age; private String nationality; private String gender;
    private TextView ageSpinner; private TextView countrySpinner; private TextView genderSpinner;
    private ArrayList<String> countriesNameArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        refugeePresenter = new RefugeePresenter(this);

        CountriesFetcher.CountryList mCountries = CountriesFetcher.getCountries(this);
        countriesNameArray = new ArrayList<>();

        for (Country country: mCountries) {
            String countryName = country.getName();
            countriesNameArray.add(countryName);
        }
        nationality = ""; age = ""; gender = "";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_refugee);
        //set user input listeners
        findViewById(R.id.registration_button).setOnClickListener(this);

        //passes in loginId, username, password  from previous activity
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            loginId =(String) b.get("loginId");
            username =(String) b.get("name");
            password =(String) b.get("password");
        }
        //System.out.println(password + "c");
        //System.out.println(username + "c");

        //sets listeners for user clicks
        countrySpinner = findViewById(R.id.nationality_spinner);
        countrySpinner.setOnClickListener(this);

        genderSpinner = findViewById(R.id.gender_spinner);
        genderSpinner.setOnClickListener(this);

        ageSpinner = findViewById(R.id.age_spinner);
        ageSpinner.setOnClickListener(this);

        but = findViewById(R.id.registration_button);
        but.setOnClickListener(this);

        //set title font
        TextView tx = findViewById(R.id.registrationTitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        tx.setTypeface(custom_font);

        //on user input labels show the input the user should be entering. eg nationality or name
        //
        TextView nameView = (EditText) findViewById(R.id.name_ref);
        nameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.name_label);
                    label.setText("");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.name_label);
                label.setText(R.string.full_name);
                label.setTextColor(getResources().getColor(R.color.TitleColor));

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.name_label);
                    label.setText(R.string.full_name);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }
        });
        //

        TextView pobView = (EditText) findViewById(R.id.place_of_birth_text);
        pobView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.city_label);
                    label.setText("");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.city_label);
                label.setText(R.string.place_of_birth);
                label.setTextColor(getResources().getColor(R.color.TitleColor));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.city_label);
                    label.setText(R.string.place_of_birth);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }
        });
        //
        TextView nicknameView = (EditText) findViewById(R.id.nickname_text);
        nicknameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.nickname_label);
                    label.setText("");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.nickname_label);
                label.setText(R.string.nickname);
                label.setTextColor(getResources().getColor(R.color.TitleColor));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.nickname_label);
                    label.setText(R.string.nickname);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }
        });
        //
        TextView tribeView = (EditText) findViewById(R.id.tribe_text);
        tribeView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.tribe_label);
                    label.setText("");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.tribe_label);
                label.setText(R.string.tribe_label);
                label.setTextColor(getResources().getColor(R.color.TitleColor));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.tribe_label);
                    label.setText(R.string.tribe_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }
        });
        //
        TextView localAreaView = (EditText) findViewById(R.id.loca_area_text);
        localAreaView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.village_label);
                    label.setText("");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.village_label);
                label.setText(R.string.local_area_label);
                label.setTextColor(getResources().getColor(R.color.TitleColor));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.village_label);
                    label.setText(R.string.local_area_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }
        });
        //
        TextView occupationView = (EditText) findViewById(R.id.occupation_text_view);
        occupationView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.occupation_label);
                    label.setText("");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.occupation_label);
                label.setText(R.string.occupation_label);
                label.setTextColor(getResources().getColor(R.color.TitleColor));
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.occupation_label);
                    label.setText(R.string.occupation_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }
        });
        //get user type on creation of activity
        getUserType();
    }

    /**
     * Asynchronous task.
     * called in onCreate.
     * task forwards to database helper method getUserType. passes in url which points to the php script to get the current user type
     * from the backend database.
     * post execution and the db helper method being invoked, the UserTypeMap is set to the UserTypeMap recorded in the database.
     */    private void getUserType() {
        class GetUserLog extends AsyncTask<Object, Object, Object> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Object... params) {
                dbHelper = new DatabaseHelper(loginId, username);
                dbHelper.getUserType();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                userTypeMap = dbHelper.getUserTypeMap();
                System.out.println(userTypeMap.toString());
            }
        }
        GetUserLog gul = new GetUserLog();
        gul.execute();
    }

    /**
     * Performs different behaviour depending on which view is clicked
     * @param v View with onClicklListener
     */
    @Override
    public void onClick(View v) {
        if (v == but) {
            //checks if details entered in text fields are acceptable values.
            // if so calls setRefugeeDb() to add refugee details to database table.
            //see checkRefugeeDetails() method header
            if (checkRefugeeDetails()) {
                setRefugeeDb();
            }
        }
        //on click display CustomDialog with list items
        // ListView is populated from string string array with gender options
        //on click list item sets gender text field to list item value.
        // Gender label is set to 'Gender' and dialog is exited.
        if (v == genderSpinner) {
            final CustomDialog cdd = new CustomDialog(this);
            //noinspection ConstantConditions
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cdd.show();
            Window view = cdd.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_tit);
            titleView.setText(R.string.gender_label);
            ListView listView = view.findViewById(R.id.list_dialog);
            ArrayAdapter genAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, R.layout.list_item);
            genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listView.setAdapter(genAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
// TODO Auto-generated method stub
                    gender = parent.getItemAtPosition(position).toString();
                    TextView label = findViewById(R.id.gender_label);
                    label.setText(R.string.gender_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                    TextView genText = findViewById(R.id.gender_spinner);
                    genText.setText(gender);
                    cdd.dismiss();
                }
            });
        }
        //on click display CustomDialog with list items
        // natAdapter populates each list entry with a country and the flag for that country
        //on click of list item, sets nationality text field to list item value.
        // Nationality label is set to 'Nationality' and dialog is exited.
        if (v == countrySpinner) {
            final CustomCountryDialog cdd= new CustomCountryDialog(this);
            //noinspection ConstantConditions
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cdd.show();
            Window view = cdd.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_tit);
            titleView.setText(R.string.nationality);
            ListView listView = view.findViewById(R.id.list_dialog);
            final CountrySpinnerAdapter natAdapter = new CountrySpinnerAdapter(this, countriesNameArray);
            natAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listView.setAdapter(natAdapter);
            listView.setTextFilterEnabled(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
// TODO Auto-generated method stub
                    nationality = parent.getItemAtPosition(position).toString();
                    TextView label = findViewById(R.id.nationality_label);
                    label.setText(R.string.nationality);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                    TextView natText = findViewById(R.id.nationality_spinner);
                    natText.setText(nationality);
                    cdd.dismiss();
                }
            });
            SearchView searchView = view.findViewById(R.id.search_view_country);
            searchView.setIconifiedByDefault(false);
            searchView.setSubmitButtonEnabled(false);
            searchView.setQueryHint("Search Here");
            ImageView icon = searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
            ImageView icon2 = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
            icon.setColorFilter(Color.WHITE);
            icon2.setColorFilter(Color.RED);
            searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener(){

                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    natAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
        //on click display CustomDialog with list items
        // ListView is populated from string string array with age options
        //on click list item sets age text field to list item value.
        // Age label is set to 'Age' and dialog is exited.
        if (v == ageSpinner) {
            final CustomDialog cdd= new CustomDialog(this);
            //noinspection ConstantConditions
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cdd.show();
            Window view = cdd.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_tit);
            titleView.setText(R.string.age_label);
            ListView listView = view.findViewById(R.id.list_dialog);
            ArrayAdapter genAdapter = ArrayAdapter.createFromResource(this, R.array.age_group_array, R.layout.list_item);
            genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listView.setAdapter(genAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
// TODO Auto-generated method stub
                    age = parent.getItemAtPosition(position).toString();
                    TextView label = findViewById(R.id.age_label);
                    label.setText(R.string.age_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                    TextView ageText = findViewById(R.id.age_spinner);
                    ageText.setText(age);

                    cdd.dismiss();
                }
            });            }
    }

    /**
     * @param loginName UserId
     * @param name Refugee Name
     * @param age Age Range
     * @param nationality Nationality
     * @param nickname Nickname
     * @param placeOfBirth PlaceOfBirth
     * @param tribe Tribe
     * @param gender Gender
     * @param localArea Local Area
     * @param occupation Occupation
     * creates Refugee object with Refugee attributes/state set to method parameters
     */
    private void createRefugee(String loginName, String name, String age,
                                 String nationality, String nickname, String placeOfBirth,
                               String tribe, String gender, String localArea, String occupation){
       refugee = new Refugee(loginName, name, age, nationality,
               nickname, placeOfBirth, tribe, gender, localArea, occupation);
        System.out.println(refugee);
    }

    /**
     * @return True if inputted Refugee details are valid. False if any of the
     * text field user inputs are invalid.
     */
    //sets input from text fields to strings.
    // sets labels of RefugeePresenter class.
    // passes these strings to RefugeePresenter class to check if inputs are valid.
    // if successful, returns true and invokes createRefugee() to create a Refugee from the entered details
    // if unsuccessful, incorrect EditText input fields have labels  set to error identified by RefugeePresenter and
    //labels are set to red to denote an incorrect value.
    private boolean checkRefugeeDetails(){

        Boolean result = false;
        TextView refugeeNameView = findViewById(R.id.name_ref);
        TextView refugeeNameLabel = findViewById(R.id.name_label);
        String refugeeName = refugeeNameView.getText().toString();


        TextView refugeeTribeView = findViewById(R.id.tribe_text);
        String refugeeTribe = refugeeTribeView.getText().toString();
        TextView refugeeTribeLabel = findViewById(R.id.tribe_label);

        TextView refugeePobView = findViewById(R.id.place_of_birth_text);
        String refugeePob = refugeePobView.getText().toString();
        TextView refugeePobLabel = findViewById(R.id.city_label);

        TextView refugeeNicknameView = findViewById(R.id.nickname_text);
        String refugeeNickname = refugeeNicknameView.getText().toString();
        TextView refugeeNicknameLabel = findViewById(R.id.nickname_label);

        TextView refugeeLocalView = findViewById(R.id.loca_area_text);
        String refugeeLocal = refugeeLocalView.getText().toString();
        TextView refugeeLocalLabel = findViewById(R.id.village_label);

        TextView refugeeOccupationView = findViewById(R.id.occupation_text_view);
        String refugeeOccupation = refugeeOccupationView.getText().toString();
        TextView refugeeOccupationLabel = findViewById(R.id.occupation_label);

        TextView refugeeAgeLabel = findViewById(R.id.age_label);
        TextView refugeeNatLabel = findViewById(R.id.nationality_label);
        TextView refugeeGenderLabel = findViewById(R.id.gender_label);


        if (refugeePresenter.init(refugeePob, nationality, refugeeName,
                age, refugeeNickname,  refugeeTribe, gender, refugeeLocal, refugeeOccupation)){
            result = true;
            CognitoUser cognitoUser = AppHelper.getPool().getUser(username);
            String loginName = cognitoUser.getUserId();
            createRefugee(loginName, refugeeName, age, nationality,
                    refugeeNickname, refugeePob, refugeeTribe, gender, refugeeLocal, refugeeOccupation);

        }

        else {
            refugeeNicknameLabel.setTextColor(refugeePresenter.getNickNameLabelColor()); refugeeNicknameLabel.setText(refugeePresenter.getNickNameLabel());
            refugeeNameLabel.setTextColor(refugeePresenter.getNameLabelColor()); refugeeNameLabel.setText(refugeePresenter.getNameLabel());
            refugeeNatLabel.setTextColor(refugeePresenter.getNationalityLabelColor()); refugeeNatLabel.setText(refugeePresenter.getNationalityLabel());
            refugeePobLabel.setTextColor(refugeePresenter.getPlaceOfBirthLabelColor()); refugeePobLabel.setText(refugeePresenter.getPlaceOfBirthLabel());
            refugeeTribeLabel.setTextColor(refugeePresenter.getTribeLabelColor()); refugeeTribeLabel.setText(refugeePresenter.getTribeLabel());
            refugeeAgeLabel.setTextColor(refugeePresenter.getAgeLabelColor()); refugeeAgeLabel.setText(refugeePresenter.getAgeLabel());
            refugeeLocalLabel.setTextColor(refugeePresenter.getLocalAreaLabelColor()); refugeeLocalLabel.setText(refugeePresenter.getLocalAreaLabel());
            refugeeOccupationLabel.setTextColor(refugeePresenter.getOccupationLabelColor()); refugeeOccupationLabel.setText(refugeePresenter.getOccupationLabel());
            refugeeGenderLabel.setTextColor(refugeePresenter.getGenderLabelColor()); refugeeGenderLabel.setText(refugeePresenter.getGenderLabel());


        }
        return result;
    }

    /**
     * signs user in. this is invoked so current device is marked in backend and user stays logged in on each app interaction
     * on the device
     * login is handled by Amazon authenticationhandler in background
     */
    private void signInUser() {
        AppHelper.init(getApplicationContext());
        AppHelper.setUser(username);
        AppHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);
    }

    //on success display Toast message showing refugee (name) added
    //starts mainactivity
    private final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            // do nothing
        }

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            //Log.e(TAG, "Auth Success");
            Toast.makeText(getApplicationContext(), refugee.getName() + " "  + " Added.", Toast.LENGTH_LONG).show();

            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(device);
            //Intent userActivity = new Intent(Registration_Refugee_Activity.this, main_checklist_refugee .class);
            Intent userActivity = new Intent(Registration_Refugee_Activity.this, MainActivity.class);
            startActivityForResult(userActivity, 4);
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void onFailure(Exception e) {
            System.out.println(username);
            System.out.println(e);
            System.out.println("Sign-in failed");

        }
        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {

        }

    };
    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {

        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }

    /**
     * Asynchronous task.
     *  task forwards to database helper method setRefugeeDb. passes in url which points to php script to add the user Refugee details
     * to the backend database.
     * post execution and the db helper method being invoked, signInUser is sent to sign in the current user and device.
     */
    private void setRefugeeDb(){
        class setRefugeeDb extends AsyncTask<Object, Object, Object> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Object... params) {
                dbHelper.setRefugeeDb("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/add_refugee_details.php", refugee);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                signInUser();
            }
        }
        setRefugeeDb srd = new setRefugeeDb();
        srd.execute();
    }
}
