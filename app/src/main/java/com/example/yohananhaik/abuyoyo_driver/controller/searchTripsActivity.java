package com.example.yohananhaik.abuyoyo_driver.controller;

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

import java.util.List;

public class searchTripsActivity extends AppCompatActivity {

    private RecyclerView tripsRecycleView;
    private List<Trip> trips;

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
        }

        @Override
        public int getItemCount() {
            return trips.size();
        }


        class tripViewHolder extends RecyclerView.ViewHolder {

            TextView locationTextView;
            TextView destentionTextView;

            tripViewHolder(View itemView) {
                super(itemView);
                locationTextView = itemView.findViewById(R.id.locationTextView);
                destentionTextView = itemView.findViewById(R.id.destentionTextView);

                // itemView.setOnClickListener();
            }
        }
    }

}


