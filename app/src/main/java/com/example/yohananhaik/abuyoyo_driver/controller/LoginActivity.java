package com.example.yohananhaik.abuyoyo_driver.controller;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class  LoginActivity extends AppCompatActivity {


    // Constants
    public static final String ABUD_PREFS = "AbudPrefs";
    public static final String DISPLAY_NAME_KEY = "username";

    // TODO: Add member variables here:
    private FirebaseAuth mAuth;
    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mLogInButtom;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Connect the objects for screen
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mLogInButtom = (Button) findViewById(R.id.sign_in_button);
        prefs = getSharedPreferences(ABUD_PREFS,0);
        if (prefs.contains(DISPLAY_NAME_KEY))
            mEmailView.setText(prefs.getString("username", ""));

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == 100 || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        // TODO: Grab an instance of FirebaseAuth
        mAuth = FirebaseAuth.getInstance();

    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        // TODO: Call attemptLogin() here
        attemptLogin();
    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    // TODO: Complete the attemptLogin() method
    private void attemptLogin() {
        Backend dataBase = BackendFactory.getBackend();
        try {
            if (mEmailView.getText().toString().equals("") || mPasswordView.getText().toString().equals(""))
                return;
            else {
                Toast.makeText(this, "Login in progress....", Toast.LENGTH_SHORT).show();
                mLogInButtom.setEnabled(false);
            }
            dataBase.isValidDriverAuthentication(mEmailView.getText().toString(),
                        mPasswordView.getText().toString(), new Backend.Action() {
                            @Override
                            public void onSuccess() {
                                mLogInButtom.setEnabled(true);
                                prefs.edit().putString(DISPLAY_NAME_KEY, mEmailView.getText().toString()).apply();
                                Toast.makeText(getBaseContext(), "login success", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                finish();
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                mLogInButtom.setEnabled(true);
                                Toast.makeText(getBaseContext(), exception.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onProgress(String status, double percent) {
                                if (percent != 100)
                                    mLogInButtom.setEnabled(false);
                            }});
            }catch(Exception e){
                Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                mLogInButtom.setEnabled(true);
            }
        }




}
