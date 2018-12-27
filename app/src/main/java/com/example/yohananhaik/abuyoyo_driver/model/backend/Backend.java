package com.example.yohananhaik.abuyoyo_driver.model.backend;

import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;

import java.util.List;

public interface Backend{
    public interface Action<T> {
    void onSuccess(T obj);

    void onFailure(Exception exception);

    void onProgress(String status, double percent);
}

    public interface NotifyDataChange<T> {
        void OnDataChanged(T obj);

        void onFailure(Exception exception);
    }

    void notifyToTripList(final NotifyDataChange<List<Trip>> notifyDataChange);
    void stopNotifyToStudentList();


  //  void addRequest(Trip trip, final Action<Void> action);
}