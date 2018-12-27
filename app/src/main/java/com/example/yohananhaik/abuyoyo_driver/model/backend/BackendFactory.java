package com.example.yohananhaik.abuyoyo_driver.model.backend;

import com.example.yohananhaik.abuyoyo_driver.model.datasource.Firebase_DBManager;

public class BackendFactory {
    private static Backend backend = null;

    public static Backend getBackend(){
        if(backend == null){
            backend = new Firebase_DBManager();
        }
        return backend;
    }
}
