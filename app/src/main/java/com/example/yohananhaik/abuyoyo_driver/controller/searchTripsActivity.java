package com.example.yohananhaik.abuyoyo_driver.controller;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yohananhaik.abuyoyo_driver.R;
import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.backend.BackendFactory;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;
import com.example.yohananhaik.abuyoyo_driver.model.entities.mTrip;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.example.yohananhaik.abuyoyo_driver.controller.LoginActivity.ABUD_PREFS;

public class searchTripsActivity extends AppCompatActivity {

    private RecyclerView tripsRecycleView;
    private List<Trip> trips;
   // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    //Define a listener that responds to location updates
    LocationListener locationListener;
    private boolean expanderVisible = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trips);

        //define the recycler view
        tripsRecycleView = findViewById(R.id.tripsRecyclerView);
        tripsRecycleView.setHasFixedSize(true);
        tripsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        updateTripList();

        //gps listener
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        getLocation();
    }

    // Define the listener and update when needed
    private void updateTripList() {
        Backend dataBase = BackendFactory.getBackend();

        dataBase.notifyToTripList(new Backend.NotifyDataChange<List<Trip>>() {
            @Override
            public void OnDataChanged(List<Trip> obj) {
                if (tripsRecycleView.getAdapter() == null) {
                    trips = obj;
                    tripsRecycleView.setAdapter(new tripsRecycleViewAdapter());
                } else
                    tripsRecycleView.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getBaseContext(), "error to get  list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public boolean check(Trip trip) {
                return trip.getTripStatus()== mTrip.Available;
            }
        });
    }

    @Override
    protected void onDestroy() {
        Backend dataBase = BackendFactory.getBackend();
        dataBase.stopNotifyToTriptList();
        super.onDestroy();
    }

    // Defin inner class for the view of each trip in the list
    public class tripsRecycleViewAdapter extends RecyclerView.Adapter<tripsRecycleViewAdapter.tripViewHolder> {

        @Override
        public tripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.trip_item_view,
                    parent,
                    false);

            return new tripViewHolder(v);


        }

        @Override
        public void onBindViewHolder(tripViewHolder holder, final int position) {

            final Trip trip = trips.get(position);
            holder.passengerLocationTextView.setText(trip.getPickUpLoc());
            holder.tripDestinationTextView.setText(trip.getDestinationLoc());
            holder.textViewTripLength.setText(trip.getTripDistance());
            holder.passengerPhoneTextView.setText(trip.getPassengerPhone());
           holder.passengerNameTextView.setText(trip.getPassengerName());

            holder.takeTrip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSMS(trip);
                    Backend dataBase = BackendFactory.getBackend();
                    dataBase.stopNotifyToTriptList();
                    Intent intent = new Intent(searchTripsActivity.this, CurrentTripActivity.class);
                    intent.putExtra("tripPosituin", position);
                    intent.putExtra("driverID",trip.getIdDriver());
                    startActivity(intent);
                }
            });


        }

        @Override
        public int getItemCount() {
            return trips.size();
        }


        class tripViewHolder extends RecyclerView.ViewHolder {

            TextView passengerLocationTextView;
            TextView tripDestinationTextView;
            TextView textViewTripLength;
            TextView passengerNameTextView;
            TextView passengerPhoneTextView;
            FloatingActionButton takeTrip;
            ConstraintLayout expanderLayout;

            tripViewHolder(View itemView) {
                super(itemView);
                passengerLocationTextView = itemView.findViewById(R.id.passengerLocationTextView);
                tripDestinationTextView = itemView.findViewById(R.id.destinationTextView);
                textViewTripLength = itemView.findViewById(R.id.textViewNumDes);
                passengerNameTextView = itemView.findViewById(R.id.passengerName);
                passengerPhoneTextView = itemView.findViewById(R.id.passengerPhone);
                takeTrip = itemView.findViewById(R.id.takeTripButton);
                expanderLayout = itemView.findViewById(R.id.expander_item);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (expanderVisible)
                        {
                            expanderLayout.setVisibility(View.GONE);
                            expanderVisible = false;
                        }
                        else
                        {
                            Log.d("abu", "cloik false");
                            expanderLayout.setVisibility(View.VISIBLE);
                            expanderVisible =true;
                        }
                    }
                });


            }
        }
    }

    private void sendSMS(Trip trip) {
        String number = trip.getPassengerPhone();
        String messageToSend = "I'm on my way!\nYou can call me if needed.";

        SmsManager.getDefault().sendTextMessage(number, null, messageToSend, null,null);
    }



    //metod fined gps location
    private void getLocation() {

        //     Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);


        } else {
            // Android version is less than 6.0 or the permission is already granted.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }

    }

    public String getPlace(Location location) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size() > 0) {
                String address = addresses.get(0).getAddressLine(0);
                return address;
            }

            return "no place: \n ("+location.getLongitude()+" , "+location.getLatitude()+")";
        }
        catch(
                IOException e)

        {
            e.printStackTrace();
        }
        return "IOException ...";
    }


}


