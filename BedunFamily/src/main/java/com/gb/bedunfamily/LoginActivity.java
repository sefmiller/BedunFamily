/*
 *  Copyright 2013-2016 Amazon.com,
 *  Inc. or its affiliates. All Rights Reserved.
 *
 *  Licensed under the Amazon Software License (the "License").
 *  You may not use this file except in compliance with the
 *  License. A copy of the License is located at
 *
 *      http://aws.amazon.com/asl/
 *
 *  or in the "license" file accompanying this file. This file is
 *  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *  CONDITIONS OF ANY KIND, express or implied. See the License
 *  for the specific language governing permissions and
 *  limitations under the License.
 */

package com.gb.bedunfamily;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoDevice;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUser;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserAttributes;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserSession;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.AuthenticationDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ChallengeContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.ForgotPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.MultiFactorAuthenticationContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.continuations.NewPasswordContinuation;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.AuthenticationHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.ForgotPasswordHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.SignUpHandler;


import java.util.Locale;
import java.util.Map;

/**
 * Activity for logging in or registering as a user of the application
 * */
public class LoginActivity extends AppCompatActivity {
    private final String TAG = "LoginActivity";

    private AlertDialog userDialog;
    private ProgressDialog waitDialog;
    private IntlPhoneInput phoneInputView;

    // Screen fields
    private EditText inPassword;

    //Continuations
    private ForgotPasswordContinuation forgotPasswordContinuation;
    private NewPasswordContinuation newPasswordContinuation;

    private String username;
    private String password;
    private String lang;

    //sets activity language
    //protected void attachBaseContext(Context newBase) {
    //    SharedPreferences shared = getSharedPreferences("com.gb.bedunfamily.preferences", MODE_PRIVATE);
    //   lang = shared.getString("lang", null);
    //super.attachBaseContext(LanguageContextWrapper.wrap(newBase, lang));
    //}
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.login_activity);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//set title font
            TextView tx = findViewById(R.id.textViewAppMainTitle);
            Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
            tx.setTypeface(custom_font);
            phoneInputView = findViewById(R.id.my_phone_input);
            phoneInputView.setNumber("");


            AppHelper.init(getApplicationContext());
            initApp();
            findCurrent();
        }
        catch (Exception e){
            System.out.println("RRRRRR" + e) ;
        }
    }

    /**
     *  initialize activity. sets listener for changes in password EditText box
     */
    //
    private void initApp() {


        inPassword = findViewById(R.id.editTextUserPassword);
        inPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.textViewUserPasswordLabel);
                    label.setText("");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.textViewUserPasswordLabel);
                label.setText(R.string.password);
                label.setTextColor(Color.WHITE);
                TextView mess = findViewById(R.id.textViewUserPasswordMessage);
                mess.setText("");
                mess.setTextColor(Color.WHITE);

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.textViewUserPasswordLabel);
                    label.setText(R.string.password);
                }
            }
        });
    }
    /**
     if @param resultCode == "RESULT_OK", apphelper handles setting the password for the cognito identity to the new password
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case 3:
                // Forgot password
                if (resultCode == RESULT_OK) {
                    String newPass = data.getStringExtra("newPass");
                    String code = data.getStringExtra("code");
                    if (newPass != null && code != null) {
                        if (!newPass.isEmpty() && !code.isEmpty()) {
                            showWaitDialog("Setting new password...");
                            forgotPasswordContinuation.setPassword(newPass);
                            forgotPasswordContinuation.setVerificationCode(code);
                            forgotPasswordContinuation.continueTask();
                        }
                    }
                }
                break;
        }
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }


    /**
     * @param view Button view
     * on click of login button sends signInUser() message to handle login authentication
     */
    public void logIn(View view) {
        signInUser();
    }

    /**
     * @param view ForgotPassword Text View
     * on click of ForgotPassword TextView sends forgotpasswordUser() message to handle changing password
     */
    // Forgot password processing
    public void forgotPassword(View view) {
        forgotpasswordUser();
    }

    /**
     * @param v Button View
     * on click of Button checks if phone number & password entered are valid.
     * if invalid, sets phone number label to display 'Please Enter a Valid Mobile Number'
     * if password is empty, sets phone number label to display 'Password cannot be empty"'
     * if phone number & password are ok, shows dialog displaying message 'Signing up...'
     * AppHelper Utility Class handles signing up user in background
     */
    public void signUpNewUser(View v) {
        CognitoUserAttributes userAttributes = new CognitoUserAttributes();
                String myInternationalNumber;
        String userpasswordInput = inPassword.getText().toString();
        if(!phoneInputView.isValid()) {
            //create label
            TextView view = findViewById(R.id.textViewUserIdMessage);
            view.setText(R.string.phone_error);
            view.setTextColor(Color.RED);
            return;

        }
        else if (userpasswordInput.isEmpty()) {
            //create label
            TextView view = findViewById(R.id.textViewUserPasswordMessage);
            view.setText(R.string.password_empty);
            view.setTextColor(Color.RED);
            return;
        }
        else {
            myInternationalNumber = phoneInputView.getNumber();
            username = myInternationalNumber;
            password = userpasswordInput;
            //userAttributes.addAttribute("phone_number", userInput);
            }


        showWaitDialog("Signing up...");
        //String username = "example";
        AppHelper.getPool().signUpInBackground(username, userpasswordInput, userAttributes,  null, signUpHandler);
    }

    private final SignUpHandler signUpHandler = new SignUpHandler() {
        @Override
        public void onSuccess(CognitoUser user, boolean signUpConfirmationState,
                              CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            // Check signUpConfirmationState to see if the user is already confirmed
            closeWaitDialog();
            Boolean regState = signUpConfirmationState;
            if (signUpConfirmationState) {
                // User is already confirmed. invokes chooseUserTypeLaunch
                showDialogMessage("Sign up successful!", "");
                chooseUserTypeLaunch(cognitoUserCodeDeliveryDetails);

            } else {
                // User not confirmed. invokes confirmSignUp
                confirmSignUp(cognitoUserCodeDeliveryDetails);
                //chooseUserTypeLaunch(cognitoUserCodeDeliveryDetails);

            }
        }
        @Override
        public void onFailure(Exception exception) {
            closeWaitDialog();
            TextView label = findViewById(R.id.textViewUserIdMessage);
            label.setText(R.string.sign_up_failed);
            label.setTextColor(Color.RED);
            showDialogMessage("Sign up failed", AppHelper.formatException(exception));


        }
    };

    /**
     * checks if phone number & password entered are valid.
            * if invalid, sets phone number label to display 'Please Enter a Valid Mobile Number'
            * if password is empty, sets phone number label to display 'Password cannot be empty"'
            * if phone number & password are ok, shows dialog displaying message 'Signing in...'
            * AppHelper Utility Class handles signing in user in background
     */
    private void signInUser() {
            if(!phoneInputView.isValid()) {
            //create label
            TextView view = findViewById(R.id.textViewUserIdMessage);
            view.setText(R.string.phone_error);
            view.setTextColor(Color.RED);
            return;
        }
        else if((inPassword.getText() == null || inPassword.getText().length() < 1)){
            TextView label = findViewById(R.id.textViewUserPasswordMessage);
            label.setText(R.string.password_empty);
                label.setTextColor(Color.RED);
                return;
        }
        else {
                username = phoneInputView.getNumber();
        AppHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);
        //inPassword.getText().
        password = inPassword.getText().toString();
        }

        showWaitDialog("Signing in...");
        AppHelper.getPool().getUser(username).getSessionInBackground(authenticationHandler);
    }


    /**
     * checks if phone number is valid.
     * if invalid, sets phone number label to display 'Please Enter a Valid Mobile Number'
     * if phone number valid, AppHelper Utility Class handles signing in user in background
     */
    private void forgotpasswordUser() {
        IntlPhoneInput phoneInputView = findViewById(R.id.my_phone_input);
        if(!phoneInputView.isValid()) {
            //create label
            TextView view = findViewById(R.id.textViewUserIdMessage);
            view.setText(R.string.phone_error);
            view.setTextColor(Color.RED);
        }
        else {
            username = phoneInputView.getNumber();
            showWaitDialog("");
            AppHelper.getPool().getUser(username).forgotPasswordInBackground(forgotPasswordHandler);
        }
    }

    /**
     * @param forgotPasswordContinuation Amazon Cognito class for handling changing password in the Cognito identity provider.
     * exits out into the Forgot Password Activity.
     */
    private void getForgotPasswordCode(ForgotPasswordContinuation forgotPasswordContinuation) {
        this.forgotPasswordContinuation = forgotPasswordContinuation;
        Intent intent = new Intent(this, ForgotPasswordActivity.class);
        intent.putExtra("destination", forgotPasswordContinuation.getParameters().getDestination());
        intent.putExtra("deliveryMed", forgotPasswordContinuation.getParameters().getDeliveryMedium());
        startActivityForResult(intent, 3);
    }

@Override
public void onResume(){
    super.onResume();
}


    /**
     * on first attempt by the user to sign in, start up the signUpConfirm activity, to confirm user credentials.
     */
    private void firstTimeSignIn() {
        Intent newPasswordActivity = new Intent(this, SignUpConfirm.class);
        startActivityForResult(newPasswordActivity, 6);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void continueWithFirstTimeSignIn() {
        newPasswordContinuation.setPassword(AppHelper.getPasswordForFirstTimeLogin());
        Map<String, String> newAttributes = AppHelper.getUserAttributesForFirstTimeLogin();
        if (newAttributes != null) {
            for (Map.Entry<String, String> attr : newAttributes.entrySet()) {
                //Log.e(TAG, String.format("Adding attribute: %s, %s", attr.getKey(), attr.getValue()));
                newPasswordContinuation.setUserAttribute(attr.getKey(), attr.getValue());
            }
        }
        try {
            newPasswordContinuation.continueTask();
        } catch (Exception e) {
            closeWaitDialog();
            TextView label = findViewById(R.id.textViewUserIdMessage);
            label.setText(R.string.sign_in_failed);
            label.setTextColor(Color.RED);
            showDialogMessage("Sign-in failed", AppHelper.formatException(e));
        }
    }

    private void confirmUser() {
        Intent confirmActivity = new Intent(this, SignUpConfirm.class);
        confirmActivity.putExtra("source", "main");
        startActivityForResult(confirmActivity, 2);
    }

    /**
     * checks to see if user is already logged in/ has previously logged on current device. if so amazon cognito user is automatically logged in
     */
    private void findCurrent() {
        CognitoUser user = AppHelper.getPool().getCurrentUser();
        username = user.getUserId();
        if (username != null) {
            AppHelper.setUser(username);
            //phoneInputView.setNumber(username);
            user.getSessionInBackground(authenticationHandler);
        }
    }

    /**
     * @param continuation Amazon Cognito authentication handler
     * @param username username / phone number
     * if password is empty, sets password label to ask user to enter password
     * Amazon AuthenticationContinuation authenticates user phone number/password combination, to see if they exist
     */
    private void getUserAuthentication(AuthenticationContinuation continuation, String username) {
        if (username != null) {
            this.username = username;
            AppHelper.setUser(this.username);
        }
        if (this.password == null) {
            //inUsername.setText(username);
            password = inPassword.getText().toString();

            if (password.length() < 1) {
                TextView label = findViewById(R.id.textViewUserPasswordMessage);
                label.setText(R.string.password_empty);
                label.setTextColor(Color.RED);

                return;
            }
        }
        AuthenticationDetails authenticationDetails = new AuthenticationDetails(this.username, password, null);
        continuation.setAuthenticationDetails(authenticationDetails);
        continuation.continueTask();
    }




    // Callbacks
    private final ForgotPasswordHandler forgotPasswordHandler = new ForgotPasswordHandler() {
        @Override
        public void onSuccess() {
            closeWaitDialog();
            showDialogMessage("Password successfully changed!", "");
            inPassword.setText("");
            inPassword.requestFocus();
        }

        @Override
        public void getResetCode(ForgotPasswordContinuation forgotPasswordContinuation) {
            closeWaitDialog();
            getForgotPasswordCode(forgotPasswordContinuation);
        }

        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            showDialogMessage("Forgot password failed", AppHelper.formatException(e));
        }
    };

    //
    private final AuthenticationHandler authenticationHandler = new AuthenticationHandler() {
        @Override
        public void getMFACode(MultiFactorAuthenticationContinuation multiFactorAuthenticationContinuation) {
            // do nothing
        }

        @Override
        public void onSuccess(CognitoUserSession cognitoUserSession, CognitoDevice device) {
            //Log.e(TAG, "Auth Success");
            AppHelper.setCurrSession(cognitoUserSession);
            AppHelper.newDevice(device);
            closeWaitDialog();
            Intent userActivity = new Intent(LoginActivity.this, MainActivity.class);
            startActivityForResult(userActivity, 4);


        }

        @Override
        public void getAuthenticationDetails(AuthenticationContinuation authenticationContinuation, String username) {
            closeWaitDialog();
            Locale.setDefault(Locale.US);
            getUserAuthentication(authenticationContinuation, username);
        }


        @Override
        public void onFailure(Exception e) {
            closeWaitDialog();
            TextView label = findViewById(R.id.textViewUserIdMessage);
            label.setText(R.string.sign_in_failed);
            label.setTextColor(Color.RED);
            showDialogMessage("Sign-in failed", AppHelper.formatException(e));
        }

        @Override
        public void authenticationChallenge(ChallengeContinuation continuation) {
            /*
              For Custom authentication challenge, implement your logic to present challenge to the
              user and pass the user's responses to the continuation.
             */
            if ("NEW_PASSWORD_REQUIRED".equals(continuation.getChallengeName())) {
                // This is the first sign-in attempt for an admin created user
                newPasswordContinuation = (NewPasswordContinuation) continuation;
                AppHelper.setUserAttributeForDisplayFirstLogIn(newPasswordContinuation.getCurrentUserAttributes(),
                        newPasswordContinuation.getRequiredAttributes());
                closeWaitDialog();
                firstTimeSignIn();
            }
        }
    };


    /**
     * @param message Message to be displayed in the dialog
     * sets dialog message to message parameter
     */
    private void showWaitDialog(String message) {
        closeWaitDialog();
        waitDialog = new ProgressDialog(this);
        waitDialog.setTitle(message);
        waitDialog.show();
    }

    /**
     * @param title Title text of dialog
     * @param body Text body of dialog
     *             sets dialog box with ok button to dismiss dialog
     */
    private void showDialogMessage(String title, String body) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                } catch (Exception e) {
                    //
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    /**
     * dismisses dialog
     */
    private void closeWaitDialog() {
        try {
            waitDialog.dismiss();
        } catch (Exception e) {
            //
        }
    }

    /**
     * exits onto confirmSignUp activity. passes over username/phone number.
     */
    private void confirmSignUp(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(this, SignUpConfirm.class);
        intent.putExtra("source","signup");
        intent.putExtra("name", username);
        intent.putExtra("password", password);
        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
        System.out.println(password + "a");
        startActivityForResult(intent, 10);
    }

    /**
     * exits onto chooseUserType activity. passes over username/phone numberr
     */
    private void chooseUserTypeLaunch(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
        Intent intent = new Intent(this, Choose_User_Type_Activity.class);
        intent.putExtra("source","signup");
        intent.putExtra("name", username);
        intent.putExtra("password", password);
        intent.putExtra("destination", cognitoUserCodeDeliveryDetails.getDestination());
        intent.putExtra("deliveryMed", cognitoUserCodeDeliveryDetails.getDeliveryMedium());
        intent.putExtra("attribute", cognitoUserCodeDeliveryDetails.getAttributeName());
        startActivityForResult(intent, 10);
    }

}

