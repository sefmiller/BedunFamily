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

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserCodeDeliveryDetails;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler;
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.VerificationHandler;
import com.gb.bedunfamily.Model.DatabaseHelper;

/**
 * Activity for confirming a user by requesting a code to be sent to their mobile phone number.
 */
public class SignUpConfirm extends AppCompatActivity {
    private TextView username;
    private TextView confCode;
    private String userName;
    private AlertDialog userDialog;
    private String extrasName;
    private DatabaseHelper db;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_confirm);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //set title font
        TextView tx = findViewById(R.id.textViewConfirmTitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        tx.setTypeface(custom_font);

        //username, password passed over from previous activity
        Intent iin= getIntent();
        Bundle b = iin.getExtras();
        if(b!=null)
        {
            extrasName =(String) b.get("name");
            password =(String) b.get("password");


        }
    init();
    }


    /**
     * listens for changes in code editText Box. sets onclick listeners for buttons. sets username textview to the user mobile number
     */
    private void init() {


        //set username to user set phone number
        username = findViewById(R.id.phone_text_view);
        username.setText(extrasName);

        //on user input, label displays 'Confirmation Code'
        confCode = (EditText) findViewById(R.id.phone_edit_text);
        confCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.phone_edit_label);
                    label.setText("");
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.phone_edit_label);
                label.setText(R.string._confirm_account);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0) {
                    TextView label = findViewById(R.id.phone_edit_label);
                    label.setText(R.string._confirm_account);
                }
            }
        });

        //listeners for user click
        Button confirm = findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendConfCode();
            }
        });

        TextView reqCode = findViewById(R.id.resend_confirm_req);
        reqCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reqConfCode();
            }
        });
    }

    /**
     *  set confirmCode and userName to strings held in the UI textviews.
        if confirmCode is empty set label to display error and return out of method
        amazon cognito checks validity of confirmation code in background and handles result with confHandler

     */
    private void sendConfCode() {
        userName = username.getText().toString();
        String confirmCode = confCode.getText().toString();
        if(confirmCode.length() < 1) {
            TextView label = findViewById(R.id.textViewConfirmCodeMessage);
            label.setText(R.string.conf_code_empty);
            return;
        }

        AppHelper.getPool().getUser(userName).confirmSignUpInBackground(confirmCode, true, confHandler);
    }

    /**
     *  requests another confirmation code to be sent to user mobile number.
     */
    private void reqConfCode() {
        userName = username.getText().toString();
        System.out.println(userName);

        AppHelper.getPool().getUser(userName).resendConfirmationCodeInBackground(resendConfCodeHandler);

    }
    //on conf code being accepted, dialog message displaying success is sent by showDialogMessage
    //on failure label is set to "Confirmation Failed". dialog message displays failure
    private final GenericHandler confHandler = new GenericHandler() {
        @Override
        public void onSuccess() {
            showDialogMessage("Success! ",userName +" has been confirmed!", true);
        }

        @Override
        public void onFailure(Exception exception) {
            TextView label = findViewById(R.id.textViewConfirmCodeMessage);
            label.setText(R.string.conf_failed);

            label = findViewById(R.id.textViewConfirmCodeMessage);
            label.setText(R.string.conf_failed);

            showDialogMessage(getString(R.string.conf_failed), AppHelper.formatException(exception), false);
        }
    };

    //on success, dialog confirms success message.
    //on failure label set to display code resend failure. dialog set to display error and error code.
    private final VerificationHandler resendConfCodeHandler = new VerificationHandler() {
        @Override
        public void onSuccess(CognitoUserCodeDeliveryDetails cognitoUserCodeDeliveryDetails) {
            TextView mainTitle = findViewById(R.id.textViewConfirmTitle);
            mainTitle.setText(R.string._confirm_account);
            confCode = findViewById(R.id.phone_edit_text);
            confCode.requestFocus();

            showDialogMessage(getString(R.string.conf_code_resend_dialog),""+cognitoUserCodeDeliveryDetails.getDestination()+" via "+cognitoUserCodeDeliveryDetails.getDeliveryMedium()+".", false);
        }

        @Override
        public void onFailure(Exception exception) {
            TextView label = findViewById(R.id.phone_edit_label);
            label.setText(getString(R.string.conf_code_request_failed));
            showDialogMessage(getString(R.string.conf_code_request_failed), AppHelper.formatException(exception), false);
        }
    };

    /**
     *     dialog displays title concatenated with body. title string is resource string such as 'confirm succeess', 'confirm failure;.
     *     body string is amazon error/success string.
     *     if method boolean parameter exitactivity is set to true then addloginDb is called to add user to database and exit acitivty.
     * @param title Ttitle of the dialog
     * @param body Text Body of the dialog
     * @param exitActivity if true can exit
     */
    private void showDialogMessage(String title, String body, final boolean exitActivity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(body).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    userDialog.dismiss();
                    if(exitActivity) {
                        addLoginDb();

                    }
                } catch (Exception e) {
                    addLoginDb();
                }
            }
        });
        userDialog = builder.create();
        userDialog.show();
    }

    /**
     *     passes username and password to Choose USer Type activity
     */
    private void exit() {
        Intent intent = new Intent(SignUpConfirm.this, Choose_User_Type_Activity.class);
        intent.putExtra("name",userName);
        intent.putExtra("password",password);
        startActivity(intent);
        finish();
    }

    /**
     * forwards to databasehelper method setLoginDb to add login to db. post execution of async task,
     * exit() method is called to exit into next activity
     */
    private void addLoginDb(){
        class SetLoginDb extends AsyncTask<Object, Object, Object> {


            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Object... params) {
                db = new DatabaseHelper(userName);
                db .setloginDb();
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                exit();
            }
        }
        SetLoginDb sld = new SetLoginDb();
        sld.execute();
    }
}
