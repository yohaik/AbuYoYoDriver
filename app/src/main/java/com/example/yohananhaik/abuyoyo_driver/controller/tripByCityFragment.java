package com.example.yohananhaik.abuyoyo_driver.controller;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.yohananhaik.abuyoyo_driver.R;
import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.backend.BackendFactory;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;

import java.util.List;


public class tripByCityFragment extends Fragment {

    private RecyclerView tripsRecycleView;
    private List<Trip> trips;
    private Button buttonSearch;
    private TextView cityTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        //define the recycler view
        cityTextView = (AutoCompleteTextView) getActivity().findViewById(R.id.cityTextView);
        buttonSearch = (Button) getActivity().findViewById(R.id.buttonSearch);
        tripsRecycleView = getActivity().findViewById(R.id.tripsRecyclerView);
        tripsRecycleView.setHasFixedSize(true);
        tripsRecycleView.setLayoutManager(new LinearLayoutManager(getContext()));

        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Backend dataBase = BackendFactory.getBackend();

                dataBase.notifyToTripList(new Backend.NotifyDataChange<List<Trip>>() {
                    @Override
                    public void OnDataChanged(List<Trip> obj) {
                        if (tripsRecycleView.getAdapter() == null) {
                            trips = obj;
                            tripsRecycleView.setAdapter(new tripByCityFragment.tripsRecycleViewAdapter());
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
                        return true;
                    }
                });
            }

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trip_by_city, container, false);
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
