package com.example.com.ecommapp;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.com.ecommapp.listener.CommonJsonCallback;
import com.example.com.ecommapp.listener.DisposeDataHandle;
import com.example.com.ecommapp.listener.DisposeDataListener;
import com.example.com.ecommapp.okhttp.CommonOkHttpClient;
import com.example.com.ecommapp.okhttp.request.CommRequest;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("tag", "running");
        CommonOkHttpClient.sendRequest(CommRequest.createGetRequest("http://www.imooc.com",null),new CommonJsonCallback(new DisposeDataHandle(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.d("tag", "success");

            }

            @Override
            public void onFailure(Object responObj) {
                Log.d("tag", "fail");
            }
        })));
    }

}
