package com.example.com.ecommapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.com.ecommapp.listener.CommonJsonCallback;
import com.example.com.ecommapp.listener.DisposeDataHandle;
import com.example.com.ecommapp.listener.DisposeDataListener;
import com.example.com.ecommapp.network.okhttp.CommonOkHttpClient;
import com.example.com.ecommapp.network.okhttp.request.CommRequest;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);
        Log.d("tag", "running");
        CommonOkHttpClient.sendRequest(CommRequest.createGetRequest("http://www.sohu.com", null), new CommonJsonCallback(new DisposeDataHandle(new DisposeDataListener() {
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
