package com.example.yohananhaik.abuyoyo_driver.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yohananhaik.abuyoyo_driver.R;
import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.backend.BackendFactory;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;
import com.example.yohananhaik.abuyoyo_driver.model.entities.mTrip;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.yohananhaik.abuyoyo_driver.controller.LoginActivity.ABUD_PREFS;

public class CurrentTripActivity extends AppCompatActivity {

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

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_trip);
        Toast.makeText(this,"Create!", Toast.LENGTH_LONG).show();
        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            position =  extras.getInt("tripPosituin");
            currentTrip = dataBase.getTrip(position);

        }
       // dataBase.updateTripStatus(currentTrip.getId(), "InProgress");
        currentTrip.setTripStatus(mTrip.InProgress);
      //  pref = getSharedPreferences(ABUD_PREFS,0);

        dataBase.updateTrip(currentTrip);

     //   setViewFields();

     //   initiateFieldBaseData();

    //    setButtons();
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
                                dataBase.updateTripStatus(tripID, "Available");
                                startActivity(new Intent(CurrentTripActivity.this,searchTripsActivity.class));
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
                builder.setTitle("Finish Trip")
                        .setMessage("Is this trip over?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dataBase.updateTripStatus(tripID, "Completed");
                                startActivity(new Intent(CurrentTripActivity.this,searchTripsActivity.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_menu_help)
                        .show();
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

    }


}
