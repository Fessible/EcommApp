package com.example.com.ecommapp.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.activity.base.BaseActivity;

/**
 * Created by rhm on 2017/10/31.
 */

public class HomeActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
