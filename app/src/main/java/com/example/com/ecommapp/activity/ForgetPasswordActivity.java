package com.example.com.ecommapp.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseActivity;

import butterknife.BindView;

/**
 * Created by rhm on 2018/1/4.
 */

public class ForgetPasswordActivity extends BaseActivity {
    public static final String TAG_FORGET_PASSWORD_ACTIVITY = "FORGET_PASSWORD_ACTIVITY";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.text_input_phone)
    EditText textInputPhone;

    @BindView(R.id.text_input_code)
    EditText textInputCode;

    @BindView(R.id.reset_code)
    TextView resetCode;

    @BindView(R.id.text_input_new_password)
    EditText textInputNewPassword;

    @BindView(R.id.text_input_confirm_new_password)
    EditText textInputConfirmNewPassword;

    @BindView(R.id.confirm)
    TextView confirm;


    @Override
    protected int getActivityResId() {
        return R.layout.activity_forget_password_layout;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {

    }
}
