package com.example.com.ecommapp.application;

import android.content.Context;

import com.example.com.ecommapp.util.Logger;
import com.mob.MobApplication;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by rhm on 2017/10/31.
 */

public class EcomAplication extends MobApplication {
    private  static Context context;
    private static final String TAG = "EcomAplictaion";

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.d(TAG, "[ExampleApplication] onCreate");
        context = getApplicationContext();
        JPushInterface.setDebugMode(true);//设置开启日志
        JPushInterface.init(this);//初始化JPush
    }

    public static Context getInstance(){
        return context;
    }
}
