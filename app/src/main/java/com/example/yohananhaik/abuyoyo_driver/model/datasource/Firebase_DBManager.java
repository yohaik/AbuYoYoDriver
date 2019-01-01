package com.example.yohananhaik.abuyoyo_driver.model.datasource;

import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class Firebase_DBManager implements Backend {

    static List<Trip> tripList;
    static DatabaseReference tripsRef;
    static ChildEventListener tripRefChildEventListener;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tripsRef = database.getReference("Trips");
        tripList = new ArrayList<>();
    }

    public void notifyToTripList(final NotifyDataChange<List<Trip>> notifyDataChange) {
        if (notifyDataChange != null) {

            if (tripRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify student list"));
                return;
            }
            tripList.clear();

            tripRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String id = dataSnapshot.getKey();
                    trip.setId(id);
                    tripList.add(trip);


                    notifyDataChange.OnDataChanged(tripList);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String id = dataSnapshot.getKey();
                    trip.setId(id);


                    for (int i = 0; i < tripList.size(); i++) {
                        if (tripList.get(i).getId().equals(id)) {
                            tripList.set(i, trip);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(tripList);
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String id = dataSnapshot.getKey();
                    trip.setId(id);


                    for (int i = 0; i < tripList.size(); i++) {
                        if (tripList.get(i).getId().equals(id)) {
                            tripList.remove(i);
                            break;
                        }
                    }
                    notifyDataChange.OnDataChanged(tripList);

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            tripsRef.addChildEventListener(tripRefChildEventListener);
        }
    }

    public void stopNotifyToStudentList() {
        if (tripRefChildEventListener != null) {
            tripsRef.removeEventListener(tripRefChildEventListener);
            tripRefChildEventListener = null;
        }
    }

}
