package com.example.com.support.okhttp;

import com.example.com.support.okhttp.listener.CommonJsonCallback;
import com.example.com.support.okhttp.listener.DisposableHandler;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by rhm on 2017/12/5.
 */

public class CommOkhttpClient {
    private final static int TIME_OUT = 30;
    private OkHttpClient mOkhttpClient;
    private static CommOkhttpClient mInstance;


    private CommOkhttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        //允许重定向
        builder.followRedirects(true);
        mOkhttpClient = builder.build();
    }

    public static CommOkhttpClient getInstance() {
        if (mInstance == null) {
            synchronized (CommOkhttpClient.class) {
                if (mInstance == null) {
                    mInstance = new CommOkhttpClient();
                }
            }
        }
        return mInstance;
    }

    public void sendRequest(Request request, DisposableHandler handler) {
        Call call = mOkhttpClient.newCall(request);
        call.enqueue(new CommonJsonCallback(handler));
    }
}
