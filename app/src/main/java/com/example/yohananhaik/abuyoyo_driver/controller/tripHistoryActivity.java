package com.example.yohananhaik.abuyoyo_driver.controller;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.yohananhaik.abuyoyo_driver.R;
import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.backend.BackendFactory;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;

import java.util.List;

public class tripHistoryActivity extends AppCompatActivity {

    public static final String ABUD_PREFS = "AbudPrefs";
    public static final String DISPLAY_ID = "id";

    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_history);
        prefs = getSharedPreferences(ABUD_PREFS,0);
        Backend dataBase = BackendFactory.getBackend();
        dataBase.stopNotifyToTriptList();
        dataBase.notifyToTripList(new Backend.NotifyDataChange<List<Trip>>() {
            @Override
            public void OnDataChanged(List<Trip> obj) {

            }
            //what

            @Override
            public void onFailure(Exception exception) {
            }

            @Override
            public boolean check(Trip trip) {
                if(trip.getIdDriver() == null)
                    return false;
                return trip.getIdDriver().equals(prefs.getString(DISPLAY_ID, "")) ;
            }
        });

        dl = (DrawerLayout)findViewById(R.id.activity_trip_history);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle("Driver Trip");
                // calling onPrepareOptionsMenu() to show action bar icons
                supportInvalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("Driver Trip");
                // calling onPrepareOptionsMenu() to hide action bar icons
                supportInvalidateOptionsMenu();
            }
        };

        dl.addDrawerListener(t);
        t.syncState();


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.allTrip:
                        loadFragment(new tripDriverFragment());
                        return true;
                    case R.id.tripByCity:
                        loadFragment(new tripByCityFragment());
                        return true;
                    case R.id.tripByDistance:
                        loadFragment(new tripByDistanceFragment());
                        return true;
                    default:
                        return true;
                }
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(t.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    private void loadFragment(Fragment fragment) {
// create a FragmentManager
        FragmentManager fm = getFragmentManager();
// create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
// replace the FrameLayout with new Fragment
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit(); // save the changes
    }

}
//
