package com.example.yohananhaik.abuyoyo_driver.controller;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yohananhaik.abuyoyo_driver.R;
import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.backend.BackendFactory;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;

import java.util.List;


public class tripDriverFragment extends Fragment {

    public static final String ABUD_PREFS = "AbudPrefs";
    public static final String DISPLAY_ID = "id";

    private RecyclerView tripsRecycleView;
    private List<Trip> trips;
    SharedPreferences prefs;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
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

        Backend dataBase = BackendFactory.getBackend();

        dataBase.notifyToTripList(new Backend.NotifyDataChange<List<Trip>>() {
            @Override
            public void OnDataChanged(List<Trip> obj) {
                if (tripsRecycleView.getAdapter() == null) {
                    trips = obj;
                    tripsRecycleView.setAdapter(new tripDriverFragment.tripsRecycleViewAdapter());
                } else
                    tripsRecycleView.getAdapter().notifyDataSetChanged();
            }
            //what

            @Override
            public void onFailure(Exception exception) {
                Toast.makeText(getActivity().getBaseContext(), "error to get  list\n" + exception.toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public boolean check(Trip trip) {
                return trip.getIdDriver().equals(prefs.getString(DISPLAY_ID, ""));

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_driver, container, false);
    }

    @Override
    public void onDestroy() {
        Backend dataBase = BackendFactory.getBackend();
        dataBase.stopNotifyToTriptList();
        super.onDestroy();
    }

    public class tripsRecycleViewAdapter extends RecyclerView.Adapter<tripDriverFragment.tripsRecycleViewAdapter.tripViewHolder> {

        @Override
        public tripDriverFragment.tripsRecycleViewAdapter.tripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity().getBaseContext()).inflate(R.layout.history_trip_item_view,
                    parent,
                    false);

            return new tripDriverFragment.tripsRecycleViewAdapter.tripViewHolder(v);
        }

        @Override
        public void onBindViewHolder(tripDriverFragment.tripsRecycleViewAdapter.tripViewHolder holder, int position) {

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
