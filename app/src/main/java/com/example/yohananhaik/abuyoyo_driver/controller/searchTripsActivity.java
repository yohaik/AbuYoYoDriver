package com.example.yohananhaik.abuyoyo_driver.controller;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
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
   // private Location driverLocation;
   // private Location passengerLocation;
    // Acquire a reference to the system Location Manager
    LocationManager locationManager;
    //Define a listener that responds to location updates
    LocationListener locationListener;
    private String dLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_trips);

        //define the recycler view
        tripsRecycleView = findViewById(R.id.tripsRecyclerView);
        tripsRecycleView.setHasFixedSize(true);
        tripsRecycleView.setLayoutManager(new LinearLayoutManager(this));

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
            //what

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getBaseContext(), "error to get  list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }
        });

        //gps listener
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
          //      driverLocation = location;
                dLocation = getPlace(location);
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
        dataBase.stopNotifyToTriptList();
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
            holder.passengerLocationTextView.setText(trip.getPickUpLoc());
            holder.tripDestinationTextView.setText(trip.getDestinationLoc());
            holder.textViewTripLength.setText(trip.getTripDistance());
            holder.passengerName.setText(trip.getPassengerName());
            holder.passengerPhone.setText(trip.getPassengerPhone());
        }

        @Override
        public int getItemCount() {
            return trips.size();
        }


        class tripViewHolder extends RecyclerView.ViewHolder {

            TextView passengerLocationTextView;
            TextView tripDestinationTextView;
            TextView textViewTripLength;
            TextView passengerName;
            TextView passengerPhone;
            FloatingActionButton takeTrip;
            FrameLayout expander;
            boolean isExpanderVisible = false;


            tripViewHolder(View itemView) {
                super(itemView);
                passengerLocationTextView = itemView.findViewById(R.id.passengerLocationTextView);
                tripDestinationTextView = itemView.findViewById(R.id.destinationTextView);
                textViewTripLength = itemView.findViewById(R.id.textViewNumDes);
                passengerName = itemView.findViewById(R.id.passenger_name);
                passengerPhone = itemView.findViewById(R.id.passenger_phone);
                takeTrip = itemView.findViewById(R.id.takeTrip);
                expander = itemView.findViewById(R.id.itemExpanderLayout);
                expander.setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isExpanderVisible)
                        {
                            expander.setVisibility(View.GONE);
                            isExpanderVisible = false;
                        }
                        else {
                            expander.setVisibility(View.VISIBLE);
                            isExpanderVisible = true;
                        }
                        }

                });
            }



        }
    }



    //find distance between address
    public String findDistance(Location locationA, Location locationB){
        if(locationA == null || locationB == null){
            return "0";
        }
        float[] results = new float[1];
        Location.distanceBetween(locationA.getLatitude(), locationA.getLongitude(),
                locationB.getLatitude(),locationB.getLongitude(), results);

        if(results[0]>1000) {
            float result = results[0] / 1000;
            String res = String.format("%.2f", result);
            return res;
        }
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

    private Location getTripDestinationAsLocation(String addressToConvert)
    {
        Location myLocation = new Location(addressToConvert);
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> geoResults = geocoder.getFromLocationName(addressToConvert, 1);
            while (geoResults.size()==0) {
                geoResults = geocoder.getFromLocationName(addressToConvert, 1);
            }
            if (geoResults.size()>0) {
                Address addr = geoResults.get(0);
                myLocation.setLatitude(addr.getLatitude());
                myLocation.setLongitude(addr.getLongitude());
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
        return myLocation;
    }



}


