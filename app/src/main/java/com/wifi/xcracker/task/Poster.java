package com.wifi.xcracker.task;

import android.os.Handler;
import android.os.Looper;

public class Poster extends Handler {
    private static Poster instance;
    static Poster getInstance(){
        if (instance == null)
            synchronized (com.wifi.xcracker.util.Poster.class){
                if (instance == null)
                    instance = new Poster();
            }
        return instance;
    }
    private Poster(){

        super(Looper.getMainLooper());
    }
}
