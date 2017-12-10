package com.example.com.ecommapp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.com.ecommapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by rhm on 2017/10/31.
 * 为我们所有的Activity提供公共的行为和方法
 */

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityResId());
        unbinder = ButterKnife.bind(this);
        onViewCreated(savedInstanceState);
    }

    protected abstract int getActivityResId();

    protected abstract void onViewCreated(Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

}
