package com.example.yohananhaik.abuyoyo_driver.controller;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yohananhaik.abuyoyo_driver.R;
import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.backend.BackendFactory;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Driver;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterActivity extends AppCompatActivity {

    // Constants
    public static final String ABUD_PREFS = "AbudPrefs";
    public static final String DISPLAY_NAME_KEY = "username";
    public static final String DISPLAY_PHONE = "phone";
    public static final String DISPLAY_ID = "id";

    // TODO: Add member variables here:
    // UI references.
    private AutoCompleteTextView mEmailView;
    private AutoCompleteTextView mUsernameView;
    private AutoCompleteTextView mIdView;
    private EditText mPasswordView;
    private EditText mConfirmPasswordView;
    private EditText mPhoneView;
    private Button mLogInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initializeFields();
    }

    //initialize the field
    void initializeFields(){
        mEmailView = (AutoCompleteTextView) findViewById(R.id.register_email);
        mPasswordView = (EditText) findViewById(R.id.register_password);
        mConfirmPasswordView = (EditText) findViewById(R.id.register_confirm_password);
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.register_username);
        mPhoneView = findViewById(R.id.register_phone);
        mIdView = (AutoCompleteTextView) findViewById(R.id.register_id);
        mLogInButton = (Button) findViewById(R.id.register_sign_up_button);

        // Keyboard sign in action
        mConfirmPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 200 || id == EditorInfo.IME_NULL) {
                    attemptRegistration();
                    return true;
                }
                return false;
            }
        });

    }


    protected Driver createDriverInstnceFromFields() {
        Driver driver = new Driver();

        driver.setId(mIdView.getText().toString());
        driver.setName(mUsernameView.getText().toString());
        driver.setPhoneNum(mPhoneView.getText().toString());
        driver.setEmail(mEmailView.getText().toString());
        driver.setPassword(mPasswordView.getText().toString());

        return  driver;
    }


    // Executed when Sign Up button is pressed.
    public void signUp(View v) {
        attemptRegistration();
    }

    private void attemptRegistration() {

        // Reset errors displayed in the form.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // TODO: Call create FirebaseUser() here
            createFirebaseUser();
        }
    }

    private boolean isEmailValid(String email) {
        // You can add more checking logic here.
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {

        String confirmPassword = mConfirmPasswordView.getText().toString();

        //TODO: Add own logic to check for a valid password (minimum 6 characters)
        return confirmPassword.equals(password) && password.length() > 5 && !password.contains(" ");
    }

    // TODO: Create a Firebase user
    private void createFirebaseUser(){
        Driver driver = createDriverInstnceFromFields();

        try {
            mLogInButton.setEnabled(false);
            Backend dataBase = BackendFactory.getBackend();
            dataBase.addDriver(driver,new Backend.Action() {
                @Override
                public void onSuccess() {
                    Toast.makeText(getBaseContext(), "createUser onComplete", Toast.LENGTH_LONG).show();
                    mLogInButton.setEnabled(true);
                    saveDisplayName();
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    finish();
                    startActivity(intent);
                }

                @Override
                public void onFailure(Exception exception) {
                    Toast.makeText(getBaseContext(), "createUser failed", Toast.LENGTH_LONG).show();
                    mLogInButton.setEnabled(true);
                }

                @Override
                public void onProgress(String status, double percent) {
                    if( percent != 100)
                        mLogInButton.setEnabled(false);
                }
            });
        } catch (Exception e){
            Toast.makeText(getBaseContext(), "Error \n", Toast.LENGTH_LONG).show();
            mLogInButton.setEnabled(true);
        }

    }

    // TODO: Save the display name to Shared Preferences
    private  void saveDisplayName(){
        String displayEmail = mEmailView.getText().toString();
        String displayPhone = mPhoneView.getText().toString();
        String displyId = mIdView.getText().toString();
        SharedPreferences prefs = getSharedPreferences(ABUD_PREFS,0);
        prefs.edit().putString(DISPLAY_NAME_KEY, displayEmail).apply();
        prefs.edit().putString(DISPLAY_PHONE,displayPhone).apply();
        prefs.edit().putString(DISPLAY_ID,displyId).apply();
    }





}