package com.example.yohananhaik.abuyoyo_driver.model.backend;

import com.example.yohananhaik.abuyoyo_driver.model.entities.Driver;
import com.example.yohananhaik.abuyoyo_driver.model.entities.Trip;

import java.util.List;

public interface Backend{
    public interface Action{
    void onSuccess();

    void onFailure(Exception exception);

    void onProgress(String status, double percent);
}

    public interface NotifyDataChange<T> {
        void OnDataChanged(T obj);

        void onFailure(Exception exception);
    }

    void notifyToTripList(final NotifyDataChange<List<Trip>> notifyDataChange);
    void stopNotifyToStudentList();

    void isValidDriverAuthentication(String emailForCheck, String passwordForCheck, Action action);

    void addDriver(Driver driver, final Action action);
}