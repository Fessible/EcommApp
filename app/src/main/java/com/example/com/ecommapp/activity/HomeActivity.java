package com.example.com.ecommapp.activity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.activity.base.BaseActivity;
import com.example.com.ecommapp.view.home.fragment.HomeFragment;

/**
 * Created by rhm on 2017/10/31.
 */

public class HomeActivity extends BaseActivity {
    private FragmentManager fm;
    private HomeFragment mHomeFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_layout);
        mHomeFragment = new HomeFragment();
        fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.replace(R.id.content_layout, mHomeFragment);
        fragmentTransaction.commit();
    }
}
