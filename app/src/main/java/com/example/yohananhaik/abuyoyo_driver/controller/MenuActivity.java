package com.example.yohananhaik.abuyoyo_driver.controller;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.yohananhaik.abuyoyo_driver.R;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    //view on activity
    private Button buttonFastTrip;
    private Button buttonSearchTrips;
    private Button buttonTripsHistory;
    private Button buttonAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        findViews();
    }

    private void findViews() {
        buttonFastTrip = (Button) findViewById(R.id.buttonFastTrip);
        buttonFastTrip.setOnClickListener(this);
        buttonSearchTrips = (Button) findViewById(R.id.buttonSearchTrips);
        buttonSearchTrips.setOnClickListener(this);
        buttonTripsHistory = (Button) findViewById(R.id.buttonTripsHistory);
        buttonTripsHistory.setOnClickListener(this);
        buttonAccount = (Button) findViewById(R.id.buttonAccount);
        buttonAccount.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == buttonFastTrip){
            startActivity(new Intent(this,fastTripActivity.class));
        }
        if(v == buttonSearchTrips){
            startActivity(new Intent(this,searchTripsActivity.class));
            // TODO: putExtra of driver location
        }
        if(v == buttonTripsHistory){
            startActivity(new Intent(this,tripHistoryActivity.class));
        }
        if(v == buttonAccount){
            startActivity(new Intent(this,accountActivity.class));
        }

    }
}
