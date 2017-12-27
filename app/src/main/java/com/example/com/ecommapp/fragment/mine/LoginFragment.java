package com.example.com.ecommapp.fragment.mine;

import android.os.Bundle;
import android.view.View;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseFragment;

/**
 * Created by rhm on 2017/12/26.
 */

public class LoginFragment extends BaseFragment {
    public static final String TAG_Login_FRAGMENT = "LOGIN_FRAGMENT";
    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_login_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {

    }
}
