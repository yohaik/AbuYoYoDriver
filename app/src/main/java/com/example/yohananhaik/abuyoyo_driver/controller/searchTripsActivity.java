package com.example.yohananhaik.abuyoyo_driver.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yohananhaik.abuyoyo_driver.R;
import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.backend.BackendFactory;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class searchTripsActivity extends AppCompatActivity {

    private RecyclerView tripsRecycleView;
    private List<Trip> trips;
    private Location driverLocation;
    Address passengerLocation;
    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    //Define a listener that responds to location updates
    LocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trips);


        //define the recycer view
        tripsRecycleView = findViewById(R.id.tripsRecyclerView);
        tripsRecycleView.setHasFixedSize(true);
        tripsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        //add

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
                Toast.makeText(getBaseContext(), "error to get students list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });

        //gps listener
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                driverLocation = location;
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

    @Override
    protected void onDestroy() {
        Backend dataBase = BackendFactory.getBackend();
        dataBase.stopNotifyToStudentList();
        super.onDestroy();
    }

    public class tripsRecycleViewAdapter extends RecyclerView.Adapter<tripsRecycleViewAdapter.tripViewHolder> {

        @Override
        public tripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getBaseContext()).inflate(R.layout.trip_item_view,
                    parent,
                    false);

            return new tripViewHolder(v);
        }

        @Override
        public void onBindViewHolder(tripViewHolder holder, int position) {

            Trip trip = trips.get(position);
            holder.locationTextView.setText(trip.getPickUpLoc());
            holder.destentionTextView.setText(trip.getDestinationLoc());
            holder.textViewNumber.setText(Integer.toString(position));
            passengerLocation = findLocationByName(trip.getPickUpLoc());
            holder.textViewNumDes.setText(findDistance(driverLocation,passengerLocation));
        }

        @Override
        public int getItemCount() {
            return trips.size();
        }


        class tripViewHolder extends RecyclerView.ViewHolder {

            TextView locationTextView;
            TextView destentionTextView;
            TextView textViewNumber;
            TextView textViewNumDes;

            tripViewHolder(View itemView) {
                super(itemView);
                locationTextView = itemView.findViewById(R.id.locationTextView);
                destentionTextView = itemView.findViewById(R.id.destentionTextView);
                textViewNumber = itemView.findViewById(R.id.textViewNumber);
                textViewNumDes = itemView.findViewById(R.id.textViewNumDes);

                // itemView.setOnClickListener();
            }
        }
    }

    //metod that fine location by string
    public Address findLocationByName(String addresStr){
        if(addresStr == null){
            return null;
        }
        List<Address> address = null;
        Geocoder coder = new Geocoder(getApplicationContext());
        try {
            address = coder.getFromLocationName(addresStr, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (address != null) {
            return address.get(0);
        }
        return null;
    }

    //find distance between address
    public String findDistance(Location locationA, Address locationB){
        if(locationA == null || locationB == null){
            return "0";
        }
        float[] results = new float[1];
        Location.distanceBetween(locationA.getLatitude(), locationA.getLongitude(),
                locationB.getLatitude(),locationB.getLongitude(), results);

       // Location loc = new Location("location");
       // loc.setLatitude(locationB.getLatitude());
       // loc.setLongitude(locationB.getLongitude());

       // float distance = locationA.distanceTo(loc);

        if(results[0]>1000)
          return ""+results[0]/1000+" km";
        else
        {
            return ""+results[0]  +" meter";
        }
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


}


