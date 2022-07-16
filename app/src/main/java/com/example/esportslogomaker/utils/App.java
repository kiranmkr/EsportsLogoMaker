package com.example.esportslogomaker.utils;

import androidx.multidex.MultiDexApplication;

public class App extends MultiDexApplication {

    private static App sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
    }


    public static App getInstance() {
        return sInstance;
    }

}
