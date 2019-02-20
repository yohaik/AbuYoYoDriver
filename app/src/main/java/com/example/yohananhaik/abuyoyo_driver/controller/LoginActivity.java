package com.example.yohananhaik.abuyoyo_driver.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseAuth;

import static com.example.yohananhaik.abuyoyo_driver.controller.RegisterActivity.DISPLAY_EMAIL;


public class  LoginActivity extends AppCompatActivity {


    public static final String ABUD_PREFS = "AbudPrefs";
    private FirebaseAuth mAuth;
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private Button mLogInButton;
    SharedPreferences prefs;

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        displaySplashScreen();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Connect the objects for screen
        initializeFields();

        // Ask for all permissions needed
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }

    private boolean hasPermissions(Context context, String[] permissions) {

            if (context != null && permissions != null) {
                for (String permission : permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
            return true;
        }

    private void displaySplashScreen() {
        setTheme(R.style.AppTheme);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void initializeFields() {
        mEmailView =  findViewById(R.id.email);
        mPasswordView =  findViewById(R.id.password);
        mLogInButton =  findViewById(R.id.sign_in_button);
        prefs = getSharedPreferences(ABUD_PREFS,0);

        if (prefs.contains(DISPLAY_EMAIL))
            mEmailView.setText(prefs.getString("email", ""));

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
        mAuth = FirebaseAuth.getInstance();
    }

    // Executed when Sign in button pressed
    public void signInExistingUser(View v)   {
        attemptLogin();
    }

    // Executed when Register button pressed
    public void registerNewUser(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        finish();
        startActivity(intent);
    }

    private void attemptLogin() {
        Backend dataBase = BackendFactory.getBackend();
        try {
            if (mEmailView.getText().toString().equals("") || mPasswordView.getText().toString().equals(""))
                return;
            else {
                Toast.makeText(this, "Login in progress....", Toast.LENGTH_SHORT).show();
                mLogInButton.setEnabled(false);
            }
            dataBase.isValidDriverAuthentication(mEmailView.getText().toString(),
                        mPasswordView.getText().toString(), new Backend.Action() {
                            @Override
                            public void onSuccess() {
                                mLogInButton.setEnabled(true);
                                prefs.edit().putString(DISPLAY_EMAIL, mEmailView.getText().toString()).apply();
                                Toast.makeText(getBaseContext(), "login success", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                finish();
                                startActivity(intent);
                            }

                            @Override
                            public void onFailure(Exception exception) {
                                mLogInButton.setEnabled(true);
                                Toast.makeText(getBaseContext(), exception.getMessage().toString(), Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onProgress(String status, double percent) {
                                if (percent != 100)
                                    mLogInButton.setEnabled(false);
                            }});
            }catch(Exception e){
                Toast.makeText(getBaseContext(), e.getMessage().toString(), Toast.LENGTH_LONG).show();
                mLogInButton.setEnabled(true);
            }
        }




}
