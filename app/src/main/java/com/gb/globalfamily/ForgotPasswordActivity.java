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

package com.gb.globalfamily;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * //Activity for changing the user password.
 * */
public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText passwordInput;
    private EditText codeInput;
    private Button setPassword;
    private AlertDialog userDialog;
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
        setContentView(R.layout.activity_forgot_password);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //set title font
        TextView tx = findViewById(R.id.textViewForgotPasswordTitle);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        tx.setTypeface(custom_font);

        init();
    }


    /**
     * @param view button view
     *             on click of button, sends getCode() message
     */
    public void forgotPassword(View view) {
        getCode();
    }


    /**
     * passes cognito identity details from login screen. listens for chnages in password and code editText boxes.
     */
    private void init(){
        Bundle extras = getIntent().getExtras();
        if (extras !=null) {
            if (extras.containsKey("destination")) {
                String dest = extras.getString("destination");
                String delMed = extras.getString("deliveryMed");
                TextView message = findViewById(R.id.textViewForgotPasswordMessage);
                String textToDisplay = "Code to set a new password was sent to " + dest + " via "+delMed;
                message.setText(textToDisplay);
            }
        }

        //password label changes on input to display 'new password'
        passwordInput = findViewById(R.id.editTextForgotPasswordPass);
        passwordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.textViewForgotPasswordUserIdLabel);
                    label.setText(passwordInput.getHint());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.textViewForgotPasswordUserIdMessage);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.textViewForgotPasswordUserIdLabel);
                    label.setText("");
                }
            }
        });

        //code input label changes on input to show 'verification code'
        codeInput = findViewById(R.id.editTextForgotPasswordCode);
        codeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.textViewForgotPasswordCodeLabel);
                    label.setText(codeInput.getHint());
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextView label = findViewById(R.id.textViewForgotPasswordCodeMessage);
                label.setText(" ");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    TextView label = findViewById(R.id.textViewForgotPasswordCodeLabel);
                    label.setText("");
                }
            }
        });
    }

    /**
     * Checks if password is empty. If empty sets label textview to display 'password cannot be empty'
     * if code sent to user is accepted and password is ok, sets new password and exits back to login activity
     */
    private void getCode() {
        String newPassword = passwordInput.getText().toString();

        if (newPassword.length() < 1) {
            TextView label = findViewById(R.id.textViewForgotPasswordUserIdMessage);
            label.setText(passwordInput.getHint() + getString(R.string.cannot_be_empty));
            return;
        }

        String verCode = codeInput.getText().toString();

        if (verCode.length() < 1) {
            TextView label = findViewById(R.id.textViewForgotPasswordCodeMessage);
            label.setText(codeInput.getHint() + getString(R.string.cannot_be_empty));
            return;
        }
        exit(newPassword, verCode);
    }

    /**
     * if new password is not null and code is not null
     * passes newPass, code to login activity. sets the result to "RESULT_OK"
     * this invokes onActivityResult in LoginActivity, which handles setting the new password through amazon cognito identity provider
     * @param newPass New Password
     * @param code ver code
     */
    private void exit(String newPass, String code) {
        Intent intent = new Intent();
        if(newPass == null || code == null) {
            newPass = "";
            code = "";
        }
        intent.putExtra("newPass", newPass);
        intent.putExtra("code", code);
        setResult(RESULT_OK, intent);
        finish();
    }
}
