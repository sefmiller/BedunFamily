package com.gb.globalfamily;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.gb.globalfamily.Model.AidWorker;
import com.gb.globalfamily.Model.DatabaseHelper;
import com.gb.globalfamily.Presenter.AidWorkerPresenter;

/**
 * Activity for recording user details and registering as an Aid Worker
 * */
public class Registration_Aid_Worker_Activity extends AppCompatActivity implements
        View.OnClickListener {
    private static final String TAG = "EmailPassword";
    private String loginId;
    private String password;
    private String lang;

    //sets activity language
    //protected void attachBaseContext(Context newBase) {
    //    SharedPreferences shared = getSharedPreferences("com.gb.globalfamily.preferences", MODE_PRIVATE);
    //   lang = shared.getString("lang", null);
    //super.attachBaseContext(LanguageContextWrapper.wrap(newBase, lang));
    //}

    private HashMap<Integer, ArrayList<String>> userTypeMap;
    private String username;
    private AidWorker aidworker;
    private AidWorkerPresenter aidWorkerPresenter;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;
    private String gender; private String nationality; private String sector; private String work_country; private String aidOrg;
    private TextView dobtx;
    private CountrySpinnerAdapter natAdapter; private CountrySpinnerAdapter workCountryAdapter;
    private TextView natSpinner; private TextView countrySpinner; private TextView genderSpinner;
    private TextView sectorSpinner; private TextView aidOrgSpinner;
    private DatabaseHelper dbHelper;
    private ArrayList<String> countriesNameArray;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        aidWorkerPresenter = new AidWorkerPresenter(this);

        CountriesFetcher.CountryList mCountries = CountriesFetcher.getCountries(this);
        countriesNameArray = new ArrayList<>();

        for (Country country: mCountries) {
            String countryName = country.getName();
            countriesNameArray.add(countryName);
        }
        nationality=""; gender=""; sector=""; aidOrg=""; work_country="";

        setContentView(R.layout.activity_registration__aid__worker);
        //set user input listeners
        findViewById(R.id.registration_button).setOnClickListener(this);
        findViewById(R.id.dob_aid_button).setOnClickListener(this);

        //passes over loginId, username, password  from previous activity.
        Bundle b = this.getIntent().getExtras();
        if (b != null) {
            loginId =(String) b.get("loginId");
            username =(String) b.get("name");
            password =(String) b.get("password");

        }

        //set title font
        TextView tx = findViewById(R.id.registrationTitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        tx.setTypeface(custom_font);


        //sets on click listeners
        countrySpinner = findViewById(R.id.work_country_spinner);
        countrySpinner.setOnClickListener(this);

        genderSpinner = findViewById(R.id.gender_aid_spinner);
        genderSpinner.setOnClickListener(this);

        natSpinner = findViewById(R.id.aid_nationality_spinner);
        natSpinner.setOnClickListener(this);

        aidOrgSpinner = findViewById(R.id.aid_org_spinner);
        aidOrgSpinner.setOnClickListener(this);

        sectorSpinner = findViewById(R.id.sector_spinner);
        sectorSpinner.setOnClickListener(this);



        dobtx = findViewById(R.id.dob_aid_worker);
        myCalendar = Calendar.getInstance();

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };

        //on user input. label is set to show what the textview should contain.
        // eg nationality label set to nationality, name set to name
        EditText name = findViewById(R.id.name);
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.name_label);
                    label.setText("");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.name_label);
                label.setText(R.string.name);
                label.setTextColor(getResources().getColor(R.color.TitleColor));


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.name_label);
                    label.setText(R.string.name);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));

                }
            }
        });
        //
        EditText position = findViewById(R.id.position);
        position.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.position_label);
                    label.setText("");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.position_label);
                label.setText(R.string.position_label);
                label.setTextColor(getResources().getColor(R.color.TitleColor));


            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.org_label);
                    label.setText(R.string.position_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));

                }
            }
        });
        //
        dobtx = findViewById(R.id.dob_aid_worker);
        dobtx.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.dob_label);
                    label.setText("");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.dob_label);
                label.setText(R.string.date_of_birth);
                label.setTextColor(getResources().getColor(R.color.TitleColor));

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.dob_label);
                    label.setText(R.string.date_of_birth);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }
        });
//
        EditText supervisor = findViewById(R.id.supervisor_text_view);
        supervisor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.supervisor_label);
                    label.setText("");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));

                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.supervisor_label);
                label.setText(R.string.supervisor_label);
                label.setTextColor(getResources().getColor(R.color.TitleColor));

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.supervisor_label);
                    label.setText(R.string.supervisor_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }
            }
        });
        getUserType();
    }


    /**
     * Performs different behaviour depending on which view is clicked
     * @param v View with onClicklListener
     */
    @Override
    public void onClick(View v) {
        //gets resource id of the View
        int i = v.getId();
        //on click of button to register, sends checkAidworkerDetails() to see if
        //user entered aid worker details are valid. if checkAidworkerDetails() passes
        //setAidWorkerDb message is sent to set an entry for the aidWorker in the backend DB
        if (i == R.id.registration_button) {
            if (checkAidworkerDetails()) {
                setAidWorkerDb();
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
            ArrayAdapter<CharSequence> genderAdapter = ArrayAdapter.createFromResource(this, R.array.gender_array, R.layout.list_item);
            genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listView.setAdapter(genderAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
// TODO Auto-generated method stub
                    gender = parent.getItemAtPosition(position).toString();
                    TextView label = findViewById(R.id.gender_label);
                    label.setText(R.string.gender_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                    TextView genText = findViewById(R.id.gender_aid_spinner);
                    genText.setText(gender);
                    cdd.dismiss();
                }
            });
        }
        //on click display CustomDialog with list items
        // CountrySpinnerAdapter populates each list entry with a country and the flag for that country
        //on click of list item, sets work country text field to list item value.
        // Work Country label is set to 'Country of Work' and dialog is exited.
        if (v == countrySpinner) {
            final CustomCountryDialog cdd= new CustomCountryDialog(this);
            //noinspection ConstantConditions
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cdd.show();
            Window view = cdd.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_tit);
            titleView.setText(R.string.work_country_label);
            ListView listView = view.findViewById(R.id.list_dialog);
            workCountryAdapter = new CountrySpinnerAdapter(this, countriesNameArray);
            workCountryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listView.setAdapter(workCountryAdapter);
            listView.setTextFilterEnabled(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
// TODO Auto-generated method stub
                    work_country = parent.getItemAtPosition(position).toString();
                    TextView label = findViewById(R.id.work_country_label);
                    label.setText(R.string.work_country_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                    TextView natText = findViewById(R.id.work_country_spinner);
                    natText.setText(work_country);

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
                    workCountryAdapter.getFilter().filter(newText);
                    return false;
                }
            });
        }
        //on click display CustomDialog with list items
        // natAdapter populates each list entry with a country and the flag for that country
        //on click of list item, sets nationality text field to list item value.
        // Nationality label is set to 'Nationality' and dialog is exited.
        if (v == natSpinner) {
            final CustomCountryDialog cdd= new CustomCountryDialog(this);
            //noinspection ConstantConditions
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cdd.show();
            Window view = cdd.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_tit);
            titleView.setText(R.string.nationality);
            ListView listView = view.findViewById(R.id.list_dialog);
            natAdapter = new CountrySpinnerAdapter(this, countriesNameArray);
            natAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listView.setAdapter(natAdapter);
            listView.setTextFilterEnabled(true);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
// TODO Auto-generated method stub
                    nationality = parent.getItemAtPosition(position).toString();
                    TextView label = findViewById(R.id.nationality_aid_label);
                    label.setText(R.string.nationality);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                    TextView natText = findViewById(R.id.aid_nationality_spinner);
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
        // ListView is populated from string array with Aid Organization options
        //on click list item sets Aid Org text field to list item value.
        // Aid Org label is set to 'Aid Organization' and dialog is exited.
        if (v == aidOrgSpinner) {
            final CustomDialog cdd= new CustomDialog(this);
            //noinspection ConstantConditions
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cdd.show();
            Window view = cdd.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_tit);
            titleView.setText(R.string.aidOrg);
            ListView listView = view.findViewById(R.id.list_dialog);
            ArrayAdapter genAdapter = ArrayAdapter.createFromResource(this, R.array.aid_org_array_array, R.layout.list_item);
            genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listView.setAdapter(genAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
// TODO Auto-generated method stub
                    aidOrg = parent.getItemAtPosition(position).toString();
                    TextView label = findViewById(R.id.org_label);
                    label.setText(R.string.aidOrg);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                    TextView ageText = findViewById(R.id.aid_org_spinner);
                    ageText.setText(aidOrg);

                    cdd.dismiss();
                }
            });            }
        if (v == sectorSpinner) {
            //on click display CustomDialog with list items
            // ListView is populated from string array with Sector options
            //on click list item sets Sector text field to list item value.
            // Aid Org label is set to 'Sector' and dialog is exited.
            final CustomDialog cdd= new CustomDialog(this);
            //noinspection ConstantConditions
            cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cdd.show();
            Window view = cdd.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_tit);
            titleView.setText(R.string.sector_label);
            ListView listView = view.findViewById(R.id.list_dialog);
            ArrayAdapter<CharSequence> sectorAdapter = ArrayAdapter.createFromResource(this, R.array.sector_array, R.layout.list_item);
            sectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            listView.setAdapter(sectorAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
// TODO Auto-generated method stub
                    sector = parent.getItemAtPosition(position).toString();
                    TextView label = findViewById(R.id.sector_label);
                    label.setText(R.string.sector_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                    TextView ageText = findViewById(R.id.sector_spinner);
                    ageText.setText(sector);

                    cdd.dismiss();
                }
            });            }
        //sets date of birth to user selected date in datePicker dialog
        if (i == R.id.dob_aid_button) {
            new DatePickerDialog(Registration_Aid_Worker_Activity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    }

    /**
     * @param loginName UserId
     * @param name Aid Worker Name
     * @param dob Date of Birth
     * @param gender Gender
     * @param nationality Nationality
     * @param workCountry Country of Work
     * @param aidOrg Aid Organization
     * @param sector Sector
     * @param position Position
     * @param supervisor Supervisor
     * creates Aid Worker object with aid worker attributes/state
     * set to method parameters
     */
    private void createAidWorker(String loginName, String name, String dob,
                                 String gender, String nationality, String workCountry,
                                 String aidOrg, String sector, String position, String supervisor){
        aidworker = new AidWorker(loginName, name, dob, gender,  nationality,
                workCountry, aidOrg, sector, position, supervisor);
        System.out.println(aidworker.toString());
    }

    /**
     * @return True if inputted aid Worker details are valid. False if any of the
     * text field user inputs are invalid.
     */
    //sets input from text fields to strings.
    // sets labels of AidWorkerPresenter class.
    // passes these strings to AidWorkerPresenter class to check if inputs are valid.
    // if successful, returns true and invokes createAidWorker() to create an AidWorker from the entered details
    // if unsuccessful, incorrect EditText input fields have labels  set to error identified by AidWorkerPresenter and
    //labels are set to red to denote an incorrect value.
    private boolean checkAidworkerDetails() {
        Boolean result = false;
        TextView aidWorkerView = findViewById(R.id.name);
        String aidWorkerName = aidWorkerView.getText().toString();
        TextView aidWorkerNameLabel = findViewById(R.id.name_label);

        TextView aidWorkerPositionView = findViewById(R.id.position);
        String aidWorkerPosition = aidWorkerPositionView.getText().toString();
        TextView aidWorkerPositionLabel = findViewById(R.id.position_label);

        TextView supervisorView = findViewById(R.id.supervisor_text_view);
        String supervisor = supervisorView.getText().toString();
        TextView supervisorLabel = findViewById(R.id.supervisor_label);

        TextView aidWorkerDobLabel = findViewById(R.id.dob_label);
        TextView aidWorkerNatLabel = findViewById(R.id.nationality_aid_label);
        TextView aidWorkerGenderLabel = findViewById(R.id.gender_label);
        TextView aidWorkerWorkCountryLabel = findViewById(R.id.work_country_label);
        TextView aidWorkerSectorLabel = findViewById(R.id.sector_label);
        TextView aidOrgLabel = findViewById(R.id.org_label);

        if (aidWorkerPresenter.init(aidOrg, nationality, aidWorkerName, myCalendar, sector,
        gender, aidWorkerPosition, supervisor, work_country)) {
            result = true;
            CognitoUser cognitoUser = AppHelper.getPool().getUser(username);
            String loginName = cognitoUser.getUserId();
            String dob = dobtx.getText().toString();
            createAidWorker(loginName, aidWorkerName,  dob, gender,
                    nationality, work_country, aidOrg, sector, aidWorkerPosition, supervisor);
        }
        else {
            aidOrgLabel.setTextColor(aidWorkerPresenter.getAidOrgLabelColor());
            aidOrgLabel.setText(aidWorkerPresenter.getAidOrgLabel());
            aidWorkerNameLabel.setTextColor(aidWorkerPresenter.getNameLabelColor());
            aidWorkerNameLabel.setText(aidWorkerPresenter.getNameLabel());
            aidWorkerNatLabel.setTextColor(aidWorkerPresenter.getNationalityLabelColor());
            aidWorkerNatLabel.setText(aidWorkerPresenter.getNationalityLabel());
            aidWorkerDobLabel.setTextColor(aidWorkerPresenter.getDateOfBirthLabelColor());
            aidWorkerDobLabel.setText(aidWorkerPresenter.getDateOfBirthLabel());
            aidWorkerSectorLabel.setTextColor(aidWorkerPresenter.getSectorLabelColor());
            aidWorkerSectorLabel.setText(aidWorkerPresenter.getSectorLabel());
            aidWorkerWorkCountryLabel.setTextColor(aidWorkerPresenter.getWorkCountryLabelColor());
            aidWorkerWorkCountryLabel.setText(aidWorkerPresenter.getWorkCountryLabel());
            aidWorkerPositionLabel.setTextColor(aidWorkerPresenter.getPositionLabelColor());
            aidWorkerPositionLabel.setText(aidWorkerPresenter.getPositionLabel());
            supervisorLabel.setTextColor(aidWorkerPresenter.getSupervisorLabelColor());
            supervisorLabel.setText(aidWorkerPresenter.getSupervisorLabel());
            aidWorkerGenderLabel.setTextColor(aidWorkerPresenter.getGenderLabelColor());
            aidWorkerGenderLabel.setText(aidWorkerPresenter.getGenderLabel());
        }
        return result;
    }


    /**
     * on date being selected from datepicker, sets date of birth text field to display
     * the user selected calender date.
     */
    private void updateLabel() {

        String myFormat = "dd/MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        dobtx.setText(sdf.format(myCalendar.getTime()));
    }

    /**
     * signs user in. this is invoked so current device is marked in backend and user stays logged in on each app interaction
     * on the device
     * login is handled by Amazon authenticationhandler in background
     */
    private void signInUser() {

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
           // Log.e(TAG, "Auth Success");
            Toast.makeText(getApplicationContext(), aidworker.getName() + " "  + " Added.", Toast.LENGTH_LONG).show();
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(device);
            Intent userActivity = new Intent(Registration_Aid_Worker_Activity.this, MainActivity.class);
            startActivityForResult(userActivity, 4);
        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }

        @Override
        public void onFailure(Exception e) {
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
     * called in onCreate.
     * task forwards to database helper method getUserType. passes in url which points to the php script to get the current user type
     * from the backend database.
     * post execution and the db helper method being invoked, the UserTypeMap is set to the UserTypeMap recorded in the database.
     */
    private void getUserType() {
        class GetUserTy extends AsyncTask<Object, Object, Object> {

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
                System.out.println(userTypeMap.toString() + "test");
            }
        }
        GetUserTy gut = new GetUserTy();
        gut.execute();
    }

    /**
     * Asynchronous task.
     *  task forwards to database helper method setAidWorkerDb. passes in url which points to php script to add the user Aid Worker details
     * to the backend database.
     * post execution and the db helper method being invoked, signInUser is sent to sign in the current user and device.
     */
    private void setAidWorkerDb(){
        class setAidWorkerDb extends AsyncTask<Object, Object, Object> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Object... params) {
                dbHelper.setAidWorkerDb(aidworker);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                signInUser();
            }
        }
        setAidWorkerDb saw = new setAidWorkerDb();
        saw.execute();
    }
}
