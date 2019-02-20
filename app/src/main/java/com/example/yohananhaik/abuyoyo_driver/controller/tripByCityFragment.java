package com.example.yohananhaik.abuyoyo_driver.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yohananhaik.abuyoyo_driver.R;
import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.backend.BackendFactory;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;

import java.util.ArrayList;
import java.util.List;


public class tripByCityFragment extends Fragment {

    public static final String ABUD_PREFS = "AbudPrefs";
    public static final String DISPLAY_ID = "id";

    private RecyclerView tripsRecycleView;
    private List<Trip> trips;
    Spinner spinnerSort;
    SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        //define the recycler view
        tripsRecycleView = getActivity().findViewById(R.id.tripsRecyclerView);
        tripsRecycleView.setHasFixedSize(true);
        tripsRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));
        prefs = getActivity().getSharedPreferences(ABUD_PREFS,0);
   }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_trip_by_city, container, false);

        initializesSpinner(view);

        return view;
    }

    void initializesSpinner(View view)
    {
        List<String> citys = new ArrayList<>();

        Backend dataBase = BackendFactory.getBackend();
        trips = dataBase.getAllTrip();
        for (Trip trip:trips) {
            if(!citys.contains(trip.getCityDestination()))
                citys.add(trip.getCityDestination());
        }
        spinnerSort = (Spinner) view.findViewById(R.id.spinner3);
        spinnerSort.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, citys));
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
                Backend dataBase = BackendFactory.getBackend();
                dataBase.stopNotifyToTriptList();
                dataBase.notifyToTripList(new Backend.NotifyDataChange<List<Trip>>() {
                    @Override
                    public void OnDataChanged(List<Trip> obj) {
                        // Sort by the city needed
                        if (tripsRecycleView.getAdapter() == null) {
                            trips = obj;
                            tripsRecycleView.setAdapter(new tripByCityFragment.tripsRecycleViewAdapter());
                        } else
                            tripsRecycleView.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        Toast.makeText(getActivity().getBaseContext(), "error to get  list\n" + exception.toString(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public boolean check(Trip trip) {
                        if(trip.getIdDriver() == null)
                            return false;
                        return trip.getIdDriver().equals(prefs.getString(DISPLAY_ID, "")) &&
                                trip.getCityDestination().equals(item) ;
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onDestroy() {
        Backend dataBase = BackendFactory.getBackend();
        dataBase.stopNotifyToTriptList();
        super.onDestroy();
    }



    public class tripsRecycleViewAdapter extends RecyclerView.Adapter<tripByCityFragment.tripsRecycleViewAdapter.tripViewHolder> {

        @Override
        public tripByCityFragment.tripsRecycleViewAdapter.tripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.history_trip_item_view,
                    parent,
                    false);

            return new tripByCityFragment.tripsRecycleViewAdapter.tripViewHolder(v);
        }

        @Override
        public void onBindViewHolder(tripByCityFragment.tripsRecycleViewAdapter.tripViewHolder holder, int position) {

            Trip trip = trips.get(position);
            holder.passengerLocationTextView.setText(trip.getPickUpLoc());
            holder.tripDestinationTextView.setText(trip.getDestinationLoc());
            holder.textViewTripLength.setText(trip.getTripDistance());
        }

        @Override
        public int getItemCount() {
            return trips.size();
        }


        class tripViewHolder extends RecyclerView.ViewHolder {

            TextView passengerLocationTextView;
            TextView tripDestinationTextView;
            TextView textViewTripLength;

            tripViewHolder(View itemView) {
                super(itemView);
                passengerLocationTextView = itemView.findViewById(R.id.passengerLocationTextView);
                tripDestinationTextView = itemView.findViewById(R.id.destinationTextView);
                textViewTripLength = itemView.findViewById(R.id.textViewNumDes);

            }
        }
    }
}
