package com.gb.bedunfamily;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aigestudio.wheelpicker.WheelPicker;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.gb.bedunfamily.Model.AidWorker;
import com.gb.bedunfamily.Model.DatabaseHelper;
import com.gb.bedunfamily.Model.Refugee;
import com.gb.bedunfamily.Model.Voicemail;
import com.gb.bedunfamily.Presenter.AidWorkerPresenter;
import com.gb.bedunfamily.Presenter.RefugeePresenter;
import com.jakewharton.threetenabp.AndroidThreeTen;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Main Activity User lands here after logging in to the application or completing registration.
 * A side navigation allows easy navigation t different screens. Rather then coding each screen as a seperate activity
 * each screen is represented by a fragment nested inside MainActivity. This means the navigation drawer and layout of the screen
 * does not need to be re-coded on each screen. Simply, the fragment manager loads a new fragment into the content frame which
 * represents the screen space below the toolbar, holding the navigation drawer.
 * */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String contactOrgType;
    private static ProgressDialog dialog;
    static DatabaseHelper dbHelper;
    private TextView nav_miss;
    private static CognitoUserSession sesh;
    static CountriesFetcher.CountryList mCountries;
    private TextView mTitle;
    private static Menu menu;
    private static ArrayList<String> countriesNameArray;
    private static String currentUserType;
    private static String lang;
    private TextView nav_user;
    private static AmazonS3Client sS3Client;
    private static CognitoCachingCredentialsProvider sCredProvider;
    private static TransferUtility sTransferUtility;

    //getters and setters
    public TextView getmTitle() {
        return mTitle;
    }
    public String getContactOrgType() {
        return contactOrgType;
    }
    private void setContactOrgType(String contactOrgType) {
        this.contactOrgType = contactOrgType;
    }
    public TextView getNav_user() {
        return nav_user;
    }
    public TextView getNav_miss() {
        return nav_miss;
    }

    //sets activity language
    //protected void attachBaseContext(Context newBase) {
    //    SharedPreferences shared = getSharedPreferences("com.gb.bedunfamily.preferences", MODE_PRIVATE);
    //   lang = shared.getString("lang", null);
    //super.attachBaseContext(LanguageContextWrapper.wrap(newBase, lang));
    //}

    /**
     * @param savedInstanceState activity's previously saved state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        //initiates the custom backport date/time picker
        AndroidThreeTen.init(this);

        //set toolbar title font
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        Toolbar toolbar = findViewById(R.id.toolbar);
        mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setTypeface(custom_font);
        setSupportActionBar(toolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //initiates navigation menu
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //applys custom font to navigation drawer
        NavigationView navView = findViewById(R.id.nvView);
        menu = navView.getMenu();
        //sets custom font for nav drawer menu items
        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);

            applyFontToMenuItem(mi);
        }

        //sets on item selected listener for clicking on nav menu items.
        NavigationMenuView navigationMenuView = (NavigationMenuView) navView.getChildAt(0);
        navigationMenuView.setScrollBarSize((int) getResources().getDimension(R.dimen._20sdp));
        navigationMenuView.setScrollbarFadingEnabled(false);
        View hView = navView.getHeaderView(0);
        nav_user = hView.findViewById(R.id.nav_header_title);
        nav_miss = hView.findViewById(R.id.missing_person_title);
        navView.setNavigationItemSelectedListener(this);

        //initialises all countries (dial code, name, locale) and sets array of country names.
        mCountries = CountriesFetcher.getCountries(this);
        countriesNameArray = new ArrayList<>();
        for (Country country: mCountries) {
           String countryName = country.getName();
            countriesNameArray.add(countryName);
        }
        //current session for device.
        sesh = AppHelper.getCurrSession();

        //gets all user data from backend
        getUserTable(this);
    }

    /**
     * Asynchronous task.
     * task creates new support fragment and replaces screen content frame with the fragment.
     */
    public void loadFragment(final Fragment fragment) {
                Fragment frag = fragment;
                setMenu();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.content_frame, frag)
                        .commitAllowingStateLoss();
    }



    /**
     * Gets an instance of a S3 CredProvider which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    private static CognitoCachingCredentialsProvider getCredProvider(Context context) {
// Initialize the Amazon Cognito credentials provider
        if (sCredProvider == null) {
            sCredProvider = new CognitoCachingCredentialsProvider(
                    context.getApplicationContext(),
                    "us-east-1:7c0124c7-fd49-4600-8d2b-10682118ca3f", // Identity pool ID
                    Regions.US_EAST_1 // Region
            );        // Set up as a credentials provider.
            Map<String, String> logins = new HashMap<>();
            String test = "cognito-idp.us-east-1.amazonaws.com/" + AppHelper.getUserPoolId();
            System.out.println(test);
            logins.put(test, sesh.getIdToken().getJWTToken());
            sCredProvider.setLogins(logins);
        }
        return sCredProvider;
    }
    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    private static AmazonS3Client getS3Client(Context context) {
        if (sS3Client == null) {
            sS3Client = new AmazonS3Client(getCredProvider(context.getApplicationContext()));
            //sS3Client.setRegion(Region.getRegion(Regions.US_EAST_1));
        }
        return sS3Client;
    }

    /**
     * Gets an instance of the TransferUtility which is constructed using the
     * given Context
     *
     * @param context Context
     * @return a TransferUtility instance
     */
    private static TransferUtility getTransferUtility(Context context) {
        if (sTransferUtility == null) {
            sTransferUtility = new TransferUtility(getS3Client(context.getApplicationContext()),
                    context.getApplicationContext());
        }

        return sTransferUtility;
    }


    /**
     * Sets the font of the menu item parameter to a custom font. used to prettify nav drawer
     * @param mi menu item
     */
    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan(font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }

    /**
     * @param item Menu item
     * @return true if item selected
     * switch statement checks resource id of menuItem parameter (item), ListItemClicked(i) message is then sent. The value of
     * i is determined by which item is selected.
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.item_home:
                ListItemClicked(0);
                break;
            case R.id.fam_tree_menu:
                ListItemClicked(1);
                break;
            case R.id.lost_fam_menu:
                ListItemClicked(2);
                break;
            case R.id.contact_org_match:
                ListItemClicked(3);
                break;
            case R.id.item_fam_search:
                ListItemClicked(4);
                break;
            case R.id.item_voice_inbox:
                ListItemClicked(5);
                break;
            case R.id.item_voice_listen:
                //GetSentVoicemailsDb();
                ListItemClicked(6);
                break;
            case R.id.item_personal:
                ListItemClicked(7);
                break;
            case R.id.item_login:
                ListItemClicked(8);
                break;
            case R.id.item_update_fam_details:
                ListItemClicked(9);
                break;
            case R.id.item_select_refugee:
                ListItemClicked(10);
                break;
            case R.id.item_add_refugee:
                ListItemClicked(11);
                break;
            case R.id.item_update_refugee_details:
                ListItemClicked(12);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * A support fragment is initialised to null. switch statement checks value of parameter position.
     * fragment is set to a different fragment (e.g. HomeFragment, SelectFamFragment) depending on which list item was clicked.
     * The support fragment manager replaces the content frame with the fragment.
     * @param position position of list item selected
     */
    private void ListItemClicked(int position) {
        android.support.v4.app.Fragment fragment = null;
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                mTitle.setText(R.string.home);
                break;
            case 1:
                fragment = new SelectFamFragment();
                mTitle.setText(R.string.select_fam);
                break;
            case 2:
                fragment = new AddFamilyInsFragment();
                mTitle.setText(R.string.add_family_member_title);
                break;
            case 3:
                fragment = new ContactOrgMenuFragment();
                mTitle.setText(R.string.contact_organisation);
                break;
            case 4:
                fragment = new SearchInsFragment();
                mTitle.setText(R.string.search_fam);
                break;
            case 5:
                fragment = new VoiceInboxFragment();
                mTitle.setText(R.string.inbox);
                break;
            case 6:
                fragment = new VoiceSentboxFragment();
                mTitle.setText(R.string.sentbox);
                break;
            case 7:
                if(currentUserType.equals("Refugee")) {
                    fragment = new PersonalDetailsFragment();
                }
                else{
                    fragment = new PersonalDetailsAidWorkerFragment();
                }
                mTitle.setText(R.string.personal_details);
                break;
            case 8:
                fragment = new SettingsFragment();
                mTitle.setText("Settings");
                break;
            case 9:
                fragment = new UpdateFamMemberFragment();
                mTitle.setText(R.string.update_fam_member_details);
                break;
            case 10:
                fragment = new SelectRefugeeFragment();
                mTitle.setText(R.string.select_ref);
                break;
            case 11:
                fragment = new AddRefInsFragment();
                mTitle.setText(R.string.add_ref);
                break;
            case 12:
                fragment = new UpdateRefugeeDetailsFragment();
                mTitle.setText(R.string.update_ref_details);
                break;
            case 13:
                fragment = new ContactOrganizationNoMatchInsFragment();
                mTitle.setText(R.string.contact_organisation);
                break;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    /**
     * Asynchronous task.
     * called in onCreate. used to retrieve all user data on login
     * task creates new database object with constructor dbHelper = new DatabaseHelper(username);
     * task forwards to database helper method getTables. passes in url which points to the php script to get the user data
     * from the backend database.
     */
    private void getUserTable(final MainActivity mainActivity) {
        class GetUserTy extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                dbHelper = new DatabaseHelper(mainActivity);
                dbHelper.getTables();
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
            }
        }
        GetUserTy gut = new GetUserTy();
        gut.execute();
    }

    /**
     * Asynchronous task.
     * post execution of the getTables task in the DatabaseHelper, this method is run to intialise the
     * task retrieves currentUserType from the database helper class.
     * getTransferUtility gives the user credentials on their current session to access amazon s3 services
     * post execution of the task, setMenu() is called
     */
    public void init() {
        class Init extends AsyncTask<Void, Void, Void> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                currentUserType = dbHelper.getCurrentUserType();
                getTransferUtility(getApplicationContext());
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                HomeFragment frag = new HomeFragment();
                loadFragment(frag);
            }
        }
        Init i = new Init();
        i.execute();
    }

    /**
     * sets visibility of the menu options in the navigation drawer.
     * called every time a fragment is loaded into the main content frame. This is so the menu always displays the correct
     * menu items, depending on the state of the user (e.g are they an aidworker or a refugee?
     * Have they registered a refugee or missing family member?)
     */
    private void setMenu() {
        while(dbHelper.getCurrentUserType() == null){
            // error check if not loaded tables after 10 sec.
        }
        if (!dbHelper.getCurrentUserType().equals("Refugee")) {
            menu.findItem(R.id.current_ref_item).setVisible(true);
            menu.findItem(R.id.item_add_refugee).setVisible(true);
            if (dbHelper.getCurrentRefugee() != null) {
                menu.findItem(R.id.item_select_refugee).setVisible(true);
                menu.findItem(R.id.item_update_refugee_details).setVisible(true);
                nav_user.setText(dbHelper.getCurrentRefugee().getName());
            }
        }
        else{
            System.out.println(dbHelper.getCurrentRefugee() + "oy");
            nav_user.setText(dbHelper.getCurrentRefugee().getName());
        }
        if (dbHelper.getCurrentRefugee() != null){
            menu.findItem(R.id.fam_menu_item).setVisible(true);
            menu.findItem(R.id.voice_menu_item).setVisible(true);
        }
        if (!dbHelper.getMissingFamLinkedToCurrentRef().isEmpty()) {
            menu.findItem(R.id.item_update_fam_details).setVisible(true);
            menu.findItem(R.id.fam_tree_menu).setVisible(true);
            menu.findItem(R.id.search_item).setVisible(true);
            menu.findItem(R.id.contact_org).setVisible(true);
            nav_miss.setText(getString(R.string.miss_title) + " " + dbHelper.getCurrentFamMember().getName());
            }
        else{
            menu.findItem(R.id.item_update_fam_details).setVisible(false);
            menu.findItem(R.id.fam_tree_menu).setVisible(false);
            menu.findItem(R.id.search_item).setVisible(false);
            menu.findItem(R.id.contact_org).setVisible(false);
            nav_miss.setText("");
        }
    }

    /**
     * called in SearchInsFragment.  After database has refugees, which match the missing family member template,
     * If no search results are returned, NoMatchFragment is loaded into the content frame.
     * If the search produces results, SearchFragment is loaded into the main content frame.
     */
    public void loadFrag() {

        class LoadFragment extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected String doInBackground(String... params) {
                if (dbHelper.getSearchResults().isEmpty()){
                    android.support.v4.app.Fragment frag = new MainActivity.NoMatchFragment();
                    loadFragment(frag);
                }
                else {
                    android.support.v4.app.Fragment frag = new MainActivity.SearchFragment();
                    loadFragment(frag);
                }
                dialog.dismiss();
                return null;
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }
        LoadFragment lf = new LoadFragment();
        lf.execute();
    }

    /**
     * Fragment for Home screen/landing page after logging in. Displays grid of clickable items. Which icons are viewable
     * depends on whether the user is an Aidworker or Refugee and the current state of the user.
     *
     */
    public static class HomeFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        ImageView viewOneImg;
        ImageView viewTwoImg;
        ImageView viewThreeImg;
        ImageView viewFiveImg;
        ImageView viewFourImg;
        ImageView viewSixImg;
        private TextView viewOneTv;
        private TextView viewTwoTv;
        private TextView viewFourTv;
        private TextView viewThreeTv;
        private TextView viewFiveTv;
        private TextView viewSixTv;
        private String currentUserType;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            //inflates xml layout. sets toolbar title to 'Home'
            View view = inflater.inflate(R.layout.fragment_home, viewGroup, false);
            MainActivity anActivity = (MainActivity) getActivity();
            assert anActivity != null;
            anActivity.getmTitle().setText(R.string.home);

            currentUserType = dbHelper.getCurrentUserType();
            viewOneImg = view.findViewById(R.id.ukimg1);
            viewTwoImg = view.findViewById(R.id.ukimg2);
            viewThreeImg = view.findViewById(R.id.ukimg3);
            viewFourImg = view.findViewById(R.id.ukimg4);
            viewFiveImg = view.findViewById(R.id.ukimg5);
            viewSixImg = view.findViewById(R.id.ukimg6);

            viewOneImg.setOnClickListener(this);

            viewOneTv = view.findViewById(R.id.uktext1);
            viewTwoTv = view.findViewById(R.id.uktext2);
            viewThreeTv = view.findViewById(R.id.uktext3);
            viewFourTv = view.findViewById(R.id.uktext4);
            viewFiveTv = view.findViewById(R.id.uktext5);
            viewSixTv = view.findViewById(R.id.uktext6);

            //sets font for icon text
            Typeface titleFont = Typeface.
                    createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            viewOneTv.setTypeface(titleFont);
            viewTwoTv.setTypeface(titleFont);
            viewThreeTv.setTypeface(titleFont);
            viewFourTv.setTypeface(titleFont);
            viewFiveTv.setTypeface(titleFont);
            viewSixTv.setTypeface(titleFont);

            //sets icons to display
            setHomeOptions();
            return view;
        }


        /**
         * Checks if user is refugee or aidworker and loads appropriate method
         */
        private void setHomeOptions() {
            if (currentUserType.equals("Refugee")) {
                setHomeOptionsRefugee();
                System.out.println(1);
            } else {
                setHomeOptionsAidWorker();
                System.out.println(2);
            }
        }

        /**
         * sets Text of home screen icons.
         * sets visibility of the icons and text, depending on the state of the user (e.g
         * Have they registered a refugee or have they registered a missing family member?)
         */
        private void setHomeOptionsAidWorker() {
            viewOneImg.setImageResource(R.drawable.addfambutton);
            viewOneTv.setText(R.string.add_ref);
            viewTwoTv.setText(R.string.select_ref);
            viewThreeTv.setText(R.string.add_family_member_title);
            viewFourTv.setText(R.string.select_fam);
            viewFiveTv.setText(R.string.search);
            viewSixTv.setText(R.string.inbox);

            if(dbHelper.getCurrentRefugee() != null) {
                viewTwoImg.setImageResource(R.drawable.selectfam);
                viewTwoTv.setVisibility(View.VISIBLE);
                viewTwoImg.setOnClickListener(this);
                viewThreeImg.setImageResource(R.drawable.addfambutton);
                viewThreeTv.setVisibility(View.VISIBLE);
                viewThreeImg.setOnClickListener(this);
                viewSixImg.setImageResource(R.drawable.inboxbutton);
                viewSixTv.setVisibility(View.VISIBLE);
                viewSixImg.setOnClickListener(this);
            }
            if (!dbHelper.getMissingFamLinkedToCurrentRef().isEmpty()) {
                viewFourTv.setVisibility(View.VISIBLE);
                viewFiveTv.setVisibility(View.VISIBLE);
                viewFourImg.setImageResource(R.drawable.selectfam);
                viewFiveImg.setImageResource(R.drawable.searchbut);

                viewFourImg.setOnClickListener(this);
                viewFiveImg.setOnClickListener(this);

            }

        }

        /**
         * sets Text of home screen icons.
         * sets visibility of the icons and text, depending on the state of the user (e.g
         * Have they registered a refugee or have they registered a missing family member?)
         */
        private void setHomeOptionsRefugee() {
            viewOneImg.setImageResource(R.drawable.addfambutton);
            viewOneTv.setText(R.string.add_family_member_title);
            viewTwoTv.setText(R.string.select_fam);
            viewThreeTv.setText(R.string.search_fam);
            viewFourTv.setText(R.string.contact_organisation);
            viewFiveTv.setText(R.string.personal_details);
            viewSixTv.setText(R.string.inbox);

            viewSixImg.setImageResource(R.drawable.inboxbutton);
            viewSixTv.setVisibility(View.VISIBLE);
            viewSixImg.setOnClickListener(this);

            if (dbHelper.getCurrentFamMember() != null) {
                viewTwoTv.setVisibility(View.VISIBLE);
                viewThreeTv.setVisibility(View.VISIBLE);
                viewFourTv.setVisibility(View.VISIBLE);
                viewFiveTv.setVisibility(View.VISIBLE);
                viewTwoImg.setImageResource(R.drawable.selectfam);
                viewThreeImg.setImageResource(R.drawable.searchbut);
                viewFourImg.setImageResource(R.drawable.contactbut);
                viewFiveImg.setImageResource(R.drawable.personalbutton);

                viewTwoImg.setOnClickListener(this);
                viewThreeImg.setOnClickListener(this);
                viewFourImg.setOnClickListener(this);
                viewFiveImg.setOnClickListener(this);

            }

        }

        /**
         * @param v View which is clicked and responded to onClicklistener
         *          Loads the fragment which relates to the icon and text. E.g if user clicks 'Add Refugee' grid option,
         *          AddRefInsFragment() is loaded into the content frame.
         *          OnClick options differ depending on whether the user is a refugee or aidworker.
         */
        @Override
        public void onClick(View v) {
            MainActivity anActivity = (MainActivity) getActivity();
            if (currentUserType.equals("Refugee")) {
                if (v == viewOneImg) {
                    android.support.v4.app.Fragment frag = new AddFamilyInsFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
                if (v == viewTwoImg) {
                    android.support.v4.app.Fragment frag = new SelectFamFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
                if (v == viewThreeImg) {
                    android.support.v4.app.Fragment frag = new SearchInsFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
                if (v == viewFourImg) {
                    android.support.v4.app.Fragment frag = new ContactOrgMenuFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
                if (v == viewFiveImg) {
                    android.support.v4.app.Fragment frag;
                    frag = new PersonalDetailsFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
                if (v == viewSixImg) {
                    android.support.v4.app.Fragment frag = new VoiceInboxFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
            }
            else{
                if (v == viewOneImg) {
                    android.support.v4.app.Fragment frag = new AddRefInsFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
                if (v == viewTwoImg) {
                    android.support.v4.app.Fragment frag = new SelectRefugeeFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);

                }
                if (v == viewThreeImg) {
                    android.support.v4.app.Fragment frag = new AddFamilyInsFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
                if (v == viewFourImg) {
                    android.support.v4.app.Fragment frag = new SelectFamFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
                if (v == viewFiveImg) {
                    android.support.v4.app.Fragment frag;
                    frag = new SearchInsFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
                if (v == viewSixImg) {
                    android.support.v4.app.Fragment frag = new VoiceInboxFragment();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
            }
        }
    }


    /**
     * Simple fragment. loads text box with description of instruction for adding a family member and a button to proceed
     */
    public static class AddFamilyInsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        Button but;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_add_fam_ins_fragment, viewGroup, false);

            MainActivity anActivity = (MainActivity) getActivity();
            assert anActivity != null;
            anActivity.getmTitle().setText(R.string.add_family_member_title);

            but = view.findViewById(R.id.proceed_add_fam_button);
            but.setOnClickListener(this);
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }

            TextView tv = view.findViewById(R.id.ins_add_fam);
            Typeface titleFont = Typeface.
                    createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);
            return view;
        }

        /**
         * @param v button view
         *          on click of button load fragment to add a missing family member
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                android.support.v4.app.Fragment frag = new AddFamilyFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                assert anActivity != null;
                anActivity.loadFragment(frag);            }

        }
    }

    /**
     * Simple fragment. loads text box with description of instruction for adding a refugee and a button to proceed
     */
    public static class AddRefInsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        Button but;


        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_add_ref_ins_fragment, viewGroup, false);
            MainActivity anActivity = (MainActivity) getActivity();
            assert anActivity != null;
            anActivity.getmTitle().setText(R.string.add_ref);

            but = view.findViewById(R.id.proceed_add_ref_button);
            but.setOnClickListener(this);
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }

            TextView tv = view.findViewById(R.id.ins_add_ref);
            Typeface titleFont = Typeface.
                    createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);

            return view;
        }

        /**
         * @param v button view
         *          loads fragment to register a refugee into the content frame
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                android.support.v4.app.Fragment frag = new AddRefFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                assert anActivity != null;
                anActivity.loadFragment(frag);
            }

        }
    }

    /**
     * Simple fragment. loads text box with description of instruction for recording a voicemail and a button to proceed
     */
    public static class RecordVoiceInsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        Button but;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_voice_ins, viewGroup, false);
            MainActivity anActivity = (MainActivity) getActivity();
            assert anActivity != null;
            anActivity.getmTitle().setText(R.string.record_voicemail);
            but = view.findViewById(R.id.proceed_connect_fam_button);
            but.setOnClickListener(this);
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }

            TextView tv = view.findViewById(R.id.ins_connect_fam);
            Typeface titleFont = Typeface.
                    createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);

            return view;
        }

        /**
         * @param v Button View
         *          on Click loads fragment to record voice message
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                android.support.v4.app.Fragment frag = new RecordVoiceFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                assert anActivity != null;
                anActivity.loadFragment(frag);
            }

        }
    }

    /**
     * Fragment for recording voice messages, uploading to backend s3 storage platform and registering voice message between two
     * seperate refugees in the database.
     */
    public static class RecordVoiceFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        Button but;
        MediaRecorder myAudioRecorder;
        ImageView imageBut;
        String AudioSavePathInDevice = null;
        final String audioFileName = dbHelper.getLoginId();
        public static final int RequestPermissionCode = 1;
        int recordButCode = 0;
        final MainActivity activity = (MainActivity) getActivity();
        private MediaPlayer mediaPlayer;
        Chronometer myChronometer;
        private Button buttonRecord;

        private long finalTime;
        private String s3Key;
        private int reply;


        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_voice, viewGroup, false);

            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }

            MainActivity anActivity = (MainActivity) getActivity();
            assert anActivity != null;
            anActivity.getmTitle().setText(R.string.record_voicemail);
            //transferListener = new TransferListener();

            //like a stopwatch. keeps track of time elapsed
            myChronometer = view.findViewById(R.id.autoCompleteTextView2);
            but = view.findViewById(R.id.button9);
            but.setOnClickListener(this);

            buttonRecord = view.findViewById(R.id.button8);
            buttonRecord.setOnClickListener(this);


            imageBut = view.findViewById(R.id.imageButton2);

            imageBut.setOnClickListener(new View.OnClickListener() {
                /**
                 * @param view imageBut View
                 */
                @Override
                public void onClick(View view) {
                    switch(recordButCode) {
                        //always initialised to 0 when fragment is created. on click of record button, audio and timer are started and
                        //recordButCode is set to 1
                        case (0):
                            //checks to see if Android permissions are granted to store voicemail and record audio
                        if (checkPermission()) {
                                //set local storage path on device.
                            AudioSavePathInDevice =
                                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                            audioFileName + ".3gp";
                            //prepares audio recorder
                            MediaRecorderReady();
                            try {
                                myAudioRecorder.prepare();
                                //stream type and volume
                                ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 40);
                                //tone type and duration. ms
                                toneG.startTone(ToneGenerator.TONE_PROP_ACK, 200);
                                myAudioRecorder.start();
                                //start time
                                myChronometer.setBase(SystemClock.elapsedRealtime());
                                myChronometer.start();
                                String uri = "@drawable/stop";
                                MainActivity anActivity = (MainActivity) getActivity();
                                assert anActivity != null;
                                int imageResource = getResources().getIdentifier(uri, null, anActivity.getPackageName());
                                Drawable res = getResources().getDrawable(imageResource);
                                imageBut.setImageDrawable(res);
                                recordButCode = 1;
                                Toast.makeText(anActivity, "Recording started",
                                        Toast.LENGTH_SHORT).show();
                            } catch (IllegalStateException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        } else {
                                //if permission not granted request permission
                            requestPermission();
                        }
                            break;
                            //on click, audio and timer are stopped, button is changed to play icon and recordButCode is set to 2;
                        case (1):
                            myAudioRecorder.stop();
                            myChronometer.stop();
                            long time = (myChronometer.getBase() - SystemClock.elapsedRealtime());
                            finalTime = Math.abs(time);
                            System.out.println(finalTime);
                            String uri = "@drawable/play";
                            MainActivity anActivity = (MainActivity) getActivity();
                            assert anActivity != null;
                            int imageResource = getResources().getIdentifier(uri, null, anActivity.getPackageName());
                            Drawable res = getResources().getDrawable(imageResource);
                            imageBut.setImageDrawable(res);
                            recordButCode = 2;
                            //stop media, set image resource to play, set recordbutcode to 2, make buttons appear
                            Toast.makeText(anActivity, "Recording Completed",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        //on click, media player is initialised. Media plays plays the audio, rather then recording it.
                        // button is changed to stop icon and recordButCode is set to 3;
                        case (2):
                            uri = "@drawable/stop";
                            myChronometer.setBase(SystemClock.elapsedRealtime());
                            MainActivity anotherActivity = (MainActivity) getActivity();
                            assert anotherActivity != null;
                            imageResource = getResources().getIdentifier(uri, null, anotherActivity.getPackageName());
                            res = getResources().getDrawable(imageResource);
                            imageBut.setImageDrawable(res);
                            //sets media player to play voicemail file stored in filepath reference by 'AudioSavePathInDevice'
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(AudioSavePathInDevice);
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            myChronometer.start();
                            mediaPlayer.start();
                            //called when the audio is finished, e.g duration of recording is reached
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    String uri = "@drawable/play";
                                    myChronometer.stop();
                                    //sets timer to display total time of voice message
                                    myChronometer.setBase(SystemClock.elapsedRealtime() - finalTime);
                                    MainActivity yetAnotherActivity = (MainActivity) getActivity();
                                    assert yetAnotherActivity != null;
                                    int imageResource = getResources().getIdentifier(uri, null, yetAnotherActivity.getPackageName());
                                    Drawable res = getResources().getDrawable(imageResource);
                                    imageBut.setImageDrawable(res);
                                    recordButCode = 2;
                                }
                            });

                            Toast.makeText(anotherActivity, "Recording Playing",
                                    Toast.LENGTH_SHORT).show();
                            //play media, set image resource to stop, set recordbutcode to 1
                            recordButCode = 3;
                            //makes buttons to re-record / send voicemail visible on screen
                            buttonRecord.setVisibility(View.VISIBLE);
                            but.setVisibility(View.VISIBLE);
                            break;
                        //stops media player playing recording and sets button code to 2 again.
                        case(3):
                            uri = "@drawable/play";
                            myChronometer.stop();
                            myChronometer.setBase(SystemClock.elapsedRealtime() - finalTime);
                            MainActivity yetAnotherActivity = (MainActivity) getActivity();
                            assert yetAnotherActivity != null;
                            imageResource = getResources().getIdentifier(uri, null, yetAnotherActivity.getPackageName());
                            res = getResources().getDrawable(imageResource);
                            imageBut.setImageDrawable(res);
                            if(mediaPlayer != null){
                                Toast.makeText(yetAnotherActivity, "Recording stopped",
                                        Toast.LENGTH_SHORT).show();
                            }
                            assert mediaPlayer != null;
                            mediaPlayer.stop();
                                mediaPlayer.release();
                            recordButCode = 2;
                            break;
                    }
                }
            });
            //check if voicemail is to be sent as a reply to a previous voicemail or a brand new one.
            checkRecievedVoicemails();
            return view;
        }

        /**
         * Async task. database helper class getRecievedVoicemailsForRef() called to see if any
         * previous voicemails have been sent by the current refugee.
         * post execution, checkForReply() is called to check if the recorded voicemail should be marked
         * as a reply to a previous voicemail
         */
        private void checkRecievedVoicemails() {
            class checkVoicemailDb extends AsyncTask<Object, Object, Object> {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.getRecievedVoicemailsForRef();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    checkForReply();
                }
            }
            checkVoicemailDb cvd = new checkVoicemailDb();
            cvd.execute();
        }

        /**
         * Async task. If the refugeeId of the refugee who is recieving the voicemail is present as a key in
         * dbHelper.getCurrentVoicemailMap(), this indicates a voicemail has already been recieived from the refugee,
         * and the current voicemail is to be marked as a reply (1)
         * if the refugee has not recieived any voicemails or the refugee id is not present as a key in dbHelper.getCurrentVoicemailMap()
         * reply is set to 0
         */
        private void checkForReply() {
            class CheckForReply extends AsyncTask<Object, Object, Object> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    int key = Integer.parseInt(dbHelper.getSearchableRefugee().getRefugeeId());
                    System.out.println(key);
                    if (dbHelper.getCurrentVoicemailMap().isEmpty() || !dbHelper.getCurrentVoicemailMap().containsKey(key)) {
                        reply = 0;
                    } else {
                        reply = 1;
                    }
                    System.out.println(reply);
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                }
            }
                CheckForReply cfr = new CheckForReply();
                cfr.execute();
        }

        /**
         * @return true if permission is granted to write audio file on the device && record audio. false if
         * permissions not granted
         */
        public boolean checkPermission() {
            int result = ContextCompat.checkSelfPermission(getContext(),
                    WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getContext(),
                    RECORD_AUDIO);
            return result == PackageManager.PERMISSION_GRANTED &&
                    result1 == PackageManager.PERMISSION_GRANTED;
        }

        /**
         * request permission to write audio file on the device && record audio. displays toast message indicating success or failure.
         */
        private void requestPermission() {
            ActivityCompat.requestPermissions(getActivity(), new
                    String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
        }
        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               @NonNull String permissions[], @NonNull int[] grantResults) {
            switch (requestCode) {
                case RequestPermissionCode:
                    if (grantResults.length> 0) {
                        boolean StoragePermission = grantResults[0] ==
                                PackageManager.PERMISSION_GRANTED;
                        boolean RecordPermission = grantResults[1] ==
                                PackageManager.PERMISSION_GRANTED;

                        if (StoragePermission && RecordPermission) {
                            Toast.makeText(getActivity(), "Permission Granted",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(),"Permission Denied",Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }

        /**
         * prepares audio recorder. sets encoder, audio source (microphone), output file type (3gp), output location and max duration (30 secs)
         */
        public void MediaRecorderReady(){
            //initialise audio recording
            myAudioRecorder=new MediaRecorder();
            //set the source , output and encoding format and output file of audio recording.
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            myAudioRecorder.setOutputFile(AudioSavePathInDevice);
            myAudioRecorder.setMaxDuration(30000);
            myAudioRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    //called when the audio recording reaches max duration of 30 seconds
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        myAudioRecorder.stop();
                        myChronometer.stop();
                        finalTime = myChronometer.getBase();
//stop countdown timer, change button to play etc
                        String uri = "@drawable/play";
                        MainActivity anActivity = (MainActivity) getActivity();
                        assert anActivity != null;
                        int imageResource = getResources().getIdentifier(uri, null, anActivity.getPackageName());
                        Drawable res = getResources().getDrawable(imageResource);
                        imageBut.setImageDrawable(res);
                        recordButCode = 2;
                        //stop media, set image resource to play, set recordbutcode to 2, make buttons appear
                        Toast.makeText(anActivity, "Recording Completed",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        });
        }

        /**
         * @param v View clicked.
         */
        @Override
        public void onClick(View v) {
            //If button to record audio is clicked dialog box is displayed with progress of the upload to amazon s3 denoted by the
            // % of the progress bar.
            if (v == but) {
                    final CustomProgressDialog dg  = new CustomProgressDialog(getActivity());
                    dg.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                dg.show();
                    final Window view = dg.getWindow();
                    TextView titleView = view.findViewById(R.id.dialog_progress);
                    titleView.setText(R.string.upload + " " + getResources().getString(R.string.percent));

                File file = new File(AudioSavePathInDevice);
                System.out.println(file.getAbsolutePath());
                //sets key of the audio recording, which is what the file will be named in the storage bucket
                //and what must be referenced to retrieve it from the s3 storage bucket.
                s3Key = dbHelper.getRefId() + "to" + dbHelper.getSearchableRefugee().getRefugeeId();
                TransferObserver transferObserver = sTransferUtility.upload("bedunfamily", s3Key, file);
                System.out.println(transferObserver.getState());
                //listens to progress of upload
                transferObserver.setTransferListener(new TransferListener() {

                    /**
                     * @param state state of upload. if completed. ok button is made visible and setVoicemailDb message is sent
                     *              to record voicemail in the database.
                     */
                    @Override
                    public void onStateChanged(int id, TransferState state) {
                        if (state.equals(TransferState.COMPLETED)) {
                            Button but = view.findViewById(R.id.proceed_voice);
                            but.setVisibility(View.VISIBLE);
                            but.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dg.dismiss();
                                    setVoiceMailDb();
                                }
                            });
                        }
                    }

                    /**
                     * @param bytesCurrent bytes uploaded
                     * @param bytesTotal Total bytes / file size of audio file to be uploaded
                     *                   sets int percentage to percentage progress ( current bytes uploaded / total bytes size * 100_
                     *                   sets progress bar to display current percentage progress of the upload
                     */
                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        int percentage = (int) (bytesCurrent/bytesTotal * 100);
                        TextView titleView = view.findViewById(R.id.dialog_progress);
                        titleView.setText(getString(R.string.upload) + percentage + getString(R.string.percent));
                        ProgressBar pb = view.findViewById(R.id.upload_bar);
                        pb.setProgress(percentage);

                    }

                    /**
                     * @param ex
                     * on error, prints error code to console and notifies user of error in the UI
                     */
                    @Override
                    public void onError(int id, Exception ex) {
                        System.out.println(ex);
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

                    }
                });

            }
            //if user clicks button record. timer is reset and recordButCode is set to 0. This restarts the process to record
            //an audio message, if the user is dissatisfied with the content/quality of the current recording.

            if (v == buttonRecord) {
                String uri = "@drawable/voice";
                MainActivity anActivity = (MainActivity) getActivity();
                assert anActivity != null;
                int imageResource = getResources().getIdentifier(uri, null, anActivity.getPackageName());
                Drawable res = getResources().getDrawable(imageResource);
                imageBut.setImageDrawable(res);
                buttonRecord.setVisibility(View.INVISIBLE);
                but.setVisibility(View.INVISIBLE);
                recordButCode = 0;
                myChronometer.stop();
                myChronometer.setBase(SystemClock.elapsedRealtime());
                //stop media, set image resource to play, set recordbutcode to 2, make buttons appear
                Toast.makeText(anActivity, "Recording Deleted",
                        Toast.LENGTH_SHORT).show();
            }
            }


        /**
         * async task. dbHelper.setVoicemailDb is called to register the voicemail in the database.
         * on post execution of the task, if reply = 0, setOngoingMatch is called, else if reply is 1,
         * setOneWayMatch() is called.
         */
        private void setVoiceMailDb() {
            class setVoicemailDb extends AsyncTask<Object, Object, Object> {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.setVoicemailDb(s3Key, finalTime, reply);
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    if (reply == 0) {
                        setOngoingMatch();
                    }
                    else {
                        setOneWayMatch();
                    }
                }
            }
            setVoicemailDb svm = new setVoicemailDb();
            svm.execute();
        }

        /**
         * async task. dbHelper.ongoingMatchDb() is called to register that a new voice exchange has commenced between two refugees
         * which is currently ongoing
         * on post execution of the method in the DatabaseHelper class, the fragment VoiceFinalFragment is loaded.
         */
        private void setOngoingMatch() {
            class OngoingMatchDb extends AsyncTask<Object, Object, Object> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.ongoingMatchDb();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                                 }

            }
            OngoingMatchDb omd = new OngoingMatchDb();
            omd.execute();
        }

        /**
         * async task. dbHelper.setOneWayMatchDb(); is called to register that the current refugee has accepted the
         * original voice message. This sets the link between the current refugee, in the direction of the other refugee, to 'Matched'.
         * for the voice exchange to be completed, the other refugee who sent the initial voicemail, must then accept the voicemail they recieve back.
         * on post execution of the method in the DatabaseHelper class, the fragment VoiceFinalFragment is loaded.
         */
        private void setOneWayMatch() {
            class OneWayMatchDb extends AsyncTask<Object, Object, Object> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.setOneWayMatchDb();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                }

            }
            OneWayMatchDb owmd = new OneWayMatchDb();
            owmd.execute();
        }

    }

    /**
     * Simple fragment. displays button to return to home screen and description text box indicating that the voicemail was sent
     * and the user should wait for a reply.
     */
    public static class VoiceFinalFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        Button but;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_voice_final, viewGroup, false);
            but = view.findViewById(R.id.voice_final_button);
            but.setOnClickListener(this);
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            return view;
        }

        /**
         * @param v View of the button.
         *          loads Home Fragment into the content frame.
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                android.support.v4.app.Fragment frag = new HomeFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                assert anActivity != null;
                anActivity.loadFragment(frag);              }

        }
    }

    /**
     * Fragment is there for downloading and listening to a sent voicemail
     */
    public static class SentVoicemailFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        Button but;
        ImageView imageBut;
        String AudioSavePathInDevice = null;
        String audioFileName;
        public static final int RequestPermissionCode = 1;
        int playButCode = 0;
        private MediaPlayer mediaPlayer;
        Chronometer aChronometer;

        private long finalTime;
        private String s3Key;
        private Button buttonNoMatch;
        private String refId;
        private Voicemail vm;


        /**
         * @param inflater inflates xml layout
         * @param viewGroup view container
         * @param savedInstanceState previous state of the fragment
         * @return  View
         * sets voicemail to the voicemail in the voicemail map referenced by the key refId, where refId is the refugee id of the voicemail
         * receiver
         * handles behaviour of button.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            Bundle extras = getArguments();
            this.refId = extras.getString("refId");
            View view = inflater.inflate(R.layout.fragment_voicemail_sent, viewGroup, false);
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            System.out.println("No whey" + refId);
            System.out.println("whey" + dbHelper.getCurrentVoicemailMap());
            dbHelper.setCurrentVoicemail(dbHelper.getCurrentVoicemailMap().get(Integer.parseInt(refId)));
            vm = dbHelper.getCurrentVoicemail();
            System.out.println(vm.toString());
            aChronometer = view.findViewById(R.id.autoCompleteTextView2);
            finalTime = Long.parseLong(vm.getLength());
            System.out.println(finalTime);
            aChronometer.setBase(SystemClock.elapsedRealtime() - finalTime);
            //aChronometer.setBase(finalTime);
            but = view.findViewById(R.id.button9);
            but.setOnClickListener(this);
            audioFileName = vm.getStorageKey();

            imageBut = view.findViewById(R.id.imageButton2);
            imageBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (playButCode) {
                        //if permissions granted, displays custom progress dialog for tracking progress of
                        // downloading the voicemail from amazon s3.
                        case (0):
                            if (checkPermission()) {
                                System.out.println("click working");

                                AudioSavePathInDevice =
                                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                                audioFileName + ".3gp";
                                final CustomProgressDialog dg = new CustomProgressDialog(getActivity());
                                dg.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                                dg.show();
                                final Window winView = dg.getWindow();
                                TextView titleView = winView.findViewById(R.id.dialog_progress);
                                titleView.setText(getString(R.string.download) + getString(R.string.percent));
                                File file = new File(AudioSavePathInDevice);
                                System.out.println(file.getAbsolutePath());
                                s3Key = vm.getStorageKey();
                                System.out.println(s3Key);
                                TransferObserver transferObserver = sTransferUtility.download("bedunfamily", s3Key, file);
                                System.out.println(transferObserver.getState());
                                transferObserver.setTransferListener(new TransferListener() {

                                    /**
                                     * @param state state of download. if completed. ok button is made visible and playButCode is set to 1;
                                     */
                                    @Override
                                    public void onStateChanged(int id, TransferState state) {
                                        if (state.equals(TransferState.COMPLETED)) {
                                            ProgressBar pb = winView.findViewById(R.id.upload_bar);
                                            pb.setProgress(100);
                                            TextView titleView = winView.findViewById(R.id.dialog_progress);
                                            titleView.setText(getString(R.string.download) + 100 + getString(R.string.percent));
                                            Button but = winView.findViewById(R.id.proceed_voice);
                                            String uri = "@drawable/play";
                                            MainActivity anActivity = (MainActivity) getActivity();
                                            int imageResource = getResources().getIdentifier(uri, null, anActivity.getPackageName());
                                            Drawable res = getResources().getDrawable(imageResource);
                                            imageBut.setImageDrawable(res);
                                            playButCode = 1;
                                            Toast.makeText(anActivity, "Voice Message Downloaded",
                                                    Toast.LENGTH_SHORT).show();
                                            but.setVisibility(View.VISIBLE);
                                            but.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dg.dismiss();
                                                }
                                            });

                                        }
                                    }
                                    /**
                                     * @param bytesCurrent bytes downloaded
                                     * @param bytesTotal Total bytes / file size of audio file to be downloaded
                                     *                   sets int percentage to percentage progress ( current bytes uploaded / total bytes size * 100_
                                     *                   sets progress bar to display current percentage progress of the download
                                     */
                                    @Override
                                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                                        try {
                                            int percentage = (int) (bytesCurrent / bytesTotal * 100);
                                            TextView titleView = winView.findViewById(R.id.dialog_progress);
                                            titleView.setText(getString(R.string.download) + percentage + getString(R.string.percent));
                                            ProgressBar pb = winView.findViewById(R.id.upload_bar);
                                            pb.setProgress(percentage);
                                        } catch (Exception e) {
                                            System.out.println(e);
                                        }

                                    }
                                    /**
                                     * @param ex
                                     * on error, prints error code to console and notifies user of error in the UI
                                     */
                                    @Override
                                    public void onError(int id, Exception ex) {
                                        // do something
                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } else {
                                //if permission not granted, asks for permission
                                requestPermission();
                            }
                            break;
                        //on click, media player is initialised. Media plays plays the audio, rather then recording it.
                        // button is changed to stop icon and playButCode is set to 2;
                        case (1):
                            aChronometer.setBase(SystemClock.elapsedRealtime());
                            String uri = "@drawable/stop";
                            MainActivity anActivity = (MainActivity) getActivity();
                            int imageResource = getResources().getIdentifier(uri, null, anActivity.getPackageName());
                            Drawable res = getResources().getDrawable(imageResource);
                            imageBut.setImageDrawable(res);
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(AudioSavePathInDevice);
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            aChronometer.start();
                            mediaPlayer.start();
                            //if button is not clicked and instead the audio recording finishes (e.g the whole message is listened to).
                            // the oncompletionlistener is called, icson is set to play again and the button to return to home screen
                            //is made visible
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    String uri = "@drawable/play";
                                    aChronometer.stop();
                                    aChronometer.setBase(SystemClock.elapsedRealtime() - finalTime);
                                    MainActivity yetAnotherActivity = (MainActivity) getActivity();
                                    int imageResource = getResources().getIdentifier(uri, null, yetAnotherActivity.getPackageName());
                                    Drawable res = getResources().getDrawable(imageResource);
                                    imageBut.setImageDrawable(res);
                                    Toast.makeText(yetAnotherActivity, "Voice Message Stopped",
                                            Toast.LENGTH_SHORT).show();
                                    //play media, set image resource to stop, set recordbutcode to 1
                                    playButCode = 1;
                                    but.setVisibility(View.VISIBLE);
                                }
                            });
                            playButCode = 2;
                            Toast.makeText(anActivity, "Recording Started",
                                    Toast.LENGTH_SHORT).show();
                            break;
                        //on click, audio recording is stopped.
                        // button is changed to play icon and playButCode is set to 2;
                        //button to return to main menu (home screen) is made visible on the screen.
                        case (2):
                            uri = "@drawable/play";
                            aChronometer.stop();
                            mediaPlayer.stop();
                            aChronometer.setBase(SystemClock.elapsedRealtime() - finalTime);
                            //aChronometer.setBase(finalTime);
                            MainActivity anotherActivity = (MainActivity) getActivity();
                            imageResource = getResources().getIdentifier(uri, null, anotherActivity.getPackageName());
                            res = getResources().getDrawable(imageResource);
                            imageBut.setImageDrawable(res);

                            Toast.makeText(anotherActivity, "Voice Message Stopped",
                                    Toast.LENGTH_SHORT).show();
                            //play media, set image resource to stop, set recordbutcode to 1
                            playButCode = 1;
                            but.setVisibility(View.VISIBLE);
                            break;
                    }
                }
            });
            return view;
        }

        /**
        * @return true if permission is granted to write audio file on the device && record audio. false if
         * permissions not granted
         */
        public boolean checkPermission() {
            int result = ContextCompat.checkSelfPermission(getContext(),
                    WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getContext(),
                    READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED &&
                    result1 == PackageManager.PERMISSION_GRANTED;
        }

        /**
         * request permission to write audio file on the device && record audio.
         * displays toast message indicating success or failure.
         */
        private void requestPermission() {
            ActivityCompat.requestPermissions(getActivity(), new
                    String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, RequestPermissionCode);
        }

        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            switch (requestCode) {
                case RequestPermissionCode:
                    if (grantResults.length > 0) {
                        boolean StoragePermission = grantResults[0] ==
                                PackageManager.PERMISSION_GRANTED;
                        boolean RecordPermission = grantResults[1] ==
                                PackageManager.PERMISSION_GRANTED;

                        if (StoragePermission && RecordPermission) {
                            Toast.makeText(getActivity(), "Permission Granted",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }

        /**
         * @param v Button View
         *          on click load Homw Fragment into cotnent frame
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                android.support.v4.app.Fragment frag = new HomeFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                anActivity.loadFragment(frag);
            }
        }
    }


    /**
     * Fragment is there for downloading and listening to a recieved voicemail and deciding if it
     * is a match or not.
     */
    public static class RecievedVoicemailFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        Button butMatch;
        ImageView imageBut;
        String AudioSavePathInDevice = null;
        String audioFileName;
        public static final int RequestPermissionCode = 1;
        int playButCode = 0;
        private MediaPlayer mediaPlayer;
        Chronometer aChronometer;
        private long finalTime;
        private String s3Key;
        private Button buttonNoMatch;
        private String refId;
        private Voicemail vm;
        private Fragment frag;
        private ConfirmDialog cad;
        private ConfirmDialog cfd;

        /**
         * @param inflater inflates xml layout
         * @param viewGroup view container
         * @param savedInstanceState previous state of the fragment
         * @return  View
         * sets voicemail to the voicemail in the voicemail map referenced by the key refId, where refId is the refugee id of the voicemail
         * sender.
         * handles behaviour of button.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            Bundle extras = getArguments();
            this.refId = extras.getString("refId");
            View view = inflater.inflate(R.layout.fragment_voicemail, viewGroup, false);
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            System.out.println("No whey" + refId);
            System.out.println("whey" + dbHelper.getCurrentVoicemailMap());
            dbHelper.setCurrentVoicemail(dbHelper.getCurrentVoicemailMap().get(Integer.parseInt(refId)));
            vm = dbHelper.getCurrentVoicemail();
            System.out.println(vm.toString());
            aChronometer = view.findViewById(R.id.autoCompleteTextView2);
            finalTime= Long.parseLong(vm.getLength());
            System.out.println(finalTime);
            aChronometer.setBase(SystemClock.elapsedRealtime() - finalTime);
            //aChronometer.setBase(finalTime);
            butMatch = view.findViewById(R.id.button9);
            butMatch.setOnClickListener(this);
            buttonNoMatch = view.findViewById(R.id.voice_no_match_button);
            buttonNoMatch.setOnClickListener(this);
            audioFileName = vm.getStorageKey();

            imageBut = view.findViewById(R.id.imageButton2);
            imageBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch(playButCode) {
                        //if permissions granted, displays custom progress dialog for tracking progress of
                        // downloading the voicemail from amazon s3.
                        case (0):
                            if (checkPermission()) {
                                System.out.println("click working");
                                AudioSavePathInDevice =
                                        Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                                audioFileName + ".3gp";
                                final CustomProgressDialog dg  = new CustomProgressDialog(getActivity());
                                dg.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                                dg.show();
                                final Window winView = dg.getWindow();
                                TextView titleView = winView.findViewById(R.id.dialog_progress);
                                titleView.setText(getString(R.string.download) + getString(R.string.percent));
                                File file = new File(AudioSavePathInDevice);
                                System.out.println(file.getAbsolutePath());
                                s3Key = vm.getStorageKey();
                                System.out.println(s3Key);
                                TransferObserver transferObserver = sTransferUtility.download("bedunfamily", s3Key, file);
                                System.out.println(transferObserver.getState());
                                transferObserver.setTransferListener(new TransferListener() {

                                    /**
                                     * @param state state of download. if completed. ok button is made visible and playButCode is set to 1;
                                     */
                                    @Override
                                    public void onStateChanged(int id, TransferState state) {
                                        if (state.equals(TransferState.COMPLETED)) {
                                            ProgressBar pb = winView.findViewById(R.id.upload_bar);
                                            pb.setProgress(100);
                                            TextView titleView = winView.findViewById(R.id.dialog_progress);
                                            titleView.setText(getString(R.string.download) + 100 + getString(R.string.percent));
                                            Button but = winView.findViewById(R.id.proceed_voice);
                                            String uri = "@drawable/play";
                                            MainActivity anActivity = (MainActivity) getActivity();
                                            int imageResource = getResources().getIdentifier(uri, null, anActivity.getPackageName());
                                            Drawable res = getResources().getDrawable(imageResource);
                                            imageBut.setImageDrawable(res);
                                            playButCode = 1;
                                            Toast.makeText(anActivity, "Voice Message Downloaded",
                                                    Toast.LENGTH_SHORT).show();
                                            but.setVisibility(View.VISIBLE);
                                            but.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    dg.dismiss();
                                                }
                                            });

                                        }
                                    }

                                    /**
                                     * @param bytesCurrent bytes downloaded
                                     * @param bytesTotal Total bytes / file size of audio file to be downloaded
                                     *                   sets int percentage to percentage progress ( current bytes uploaded / total bytes size * 100_
                                     *                   sets progress bar to display current percentage progress of the download
                                     */
                                    @Override
                                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                                        try {
                                            int percentage = (int) (bytesCurrent / bytesTotal * 100);
                                            TextView titleView = winView.findViewById(R.id.dialog_progress);
                                            titleView.setText(getString(R.string.download) + percentage + getString(R.string.percent));
                                            ProgressBar pb = winView.findViewById(R.id.upload_bar);
                                            pb.setProgress(percentage);
                                        }
                                        catch (Exception e){
                                            System.out.println(e);
                                        }

                                    }

                                    /**
                                     * @param ex
                                     * on error, prints error code to console and notifies user of error in the UI
                                     */
                                    @Override
                                    public void onError(int id, Exception ex) {
                                        // do something
                                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            } else {
                                //if permission not granted, asks for permission
                                requestPermission();
                            }
                            break;
                        //on click, media player is initialised. Media plays plays the audio, rather then recording it.
                        // button is changed to stop icon and playButCode is set to 2;
                        case (1):
                            aChronometer.setBase(SystemClock.elapsedRealtime());
                            String uri = "@drawable/stop";
                            MainActivity anActivity = (MainActivity) getActivity();
                            int imageResource = getResources().getIdentifier(uri, null, anActivity.getPackageName());
                            Drawable res = getResources().getDrawable(imageResource);
                            imageBut.setImageDrawable(res);
                            mediaPlayer = new MediaPlayer();
                            try {
                                mediaPlayer.setDataSource(AudioSavePathInDevice);
                                mediaPlayer.prepare();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            aChronometer.start();
                            mediaPlayer.start();
                            playButCode = 2;
                            //stop media, set image resource to play, set recordbutcode to 2, make buttons appear
                            Toast.makeText(anActivity, "Recording Started",
                                    Toast.LENGTH_SHORT).show();
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    String uri = "@drawable/play";
                                    aChronometer.stop();
                                    aChronometer.setBase(SystemClock.elapsedRealtime() - finalTime);
                                    MainActivity yetAnotherActivity = (MainActivity) getActivity();
                                    int imageResource = getResources().getIdentifier(uri, null, yetAnotherActivity.getPackageName());
                                    Drawable res = getResources().getDrawable(imageResource);
                                    imageBut.setImageDrawable(res);
                                    Toast.makeText(yetAnotherActivity, "Voice Message Stopped",
                                            Toast.LENGTH_SHORT).show();
                                    //play media, set image resource to stop, set recordbutcode to 1
                                    playButCode = 1;
                                    buttonNoMatch.setVisibility(View.VISIBLE);
                                    butMatch.setVisibility(View.VISIBLE);
                                }
                            });
                            break;
                        //on click, audio recording is stopped.
                        // button is changed to play icon and playButCode is set to 2;
                        //buttons to match or not match are made visible on the screen.
                        case (2):
                            uri = "@drawable/play";
                            aChronometer.stop();
                            mediaPlayer.stop();
                            aChronometer.setBase(SystemClock.elapsedRealtime() - finalTime);
                            //aChronometer.setBase(finalTime);
                            MainActivity anotherActivity = (MainActivity) getActivity();
                            imageResource = getResources().getIdentifier(uri, null, anotherActivity.getPackageName());
                            res = getResources().getDrawable(imageResource);
                            imageBut.setImageDrawable(res);
                            Toast.makeText(anotherActivity, "Voice Message Stopped",
                                    Toast.LENGTH_SHORT).show();
                            //play media, set image resource to stop, set recordbutcode to 1
                            playButCode = 1;
                            buttonNoMatch.setVisibility(View.VISIBLE);
                            butMatch.setVisibility(View.VISIBLE);

                            break;
                    }
                }
            });
            return view;
        }
        /**
         * @return true if permission is granted to write audio file on the device && record audio. false if
         * permissions not granted
         */
        public boolean checkPermission() {
            int result = ContextCompat.checkSelfPermission(getContext(),
                    WRITE_EXTERNAL_STORAGE);
            int result1 = ContextCompat.checkSelfPermission(getContext(),
                    READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED &&
                    result1 == PackageManager.PERMISSION_GRANTED;
        }

        /**
         * request permission to write audio file on the device && record audio.
         * displays toast message indicating success or failure.
         */
        private void requestPermission() {
            ActivityCompat.requestPermissions(getActivity(), new
                    String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, RequestPermissionCode);
        }
        @Override
        public void onRequestPermissionsResult(int requestCode,
                                               String permissions[], int[] grantResults) {
            switch (requestCode) {
                case RequestPermissionCode:
                    if (grantResults.length> 0) {
                        boolean StoragePermission = grantResults[0] ==
                                PackageManager.PERMISSION_GRANTED;
                        boolean RecordPermission = grantResults[1] ==
                                PackageManager.PERMISSION_GRANTED;

                        if (StoragePermission && RecordPermission) {
                            Toast.makeText(getActivity(), "Permission Granted",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(),"Permission Denied",Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }

        /**
         * dialog box is displayed asking user if they believe the voicemail is a match or not
         */
        private void matchDialog() {
            cfd = new ConfirmDialog(getActivity());
            cfd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cfd.show();
            Window view = cfd.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_alert_text_title);
            titleView.setText(R.string.match);
            titleView.setTextColor(Color.GREEN);
            TextView tx= view.findViewById(R.id.dialog_alert_text);
            tx.setText(getString(R.string.match_dialog));
            Button yesBut = view.findViewById(R.id.yes_button);
            Button noBut = view.findViewById(R.id.no_button);
            yesBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    match();
                }
            } );
            noBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cfd.dismiss();
                }
            } );
        }
        /**
         * dialog box is displayed asking user if they believe the voicemail is a match or not
         */
        private void noMatchDialog() {
            cad = new ConfirmDialog(getActivity());
            cad.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cad.show();
            Window view = cad.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_alert_text_title);
            titleView.setText(R.string.no_match);
            titleView.setTextColor(Color.RED);
            TextView tx= view.findViewById(R.id.dialog_alert_text);
            tx.setText(getString(R.string.no_match_dialog));
            Button yesBut = view.findViewById(R.id.yes_button);
            Button noBut = view.findViewById(R.id.no_button);
            yesBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noMatch();
                }
            } );
            noBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cad.dismiss();
                }
            } );
        }

        /**
         * @param v View of Button which is clicked
         */
        @Override
        public void onClick(View v) {if (v == buttonNoMatch) {
                noMatchDialog();
            }
            // if the voicemail is a reply to a previous voicemail, match the users
            //if not, load recordvoiceinsfragment to send a reply back.
            if (v == butMatch) {
                //check whether reply or nay
                if (Integer.parseInt(vm.getReply()) == 0) {

                    android.support.v4.app.Fragment frag = new RecordVoiceInsFragment();
                    MainActivity anActivity = (MainActivity) getActivity();
                    anActivity.loadFragment(frag);                }
                else {
                    matchDialog();
                }
            }

        }

        /**
         * Async task. set the match entry in the database. on post execution of the task,
         * matchAlertDialog() is called, to alert user of match success
         */
        private void match() {
            class matchDb extends AsyncTask<Object, Object, Object> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.matchDb();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    cfd.dismiss();
                    matchAlertDialog();
                                   }

            }
            matchDb md = new matchDb();
            md.execute();
        }
        /**
         * Async task. set the match entry in the database. on post execution of the task,
         * noMatchAlertDialog() is called, to alert user of match being removed
         */
        private void noMatch() {
            class NoMatchDb extends AsyncTask<Object, Object, Object> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.noMatchDb();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    cad.dismiss();
                    noMatchAlertDialog();
                }

            }
            NoMatchDb nmd = new NoMatchDb();
            nmd.execute();
        }

        /**
         * dialog box is displayed telling user it is a match.
         * on click of ok button, dialog is dismissed and contact org fragment is loaded.
         */
        private void matchAlertDialog() {
            final CustomAlertDialog cad = new CustomAlertDialog(getActivity());
            cad.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cad.show();
            Window view = cad.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_alert_text_title);
            titleView.setText(R.string.match);
            titleView.setTextColor(Color.GREEN);
            TextView tx= view.findViewById(R.id.dialog_alert_text);
            tx.setText(getString(R.string.matched_part_one) + " " + dbHelper.getSearchableRefugee().getName() + ". "
                    + getString(R.string.matched_part_two));
            Button but = view.findViewById(R.id.ok_alert_dialog);
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    android.support.v4.app.Fragment frag = new ContactOrganizationMatchInsFragment();
                    MainActivity anActivity = (MainActivity) getActivity();
                    anActivity.loadFragment(frag);
                    cad.dismiss();
                }
            } );
        }

        /**
         * dialog box is displayed telling user it is not a match on click of ok
         * button, dialog is dismissed and home page is loaded
         */
        private void noMatchAlertDialog() {
            final CustomAlertDialog cad = new CustomAlertDialog(getActivity());
            cad.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cad.show();
            Window view = cad.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_alert_text_title);
            titleView.setText(R.string.no_match);
            titleView.setTextColor(Color.RED);
            TextView tx= view.findViewById(R.id.dialog_alert_text);
            tx.setText(getString(R.string.no_match_part_one));
            Button but = view.findViewById(R.id.ok_alert_dialog);
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cad.dismiss();
                    android.support.v4.app.Fragment frag = new HomeFragment();
                    MainActivity anActivity = (MainActivity) getActivity();
                    anActivity.loadFragment(frag);
                }
            } );
        }
    }

    /**
     * Displays all search results, prepared in the previous earch instructions screen.
     * The user has the option to accept the search as a possible match and move onto recording a voicemail
     * or reject the match and another search result is displayed on screen.
     * If all the matches are rejected and no matches remain, the no matches fragment is laoded
     */
    public static class SearchFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        ImageView tickBut;
        ImageView crossBut;
        ArrayList<Refugee> searchResults;

        int i = 0;
        private TextView nameView; private TextView nicknameView; private TextView ageView; private TextView cityView; private TextView localView;
        private TextView occView; private TextView tribeView; private CircleImageView natView;
        private Refugee aRef;
        private ConfirmDialog cad;

        //getters & setters
        public TextView getNameView() {
            return nameView;
        }
        public void setNameView(TextView nameView) {
            this.nameView = nameView;
        }
        public TextView getNicknameView() {
            return nicknameView;
        }
        public void setNicknameView(TextView nicknameView) {
            this.nicknameView = nicknameView;
        }
        public TextView getAgeView() {
            return ageView;
        }
        public void setAgeView(TextView ageView) {
            this.ageView = ageView;
        }
        public TextView getCityView() {
            return cityView;
        }
        public void setCityView(TextView cityView) {
            this.cityView = cityView;
        }
        public TextView getLocalView() {
            return localView;
        }
        public void setLocalView(TextView localView) {
            this.localView = localView;
        }
        public TextView getOccView() {
            return occView;
        }
        public void setOccView(TextView occView) {
            this.occView = occView;
        }
        public TextView getTribeView() {
            return tribeView;
        }
        public void setTribeView(TextView tribeView) {
            this.tribeView = tribeView;
        }
        public CircleImageView getNatView() {
            return natView;
        }
        public void setNatView(CircleImageView natView) {
            this.natView = natView;
        }

        /**
         * @param inflater inflates xml layout
         * @param viewGroup view container
         * @param savedInstanceState previous state of the fragment
         * @return  View
         * Loads in the first search result to the screen
         * Sets OnClick listeners for cross / tick buttons.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_search, viewGroup, false);
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.getmTitle().setText(R.string.search_fam);

            tickBut = view.findViewById(R.id.tick_button);
            crossBut = view.findViewById(R.id.cross_button);
            tickBut.setOnClickListener(this);
            crossBut.setOnClickListener(this);
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }

            nameView = view.findViewById(R.id.ref_name);
            nicknameView = view.findViewById(R.id.nickname_text);
            ageView = view.findViewById(R.id.age_text);
            cityView = view.findViewById(R.id.city_text);
            localView = view.findViewById(R.id.local_text);
            occView = view.findViewById(R.id.occ_text);
            tribeView = view.findViewById(R.id.tribe_text);
            natView = view.findViewById(R.id.nat_circle_image_view);
            searchResults = dbHelper.getSearchResults();
            Typeface titleFont = Typeface.
                    createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            nameView.setTypeface(titleFont);
            setRefugeeDetails(i);
            return view;
        }

        /**
         * dialog box is displayed asking user if they believe the search result is a match or not
         * if the user accepts, the fragment is replaced by RecordVoiceInsFragment.
         */
        private void matchDialog() {
            final ConfirmDialog cfd = new ConfirmDialog(getActivity());
            cfd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cfd.show();
            Window view = cfd.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_alert_text_title);
            titleView.setText(R.string.match);
            titleView.setTextColor(Color.GREEN);
            TextView tx= view.findViewById(R.id.dialog_alert_text);
            tx.setText(getString(R.string.possible_match));
            Button yesBut = view.findViewById(R.id.yes_button);
            Button noBut = view.findViewById(R.id.no_button);
            yesBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dbHelper.setSearchableRefugee(aRef);
                    android.support.v4.app.Fragment frag = new RecordVoiceInsFragment();
                    cfd.dismiss();
                    MainActivity anActivity = (MainActivity) getActivity();
                    anActivity.loadFragment(frag);                }
            } );
            noBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cad.dismiss();
                }
            } );
        }

        /**
         * dialog box is displayed asking user if they believe the search result is not a match
         * of their family member
         * if the user confirms it is not a match, noMatch() is run to iterate to another search result.
         */
        private void noMatchDialog() {
            cad = new ConfirmDialog(getActivity());
                cad.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cad.show();
            Window view = cad.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_alert_text_title);
            titleView.setText(R.string.no_match);
            titleView.setTextColor(Color.RED);
            TextView tx= view.findViewById(R.id.dialog_alert_text);
            tx.setText(getString(R.string.possible_no_match));
            Button yesBut = view.findViewById(R.id.yes_button);
            Button noBut = view.findViewById(R.id.no_button);
            yesBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    noMatch();
                }
            } );
            noBut.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cad.dismiss();
                }
            } );
        }

        /**
         * @param in the iterated position of the refugee object in the searchresults
         */
        private void setRefugeeDetails(int in) {
            aRef = searchResults.get(in);
            dbHelper.setSearchableRefugee(aRef);
            nameView.setText(aRef.getName());
            nicknameView.setText(aRef.getNickname());
            ageView.setText(aRef.getAgeGroup());
            cityView.setText(aRef.getPlaceOfBirth());
            localView.setText(aRef.getLoc());
            occView.setText(aRef.getOcc());
            tribeView.setText(aRef.getTribe());
            int index = mCountries.indexOfName(aRef.getNationality());
            Country aCountry = mCountries.get(index);
            natView.setImageResource(getFlagResource(aCountry));
        }

        /**
         * async task. on execution of the task, dbHelper sets the search result
         * as no match in the database, so it cannot be returned in future searches.
         * on post execution of the task, loadNextRefugee is called to load the next refugee
         * into the search box
         */
        private void noMatch() {
            class NoMatchDb extends AsyncTask<Object, Object, Object> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.noMatchDb();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    cad.dismiss();
                    loadNextRefugee();
                    super.onPostExecute(o);
                }

            }
            NoMatchDb nmd = new NoMatchDb();
            nmd.execute();
        }

        /**
         * checks if the iterator, i, is less than the size of searchresults.
         * if so changes the refugee displayed on screen.
         * else, loads noMatchFragment into the content frame, to notify the user
         * that no matches could be found at present
         */
        private void loadNextRefugee() {
            i++;
            if (i < searchResults.size()) {
                setRefugeeDetails(i);
            }
            else{
                android.support.v4.app.Fragment frag = new NoMatchFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                anActivity.loadFragment(frag);
            }
        }

        /**
         * Fetch flag resource by Country
         * @param country Country
         * @return int of resource | 0 value if not exists
         */
        private int getFlagResource(Country country) {
            return getContext().getResources().getIdentifier("country_" + country.getIso().toLowerCase(), "drawable", getContext().getPackageName());
        }
        @Override
        public void onClick(View v) {
            if (v == tickBut) {
                matchDialog();
            }
            if (v == crossBut) {
                noMatchDialog();
            }
        }
        @Override
        public void onResume(){
            super.onResume();
            searchResults = dbHelper.getSearchResults();
        }
    }

    /**
     * Simple Fragment. text box description, details that no matches were found from the search. button to exit out to home screen.
     */
    public static class NoMatchFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        Button but;


        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.no_matches_fragment, viewGroup, false);

            MainActivity anActivity = (MainActivity) getActivity();
            assert anActivity != null;
            anActivity.getmTitle().setText(R.string.search);

            but = view.findViewById(R.id.no_matches_button);
            but.setOnClickListener(this);
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }

            TextView tv = view.findViewById(R.id.no_matches);
            Typeface titleFont = Typeface.
                    createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);
            return view;
        }

        /**
         * @param v View Button View
         *          loads HomeFragment
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                android.support.v4.app.Fragment frag = new HomeFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                assert anActivity != null;
                anActivity.loadFragment(frag);
            }
        }
    }

    /**
     * Fragment to add details (phone number, location) and send over to aid organisation
     */
    public static class ContactOrganizationFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        private IntlPhoneInput phoneView;
        private EditText locationView;
        private String location;
        private String phone;
        private Button but;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            final View view = inflater.inflate(R.layout.fragment_contact_org, viewGroup, false);

            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            but = view.findViewById(R.id.contact_button);
            but.setOnClickListener(this);
            MainActivity anActivity = (MainActivity) getActivity();

            anActivity.getmTitle().setText(R.string.contact_org);
            TextView tv = view.findViewById(R.id.ins_connect_fam);
            Typeface titleFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);

            //TextWatchers listen for input and set labels to change on text being entered.
            locationView = view.findViewById(R.id.location);
            locationView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.location_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.location_label);
                    label.setText(R.string.location);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.location_label);
                        label.setText(R.string.location);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            phoneView = view.findViewById(R.id.my_phone_input);
            EditText phoneEdit = phoneView.findViewById(R.id.intl_phone_edit__phone);
            phoneEdit.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.textViewUserIdLabel);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.textViewUserIdLabel);
                    label.setText(R.string.phone);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.textViewUserIdLabel);
                        label.setText(R.string.phone);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            return view;
        }

        /**
         * @param v Button View
         *          checks if the inputted location and phone are valid. If so, calls setContactOrgDb() to set
         *          in database
         *
         */
        @Override
        public void onClick(View v) {
            location = ""; phone = "";
            location = locationView.getText().toString();
            phone = phoneView.getNumber();
            MainActivity anActivity = (MainActivity) getActivity();
            if(InputsValid()){
                setContactOrgDb();
            }
        }

        /**
         * @return true if phone number is a valid number && location is not empty.
         * else, return false. set the label of the invalid
         * element (phone or location) to error message and label colour to red
         */
        private boolean InputsValid() {
            boolean result = true;
            if (!phoneView.isValid()){
                result = false;
                TextView label = getView().findViewById(R.id.textViewUserIdLabel);
                label.setText(R.string.phone_error);
                label.setTextColor(getResources().getColor(R.color.red));
            }
            if (location.length() < 1){
                result = false;
                TextView label = getView().findViewById(R.id.location_label);
                label.setText(R.string.location_error);
                label.setTextColor(getResources().getColor(R.color.red));
            }
            return result;
        }

        /**
         * Async task. set the contact organisation entry in the database. on post execution of the task, loadfragment() is called,
         */
        private void setContactOrgDb() {
            //exec
            class SetContactOrg extends AsyncTask<String, Void, String>{

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected String doInBackground(String... strings) {
                    MainActivity anActivity = (MainActivity) getActivity();
                    String contactType = anActivity.contactOrgType;
                    dbHelper.setContactOrgDb(location, phone, contactType);
                    return null;
                }
                @Override
                protected void onPostExecute(String s){
                    loadFragment();
                    super.onPostExecute(s);
                }
            }
            SetContactOrg sco = new SetContactOrg();
            sco.execute();
        }

        /**
         * if the user is contacting an organisation to report a found family member, the toolbar title is set to the found refugee name.
         * else, the toolbar title is set to the missing family member name.
         * ContactDoneFragment() is loaded into the main content frame.
         */
        public void loadFragment() {

            MainActivity activity = ((MainActivity) getActivity());
            if (activity.contactOrgType == "Match"){
                activity.getmTitle().setText(dbHelper.getFoundRefugee().getName());
            }
            else{
                activity.getmTitle().setText(dbHelper.getCurrentFamMember().getName());
            }
            android.support.v4.app.Fragment frag = new ContactDoneFragment();
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.loadFragment(frag);
        }
    }

    /**
     * Simple fragment. displays text description telling the user to await contact from the aid organisation.
     * button exits out to home screen on click.
     */
    public static class ContactDoneFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

        Button but;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_contact_done, viewGroup, false);

            but = view.findViewById(R.id.contact_button);
            but.setOnClickListener(this);
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            TextView tv = view.findViewById(R.id.ins_connect_fam);
            Typeface titleFont = Typeface.
                    createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);
            return view;
        }

        /**
         * @param v Button View
         *          loads home fragment into main content frame, on button being clicked.
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                android.support.v4.app.Fragment frag = new HomeFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                assert anActivity != null;
                anActivity.loadFragment(frag);
            }
        }
    }

    /**
     * Fragment for adding details of missing family members. Presenter class, RefugeePresenter, checks details are valid.
     */
    @SuppressWarnings("ConstantConditions")
    public static class AddFamilyFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        Button but;
        private TextView tx;
        private RefugeePresenter refugeePresenter;
        private Refugee refugee;
        ArrayAdapter<CharSequence> adapter;
        ArrayAdapter<CharSequence> ageAdapter;
        ArrayAdapter<CharSequence> genderAdapter;
        String age;
        String nationality;
        String gender;
        String fam;
        TextView nameView;
        TextView pobView;
        TextView tribeView;
        TextView nicknameView;
        TextView occupationView;
        TextView localAreaView;
        TextView ageSpinner;
        TextView countrySpinner;
        TextView genderSpinner;
        private TextView famSpinner;

        /**
         * @param inflater inflater. inflates xml layout to screen
         * @param viewGroup contains layout views
         * @param savedInstanceState fragment's previously saved state
         * initialises refugee presenter.
           Text Watchers listen for user inputting text. On text being input (e.g name), the label is set to describe
            below field (e.g name)
         * @return inflated View
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            final View view = inflater.inflate(R.layout.fragment_add_family, viewGroup, false);
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.getmTitle().setText(R.string.add_family_member_title);
            but = view.findViewById(R.id.add_fam_button);
            but.setOnClickListener(this);
            refugeePresenter = new RefugeePresenter(getContext());

            nationality = "";
            age = "";
            gender = "";
            fam = "";

            ageSpinner = view.findViewById(R.id.age_spinner);
            ageSpinner.setOnClickListener(this);
            famSpinner = view.findViewById(R.id.spinner_family);
            famSpinner.setOnClickListener(this);
            ageSpinner.setOnClickListener(this);
            genderSpinner = view.findViewById(R.id.gender_spinner);
            genderSpinner.setOnClickListener(this);
            countrySpinner = view.findViewById(R.id.spnCountry);
            countrySpinner.setOnClickListener(this);


            nameView = (EditText) view.findViewById(R.id.name_ref);
            nameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.name_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.name_label);
                    label.setText(R.string.full_name);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.name_label);
                        label.setText(R.string.full_name);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            pobView = (EditText) view.findViewById(R.id.place_of_birth_text);
            pobView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.city_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.city_label);
                    label.setText(R.string.place_of_birth);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.city_label);
                        label.setText(R.string.place_of_birth);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            nicknameView = (EditText) view.findViewById(R.id.nickname_text);
            nicknameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.nickname_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.nickname_label);
                    label.setText(R.string.nickname);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.nickname_label);
                        label.setText(R.string.nickname);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            tribeView = (EditText) view.findViewById(R.id.tribe_text);
            tribeView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.tribe_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.tribe_label);
                    label.setText(R.string.tribe_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.tribe_label);
                        label.setText(R.string.tribe_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            localAreaView = (EditText) view.findViewById(R.id.loca_area_text);
            localAreaView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.village_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.village_label);
                    label.setText(R.string.local_area_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.village_label);
                        label.setText(R.string.local_area_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            occupationView = (EditText) view.findViewById(R.id.occupation_text_view);
            occupationView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.occupation_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.occupation_label);
                    label.setText(R.string.local_area_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.occupation_label);
                        label.setText(R.string.local_area_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });

            return view;

        }

        /**
         * @param v View which recieves click input
         */
        @Override
        public void onClick(View v) {
            // If proceed button is clicked. checks to see if inputted details are valid. if so, sets the missing person in the
            // database
            if (v == but) {
                if (checkRefugeeDetails()) {
                    setMissingPersonDb();
                }
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list gender options, retrieved from resource string array, gender_array.
            //on click of list item, dialog is dismissed, the label on the activity is set to gender and the edittext is set to the
            //gender option
            if (v == genderSpinner) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.gender_label);
                ListView listView = view.findViewById(R.id.list_dialog);
                genderAdapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_array, R.layout.list_item);
                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(genderAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        gender = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.gender_label);
                        label.setText(R.string.gender_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView genText = getView().findViewById(R.id.gender_spinner);
                        genText.setText(gender);

                        cdd.dismiss();
                    }
                });
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list nationality options, retrieved from countriesNameArray by the CountrySpinnerAdapter
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected country
            //OnQueryTextListener set on searchview in dialog. Listens for text being input into searchview. on input, filter
            //takes inputted charecters and filters down countries to those that contain the sequence of characters
            if (v == countrySpinner) {
                final CustomCountryDialog cdd = new CustomCountryDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.nationality);
                final ListView listView = view.findViewById(R.id.list_dialog);
                final CountrySpinnerAdapter natAdapter = new CountrySpinnerAdapter(getContext(), countriesNameArray);
                natAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(natAdapter);
                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        //Country aCountry = (Country)parent.getItemAtPosition(position);
                        //nationality = aCountry.getName();
                        nationality = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.nationality_label);
                        label.setText(R.string.nationality);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView natText = getView().findViewById(R.id.spnCountry);
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
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list age options, retrieved from string array resource , age_group_array.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            if (v == ageSpinner) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.age_label);
                ListView listView = view.findViewById(R.id.list_dialog);
                ageAdapter = ArrayAdapter.createFromResource(getContext(), R.array.age_group_array, R.layout.list_item);
                ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(ageAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        age = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.age_label);
                        label.setText(R.string.age_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView ageText = getView().findViewById(R.id.age_spinner);
                        ageText.setText(age);
                        cdd.dismiss();
                    }
                });
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list family member options, retrieved from string array resource , family_array.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            if (v == famSpinner) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.relationship_label);
                ListView listView = view.findViewById(R.id.list_dialog);
                ArrayAdapter genAdapter = ArrayAdapter.createFromResource(getContext(), R.array.family_array, R.layout.list_item);
                genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(genAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        fam = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.family_label);
                        label.setText(R.string.relationship_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView natText = getView().findViewById(R.id.spinner_family);
                        natText.setText(fam);

                        cdd.dismiss();
                    }
                });
            }

        }

        /**
         * async task. send message to database helper class to set the missing person in the database.
         * after executing, sends message toastMsg, to send message to UI denoting the person being added.
         */
        private void setMissingPersonDb() {
            class setRefugeeDb extends AsyncTask<Object, Object, Object> {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.setMissingPersonDb("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/add_missing_person_details.php", refugee);
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    toastMsg();
                }
            }
            setRefugeeDb srd = new setRefugeeDb();
            srd.execute();
        }


        /**
         creates a refugee object by feeding the method parameters into the
         Refugee constructer.
         */
        private void createRefugee(String refugeeId, String name, String age,
                                   String nationality, String nickname, String placeOfBirth,
                                   String tribe, String gender, String localArea, String occupation,
                                   String relation, String dateCreated) {
            refugee = new Refugee(refugeeId, name, age, nationality,
                    nickname, placeOfBirth, tribe, gender, localArea, occupation, relation, dateCreated);
            System.out.println(refugee);
        }

        /**
         * refugee presenter checks the inputted details for each field (e.g name, relationship). sends message createrefugee if successful
         * @return true if inputted details are valid. false if one or more of the fields are not valid (e.g name is less then 6 characters)
         * sets label of invalid field(s) to error message and changes label colour to red.
         */
        private boolean checkRefugeeDetails() {

            Boolean result = false;
            TextView refugeeNameView = getView().findViewById(R.id.name_ref);
            TextView refugeeNameLabel = getView().findViewById(R.id.name_label);
            String refugeeName = refugeeNameView.getText().toString();

            TextView refugeeTribeView = getView().findViewById(R.id.tribe_text);
            String refugeeTribe = refugeeTribeView.getText().toString();
            TextView refugeeTribeLabel = getView().findViewById(R.id.tribe_label);

            TextView refugeePobView = getView().findViewById(R.id.place_of_birth_text);
            String refugeePob = refugeePobView.getText().toString();
            TextView refugeePobLabel = getView().findViewById(R.id.city_label);

            TextView refugeeNicknameView = getView().findViewById(R.id.nickname_text);
            String refugeeNickname = refugeeNicknameView.getText().toString();
            TextView refugeeNicknameLabel = getView().findViewById(R.id.nickname_label);

            TextView refugeeLocalView = getView().findViewById(R.id.loca_area_text);
            String refugeeLocal = refugeeLocalView.getText().toString();
            TextView refugeeLocalLabel = getView().findViewById(R.id.village_label);

            TextView refugeeOccupationView = getView().findViewById(R.id.occupation_text_view);
            String refugeeOccupation = refugeeOccupationView.getText().toString();
            TextView refugeeOccupationLabel = getView().findViewById(R.id.occupation_label);

            TextView refugeeAgeLabel = getView().findViewById(R.id.age_label);
            TextView refugeeNatLabel = getView().findViewById(R.id.nationality_label);
            TextView refugeeGenderLabel = getView().findViewById(R.id.gender_label);
            TextView refugeeRelationshipLabel = getView().findViewById(R.id.family_label);

            if (refugeePresenter.init(refugeePob, nationality, refugeeName,
                    age, refugeeNickname, refugeeTribe, gender, refugeeLocal, refugeeOccupation, fam)) {
                result = true;
                String refId = dbHelper.getRefId();
                Calendar myCalendar = Calendar.getInstance();
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                String dateCreated = sdf.format(myCalendar.getTime());
                createRefugee(refId, refugeeName, age, nationality,
                        refugeeNickname, refugeePob, refugeeTribe, gender, refugeeLocal, refugeeOccupation, fam, dateCreated);

            } else {
                refugeeNicknameLabel.setTextColor(refugeePresenter.getNickNameLabelColor());
                refugeeNicknameLabel.setText(refugeePresenter.getNickNameLabel());
                refugeeNameLabel.setTextColor(refugeePresenter.getNameLabelColor());
                refugeeNameLabel.setText(refugeePresenter.getNameLabel());
                refugeeNatLabel.setTextColor(refugeePresenter.getNationalityLabelColor());
                refugeeNatLabel.setText(refugeePresenter.getNationalityLabel());
                refugeePobLabel.setTextColor(refugeePresenter.getPlaceOfBirthLabelColor());
                refugeePobLabel.setText(refugeePresenter.getPlaceOfBirthLabel());
                refugeeTribeLabel.setTextColor(refugeePresenter.getTribeLabelColor());
                refugeeTribeLabel.setText(refugeePresenter.getTribeLabel());
                refugeeAgeLabel.setTextColor(refugeePresenter.getAgeLabelColor());
                refugeeAgeLabel.setText(refugeePresenter.getAgeLabel());
                refugeeLocalLabel.setTextColor(refugeePresenter.getLocalAreaLabelColor());
                refugeeLocalLabel.setText(refugeePresenter.getLocalAreaLabel());
                refugeeOccupationLabel.setTextColor(refugeePresenter.getOccupationLabelColor());
                refugeeOccupationLabel.setText(refugeePresenter.getOccupationLabel());
                refugeeGenderLabel.setTextColor(refugeePresenter.getGenderLabelColor());
                refugeeGenderLabel.setText(refugeePresenter.getGenderLabel());
                refugeeRelationshipLabel.setTextColor(refugeePresenter.getRelationshipLabelColor());
                refugeeRelationshipLabel.setText(refugeePresenter.getRelationshipLabel());

            }
            return result;
        }

        /**
         * displays refugee name + "updated" to the user interface
         */
        public void toastMsg() {
            Toast.makeText(getContext(), refugee.getName() + " " + " Added.", Toast.LENGTH_SHORT).show();
            android.support.v4.app.Fragment frag = new HomeFragment();
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.loadFragment(frag);
        }
    }

    /**
     * Fragment for adding details of refugee. Presenter class, RefugeePresenter, checks details are valid.
     */
    @SuppressWarnings("ConstantConditions")
    public static class AddRefFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        Button but;
        private TextView tx;
        private RefugeePresenter refugeePresenter;
        private Refugee refugee;
        ArrayAdapter<CharSequence> adapter;
        ArrayAdapter<CharSequence> ageAdapter;
        ArrayAdapter<CharSequence> genderAdapter;
        String age;
        String nationality;
        String gender;
        TextView nameView;
        TextView pobView;
        TextView tribeView;
        TextView nicknameView;
        TextView occupationView;
        TextView localAreaView;
        TextView ageSpinner;
        TextView countrySpinner;
        TextView genderSpinner;


        /**
         * @param inflater inflater. inflates xml layout to screen
         * @param viewGroup contains layout views
         * @param savedInstanceState fragment's previously saved state
         * initialises refugee presenter.
        Text Watchers listen for user inputting text. On text being input (e.g name), the label is set to describe
        below field (e.g name)
         * @return inflated View
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            final View view = inflater.inflate(R.layout.fragment_add_refugee, viewGroup, false);
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.getmTitle().setText(R.string.add_ref);
            but = view.findViewById(R.id.add_fam_button);
            but.setOnClickListener(this);

            refugeePresenter = new RefugeePresenter(getContext());

            nationality = "";
            age = "";
            gender = "";


            ageSpinner = view.findViewById(R.id.age_spinner);
            ageSpinner.setOnClickListener(this);
            genderSpinner = view.findViewById(R.id.gender_spinner);
            genderSpinner.setOnClickListener(this);
            countrySpinner = view.findViewById(R.id.spnCountry);
            countrySpinner.setOnClickListener(this);


            nameView = (EditText) view.findViewById(R.id.name_ref);
            nameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.name_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.name_label);
                    label.setText(R.string.full_name);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.name_label);
                        label.setText(R.string.full_name);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            pobView = (EditText) view.findViewById(R.id.place_of_birth_text);
            pobView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.city_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.city_label);
                    label.setText(R.string.place_of_birth);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.city_label);
                        label.setText(R.string.place_of_birth);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            nicknameView = (EditText) view.findViewById(R.id.nickname_text);
            nicknameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.nickname_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.nickname_label);
                    label.setText(R.string.nickname);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.nickname_label);
                        label.setText(R.string.nickname);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            tribeView = (EditText) view.findViewById(R.id.tribe_text);
            tribeView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.tribe_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.tribe_label);
                    label.setText(R.string.tribe_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.tribe_label);
                        label.setText(R.string.tribe_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            localAreaView = (EditText) view.findViewById(R.id.loca_area_text);
            localAreaView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.village_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.village_label);
                    label.setText("Village / Local Area");
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.village_label);
                        label.setText("Village / Local Area");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            occupationView = (EditText) view.findViewById(R.id.occupation_text_view);
            occupationView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.occupation_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.occupation_label);
                    label.setText(R.string.occupation_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.occupation_label);
                        label.setText(R.string.occupation_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });

            return view;

        }

        /**
         * @param v View which recieves click input
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                if (checkRefugeeDetails()) {
                    setRefugeeDb();
                }
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list gender options, retrieved from string array resource , gender_array.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            if (v == genderSpinner) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.gender_label);
                ListView listView = view.findViewById(R.id.list_dialog);
                genderAdapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_array, R.layout.list_item);
                genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(genderAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        gender = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.gender_label);
                        label.setText(R.string.gender_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView genText = getView().findViewById(R.id.gender_spinner);
                        genText.setText(gender);

                        cdd.dismiss();
                    }
                });
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list nationality options, retrieved from countriesNameArray by the CountrySpinnerAdapter
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected country
            //OnQueryTextListener set on searchview in dialog. Listens for text being input into searchview. on input, filter
            //takes inputted charecters and filters down countries to those that contain the sequence of characters
            if (v == countrySpinner) {
                final CustomCountryDialog cdd = new CustomCountryDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.nationality);
                ListView listView = view.findViewById(R.id.list_dialog);
                final CountrySpinnerAdapter natAdapter = new CountrySpinnerAdapter(getContext(), countriesNameArray);
                natAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(natAdapter);
                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        nationality = parent.getItemAtPosition(position).toString();
                        System.out.println(nationality);
                        TextView label = getView().findViewById(R.id.nationality_label);
                        label.setText(R.string.nationality);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView natText = getView().findViewById(R.id.spnCountry);
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
                });            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list age options, retrieved from string array resource , age_group_array.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            if (v == ageSpinner) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.age_label);
                ListView listView = view.findViewById(R.id.list_dialog);
                ageAdapter = ArrayAdapter.createFromResource(getContext(), R.array.age_group_array, R.layout.list_item);
                ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(ageAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        age = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.age_label);
                        label.setText(R.string.age_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView ageText = getView().findViewById(R.id.age_spinner);
                        ageText.setText(age);
                        cdd.dismiss();
                    }
                });
            }
                  }

        /**
         * async task. send message to database helper class to set the refugee in the database.
         * after executing, sends message toastMsg, to send message to UI denoting the person being added.
         */
        private void setRefugeeDb() {
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
                    toastMsg();
                }
            }
            setRefugeeDb srd = new setRefugeeDb();
            srd.execute();
        }

        /**
         creates a refugee object by feeding the method parameters into the
         Refugee constructer.
         */
        private void createRefugee(String name, String age,
                                   String nationality, String nickname, String placeOfBirth,
                                   String tribe, String gender, String localArea, String occupation) {
            System.out.println("HERE MISS");
            refugee = new Refugee(name, age, nationality,
                    nickname, placeOfBirth, tribe, gender, localArea, occupation);
            System.out.println(refugee.toString());
        }

        /**
         * refugee presenter checks the inputted details for each field (e.g name, relationship). sends message createrefugee if successful
         * @return true if inputted details are valid. false if one or more of the fields are not valid (e.g name is less then 6 characters)
         * sets label of invalid field(s) to error message and changes label colour to red.
         */
        private boolean checkRefugeeDetails() {

            Boolean result = false;
            TextView refugeeNameView = getView().findViewById(R.id.name_ref);
            TextView refugeeNameLabel = getView().findViewById(R.id.name_label);
            String refugeeName = refugeeNameView.getText().toString();

            TextView refugeeTribeView = getView().findViewById(R.id.tribe_text);
            String refugeeTribe = refugeeTribeView.getText().toString();
            TextView refugeeTribeLabel = getView().findViewById(R.id.tribe_label);

            TextView refugeePobView = getView().findViewById(R.id.place_of_birth_text);
            String refugeePob = refugeePobView.getText().toString();
            TextView refugeePobLabel = getView().findViewById(R.id.city_label);

            TextView refugeeNicknameView = getView().findViewById(R.id.nickname_text);
            String refugeeNickname = refugeeNicknameView.getText().toString();
            TextView refugeeNicknameLabel = getView().findViewById(R.id.nickname_label);

            TextView refugeeLocalView = getView().findViewById(R.id.loca_area_text);
            String refugeeLocal = refugeeLocalView.getText().toString();
            TextView refugeeLocalLabel = getView().findViewById(R.id.village_label);

            TextView refugeeOccupationView = getView().findViewById(R.id.occupation_text_view);
            String refugeeOccupation = refugeeOccupationView.getText().toString();
            TextView refugeeOccupationLabel = getView().findViewById(R.id.occupation_label);

            TextView refugeeAgeLabel = getView().findViewById(R.id.age_label);
            TextView refugeeNatLabel = getView().findViewById(R.id.nationality_label);
            TextView refugeeGenderLabel = getView().findViewById(R.id.gender_label);

            //check relationship
            if (refugeePresenter.init(refugeePob, nationality, refugeeName,
                    age, refugeeNickname, refugeeTribe, gender, refugeeLocal, refugeeOccupation)) {
                result = true;
                createRefugee(refugeeName, age, nationality,
                        refugeeNickname, refugeePob, refugeeTribe, gender, refugeeLocal, refugeeOccupation);

            } else {
                refugeeNicknameLabel.setTextColor(refugeePresenter.getNickNameLabelColor());
                refugeeNicknameLabel.setText(refugeePresenter.getNickNameLabel());
                refugeeNameLabel.setTextColor(refugeePresenter.getNameLabelColor());
                refugeeNameLabel.setText(refugeePresenter.getNameLabel());
                refugeeNatLabel.setTextColor(refugeePresenter.getNationalityLabelColor());
                refugeeNatLabel.setText(refugeePresenter.getNationalityLabel());
                refugeePobLabel.setTextColor(refugeePresenter.getPlaceOfBirthLabelColor());
                refugeePobLabel.setText(refugeePresenter.getPlaceOfBirthLabel());
                refugeeTribeLabel.setTextColor(refugeePresenter.getTribeLabelColor());
                refugeeTribeLabel.setText(refugeePresenter.getTribeLabel());
                refugeeAgeLabel.setTextColor(refugeePresenter.getAgeLabelColor());
                refugeeAgeLabel.setText(refugeePresenter.getAgeLabel());
                refugeeLocalLabel.setTextColor(refugeePresenter.getLocalAreaLabelColor());
                refugeeLocalLabel.setText(refugeePresenter.getLocalAreaLabel());
                refugeeOccupationLabel.setTextColor(refugeePresenter.getOccupationLabelColor());
                refugeeOccupationLabel.setText(refugeePresenter.getOccupationLabel());
                refugeeGenderLabel.setTextColor(refugeePresenter.getGenderLabelColor());
                refugeeGenderLabel.setText(refugeePresenter.getGenderLabel());

            }
            return result;
        }

        public void toberemovedmethod() {

            HashMap<Integer, Refugee> tempMap = new HashMap<>();
            for (Integer missId : dbHelper.getMissingFamMap().keySet()) {
                Refugee aRef = dbHelper.getMissingFamMap().get(missId);
                if (aRef.getRefugeeId().equals(dbHelper.getRefId())) {
                    tempMap.put(missId, aRef);
                }
            }
            dbHelper.setMissingFamLinkedToCurrentRef(tempMap);
        }
        public void toastMsg() {
            Toast.makeText(getContext(), refugee.getName() + " " + " Added.", Toast.LENGTH_LONG).show();
            android.support.v4.app.Fragment frag = new HomeFragment();
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.loadFragment(frag);
        }
    }

    /**
     * class for selecting refugees, so the aid worker can find family members for different refugees
     * under their duty of care
     */
    @SuppressWarnings("ConstantConditions")
    public static class  SelectRefugeeFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        Button but;
        TextView select_ref_text;
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_select_refugee, viewGroup, false);

            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.getmTitle().setText(R.string.select_refugee);
            but = view.findViewById(R.id.select_ref_button);
            but.setOnClickListener(this);

            select_ref_text = view.findViewById(R.id.search_dialog_box);
            select_ref_text.setOnClickListener(this);

            TextView tv = view.findViewById(R.id.select_ref_title);
            Typeface titleFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);
            return view;
        }

        /**
         * @param v Button view which receives the click
         */
        @Override
        public void onClick(View v) {
            //on click, returns to home screen
            if (v == but) {
                MainActivity anActivity = (MainActivity)getActivity();
                android.support.v4.app.Fragment frag = new HomeFragment();
                anActivity.loadFragment(frag);
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list a refugee name and nickname.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            //The current refugee is set to the listed item and if the refugee
            //has registered missing family members, the family members belonging to
            //the refugee are loaded into the family member map dbHelper.sissingFamLinkedToCurrentRef
            if (v == select_ref_text) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.select_ref);
                ListView listView = view.findViewById(R.id.list_dialog);
                HashMap<Integer, Refugee> refMap = dbHelper.getRefMap();
                final ArrayList<String> refDetailsArray = new ArrayList<>();
                final ArrayList<Refugee> refArray = new ArrayList<>();
                final ArrayList<String> refIdArray = new ArrayList<>();
                for (Integer id : refMap.keySet()) {
                    System.out.println(id);
                    refArray.add(refMap.get(id));
                    String refugee = refMap.get(id).getName() + " : " + refMap.get(id).getNickname();
                    refDetailsArray.add(refugee);
                    System.out.println(refMap.get(id).toString());
                    refIdArray.add(id.toString());
                }
                ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.list_item, refDetailsArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        String ref = parent.getItemAtPosition(position).toString();
                        select_ref_text.setText(ref);
                        dbHelper.setCurrentRefugee(refArray.get(position));
                        dbHelper.setRefId(refIdArray.get(position));
                        HashMap<Integer, Refugee> tempMap;
                        tempMap = new HashMap<>();
                        for (Integer missId : dbHelper.getMissingFamMap().keySet()) {
                            Refugee aRef = dbHelper.getMissingFamMap().get(missId);
                            if (aRef.getRefugeeId().equals(dbHelper.getRefId())) {
                                tempMap.put(missId, aRef);
                            }
                        }
                        dbHelper.setMissingFamLinkedToCurrentRef(tempMap);
                        System.out.println(dbHelper.getMissingFamLinkedToCurrentRef());
                        but.setVisibility(View.VISIBLE);
                        cdd.dismiss();
                    }
                });
            }
        }
    }

    /**
     * class for selecting refugees, so the aid worker can find family members for different refugees
     * under their duty of care
     */
    @SuppressWarnings("ConstantConditions")
    public static class  SelectFamFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        Button but;
        TextView select_fam_text;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_select_fam_member, viewGroup, false);

            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.getmTitle().setText(R.string.select_fam);
            but = view.findViewById(R.id.select_fam_button);
            but.setOnClickListener(this);

            select_fam_text = view.findViewById(R.id.search_dialog_box);
            select_fam_text.setOnClickListener(this);

            TextView tv = view.findViewById(R.id.select_fam_title);
            Typeface titleFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);
            return view;
        }

        /**
         * @param v View of button which recieives click input
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                MainActivity anActivity = (MainActivity) getActivity();
                android.support.v4.app.Fragment frag = new HomeFragment();
                anActivity.loadFragment(frag);
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list a missing family member name and nickname.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            //The current family member is set to the listed item.
            if (v == select_fam_text) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.select_fam);
                ListView listView = view.findViewById(R.id.list_dialog);
                HashMap<Integer, Refugee> famMap = dbHelper.getMissingFamLinkedToCurrentRef();
                final ArrayList<String> famDetailsArray = new ArrayList<>();
                final ArrayList<Refugee> famArray = new ArrayList<>();
                final ArrayList<String> famIdArray = new ArrayList<>();
                for (Integer id : famMap.keySet()) {
                    System.out.println(id);
                    famArray.add(famMap.get(id));
                    String famMember = famMap.get(id).getName() + " : " + famMap.get(id).getNickname();
                    famDetailsArray.add(famMember);
                    System.out.println(famMap.get(id).toString());
                    famIdArray.add(id.toString());
                }
                ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.list_item, famDetailsArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        String ref = parent.getItemAtPosition(position).toString();
                        select_fam_text.setText(ref);
                        dbHelper.setCurrentFamMember(famArray.get(position));
                        dbHelper.setMiss_id(famIdArray.get(position));
                        System.out.println(dbHelper.getCurrentFamMember());
                        but.setVisibility(View.VISIBLE);
                        cdd.dismiss();
                    }
                });
            }
        }
    }

    /**
     * Simple Fragment. Shows text description of how to contact an organisation on the following screen
     */
    @SuppressWarnings("ConstantConditions")
    public static class ContactOrganizationMatchInsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        Button but;
        TextView select_family_text;
        private TextView select_ref_text;
        private ArrayList<Integer> matchArray;
        private MainActivity anActivity;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_contact_org_match_ins, viewGroup, false);

            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.getmTitle().setText(R.string.contact_org);
            but = view.findViewById(R.id.cont_match_button);
            but.setOnClickListener(this);
            select_ref_text = view.findViewById(R.id.search_dialog_box);
            select_ref_text.setOnClickListener(this);
            TextView tv = view.findViewById(R.id.select_ref_title);
            Typeface titleFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);
            //prepare all matches is called to prepare all refugees matched to the current
            //refugee. This is ran on each load of this fragment, as the match may occur
            //by another user accepting a voicemail, so the app needs to see this live.
            prepareMatches();
            System.out.println("here");

            return view;
        }

        private void prepareMatches() {
            HashMap<Integer, ArrayList<String>> removedFromSearchMap = dbHelper.getRemovedFromSearchMap();
            matchArray = new ArrayList<>();
            //for (Integer id : removedFromSearchMap.keySet()) {
             //   ArrayList<String> tempArray = removedFromSearchMap.get(id);
             //   System.out.println(tempArray + "test");
              //  String match = tempArray.get(2);
              //  String refId = tempArray.get(0);
              //  if (match.equals("Match") && refId.equals(dbHelper.getRefId())) {
               //     matchArray.add(id);
               // }
           // }
            loadMatches();
        }

        /**
         * async task. send message to database helper class to find all refugees who are matched with the currently selected refugee.
         * after executing, sends message toastMsg, to send message to UI denoting the person being added.
         */        private void loadMatches() {
            class LoadMatchesDb extends AsyncTask<Object, Object, Object> {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    System.out.println("cooey");
                    dbHelper.loadMatchesDb();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                }
            }
            LoadMatchesDb lmd = new LoadMatchesDb();
            lmd.execute();
        }

        /**
         * @param v View of the button being clicked
         */
        @Override
        public void onClick(View v) {
            //checks if the contact org map contains the match between the refugee and the found refugee
            //if so result is set to true and the contactDoneFragment is loaded
            //else, if the refugee has not previously contacted to org about this match
            //then contactOrganizationFragment is loaded, to establish the contact.
            if (v == but) {
                Boolean result = false;
                anActivity = (MainActivity) getActivity();
                HashMap<Integer, ArrayList<String>> contactMap = dbHelper.getContactOrgMap();
                for (Integer conId : contactMap.keySet()
                        ) {
                    ArrayList<String> conArray = contactMap.get(conId);
                    if (conArray.get(1).equals(dbHelper.getCurrentMatchedRefId()) &&
                            conArray.get(0).equals(dbHelper.getRefId())) {
                        result = true;
                    }
                }
                anActivity = (MainActivity) getActivity();
                if (result) {
                    android.support.v4.app.Fragment frag = new ContactDoneFragment();
                    anActivity.getmTitle().setText(dbHelper.getFoundRefugee().getName());
                    anActivity.loadFragment(frag);
                } else {
                    anActivity.setContactOrgType("Match");
                    android.support.v4.app.Fragment frag = new ContactOrganizationFragment();
                    anActivity.loadFragment(frag);
                }
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list refugee names and nicknames
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
                if (v == select_ref_text) {
                    final CustomDialog cdd = new CustomDialog(getActivity());
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                    cdd.show();
                    Window view = cdd.getWindow();
                    TextView titleView = view.findViewById(R.id.dialog_tit);
                    titleView.setText(R.string.select_ref);
                    ListView listView = view.findViewById(R.id.list_dialog);
                    final HashMap<Integer, Refugee> refMap = dbHelper.getMatchedRefugees();
                    final ArrayList<String> refDetailsArray = new ArrayList<>();
                    final ArrayList<Refugee> refArray = new ArrayList<>();
                    final ArrayList<String> refIdArray = new ArrayList<>();
                    for (Integer id : refMap.keySet()) {
                        System.out.println(id);
                        refArray.add(refMap.get(id));
                        String refugee = refMap.get(id).getName() + " : " + refMap.get(id).getNickname();
                        refDetailsArray.add(refugee);
                        System.out.println(refMap.get(id).toString());
                        refIdArray.add(id.toString());
                    }
                    ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.list_item, refDetailsArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
// TODO Auto-generated method stub
                            String ref = parent.getItemAtPosition(position).toString();
                            select_ref_text.setText(ref);
                            String idRef = refIdArray.get(position);
                            int idRefInt = Integer.parseInt(idRef);
                            dbHelper.setCurrentMatchedRefId(idRef);
                            dbHelper.setFoundRefugee(refMap.get(idRefInt));
                            but.setVisibility(View.VISIBLE);
                            cdd.dismiss();
                        }
                    });
                }
            }
    }

    /**
     * Simple Fragment. Shows text description of how to contact an organisation on the following screen
     */
    @SuppressWarnings("ConstantConditions")
    public static class ContactOrganizationNoMatchInsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        Button but;
        TextView select_family_text;
        private TextView select_fam_text;
        private final int twentyeightdays = 28;
        private int daysToWait;
        private MainActivity anActivity;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_contact_org_no_match_ins, viewGroup, false);

            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.getmTitle().setText(R.string.contact_org);
            but = view.findViewById(R.id.cont_no_match_button);
            but.setOnClickListener(this);
            select_fam_text = view.findViewById(R.id.select_fam_dialog);
            select_fam_text.setOnClickListener(this);


            TextView tv = view.findViewById(R.id.ins_connect_fam);
            Typeface titleFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);
            return view;
        }

        /**
         * @param v View Button view to be clicked.
         */
        @Override
        public void onClick(View v) {
            //first conditional check, checks if 28 days have elapsed since the user
            //elected the family member missing. if this fails, the user is notified
            //of the days remaining.
            //the second, nested conditional check, checks if the contactOrgMap contains
            //the missing family member. This check is to see if the contact to the aid organisation
            //has already been made. If so, the user is
            //if so result is set to true and the contactDoneFragment is loaded
            //else, if the refugee has not previously contacted the org about their missing family member
            //then contactOrganizationFragment is loaded, to establish the contact.
            if (v == but) {
                if (daysHaveElapsed()) {
                    Boolean result = false;
                    anActivity = (MainActivity) getActivity();
                    HashMap<Integer, ArrayList<String>> contactMap = dbHelper.getContactOrgMap();
                    System.out.println(dbHelper.getContactOrgMap());
                    for (Integer conId: contactMap.keySet()
                         ) {
                        ArrayList<String> conArray = contactMap.get(conId);
                        if (conArray.get(4).equals(dbHelper.getMiss_id()) && conArray.get(0).equals(dbHelper.getRefId())){
                            result = true;
                        }
                    }
                    anActivity = (MainActivity) getActivity();
                    if (result) {
                        android.support.v4.app.Fragment frag = new ContactDoneFragment();
                        anActivity.getmTitle().setText(dbHelper.getCurrentFamMember().getName());
                        anActivity.loadFragment(frag);
                    }
                    else {
                        anActivity.setContactOrgType("NoMatch");
                        android.support.v4.app.Fragment frag = new ContactOrganizationFragment();
                        anActivity.loadFragment(frag);
                    }

                }
                else{
                    alertDaysRemaining();
                }
                }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list family member names and nicknames.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            if (v == select_fam_text) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.select_fam);
                ListView listView = view.findViewById(R.id.list_dialog);
                HashMap<Integer, Refugee> famMap = dbHelper.getMissingFamLinkedToCurrentRef();
                final ArrayList<String> famDetailsArray = new ArrayList<>();
                final ArrayList<Refugee> famArray = new ArrayList<>();
                final ArrayList<String> famIdArray = new ArrayList<>();
                for (Integer id : famMap.keySet()) {
                    System.out.println(id);
                    famArray.add(famMap.get(id));
                    String famMember = famMap.get(id).getName() + " : " + famMap.get(id).getNickname();
                    famDetailsArray.add(famMember);
                    System.out.println(famMap.get(id).toString());
                    famIdArray.add(id.toString());
                }
                ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.list_item, famDetailsArray);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        String ref = parent.getItemAtPosition(position).toString();
                        select_fam_text.setText(ref);
                        dbHelper.setCurrentFamMember(famArray.get(position));
                        dbHelper.setMiss_id(famIdArray.get(position));
                        System.out.println(dbHelper.getCurrentFamMember());
                        but.setVisibility(View.VISIBLE);
                        cdd.dismiss();
                    }
                });
            }
            }

        /**
         * Dialog stating how many days are remaining. until the user can contact
         * an aid org
         */
        private void alertDaysRemaining() {
            final CustomAlertDialog cad = new CustomAlertDialog(getActivity());
            cad.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
            cad.show();
            Window view = cad.getWindow();
            TextView titleView = view.findViewById(R.id.dialog_alert_text_title);
            titleView.setText(R.string.error);
            TextView tx= view.findViewById(R.id.dialog_alert_text);
            tx.setText(getString(R.string.please_wait) + " " + twentyeightdays + " " + getString(R.string.please_wait_two));
            Button but = view.findViewById(R.id.ok_alert_dialog);
            but.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cad.dismiss();
                }
            } );
        }


        /**
         * @return true if 28 days have elapsed. false if not
         */
        private boolean daysHaveElapsed() {
            boolean result;
            String dayFamMemberCreated = dbHelper.getCurrentFamMember().getDateCreated();
            org.threeten.bp.LocalDate dateCreated = convertStringToDate(dayFamMemberCreated);
            String todaysDateString = formattedDateString();
            org.threeten.bp.LocalDate todaysDate = convertStringToDate(todaysDateString);
            org.threeten.bp.Period p = org.threeten.bp.Period.between(dateCreated, todaysDate);
            int daysElapsed = p.getDays();
            if (daysElapsed < twentyeightdays){
                daysToWait = twentyeightdays - daysElapsed;
                result = false;
            }
            else{
            result = true;
            }
            return result;
        }

        /**
         * @param hotDateString string representing a date
         * @return a date object
         * converts a string representing a date into a LocalDate object
         */
        private org.threeten.bp.LocalDate convertStringToDate(String hotDateString) {
            org.threeten.bp.format.DateTimeFormatter formatter = org.threeten.bp.format.DateTimeFormatter.ofPattern("dd/MM/yy");
            org.threeten.bp.LocalDate parsedDate = org.threeten.bp.LocalDate.parse(hotDateString, formatter);
            return parsedDate;
        }

        /**
         * @return String formatted todays date string
         */
        private String formattedDateString() {
            String myFormat = "dd/MM/yy"; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
            Calendar c = Calendar.getInstance();
            String date = sdf.format(c.getTime());
            return date;
        }
    }

    /**
     * fragment povides instructions on performing a search. they search is conducted
     * before the search results are loaded into SearchFragment
     */
    @SuppressWarnings("ConstantConditions")
    public static class SearchInsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        Button but;
        TextView select_family_text;
        private Handler mHandler;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_search_ins, viewGroup, false);

            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.getmTitle().setText(R.string.search_fam);
            but = view.findViewById(R.id.search_fam_button);
            but.setOnClickListener(this);
            mHandler = new Handler();
            TextView tv = view.findViewById(R.id.ins_connect_fam);
            Typeface titleFont = Typeface.createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);
            return view;
        }

        /**
         * async task. send message to database helper class to search for all refugees. All results above
         * 25% match are gathered.
         * after executing, progress dialog is dismissed and searchfragment is loaded
         */
        private void prepareSearchResults() {
            class PrepareSearch extends AsyncTask<String, Void, String> {
                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @Override
                protected String doInBackground(String... params) {
                    dbHelper.searchForRef();
                    return null;
                }
                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                }
            }
            PrepareSearch ps = new PrepareSearch();
            ps.execute();
            }


        /**
         * @param v Button View
         *          on click prepares the search result and prepares the dialog
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                //
                    dialog = ProgressDialog.show(getActivity(), "",
                            "Please wait a moment....", true);
                prepareSearchResults();


            }
        }
    }


    /**
     * Displays the inbox of voicemails recieved by the current refugee. each inbox item can be clicked to
     * produce the recieved voicemail.
     */
    public static class VoiceInboxFragment extends android.support.v4.app.Fragment {
        private static RecyclerView.Adapter adapter;
        private RecyclerView.LayoutManager layoutManager;
        private static RecyclerView recyclerView;
        private static ArrayList<Refugee> data;
        static View.OnClickListener myOnClickListener;
        private static ArrayList<Integer> removedItems;
        private ProgressDialog dialog;
        private ArrayList<Integer> keySet;
        private ArrayList<Voicemail> vmdata;

        /**
         * @param inflater inflates xml layout
         * @param viewGroup view container
         * @param savedInstanceState previous state of the fragment
         * @return  View
         * a progress dialog box is created and shown, whilst loadInbox
         * method is executing.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            if (viewGroup != null) {
                viewGroup.removeAllViews();
                MainActivity anActivity = (MainActivity) getActivity();
                anActivity.getmTitle().setText(R.string.inbox);
            }
            final View view = inflater.inflate(R.layout.fragment_inbox, viewGroup, false);

            recyclerView = view.findViewById(R.id.my_recycler_view);
            dialog = ProgressDialog.show(getActivity(), "",
                    "Please wait a moment....", true);
            loadInbox();

            return view;
        }

        /**
         * async task. on execution of task, gets all recieved voicemails for the current refugee
         * post execution, setInboxItems is called to load the inbox items into the
         * recycler view
         */
        private void loadInbox() {
            class LoadInboxDb extends AsyncTask<Object, Object, Object> {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.getRecievedVoicemailsForRef();
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    setInboxItems();
                }
            }
            LoadInboxDb lid = new LoadInboxDb();
            lid.execute();
        }

        /**
         * async task. retrieves the Refugees and keys from currentRecievedRefugeeMap()
         * retrieves the Voicemails from currentVoicemailMap.
         * post execution, calls setAdaptor, to feed these variables into the adaptor
         */
        private void setInboxItems() {
            class LoadInboxD extends AsyncTask<Object, Object, Object> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    Collection<Refugee> tempRefs = dbHelper.getCurrentRecievedRefugeeMap().values();
                    Set<Integer> tempkey = dbHelper.getCurrentRecievedRefugeeMap().keySet();
                    keySet = new ArrayList<>(tempkey);
                    data = new ArrayList<>(tempRefs);
                    Collection<Voicemail> tempVms = dbHelper.getCurrentVoicemailMap().values();
                    vmdata = new ArrayList<>(tempVms);
                    System.out.println("Reached me" + dbHelper.getCurrentVoicemailMap());                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    setAdaptor();
                }
            }
            LoadInboxD lid = new LoadInboxD();
            lid.execute();
            dialog.dismiss();
        }

        /**
         * if conditional check, checks if the refugee has recieved any voicemails.
         * if they have, voicemail adapter takes the voicemail and refugee sender details
         * and initialises a VoiceMailAdapter. This adapter is set in the recyclerview
         * and adapter.notifyDataSetChanged() is called to referesh the recyclerview
         * on screen to display the inbox items.
         */
        private void setAdaptor() {
                if (!dbHelper.getCurrentVoicemailMap().isEmpty()) {
                recyclerView.setHasFixedSize(true);
                layoutManager = new LinearLayoutManager(getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                adapter = new VoicemailAdapter(data, keySet, vmdata, getContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Displays the sentbox of voicemails sent by the current refugee. each sentbox item can be clicked to
     * produce the sent voicemail.
     */
        public static class VoiceSentboxFragment extends android.support.v4.app.Fragment {
            private static RecyclerView.Adapter adapter;
            private RecyclerView.LayoutManager layoutManager;
            private static RecyclerView recyclerView;
            private static ArrayList<Refugee> data;
            static View.OnClickListener myOnClickListener;
            private static ArrayList<Integer> removedItems;
            private ProgressDialog dialog;
            private ArrayList<Integer> keySet;
            private ArrayList<Voicemail> vmdata;

        /**
         * @param inflater inflates xml layout
         * @param viewGroup view container
         * @param savedInstanceState previous state of the fragment
         * @return  View
         * a progress dialog box is created and shown, whilst loadVoicemails
         * method is executing.
         */
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
                if (viewGroup != null) {
                    viewGroup.removeAllViews();
                    MainActivity anActivity = (MainActivity) getActivity();
                    anActivity.getmTitle().setText(R.string.sentbox);
                }
                final View view = inflater.inflate(R.layout.fragment_inbox, viewGroup, false);

                recyclerView = view.findViewById(R.id.my_recycler_view);
                dialog = ProgressDialog.show(getActivity(), "",
                        "Please wait a moment....", true);
                loadVoicemails();

                return view;
            }

        /**
         * async task. on execution of task, gets all sent voicemails for the current refugee
         * post execution, setSentboxItems is called to load the sentbox items into the
         * recycler view
         */
            private void loadVoicemails() {
                class LoadVoicemailsDb extends AsyncTask<Object, Object, Object> {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Object... params) {
                        dbHelper.getSentVoicemailsForRef();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        setSentboxItems();
                    }
                }
                LoadVoicemailsDb lvd = new LoadVoicemailsDb();
                lvd.execute();
            }

        /**
         * async task. retrieves the Refugees and keys from currentSentRefugeeMap()
         * retrieves the Voicemails from currentVoicemailMap.
         * post execution, calls setAdaptor, to feed these variables into the adaptor
         */
            private void setSentboxItems() {
                class LoadSentBoxD extends AsyncTask<Object, Object, Object> {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Object... params) {
                        Collection<Refugee> tempRefs = dbHelper.getCurrentSentRefugeeMap().values();
                        Set<Integer> tempkey = dbHelper.getCurrentSentRefugeeMap().keySet();
                        keySet = new ArrayList<>(tempkey);
                        data = new ArrayList<>(tempRefs);
                        Collection<Voicemail> tempVms = dbHelper.getCurrentVoicemailMap().values();
                        vmdata = new ArrayList<>(tempVms);
                        System.out.println("Reached me" + dbHelper.getCurrentSentRefugeeMap());
                        System.out.println("Reached me" + dbHelper.getCurrentVoicemailMap());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        setAdaptor();
                    }
                }
                LoadSentBoxD lsd = new LoadSentBoxD();
                lsd.execute();
                dialog.dismiss();
            }

        /**
         * if conditional check, checks if the refugee has sent any voicemails.
         * if they have, voicemail adapter takes the voicemail and refugee sender details
         * and initialises a VoiceMailAdapter. This adapter is set in the recyclerview
         * and adapter.notifyDataSetChanged() is called to referesh the recyclerview
         * on screen to display the inbox items.
         */
            private void setAdaptor() {
                if (!dbHelper.getCurrentVoicemailMap().isEmpty()) {
                    recyclerView.setHasFixedSize(true);
                    layoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(layoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    adapter = new VoicemailAdapter(data, keySet, vmdata, getContext());
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        }

    /**
     * Fragment for updating details of the currently selected refugee. Presenter class, RefugeePresenter, checks details are valid and
     * the refugee is updated locally and in the database
     */
        @SuppressWarnings("ConstantConditions")
        public static class UpdateRefugeeDetailsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
            Button but;
            private RefugeePresenter refugeePresenter;
            private Refugee refugee;
            String age;
            String nationality;
            String gender;
            TextView nameView;
            TextView pobView;
            TextView tribeView;
            TextView nicknameView;
            TextView occupationView;
            TextView localAreaView;
            TextView ageSpinner;
            TextView countrySpinner;
            TextView genderSpinner;

            private HashMap<Integer, ArrayList<String>> personalMap;

        /**
         * @param inflater inflater. inflates xml layout to screen
         * @param viewGroup contains layout views
         * @param savedInstanceState fragment's previously saved state
         * initialises refugee presenter.
        Text Watchers listen for user inputting text. On text being input (e.g name), the label is set to describe
        below field (e.g name)
         * @return inflated View
         */
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
                if (viewGroup != null) {
                    viewGroup.removeAllViews();
                }
                final View view = inflater.inflate(R.layout.fragment_current_ref_details, viewGroup, false);
                MainActivity anActivity = (MainActivity) getActivity();
                anActivity.getmTitle().setText(R.string.update_ref_details);
                but = view.findViewById(R.id.add_ref_button);
                but.setOnClickListener(this);
                String refIdString = dbHelper.getRefId();
                System.out.println(refIdString);

                int refId = Integer.parseInt(refIdString);
                // personalMap = dbHelper.getRefMap();

                refugeePresenter = new RefugeePresenter(getContext());
                nationality = dbHelper.getCurrentRefugee().getNationality();
                age = dbHelper.getCurrentRefugee().getAgeGroup();
                gender = dbHelper.getCurrentRefugee().getGen();
                countrySpinner = view.findViewById(R.id.spnCountry);
                countrySpinner.setText(nationality);
                countrySpinner.setOnClickListener(this);
                ageSpinner = view.findViewById(R.id.age_spinner);
                ageSpinner.setText(age);
                ageSpinner.setOnClickListener(this);
                ageSpinner.setOnClickListener(this);
                genderSpinner = view.findViewById(R.id.gender_spinner);
                genderSpinner.setText(gender);
                genderSpinner.setOnClickListener(this);

                nameView = (EditText) view.findViewById(R.id.name_ref);
                nameView.setText(dbHelper.getCurrentRefugee().getName());

                nameView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.name_label);
                            label.setText("");
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TextView label = view.findViewById(R.id.name_label);
                        label.setText(R.string.full_name);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.name_label);
                            label.setText(R.string.full_name);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }
                });
                //
                pobView = (EditText) view.findViewById(R.id.place_of_birth_text);
                pobView.setText(dbHelper.getCurrentRefugee().getPlaceOfBirth());
                pobView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.city_label);
                            label.setText("");
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TextView label = view.findViewById(R.id.city_label);
                        label.setText(R.string.place_of_birth);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.city_label);
                            label.setText(R.string.place_of_birth);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }
                });
                //
                nicknameView = (EditText) view.findViewById(R.id.nickname_text);
                nicknameView.setText(dbHelper.getCurrentRefugee().getNickname());
                nicknameView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.nickname_label);
                            label.setText("");
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TextView label = view.findViewById(R.id.nickname_label);
                        label.setText(R.string.nickname);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.nickname_label);
                            label.setText(R.string.nickname);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }
                });
                //
                tribeView = (EditText) view.findViewById(R.id.tribe_text);
                tribeView.setText(dbHelper.getCurrentRefugee().getTribe());

                tribeView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.tribe_label);
                            label.setText("");
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TextView label = view.findViewById(R.id.tribe_label);
                        label.setText(R.string.tribe_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.tribe_label);
                            label.setText(R.string.tribe_label);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }
                });
                //
                localAreaView = (EditText) view.findViewById(R.id.loca_area_text);
                localAreaView.setText(dbHelper.getCurrentRefugee().getLoc());

                localAreaView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.village_label);
                            label.setText("");
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TextView label = view.findViewById(R.id.village_label);
                        label.setText(R.string.local_area_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.village_label);
                            label.setText(R.string.local_area_label);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }
                });
                //
                occupationView = (EditText) view.findViewById(R.id.occupation_text_view);
                occupationView.setText(dbHelper.getCurrentRefugee().getOcc());
                occupationView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.occupation_label);
                            label.setText("");
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TextView label = view.findViewById(R.id.occupation_label);
                        label.setText(R.string.occupation_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.occupation_label);
                            label.setText(R.string.occupation_label);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }
                });
                return view;

            }

            @Override
            public void onClick(View v) {
                if (v == but) {
                    if (checkRefugeeDetails()) {
                        changeRefugeeDb();
                    }
                }
                // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
                //adapter, to list gender options.
                //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
                //selected item from the list view
                if (v == genderSpinner) {
                    final CustomDialog cdd = new CustomDialog(getActivity());
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                    cdd.show();
                    Window view = cdd.getWindow();
                    TextView titleView = view.findViewById(R.id.dialog_tit);
                    titleView.setText(R.string.gender_label);
                    ListView listView = view.findViewById(R.id.list_dialog);
                    ArrayAdapter genAdapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_array, R.layout.list_item);
                    genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    listView.setAdapter(genAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
// TODO Auto-generated method stub
                            gender = parent.getItemAtPosition(position).toString();
                            TextView label = getView().findViewById(R.id.gender_label);
                            label.setText(R.string.gender_label);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                            TextView genText = getView().findViewById(R.id.gender_spinner);
                            genText.setText(gender);

                            cdd.dismiss();
                        }
                    });
                }
                // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
                //adapter, to list nationality options, retrieved from countriesNameArray by the CountrySpinnerAdapter
                //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
                //selected country
                //OnQueryTextListener set on searchview in dialog. Listens for text being input into searchview. on input, filter
                //takes inputted charecters and filters down countries to those that contain the sequence of characters
                if (v == countrySpinner) {
                    final CustomCountryDialog cdd = new CustomCountryDialog(getActivity());
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                    cdd.show();
                    Window view = cdd.getWindow();
                    TextView titleView = view.findViewById(R.id.dialog_tit);
                    titleView.setText(R.string.nationality);
                    ListView listView = view.findViewById(R.id.list_dialog);
                    final CountrySpinnerAdapter natAdapter = new CountrySpinnerAdapter(getContext(), countriesNameArray);
                    natAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    listView.setAdapter(natAdapter);
                    listView.setTextFilterEnabled(true);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
// TODO Auto-generated method stub
                            nationality = parent.getItemAtPosition(position).toString();
                            TextView label = getView().findViewById(R.id.nationality_label);
                            label.setText(R.string.nationality);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                            TextView natText = getView().findViewById(R.id.spnCountry);
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
                    }); }
                // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
                //adapter, to list age options, retrieved from string array resource , age_group_array.
                //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
                //selected item from the list view
                if (v == ageSpinner) {
                    final CustomDialog cdd = new CustomDialog(getActivity());
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                    cdd.show();
                    Window view = cdd.getWindow();
                    TextView titleView = view.findViewById(R.id.dialog_tit);
                    titleView.setText(R.string.age_label);
                    ListView listView = view.findViewById(R.id.list_dialog);
                    ArrayAdapter genAdapter = ArrayAdapter.createFromResource(getContext(), R.array.age_group_array, R.layout.list_item);
                    genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    listView.setAdapter(genAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
// TODO Auto-generated method stub
                            age = parent.getItemAtPosition(position).toString();
                            TextView label = getView().findViewById(R.id.age_label);
                            label.setText(R.string.age_label);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                            TextView ageText = getView().findViewById(R.id.age_spinner);
                            ageText.setText(age);

                            cdd.dismiss();
                        }
                    });
                }
            }

        /**
         * async task. send message to database helper class to update the refugee in the database.
         * after executing, sends message toastMsg, to send message to UI denoting the refugee being updated.
         */
            private void changeRefugeeDb() {
                class ChangeRefugeeDb extends AsyncTask<Object, Object, Object> {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Object... params) {
                        dbHelper.changeRefugeeDb("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/change_refugee_details.php", refugee);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        loadFragment();
                    }
                }
                ChangeRefugeeDb crd = new ChangeRefugeeDb();
                crd.execute();
            }

        /**
         creates a refugee object by feeding the method parameters into the
         Refugee constructer.
         */
            private void createRefugee(String loginName, String name, String age,
                                       String nationality, String nickname, String placeOfBirth,
                                       String tribe, String gender, String localArea, String occupation) {
                refugee = new Refugee(loginName, name, age, nationality,
                        nickname, placeOfBirth, tribe, gender, localArea, occupation);
                System.out.println(refugee.toString());
                //addRefugeetoDB();
            }

        /**
         * refugee presenter checks the inputted details for each field (e.g name, relationship). sends message createrefugee if successful
         * @return true if inputted details are valid. false if one or more of the fields are not valid (e.g name is less then 6 characters)
         * sets label of invalid field(s) to error message and changes label colour to red.
         */
            private boolean checkRefugeeDetails() {

                Boolean result = false;
                TextView refugeeNameView = getView().findViewById(R.id.name_ref);
                TextView refugeeNameLabel = getView().findViewById(R.id.name_label);
                String refugeeName = refugeeNameView.getText().toString();

                TextView refugeeTribeView = getView().findViewById(R.id.tribe_text);
                String refugeeTribe = refugeeTribeView.getText().toString();
                TextView refugeeTribeLabel = getView().findViewById(R.id.tribe_label);

                TextView refugeePobView = getView().findViewById(R.id.place_of_birth_text);
                String refugeePob = refugeePobView.getText().toString();
                TextView refugeePobLabel = getView().findViewById(R.id.city_label);

                TextView refugeeNicknameView = getView().findViewById(R.id.nickname_text);
                String refugeeNickname = refugeeNicknameView.getText().toString();
                TextView refugeeNicknameLabel = getView().findViewById(R.id.nickname_label);

                TextView refugeeLocalView = getView().findViewById(R.id.loca_area_text);
                String refugeeLocal = refugeeLocalView.getText().toString();
                TextView refugeeLocalLabel = getView().findViewById(R.id.village_label);

                TextView refugeeOccupationView = getView().findViewById(R.id.occupation_text_view);
                String refugeeOccupation = refugeeOccupationView.getText().toString();
                TextView refugeeOccupationLabel = getView().findViewById(R.id.occupation_label);

                TextView refugeeAgeLabel = getView().findViewById(R.id.age_label);
                TextView refugeeNatLabel = getView().findViewById(R.id.nationality_label);
                TextView refugeeGenderLabel = getView().findViewById(R.id.gender_label);

                if (refugeePresenter.init(refugeePob, nationality, refugeeName,
                        age, refugeeNickname, refugeeTribe, gender, refugeeLocal, refugeeOccupation)) {
                    result = true;
                    CognitoUser cognitoUser = AppHelper.getPool().getCurrentUser();
                    String loginName = cognitoUser.getUserId();
                    createRefugee(loginName, refugeeName, age, nationality,
                            refugeeNickname, refugeePob, refugeeTribe, gender, refugeeLocal, refugeeOccupation);
                } else {
                    refugeeNicknameLabel.setTextColor(refugeePresenter.getNickNameLabelColor());
                    refugeeNicknameLabel.setText(refugeePresenter.getNickNameLabel());
                    refugeeNameLabel.setTextColor(refugeePresenter.getNameLabelColor());
                    refugeeNameLabel.setText(refugeePresenter.getNameLabel());
                    refugeeNatLabel.setTextColor(refugeePresenter.getNationalityLabelColor());
                    refugeeNatLabel.setText(refugeePresenter.getNationalityLabel());
                    refugeePobLabel.setTextColor(refugeePresenter.getPlaceOfBirthLabelColor());
                    refugeePobLabel.setText(refugeePresenter.getPlaceOfBirthLabel());
                    refugeeTribeLabel.setTextColor(refugeePresenter.getTribeLabelColor());
                    refugeeTribeLabel.setText(refugeePresenter.getTribeLabel());
                    refugeeAgeLabel.setTextColor(refugeePresenter.getAgeLabelColor());
                    refugeeAgeLabel.setText(refugeePresenter.getAgeLabel());
                    refugeeLocalLabel.setTextColor(refugeePresenter.getLocalAreaLabelColor());
                    refugeeLocalLabel.setText(refugeePresenter.getLocalAreaLabel());
                    refugeeOccupationLabel.setTextColor(refugeePresenter.getOccupationLabelColor());
                    refugeeOccupationLabel.setText(refugeePresenter.getOccupationLabel());
                    refugeeGenderLabel.setTextColor(refugeePresenter.getGenderLabelColor());
                    refugeeGenderLabel.setText(refugeePresenter.getGenderLabel());


                }
                return result;
            }

            public void loadFragment() {
                        makeToast();
                        android.support.v4.app.Fragment frag = new HomeFragment();
                        MainActivity anActivity = (MainActivity) getActivity();
                        anActivity.loadFragment(frag);
            }

        /**
         * displays refugee name + "added" to the user interface
         */
            public void makeToast(){
                Toast.makeText(getContext(), "Updated " + dbHelper.getCurrentRefugee().getName() +
                        " details", Toast.LENGTH_LONG).show();
            }
        }

    /**
     * Fragment for updating the details of a user who is an Aid Worker. Presenter class, AidWorkerPresenter, checks details are valid.
     */
        public static class PersonalDetailsAidWorkerFragment extends android.support.v4.app.Fragment implements View.OnClickListener {

            private AidWorker aidworker;
            private TextView tx;
            private AidWorkerPresenter aidWorkerPresenter;
            private Calendar myCalendar;
            private DatePickerDialog.OnDateSetListener date;
            private String gender; private String nationality; private String sector; private String work_country; private String aidOrg;
            private TextView dobtx; private EditText supervisor; private EditText position; private EditText name;
            private ArrayAdapter<CharSequence> natAdapter; private ArrayAdapter<CharSequence> workCountryAdapter; private ArrayAdapter<CharSequence> genderAdapter;
            private ArrayAdapter<CharSequence> sectorAdapter; ArrayAdapter<CharSequence> positionAdapter;
            private TextView natSpinner; private TextView countrySpinner; private TextView genderSpinner;
            private TextView sectorSpinner; private TextView aidOrgSpinner;

            /**
             * @param inflater inflater. inflates xml layout to screen
             * @param viewGroup contains layout views
             * @param savedInstanceState activity's previously saved state
             * initialises aid worker presenter.
            Text Watchers listen for user inputting text. On text being input (e.g name), the label is set to describe
            below field (e.g name)
             * @return inflated View
             */
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
                if (viewGroup != null) {
                    viewGroup.removeAllViews();
                }
                final View view = inflater.inflate(R.layout.fragment_personal_aid_worker_details, viewGroup, false);
                MainActivity anActivity = (MainActivity) getActivity();
                anActivity.getmTitle().setText(R.string.personal_details);
                aidWorkerPresenter = new AidWorkerPresenter(getContext());

                nationality= dbHelper.getAidWorker().getNationality(); gender=dbHelper.getAidWorker().getGender();
                sector= dbHelper.getAidWorker().getSector(); aidOrg= dbHelper.getAidWorker().getAidOrganization();
                work_country= dbHelper.getAidWorker().getWorkCountry();

                //set user input listeners
                view.findViewById(R.id.registration_button).setOnClickListener(this);
                view.findViewById(R.id.dob_aid_button).setOnClickListener(this);

                //sets on click listeners
                countrySpinner = view.findViewById(R.id.work_country_spinner);
                countrySpinner.setText(nationality);
                countrySpinner.setOnClickListener(this);

                genderSpinner = view.findViewById(R.id.gender_aid_spinner);
                genderSpinner.setText(gender);
                genderSpinner.setOnClickListener(this);

                natSpinner = view.findViewById(R.id.aid_nationality_spinner);
                natSpinner.setText(nationality);
                natSpinner.setOnClickListener(this);

                aidOrgSpinner = view.findViewById(R.id.aid_org_spinner);
                aidOrgSpinner.setText(aidOrg);
                aidOrgSpinner.setOnClickListener(this);

                sectorSpinner = view.findViewById(R.id.sector_spinner);
                sectorSpinner.setText(sector);
                sectorSpinner.setOnClickListener(this);

                dobtx = view.findViewById(R.id.dob_aid_worker);
                dobtx.setText(dbHelper.getAidWorker().getDateOfBirth());

                //sets calender from date of birth string returned
                // from the aidworker class
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                Date aDate = null;
                try {
                    aDate = sdf.parse(dobtx.getText().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                myCalendar = Calendar.getInstance();
                myCalendar.setTime(aDate);

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
                name = view.findViewById(R.id.name);
                name.setText(dbHelper.getAidWorker().getName());
                name.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.name_label);
                            label.setText("");
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TextView label = view.findViewById(R.id.name_label);
                        label.setText(R.string.name);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));


                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.name_label);
                            label.setText(R.string.name);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));

                        }
                    }
                });
                //
                position = view.findViewById(R.id.position);
                position.setText(dbHelper.getAidWorker().getPosition());
                position.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.position_label);
                            label.setText("");
                            label.setTextColor(getResources().getColor(R.color.TitleColor));

                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TextView label = view.findViewById(R.id.position_label);
                        label.setText(R.string.position_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));


                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.org_label);
                            label.setText(R.string.position_label);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));

                        }
                    }
                });
                //
                dobtx = view.findViewById(R.id.dob_aid_worker);
                dobtx.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.dob_label);
                            label.setText("");
                            label.setTextColor(getResources().getColor(R.color.TitleColor));

                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TextView label = view.findViewById(R.id.dob_label);
                        label.setText(R.string.date_of_birth);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.dob_label);
                            label.setText(R.string.date_of_birth);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }
                });
//
                supervisor = view.findViewById(R.id.supervisor_text_view);
                supervisor.setText(dbHelper.getAidWorker().getSupervisor());
                supervisor.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.supervisor_label);
                            label.setText("");
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        TextView label = view.findViewById(R.id.supervisor_label);
                        label.setText(R.string.supervisor_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() == 0) {
                            TextView label = view.findViewById(R.id.supervisor_label);
                            label.setText(R.string.supervisor_label);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                        }
                    }
                });
                return view;
            }

        /**
         * @param v View which receives click input
         */
            @Override
            public void onClick(View v) {
                int i = v.getId();
                if (i == R.id.registration_button) {
                    if (checkAidworkerDetails()) {
                        changeAidWorkerDb();
                    }
                }
                // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
                //adapter, to list gender options, retrieved from string array resource , gender_array.
                //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
                //selected item from the list view
                if (v == genderSpinner) {
                    final CustomDialog cdd = new CustomDialog(getActivity());
                    //noinspection ConstantConditions
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                    cdd.show();
                    Window view = cdd.getWindow();
                    TextView titleView = view .findViewById(R.id.dialog_tit);
                    titleView.setText(R.string.gender_label);
                    ListView listView = view .findViewById(R.id.list_dialog);
                    genderAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.gender_array, R.layout.list_item);
                    genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    listView.setAdapter(genderAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
// TODO Auto-generated method stub
                            gender = parent.getItemAtPosition(position).toString();
                            TextView label = getView().findViewById(R.id.gender_label);
                            label.setText(R.string.gender_label);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                            TextView genText = getView().findViewById(R.id.gender_aid_spinner);
                            genText.setText(gender);
                            cdd.dismiss();
                        }
                    });
                }
                // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
                //adapter, to list nationality options, retrieved from countriesNameArray by the CountrySpinnerAdapter
                //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
                //selected country
                //OnQueryTextListener set on searchview in dialog. Listens for text being input into searchview. on input, filter
                //takes inputted charecters and filters down countries to those that contain the sequence of characters
                if (v == countrySpinner) {
                    final CustomCountryDialog cdd= new CustomCountryDialog(getActivity());
                    //noinspection ConstantConditions
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                    cdd.show();
                    Window view = cdd.getWindow();
                    TextView titleView = view.findViewById(R.id.dialog_tit);
                    titleView.setText(R.string.work_country_label);
                    ListView listView = view.findViewById(R.id.list_dialog);
                    final CountrySpinnerAdapter natAdapter = new CountrySpinnerAdapter(getContext(), countriesNameArray);
                    natAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    listView.setAdapter(natAdapter);
                    listView.setTextFilterEnabled(true);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
// TODO Auto-generated method stub
                            work_country = parent.getItemAtPosition(position).toString();
                            TextView label = getView().findViewById(R.id.work_country_label);
                            label.setText(R.string.work_country_label);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                            TextView natText = getView().findViewById(R.id.work_country_spinner);
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
                            natAdapter.getFilter().filter(newText);
                            return false;
                        }
                    });
                }
                // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
                //adapter, to list nationality options, retrieved from countriesNameArray by the CountrySpinnerAdapter
                //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
                //selected country
                //OnQueryTextListener set on searchview in dialog. Listens for text being input into searchview. on input, filter
                //takes inputted charecters and filters down countries to those that contain the sequence of characters
                if (v == natSpinner) {
                    final CustomCountryDialog cdd= new CustomCountryDialog(getActivity());
                    //noinspection ConstantConditions
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                    cdd.show();
                    Window view = cdd.getWindow();
                    TextView titleView = view.findViewById(R.id.dialog_tit);
                    titleView.setText(R.string.nationality);
                    ListView listView = view.findViewById(R.id.list_dialog);
                    final CountrySpinnerAdapter natAdapter = new CountrySpinnerAdapter(getContext(), countriesNameArray);
                    natAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    listView.setAdapter(natAdapter);
                    listView.setTextFilterEnabled(true);

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
// TODO Auto-generated method stub
                            nationality = parent.getItemAtPosition(position).toString();
                            TextView label = getView().findViewById(R.id.nationality_aid_label);
                            label.setText(R.string.nationality);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                            TextView natText = getActivity().findViewById(R.id.aid_nationality_spinner);
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
                // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
                //adapter, to list aid organisations, retrieved from string array resource , aid_org_array_array.
                //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
                //selected item from the list view
                if (v == aidOrgSpinner) {
                    final CustomDialog cdd= new CustomDialog(getActivity());
                    //noinspection ConstantConditions
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                    cdd.show();
                    Window view = cdd.getWindow();
                    TextView titleView = view.findViewById(R.id.dialog_tit);
                    titleView.setText(R.string.aidOrg);
                    ListView listView = view.findViewById(R.id.list_dialog);
                    ArrayAdapter genAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.aid_org_array_array, R.layout.list_item);
                    genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    listView.setAdapter(genAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
// TODO Auto-generated method stub
                            aidOrg = parent.getItemAtPosition(position).toString();
                            TextView label = getView().findViewById(R.id.org_label);
                            label.setText(R.string.aidOrg);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                            TextView ageText = getView().findViewById(R.id.aid_org_spinner);
                            ageText.setText(aidOrg);

                            cdd.dismiss();
                        }
                    });            }
                // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
                //adapter, to list sector options, retrieved from string array resource , sector_array.
                //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
                //selected item from the list view
                if (v == sectorSpinner) {
                    final CustomDialog cdd= new CustomDialog(getActivity());
                    //noinspection ConstantConditions
                    cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                    cdd.show();
                    Window view = cdd.getWindow();
                    TextView titleView = view.findViewById(R.id.dialog_tit);
                    titleView.setText(R.string.sector_label);
                    ListView listView = view.findViewById(R.id.list_dialog);
                    sectorAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.sector_array, R.layout.list_item);
                    sectorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    listView.setAdapter(sectorAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {
// TODO Auto-generated method stub
                            sector = parent.getItemAtPosition(position).toString();
                            TextView label = getView().findViewById(R.id.sector_label);
                            label.setText(R.string.sector_label);
                            label.setTextColor(getResources().getColor(R.color.TitleColor));
                            TextView ageText = getView().findViewById(R.id.sector_spinner);
                            ageText.setText(sector);

                            cdd.dismiss();
                        }
                    });            }
                //sets date of birth
                if (i == R.id.dob_aid_button) {
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            }

            //creates Aid Worker object from method parameters
            private void createAidWorker(String loginName, String name, String dob,
                                         String gender, String nationality, String workCountry,
                                         String aidOrg, String sector, String position, String supervisor){
                aidworker = new AidWorker(loginName, name, dob, gender,  nationality,
                        workCountry, aidOrg, sector, position, supervisor);
                System.out.println(aidworker.toString());
                //addAidWorkertoDB();
            }

            //sets input from text to strings. passes these strings to AidWorkerPresenter class to check if inputs are valid.
            // if successful invokes createAidWorker()
            // if unsuccessful, incorrect text inputs have labels  set to error identified by AidWorkerpresenter
            private boolean checkAidworkerDetails() {
                Boolean result = false;
                TextView aidWorkerView = getView().findViewById(R.id.name);
                String aidWorkerName = aidWorkerView.getText().toString();
                TextView aidWorkerNameLabel = getView().findViewById(R.id.name_label);

                TextView aidWorkerPositionView = getView().findViewById(R.id.position);
                String aidWorkerPosition = aidWorkerPositionView.getText().toString();
                TextView aidWorkerPositionLabel = getView().findViewById(R.id.position_label);

                TextView supervisorView = getView().findViewById(R.id.supervisor_text_view);
                String supervisor = supervisorView.getText().toString();
                TextView supervisorLabel = getView().findViewById(R.id.supervisor_label);

                TextView aidWorkerDobLabel = getView().findViewById(R.id.dob_label);
                TextView aidWorkerNatLabel = getView().findViewById(R.id.nationality_aid_label);
                TextView aidWorkerGenderLabel = getView().findViewById(R.id.gender_label);
                TextView aidWorkerWorkCountryLabel = getView().findViewById(R.id.work_country_label);
                TextView aidWorkerSectorLabel = getView().findViewById(R.id.sector_label);
                TextView aidOrgLabel = getView().findViewById(R.id.org_label);

                if (aidWorkerPresenter.init(aidOrg, nationality, aidWorkerName, myCalendar, sector,
                        gender, aidWorkerPosition, supervisor, work_country)) {
                    result = true;
                    CognitoUser cognitoUser = AppHelper.getPool().getCurrentUser();
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

                private void updateLabel() {
                String myFormat = "dd/MM/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
                dobtx.setText(sdf.format(myCalendar.getTime()));
            }

            //aid worker object and url is passed over to database helper, to add aid worker to database.
            private void changeAidWorkerDb() {
                class ChangeAidWorkerDb extends AsyncTask<Object, Object, Object> {


                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(Object... params) {
                        dbHelper.changeAidWorkerDb("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/change_aid_worker_details.php", aidworker);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        loadFragment();
                    }
                }
                ChangeAidWorkerDb caw = new ChangeAidWorkerDb();
                caw.execute();
            }
            public void loadFragment() {
                Toast.makeText(getContext(), "Updated your details,", Toast.LENGTH_LONG).show();
                android.support.v4.app.Fragment frag = new HomeFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                anActivity.loadFragment(frag);
            }
        }

    /**
     * Fragment for adding details of missing family members. Presenter class, RefugeePresenter, checks details are valid.
     */
    @SuppressWarnings("ConstantConditions")
    public static class PersonalDetailsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        Button but;
        private RefugeePresenter refugeePresenter;
        private Refugee refugee;
        String age;
        String nationality;
        String gender;
        TextView nameView;
        TextView pobView;
        TextView tribeView;
        TextView nicknameView;
        TextView occupationView;
        TextView localAreaView;
        TextView ageSpinner;
        TextView countrySpinner;
        TextView genderSpinner;

        private HashMap<Integer, ArrayList<String>> personalMap;

        /**
         * @param inflater           inflater. inflates xml layout to screen
         * @param viewGroup          contains layout views
         * @param savedInstanceState activity's previously saved state
         *                           initialises refugee presenter.
         *                           Text Watchers listen for user inputting text. On text being input (e.g name), the label is set to describe
         *                           below field (e.g name)
         * @return inflated View
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            final View view = inflater.inflate(R.layout.fragment_personal_details, viewGroup, false);
            MainActivity anActivity = (MainActivity) getActivity();
            refugeePresenter = new RefugeePresenter(getContext());
            anActivity.getmTitle().setText(R.string.personal_details);
            but = view.findViewById(R.id.add_fam_button);
            but.setOnClickListener(this);
            String refIdString = dbHelper.getRefId();
            System.out.println(refIdString);
            int refId = Integer.parseInt(refIdString);
            // personalMap = dbHelper.getRefMap();

            nationality = dbHelper.getCurrentRefugee().getNationality();
            age = dbHelper.getCurrentRefugee().getAgeGroup();
            gender = dbHelper.getCurrentRefugee().getGen();
            countrySpinner = view.findViewById(R.id.spnCountry);
            countrySpinner.setText(nationality);
            countrySpinner.setOnClickListener(this);
            ageSpinner = view.findViewById(R.id.age_spinner);
            ageSpinner.setText(age);
            ageSpinner.setText(age);
            ageSpinner.setOnClickListener(this);
            ageSpinner.setOnClickListener(this);
            genderSpinner = view.findViewById(R.id.gender_spinner);
            genderSpinner.setText(gender);
            genderSpinner.setOnClickListener(this);

            nameView = (EditText) view.findViewById(R.id.name_ref);
            nameView.setText(dbHelper.getCurrentRefugee().getName());

            nameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.name_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.name_label);
                    label.setText(R.string.full_name);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.name_label);
                        label.setText(R.string.full_name);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            pobView = (EditText) view.findViewById(R.id.place_of_birth_text);
            pobView.setText(dbHelper.getCurrentRefugee().getPlaceOfBirth());
            pobView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.city_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.city_label);
                    label.setText(R.string.place_of_birth);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.city_label);
                        label.setText(R.string.place_of_birth);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            nicknameView = (EditText) view.findViewById(R.id.nickname_text);
            nicknameView.setText(dbHelper.getCurrentRefugee().getNickname());
            nicknameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.nickname_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.nickname_label);
                    label.setText(R.string.nickname);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.nickname_label);
                        label.setText(R.string.nickname);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            tribeView = (EditText) view.findViewById(R.id.tribe_text);
            tribeView.setText(dbHelper.getCurrentRefugee().getTribe());

            tribeView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.tribe_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.tribe_label);
                    label.setText(R.string.tribe_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.tribe_label);
                        label.setText(R.string.tribe_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            localAreaView = (EditText) view.findViewById(R.id.loca_area_text);
            localAreaView.setText(dbHelper.getCurrentRefugee().getLoc());

            localAreaView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.village_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.village_label);
                    label.setText(R.string.local_area_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.village_label);
                        label.setText(R.string.local_area_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            occupationView = (EditText) view.findViewById(R.id.occupation_text_view);
            occupationView.setText(dbHelper.getCurrentRefugee().getOcc());
            occupationView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.occupation_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.occupation_label);
                    label.setText(R.string.occupation_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.occupation_label);
                        label.setText(R.string.occupation_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            return view;

        }

        /**
         * @param v View which recieves click input
         */
        @Override
        public void onClick(View v) {
            if (v == but) {
                if (checkRefugeeDetails()) {
                    changeRefugeeDb();
                }
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list gender options, retrieved from string array resource , gender_array.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            if (v == genderSpinner) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.gender_label);
                ListView listView = view.findViewById(R.id.list_dialog);
                ArrayAdapter genAdapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_array, R.layout.list_item);
                genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(genAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        gender = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.gender_label);
                        label.setText(R.string.gender_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView genText = getView().findViewById(R.id.gender_spinner);
                        genText.setText(gender);

                        cdd.dismiss();
                    }
                });
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list nationality options, retrieved from countriesNameArray by the CountrySpinnerAdapter
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected country
            //OnQueryTextListener set on searchview in dialog. Listens for text being input into searchview. on input, filter
            //takes inputted charecters and filters down countries to those that contain the sequence of characters
            if (v == countrySpinner) {
                final CustomCountryDialog cdd = new CustomCountryDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.nationality);
                ListView listView = view.findViewById(R.id.list_dialog);
                final CountrySpinnerAdapter natAdapter = new CountrySpinnerAdapter(getContext(), countriesNameArray);
                natAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(natAdapter);
                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        nationality = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.nationality_label);
                        label.setText(R.string.nationality);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView natText = getView().findViewById(R.id.spnCountry);
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
                searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {

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
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list age options, retrieved from string array resource , age_group_array.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            if (v == ageSpinner) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.age_label);
                ListView listView = view.findViewById(R.id.list_dialog);
                ArrayAdapter genAdapter = ArrayAdapter.createFromResource(getContext(), R.array.age_group_array, R.layout.list_item);
                genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(genAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        age = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.age_label);
                        label.setText(R.string.age_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView ageText = getView().findViewById(R.id.age_spinner);
                        ageText.setText(age);

                        cdd.dismiss();
                    }
                });
            }
        }

        private void changeRefugeeDb() {
            class ChangeRefugeeDb extends AsyncTask<Object, Object, Object> {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.changeRefugeeDb("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/change_refugee_details.php", refugee);
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    toastMsg();
                }
            }
            ChangeRefugeeDb crd = new ChangeRefugeeDb();
            crd.execute();
        }

        /**
         * creates a refugee object by feeding the method parameters into the
         * Refugee constructor.
         */
        private void createRefugee(String loginName, String name, String age,
                                   String nationality, String nickname, String placeOfBirth,
                                   String tribe, String gender, String localArea, String occupation) {
            refugee = new Refugee(loginName, name, age, nationality,
                    nickname, placeOfBirth, tribe, gender, localArea, occupation);
            System.out.println(refugee.toString());
            //addRefugeetoDB();
        }

        /**
         * refugee presenter checks the inputted details for each field (e.g name, relationship). sends message createrefugee if successful
         *
         * @return true if inputted details are valid. false if one or more of the fields are not valid (e.g name is less then 6 characters)
         * sets label of invalid field(s) to error message and changes label colour to red.
         */
        private boolean checkRefugeeDetails() {

            Boolean result = false;
            TextView refugeeNameView = getView().findViewById(R.id.name_ref);
            TextView refugeeNameLabel = getView().findViewById(R.id.name_label);
            String refugeeName = refugeeNameView.getText().toString();

            TextView refugeeTribeView = getView().findViewById(R.id.tribe_text);
            String refugeeTribe = refugeeTribeView.getText().toString();
            TextView refugeeTribeLabel = getView().findViewById(R.id.tribe_label);

            TextView refugeePobView = getView().findViewById(R.id.place_of_birth_text);
            String refugeePob = refugeePobView.getText().toString();
            TextView refugeePobLabel = getView().findViewById(R.id.city_label);

            TextView refugeeNicknameView = getView().findViewById(R.id.nickname_text);
            String refugeeNickname = refugeeNicknameView.getText().toString();
            TextView refugeeNicknameLabel = getView().findViewById(R.id.nickname_label);

            TextView refugeeLocalView = getView().findViewById(R.id.loca_area_text);
            String refugeeLocal = refugeeLocalView.getText().toString();
            TextView refugeeLocalLabel = getView().findViewById(R.id.village_label);

            TextView refugeeOccupationView = getView().findViewById(R.id.occupation_text_view);
            String refugeeOccupation = refugeeOccupationView.getText().toString();
            TextView refugeeOccupationLabel = getView().findViewById(R.id.occupation_label);

            TextView refugeeAgeLabel = getView().findViewById(R.id.age_label);
            TextView refugeeNatLabel = getView().findViewById(R.id.nationality_label);
            TextView refugeeGenderLabel = getView().findViewById(R.id.gender_label);

            if (refugeePresenter.init(refugeePob, nationality, refugeeName,
                    age, refugeeNickname, refugeeTribe, gender, refugeeLocal, refugeeOccupation)) {
                result = true;
                CognitoUser cognitoUser = AppHelper.getPool().getCurrentUser();
                String loginName = cognitoUser.getUserId();
                createRefugee(loginName, refugeeName, age, nationality,
                        refugeeNickname, refugeePob, refugeeTribe, gender, refugeeLocal, refugeeOccupation);
            } else {
                refugeeNicknameLabel.setTextColor(refugeePresenter.getNickNameLabelColor());
                refugeeNicknameLabel.setText(refugeePresenter.getNickNameLabel());
                refugeeNameLabel.setTextColor(refugeePresenter.getNameLabelColor());
                refugeeNameLabel.setText(refugeePresenter.getNameLabel());
                refugeeNatLabel.setTextColor(refugeePresenter.getNationalityLabelColor());
                refugeeNatLabel.setText(refugeePresenter.getNationalityLabel());
                refugeePobLabel.setTextColor(refugeePresenter.getPlaceOfBirthLabelColor());
                refugeePobLabel.setText(refugeePresenter.getPlaceOfBirthLabel());
                refugeeTribeLabel.setTextColor(refugeePresenter.getTribeLabelColor());
                refugeeTribeLabel.setText(refugeePresenter.getTribeLabel());
                refugeeAgeLabel.setTextColor(refugeePresenter.getAgeLabelColor());
                refugeeAgeLabel.setText(refugeePresenter.getAgeLabel());
                refugeeLocalLabel.setTextColor(refugeePresenter.getLocalAreaLabelColor());
                refugeeLocalLabel.setText(refugeePresenter.getLocalAreaLabel());
                refugeeOccupationLabel.setTextColor(refugeePresenter.getOccupationLabelColor());
                refugeeOccupationLabel.setText(refugeePresenter.getOccupationLabel());
                refugeeGenderLabel.setTextColor(refugeePresenter.getGenderLabelColor());
                refugeeGenderLabel.setText(refugeePresenter.getGenderLabel());


            }
            return result;
        }

        /**
         * displays  name of user  + "Updated" to the user interface
         */
        public void toastMsg() {
            Toast.makeText(getContext(), "Updated " + dbHelper.getCurrentRefugee().getName() +
                    " details", Toast.LENGTH_LONG).show();
            android.support.v4.app.Fragment frag = new HomeFragment();
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.loadFragment(frag);
        }
    }

    /**
     * Fragment for adding details of refugee. Presenter class, RefugeePresenter, checks details are valid.
     */
    @SuppressWarnings("ConstantConditions")
    public static class UpdateFamMemberFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        Button but;
        private RefugeePresenter refugeePresenter;
        private Refugee refugee;
        String age;
        String nationality;
        String gender;
        String rel;
        TextView nameView;
        TextView pobView;
        TextView tribeView;
        TextView nicknameView;
        TextView occupationView;
        TextView localAreaView;
        TextView ageSpinner;
        TextView countrySpinner;
        TextView genderSpinner;

        private HashMap<Integer, ArrayList<String>> personalMap;
        private String fam;
        private TextView famSpinner;

        /**
         * @param inflater inflater. inflates xml layout to screen
         * @param viewGroup contains layout views
         * @param savedInstanceState activity's previously saved state
         * initialises refugee presenter.
        Text Watchers listen for user inputting text. On text being input (e.g name), the label is set to describe
        below field (e.g name)
         * @return inflated View
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            final View view = inflater.inflate(R.layout.fragment_fam_member_details, viewGroup, false);
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.getmTitle().setText(R.string.update_fam_member_details);
            but = view.findViewById(R.id.change_fam_button);
            but.setOnClickListener(this);
            refugeePresenter = new RefugeePresenter(getContext());
            nationality = dbHelper.getCurrentFamMember().getNationality();
            age = dbHelper.getCurrentFamMember().getAgeGroup();
            gender = dbHelper.getCurrentFamMember().getGen();
            fam = dbHelper.getCurrentFamMember().getRelationship();
            countrySpinner = view.findViewById(R.id.spnCountry);
            countrySpinner.setText(nationality);
            countrySpinner.setOnClickListener(this);
            ageSpinner = view.findViewById(R.id.age_spinner);
            ageSpinner.setText(age);
            ageSpinner.setOnClickListener(this);
            ageSpinner.setOnClickListener(this);
            genderSpinner = view.findViewById(R.id.gender_spinner);
            genderSpinner.setText(gender);
            genderSpinner.setOnClickListener(this);
            famSpinner = view.findViewById(R.id.spinner_family);
            famSpinner.setText(fam);
            famSpinner.setOnClickListener(this);

            nameView = (EditText) view.findViewById(R.id.name_ref);
            nameView.setText(dbHelper.getCurrentFamMember().getName());

            nameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.name_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.name_label);
                    label.setText(R.string.full_name);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.name_label);
                        label.setText(R.string.full_name);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            pobView = (EditText) view.findViewById(R.id.place_of_birth_text);
            pobView.setText(dbHelper.getCurrentFamMember().getPlaceOfBirth());
            pobView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.city_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.city_label);
                    label.setText(R.string.place_of_birth);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.city_label);
                        label.setText(R.string.place_of_birth);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            nicknameView = (EditText) view.findViewById(R.id.nickname_text);
            nicknameView.setText(dbHelper.getCurrentFamMember().getNickname());
            nicknameView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.nickname_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.nickname_label);
                    label.setText(R.string.nickname);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.nickname_label);
                        label.setText(R.string.nickname);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            tribeView = (EditText) view.findViewById(R.id.tribe_text);
            tribeView.setText(dbHelper.getCurrentFamMember().getTribe());

            tribeView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.tribe_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.tribe_label);
                    label.setText(R.string.tribe_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.tribe_label);
                        label.setText(R.string.tribe_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            localAreaView = (EditText) view.findViewById(R.id.loca_area_text);
            localAreaView.setText(dbHelper.getCurrentFamMember().getLoc());

            localAreaView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.village_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.village_label);
                    label.setText(R.string.place_of_birth);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.village_label);
                        label.setText(R.string.place_of_birth);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            //
            occupationView = (EditText) view.findViewById(R.id.occupation_text_view);
            occupationView.setText(dbHelper.getCurrentFamMember().getOcc());
            occupationView.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.occupation_label);
                        label.setText("");
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    TextView label = view.findViewById(R.id.occupation_label);
                    label.setText(R.string.occupation_label);
                    label.setTextColor(getResources().getColor(R.color.TitleColor));
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 0) {
                        TextView label = view.findViewById(R.id.occupation_label);
                        label.setText(R.string.occupation_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                    }
                }
            });
            return view;

        }

        @Override
        public void onClick(View v) {
            if (v == but) {
                if (checkRefugeeDetails()) {
                    changeFamMemberDb();
                }
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list gender options, retrieved from string array resource , gender_array.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            if (v == genderSpinner) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.gender_label);
                ListView listView = view.findViewById(R.id.list_dialog);
                ArrayAdapter genAdapter = ArrayAdapter.createFromResource(getContext(), R.array.gender_array, R.layout.list_item);
                genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(genAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        gender = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.gender_label);
                        label.setText(R.string.gender_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView genText = getView().findViewById(R.id.gender_spinner);
                        genText.setText(gender);

                        cdd.dismiss();
                    }
                });
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list nationality options, retrieved from countriesNameArray by the CountrySpinnerAdapter
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected country
            //OnQueryTextListener set on searchview in dialog. Listens for text being input into searchview. on input, filter
            //takes inputted charecters and filters down countries to those that contain the sequence of characters
            if (v == countrySpinner) {
                final CustomCountryDialog cdd = new CustomCountryDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.nationality);
                ListView listView = view.findViewById(R.id.list_dialog);
                final CountrySpinnerAdapter natAdapter = new CountrySpinnerAdapter(getContext(), countriesNameArray);
                natAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(natAdapter);
                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        nationality = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.nationality_label);
                        label.setText(R.string.gender_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView natText = getView().findViewById(R.id.spnCountry);
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
                });             }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list family member options, retrieved from string array resource , family_array.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            if (v == famSpinner) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.relationship_label);
                ListView listView = view.findViewById(R.id.list_dialog);
                ArrayAdapter genAdapter = ArrayAdapter.createFromResource(getContext(), R.array.family_array, R.layout.list_item);
                genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(genAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        fam = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.family_label);
                        label.setText(R.string.relationship_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView natText = getView().findViewById(R.id.spinner_family);
                        natText.setText(fam);

                        cdd.dismiss();
                    }
                });
            }
            // initialises custom dialog view. dialog contains title and a listView. the listview is populated by the array
            //adapter, to list age options, retrieved from string array resource , age_group_array.
            //on click of list item, dialog is dismissed, the label on the activity is set and the edittext is set to the
            //selected item from the list view
            if (v == ageSpinner) {
                final CustomDialog cdd = new CustomDialog(getActivity());
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.dialogColour)));
                cdd.show();
                Window view = cdd.getWindow();
                TextView titleView = view.findViewById(R.id.dialog_tit);
                titleView.setText(R.string.age_label);
                ListView listView = view.findViewById(R.id.list_dialog);
                ArrayAdapter genAdapter = ArrayAdapter.createFromResource(getContext(), R.array.age_group_array, R.layout.list_item);
                genAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                listView.setAdapter(genAdapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
// TODO Auto-generated method stub
                        age = parent.getItemAtPosition(position).toString();
                        TextView label = getView().findViewById(R.id.age_label);
                        label.setText(R.string.age_label);
                        label.setTextColor(getResources().getColor(R.color.TitleColor));
                        TextView ageText = getView().findViewById(R.id.age_spinner);
                        ageText.setText(age);

                        cdd.dismiss();
                    }
                });
            }
        }

        /**
         * async task. send message to database helper class to update the missing person in the database.
         * after executing, sends message toastMsg, to send message to UI denoting the person being updated.
         */
        private void changeFamMemberDb() {
            class ChangeFamMemberDb extends AsyncTask<Object, Object, Object> {


                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }

                @Override
                protected Void doInBackground(Object... params) {
                    dbHelper.changeFamilyMemberDb("http://ec2-52-90-145-100.compute-1.amazonaws.com/globalfam/change_fam_member_details.php", refugee);
                    return null;
                }

                @Override
                protected void onPostExecute(Object o) {
                    super.onPostExecute(o);
                    loadFragment();
                }
            }
            ChangeFamMemberDb crd = new ChangeFamMemberDb();
            crd.execute();
        }

        /**
         creates a refugee object by feeding the method parameters into the
         Refugee constructor.
         */
        private void createRefugee(String refugeeId, String name, String age,
                                   String nationality, String nickname, String placeOfBirth,
                                   String tribe, String gender, String localArea, String occupation, String relation) {
            refugee = new Refugee(refugeeId, name, age, nationality,
                    nickname, placeOfBirth, tribe, gender, localArea, occupation, relation, dbHelper.getCurrentFamMember().getDateCreated());
            System.out.println(refugee.toString());
        }

        /**
         * refugee presenter checks the inputted details for each field (e.g name, relationship). sends message createrefugee if successful
         * @return true if inputted details are valid. false if one or more of the fields are not valid (e.g name is less then 6 characters)
         * sets label of invalid field(s) to error message and changes label colour to red.
         */
        private boolean checkRefugeeDetails() {

            Boolean result = false;
            TextView refugeeNameView = getView().findViewById(R.id.name_ref);
            TextView refugeeNameLabel = getView().findViewById(R.id.name_label);
            String refugeeName = refugeeNameView.getText().toString();

            TextView refugeeTribeView = getView().findViewById(R.id.tribe_text);
            String refugeeTribe = refugeeTribeView.getText().toString();
            TextView refugeeTribeLabel = getView().findViewById(R.id.tribe_label);

            TextView refugeePobView = getView().findViewById(R.id.place_of_birth_text);
            String refugeePob = refugeePobView.getText().toString();
            TextView refugeePobLabel = getView().findViewById(R.id.city_label);

            TextView refugeeNicknameView = getView().findViewById(R.id.nickname_text);
            String refugeeNickname = refugeeNicknameView.getText().toString();
            TextView refugeeNicknameLabel = getView().findViewById(R.id.nickname_label);

            TextView refugeeLocalView = getView().findViewById(R.id.loca_area_text);
            String refugeeLocal = refugeeLocalView.getText().toString();
            TextView refugeeLocalLabel = getView().findViewById(R.id.village_label);

            TextView refugeeOccupationView = getView().findViewById(R.id.occupation_text_view);
            String refugeeOccupation = refugeeOccupationView.getText().toString();
            TextView refugeeOccupationLabel = getView().findViewById(R.id.occupation_label);

            TextView refugeeAgeLabel = getView().findViewById(R.id.age_label);
            TextView refugeeNatLabel = getView().findViewById(R.id.nationality_label);
            TextView refugeeGenderLabel = getView().findViewById(R.id.gender_label);
            TextView refugeeRelationshipLabel = getView().findViewById(R.id.family_label);

            if (refugeePresenter.init(refugeePob, nationality, refugeeName,
                    age, refugeeNickname, refugeeTribe, gender, refugeeLocal, refugeeOccupation, fam)) {
                result = true;
                String refId = dbHelper.getRefId();
                createRefugee(refId, refugeeName, age, nationality,
                        refugeeNickname, refugeePob, refugeeTribe, gender, refugeeLocal, refugeeOccupation, fam);

            } else {
                refugeeNicknameLabel.setTextColor(refugeePresenter.getNickNameLabelColor());
                refugeeNicknameLabel.setText(refugeePresenter.getNickNameLabel());
                refugeeNameLabel.setTextColor(refugeePresenter.getNameLabelColor());
                refugeeNameLabel.setText(refugeePresenter.getNameLabel());
                refugeeNatLabel.setTextColor(refugeePresenter.getNationalityLabelColor());
                refugeeNatLabel.setText(refugeePresenter.getNationalityLabel());
                refugeePobLabel.setTextColor(refugeePresenter.getPlaceOfBirthLabelColor());
                refugeePobLabel.setText(refugeePresenter.getPlaceOfBirthLabel());
                refugeeTribeLabel.setTextColor(refugeePresenter.getTribeLabelColor());
                refugeeTribeLabel.setText(refugeePresenter.getTribeLabel());
                refugeeAgeLabel.setTextColor(refugeePresenter.getAgeLabelColor());
                refugeeAgeLabel.setText(refugeePresenter.getAgeLabel());
                refugeeLocalLabel.setTextColor(refugeePresenter.getLocalAreaLabelColor());
                refugeeLocalLabel.setText(refugeePresenter.getLocalAreaLabel());
                refugeeOccupationLabel.setTextColor(refugeePresenter.getOccupationLabelColor());
                refugeeOccupationLabel.setText(refugeePresenter.getOccupationLabel());
                refugeeGenderLabel.setTextColor(refugeePresenter.getGenderLabelColor());
                refugeeGenderLabel.setText(refugeePresenter.getGenderLabel());
                refugeeRelationshipLabel.setTextColor(refugeePresenter.getRelationshipLabelColor());
                refugeeRelationshipLabel.setText(refugeePresenter.getRelationshipLabel());
            }
            return result;
        }

        public void loadFragment() {
                    makeToast();
                    android.support.v4.app.Fragment frag = new HomeFragment();
                    MainActivity anActivity = (MainActivity) getActivity();
                    anActivity.loadFragment(frag);

        }
        /**
         * displays missing family members name + "updated" to the user interface
         */
        public void makeToast(){
            Toast.makeText(getContext(), "Updated " + dbHelper.getCurrentFamMember().getName() + " details,", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Fragment displays two buttons to change language and phone number.
     */
    public static class SettingsFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
            RelativeLayout langLayout;
            RelativeLayout phoneLayout;
            RelativeLayout pwordLayout;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
                if (viewGroup != null) {
                    viewGroup.removeAllViews();
                }
                View view = inflater.inflate(R.layout.fragment_settings, viewGroup, false);
                MainActivity anActivity = (MainActivity) getActivity();
                anActivity.getmTitle().setText(R.string.settings);
                TextView tv = view.findViewById(R.id.phone_text);
                Typeface titleFont = Typeface.
                        createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
                tv.setTypeface(titleFont);


                TextView tvlang = view.findViewById(R.id.lang_text);
                tvlang.setTypeface(titleFont);

                langLayout = view.findViewById(R.id.lang__but);
                phoneLayout = view.findViewById(R.id.phone__but);

                langLayout.setOnClickListener(this);
                phoneLayout.setOnClickListener(this);

                return view;
            }

            @Override
            public void onClick(View v) {
                if (v == langLayout) {
                    android.support.v4.app.Fragment frag = new LanguageFragment();
                    MainActivity anActivity = (MainActivity) getActivity();
                    anActivity.loadFragment(frag);
                } else if (v == phoneLayout) {
                    //android.support.v4.app.Fragment frag = new ChangePhoneFragment();
                    //MainActivity anActivity = (MainActivity) getActivity();
                   // anActivity.loadFragment(frag);
                }
            }
        }

    /**
     * Fragment displays two buttons to change language and phone number.
     */
    public static class ContactOrgMenuFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
        RelativeLayout noMatchLayout;
        RelativeLayout matchLayout;
        RelativeLayout pwordLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
            if (viewGroup != null) {
                viewGroup.removeAllViews();
            }
            View view = inflater.inflate(R.layout.fragment_contact_org_menu, viewGroup, false);
            MainActivity anActivity = (MainActivity) getActivity();
            anActivity.getmTitle().setText(R.string.contact_org);
            TextView tv = view.findViewById(R.id.match_text);
            Typeface titleFont = Typeface.
                    createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
            tv.setTypeface(titleFont);

            TextView tvNoMatch = view.findViewById(R.id.no_match_text);
            tvNoMatch.setTypeface(titleFont);

            matchLayout = view.findViewById(R.id.match_but);
            noMatchLayout = view.findViewById(R.id.no_match_but);

            matchLayout.setOnClickListener(this);
            noMatchLayout.setOnClickListener(this);

            return view;
        }

        @Override
        public void onClick(View v) {
            if (v == matchLayout) {
                android.support.v4.app.Fragment frag = new ContactOrganizationMatchInsFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                anActivity.loadFragment(frag);
            } else if (v == noMatchLayout) {
                android.support.v4.app.Fragment frag = new ContactOrganizationNoMatchInsFragment();
                MainActivity anActivity = (MainActivity) getActivity();
                anActivity.loadFragment(frag);
            }
        }
    }

    /**
     * almost identical to LanguageActivity. wheelpicker sets the language/locale
     * the language variable stored in SharedPreferences is then updated to the selected language
     */
        public static class LanguageFragment extends android.support.v4.app.Fragment implements WheelPicker.OnItemSelectedListener, View.OnClickListener {

            private TextView tx;
            private WheelPicker wheel;
            private Button but;

            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
                if (viewGroup != null) {
                    viewGroup.removeAllViews();
                }
                View view = inflater.inflate(R.layout.fragment_language, viewGroup, false);
                MainActivity anActivity = (MainActivity) getActivity();
                anActivity.getmTitle().setText(R.string.language_title);
                but = view.findViewById(R.id.langbutton);
                but.setOnClickListener(this);
                wheel = view.findViewById(R.id.main_wheel);
                wheel.setOnItemSelectedListener(this);
                return view;
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
                        ((MainActivity) getActivity()).mTitle.setText(R.string.language_title);
                        but.setText(R.string.language_title);
                        break;
                    case 1:
                        lang = "ar";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.arabic_title);
                        but.setText(R.string.arabic_title);
                        break;
                    case 2:
                        lang = "fr";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.french_title);
                        but.setText(R.string.french_title);
                        break;
                    case 3:
                        lang = "ps";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.french_title);
                        but.setText(R.string.french_title);
                        break;
                    case 4:
                        lang = "ur";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.urdu_title);
                        but.setText(R.string.urdu_title);
                        break;
                    case 5:
                        lang = "it";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.italian_title);
                        but.setText(R.string.italian_title);
                        break;
                    case 6:
                        lang = "ku";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.kurdish_title);
                        but.setText(R.string.kurdish_title);
                        break;
                    case 7:
                        lang = "pt";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.portugese_title);
                        but.setText(R.string.portugese_title);
                        break;
                    case 8:
                        lang = "de";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.german_title);
                        but.setText(R.string.german_title);
                        break;
                    case 9:
                        lang = "sr";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.serbian_title);
                        but.setText(R.string.serbian_title);
                        break;
                    case 10:
                        lang = "so";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.somali_title);
                        but.setText(R.string.somali_title);
                        break;
                    case 11:
                        lang = "es";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.spanish_title);
                        but.setText(R.string.spanish_title);
                        break;
                    case 12:
                        lang = "el";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.greek_title);
                        but.setText(R.string.greek_title);
                        break;
                    case 13:
                        lang = "pa";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.pashto_title);
                        but.setText(R.string.pashto_title);
                        break;
                    case 14:
                        lang = "tr";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.turkish_title);
                        but.setText(R.string.turkish_title);
                        break;
                    case 15:
                        lang = "ru";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.russian_title);
                        but.setText(R.string.russian_title);
                        break;
                    case 16:
                        lang = "sq";
                        ((MainActivity) getActivity()).mTitle.setText(R.string.albanian_title);
                        but.setText(R.string.albanian_title);
                        break;
                }
            }

        /**
         * replaces content frame with settings fragment. puts selected language into the sharedpreferences.
         * @param v View of the Button
         */
            @Override
            public void onClick(View v) {
                if (v == but) {
                    SharedPreferences.Editor editor = getContext().getSharedPreferences("com.gb.bedunfamily.preferences", MODE_PRIVATE).edit();
                    editor.putString("lang", lang).commit();
                    android.support.v4.app.Fragment frag = new SettingsFragment();
                    MainActivity anActivity = (MainActivity) getActivity();
                    anActivity.loadFragment(frag);
                }
            }
        }

    /**
     * to be completed
     */
        public static class ChangePhoneFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
            Button but;

            @Override
            public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {
                if (viewGroup != null) {
                    viewGroup.removeAllViews();
                }
                View view = inflater.inflate(R.layout.fragment_change_phone, viewGroup, false);
                MainActivity anActivity = (MainActivity) getActivity();
                assert anActivity != null;
                anActivity.getmTitle().setText(R.string.change_phone);
                TextView tv = view.findViewById(R.id.set_phone_title);
                Typeface titleFont = Typeface.
                        createFromAsset(getContext().getAssets(), "fonts/Pacifico.ttf");
                tv.setTypeface(titleFont);

                TextView oldPhoneTitle = view.findViewById(R.id.old_phone_title);
                oldPhoneTitle.setTypeface(titleFont);

                TextView phoneTextView = view.findViewById(R.id.phone_text);
                phoneTextView.setText(dbHelper.getUsername());

                but = view.findViewById(R.id.change_phone_button);

                return view;
            }

            @Override
            public void onClick(View v) {
                if (v == but) {
                    android.support.v4.app.Fragment frag = new SettingsFragment();
                    MainActivity anActivity = (MainActivity) getActivity();
                    assert anActivity != null;
                    anActivity.loadFragment(frag);
                }
            }
        }

    }