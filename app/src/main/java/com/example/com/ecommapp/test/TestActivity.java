package com.example.com.ecommapp.test;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import com.example.com.ecommapp.R;
import com.example.com.support.CustomVideoView;

/**
 * Created by rhm on 2017/12/19.
 */

public class TestActivity extends AppCompatActivity {
    private RelativeLayout contentLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_layout);
        contentLayout = findViewById(R.id.relative_layout);

        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.baishi);
        CustomVideoView customVideoView = new CustomVideoView(this, contentLayout);
        customVideoView.setDataSource(this,uri);
        contentLayout.addView(customVideoView);
    }
}
