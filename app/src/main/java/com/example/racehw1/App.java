package com.example.racehw1;

import android.app.Application;

import utils.LocationFinder;
import utils.SPTool;
import utils.SignalGenerator;
import utils.SoundGenerator;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        SignalGenerator.init(this);
        SoundGenerator.init(this);
        SPTool.init(this);
        //LocationFinder.init(this); works better when init in game activity
    }
}
