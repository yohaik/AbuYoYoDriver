package com.example.yohananhaik.abuyoyo_driver.controller;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.yohananhaik.abuyoyo_driver.R;
import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.backend.BackendFactory;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;
import com.example.yohananhaik.abuyoyo_driver.model.entities.mTrip;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.yohananhaik.abuyoyo_driver.controller.LoginActivity.ABUD_PREFS;
import static com.example.yohananhaik.abuyoyo_driver.controller.RegisterActivity.DISPLAY_ID;

public class CurrentTripActivity extends AppCompatActivity {
    private static final int REQUEST_PHONE_CALL = 1;

    private String tripID;
    private int position;
    Trip currentTrip = new Trip();
    Backend dataBase = BackendFactory.getBackend();

    TextView passengerName;
    TextView passengerPhone;
    TextView passengerLocation;
    TextView tripDestination;
    Button cancelTrip;
    Button finishTrip;
    Button callButton;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trip);
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            position = extras.getInt("tripPosituin");
            currentTrip = dataBase.getTrip(position);

        }
        prefs = getSharedPreferences(ABUD_PREFS, 0);

        // dataBase.updateTripStatus(currentTrip.getId(), "InProgress");
        currentTrip.setTripStatus(mTrip.InProgress);
        currentTrip.setIdDriver(prefs.getString(DISPLAY_ID, ""));
        //  pref = getSharedPreferences(ABUD_PREFS,0);

        dataBase.updateTrip(currentTrip);

        setViewFields();

        initiateFieldBaseData();

        setButtons();
    }

    private void setButtons() {

        cancelTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(CurrentTripActivity.this);
                builder.setTitle("Cancel Trip")
                        .setMessage("Are you sure you want to cancel this trip?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                currentTrip.setTripStatus(mTrip.Available);
                                currentTrip.setTripStartTime(null);
                                currentTrip.setIdDriver(null);
                                dataBase.updateTrip(currentTrip);

                                startActivity(new Intent(CurrentTripActivity.this, MenuActivity.class));
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        finishTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;

                builder = new AlertDialog.Builder(CurrentTripActivity.this);
                builder.setTitle("Finish Trip");
                builder.setMessage("Is this trip over?");
                builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        currentTrip.setTripEndTime(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
                        currentTrip.setTripStatus(mTrip.Completed);
                        dataBase.updateTrip(currentTrip);

                        startActivity(new Intent(CurrentTripActivity.this, MenuActivity.class));
                        finish();
                    }
                });
                builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                builder.setIcon(android.R.drawable.ic_menu_help);
                builder.show();
            }


        });

        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:"+currentTrip.getPassengerPhone()));
                if (ActivityCompat.checkSelfPermission(CurrentTripActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CurrentTripActivity.this, new String[]{Manifest.permission.CALL_PHONE},REQUEST_PHONE_CALL);
                    return;
                }
                startActivity(callIntent);
            }
        });
    }

    private void initiateFieldBaseData() {
        passengerName.setText(currentTrip.getPassengerName());
        passengerPhone.setText(currentTrip.getPassengerPhone());
        passengerLocation.setText(currentTrip.getPickUpLoc());
        tripDestination.setText(currentTrip.getDestinationLoc());
    }

    private void setViewFields() {
        passengerName = findViewById(R.id.passengerName);
        passengerPhone = findViewById(R.id.passengerPhone);
        passengerLocation = findViewById(R.id.from);
        tripDestination = findViewById(R.id.to);
        cancelTrip = findViewById(R.id.cancelTripButton);
        finishTrip = findViewById(R.id.finishTripButton);
        callButton = (Button) findViewById(R.id.callButton);

    }


}
