package com.example.com.ecommapp.application;

import android.app.Application;
import android.content.Context;

import com.mob.MobApplication;

/**
 * Created by rhm on 2017/10/31.
 */

public class EcomAplication extends MobApplication {
    private  static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getInstance(){
        return context;
    }
}
