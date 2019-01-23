package com.example.yohananhaik.abuyoyo_driver.model.backend;

import com.example.yohananhaik.abuyoyo_driver.model.entities.Driver;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;
import com.example.yohananhaik.abuyoyo_driver.model.entities.mTrip;

import java.util.List;

public interface Backend{
    void updateTrip(Trip currentTrip);

    public interface Action{
    void onSuccess();

    void onFailure(Exception exception);

    void onProgress(String status, double percent);
}

    public interface NotifyDataChange<T> {
        void OnDataChanged(T obj);

        void onFailure(Exception exception);

        boolean check(Trip trip);
    }

    void notifyToTripList(final NotifyDataChange<List<Trip>> notifyDataChange);
    void stopNotifyToTriptList();

    void isValidDriverAuthentication(String emailForCheck, String passwordForCheck, Action action);

    void addDriver(Driver driver, final Action action);
   // void updateTrip(Trip trip, final Action actuon);

    void updateTripStatus(String tripID, String status);
     Trip getTrip(int position);
     List<Trip> getAllTrip();

}