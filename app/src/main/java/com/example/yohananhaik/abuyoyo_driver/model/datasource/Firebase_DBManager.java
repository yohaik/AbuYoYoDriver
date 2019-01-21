package com.example.yohananhaik.abuyoyo_driver.model.datasource;

import android.support.annotation.NonNull;

import com.example.yohananhaik.abuyoyo_driver.model.backend.Backend;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Driver;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Firebase_DBManager implements Backend {

    static List<Trip> tripList;
    static DatabaseReference tripsRef;
    static DatabaseReference driverRef;
    static ChildEventListener tripRefChildEventListener;

    static {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        tripsRef = database.getReference("Trips");
        driverRef = database.getReference("Drivers");
        tripList = new ArrayList<>();
    }

    //add or update the database
    @Override
    public void addDriver(Driver driver,final Action action) {
        Task<Void> task = driverRef.push().setValue(driver);

        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess();
                action.onProgress("upload driver data", 100);

            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("error upload driver data", 100);
            }
        });


    }
    //public void updateTrip(Trip trip, final Action actuon);

    //get trip list
    public void notifyToTripList(final NotifyDataChange<List<Trip>> notifyDataChange) {
        if (notifyDataChange != null) {

            if (tripRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify trip list"));
                return;
            }
            tripList.clear();

            tripRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String id = dataSnapshot.getKey();
                    trip.setId(id);

                    if(notifyDataChange.check(trip) == true){
                    tripList.add(trip);
                    notifyDataChange.OnDataChanged(tripList);}
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

    public void stopNotifyToTriptList() {
        if (tripRefChildEventListener != null) {
            tripsRef.removeEventListener(tripRefChildEventListener);
            tripRefChildEventListener = null;
        }
    }

    //check if driver exsixsit
    @Override
    public void isValidDriverAuthentication(String emailForCheck,final String passwordForCheck,final Action action) {
        Query query  = driverRef.orderByChild("email").equalTo(emailForCheck);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    Driver checkDriver=dataSnapshot.getChildren().iterator().next().getValue(Driver.class);
                    if(checkDriver.getPassword().equals(passwordForCheck))
                        action.onSuccess();
                    else
                        action.onFailure(new Exception("wrong password"));
                }
                else
                    action.onFailure(new Exception("User does not exist"));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
