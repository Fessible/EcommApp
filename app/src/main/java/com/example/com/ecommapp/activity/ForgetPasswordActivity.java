package com.example.com.ecommapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseActivity;
import com.example.com.ecommapp.module.BaseModel;
import com.example.com.ecommapp.network.http.HttpRequest;
import com.example.com.support.okhttp.listener.DisposeListener;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.com.ecommapp.network.http.HttpConstants.SMSCODE;

/**
 * Created by rhm on 2018/1/4.
 */

public class ForgetPasswordActivity extends BaseActivity {
    public static final String TAG_FORGET_PASSWORD_ACTIVITY = "FORGET_PASSWORD_ACTIVITY";
    private static final int PHONE_LENGTH = 11;
    private static final int CODE_LENGTH = 4;
    public static final String KEY_PHONE = "phone";
    public static final String KEY_PASSWORD = "password";

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

    //验证码涉及多线程,
    private final AtomicBoolean resetCodeIdle = new AtomicBoolean(true);
    private CountDownTimer countDownTimer;

    @Override
    protected int getActivityResId() {
        return R.layout.activity_forget_password_layout;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        toolbar.setNavigationOnClickListener(navigationClickListener);
        textInputPhone.addTextChangedListener(phoneWatcher);
        textInputCode.addTextChangedListener(codeChangeWatcher);
        textInputNewPassword.addTextChangedListener(newPasswordWatcher);
        textInputConfirmNewPassword.addTextChangedListener(confirmPasswordWatcher);
        textInputConfirmNewPassword.setOnEditorActionListener(editorActionListener);//添加按键监听
    }

    //发送验证码
    @OnClick(R.id.reset_code)
    public void resetCode() {
        //发送请求，验证电话号码是否正确
        HttpRequest.recodeRequest(textInputPhone.getText().toString(), new DisposeListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BaseModel baseModel = (BaseModel) responseObj;
                showToast(baseModel.emsg);
                if (textInputCode != null) {
                    textInputCode.requestFocus();
                }
                countDownTimer = new CountDownTimer(60 * 1000, 1 * 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (resetCode != null) {
                            resetCode.setEnabled(false);
                            resetCode.setText(getString(R.string.count_down_timer_reset_code_format, millisUntilFinished / 1000));
                        }
                    }

                    @Override
                    public void onFinish() {
                        resetCodeIdle.set(true);
                        updateResetCodeStatus();
                    }
                };
                countDownTimer.start();
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg);
                resetCodeIdle.set(true);
                updateResetCodeStatus();
            }
        });
    }

    @OnClick(R.id.confirm)
    public void confirm() {
        HttpRequest.passwordRequest(textInputPhone.getText().toString(), textInputCode.getText().toString(), textInputNewPassword.getText().toString(), textInputConfirmNewPassword.getText().toString(), new DisposeListener() {
            @Override
            public void onSuccess(Object responseObj) {
                BaseModel baseModel = (BaseModel) responseObj;
                showToast(baseModel.emsg);
                Intent data = new Intent();
                data.putExtra(KEY_PHONE,textInputPhone.getText().toString());
                data.putExtra(KEY_PASSWORD, textInputNewPassword.getText().toString());
                setResult(RESULT_OK,data);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                showToast(msg);
            }
        });

    }

    private void updateResetCodeStatus() {
        if (textInputPhone != null && resetCode != null) {
            resetCode.setEnabled(resetCodeIdle.get()
                    && !TextUtils.isEmpty(textInputPhone.getText())
                    && textInputPhone.getText().length() == PHONE_LENGTH); // 在号码框长度为11时可发送验证码
        }
        resetCode.setText(R.string.reset_code);
    }

    private void updateConfirmStatus() {
        if (textInputPhone != null
                && textInputCode != null
                && textInputNewPassword != null
                && textInputConfirmNewPassword != null
                && confirm != null
                ) {
            confirm.setEnabled(!TextUtils.isEmpty(textInputPhone.getText())
                    && textInputPhone.getText().length() == PHONE_LENGTH
                    && !TextUtils.isEmpty(textInputCode.getText())
                    && textInputCode.getText().length() == CODE_LENGTH
                    && !TextUtils.isEmpty(textInputNewPassword.getText())
                    && !TextUtils.isEmpty(textInputConfirmNewPassword.getText())
            );
        }
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.text_input_confirm_new_password:
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (confirm != null && confirm.isEnabled()) {
                            confirm();
                        }
                        return true;
                    }
                    break;
            }
            return false;
        }
    };

    private TextWatcher phoneWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            resetCodeIdle.set(true);
            //暂停倒计时
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
            updateResetCodeStatus();
            updateConfirmStatus();
        }
    };

    private TextWatcher codeChangeWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateConfirmStatus();
        }
    };

    private TextWatcher newPasswordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateConfirmStatus();
        }
    };

    private TextWatcher confirmPasswordWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateConfirmStatus();
        }
    };

    private View.OnClickListener navigationClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
