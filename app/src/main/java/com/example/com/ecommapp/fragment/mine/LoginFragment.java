package com.example.com.ecommapp.fragment.mine;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CheckableImageButton;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.activity.ForgetPasswordActivity;
import com.example.com.ecommapp.base.BaseFragment;
import com.example.com.ecommapp.util.IntentUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by rhm on 2017/12/26.
 */

public class LoginFragment extends BaseFragment {
    public static final String TAG_Login_FRAGMENT = "LOGIN_FRAGMENT";
    @BindView(R.id.logo)
    ImageView logo;

    @BindView(R.id.text_input_phone)
    EditText editPhone;

    @BindView(R.id.text_clear)
    ImageButton textClear;

    @BindView(R.id.text_input_password)
    EditText editPassword;

    @BindView(R.id.password_toggle)
    CheckableImageButton passwordToggle;

    @BindView(R.id.login)
    TextView login;

    @BindView(R.id.forgot_password)
    TextView forgotPassword;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_login_layout;
    }

    public static final int PHONE_NUMBER_LENGHT = 11;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        textClear.setVisibility(View.INVISIBLE);
        editPhone.addTextChangedListener(phoneWatcher);
        editPassword.addTextChangedListener(passWatcher);
        //添加软件盘监听,输入完成后点击键盘完成
        editPassword.setOnEditorActionListener(editorActionListener);
        setImmersiveStatusBar(true, Color.WHITE);
    }


    @OnClick(R.id.text_clear)
    public void onClear() {
        if (editPhone != null) {
            editPhone.setText("");
            editPhone.requestFocus();
        }
    }

    @OnClick(R.id.login)
    public void login() {
        if (editPhone != null && editPassword != null) {
            CharSequence phone = editPhone.getText();
            CharSequence password = editPassword.getText();
        }

    }

    @OnClick(R.id.password_toggle)
    public void passwordToggle() {
        if (editPassword != null && passwordToggle != null) {
            int selection = editPassword.getSelectionEnd();
            //当前状态为密码类型，转换为无类型
            if (editPassword.getTransformationMethod() instanceof PasswordTransformationMethod) {
                editPassword.setTransformationMethod(null);
                passwordToggle.setChecked(true);
            } else {
                editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());//设置为密码类型
                passwordToggle.setChecked(false);
            }
            editPassword.setSelection(selection);
        }

    }

    @OnClick(R.id.forgot_password)
    public void forgetPassword() {
        IntentUtil.startActivity(LoginFragment.this, ForgetPasswordActivity.class,ForgetPasswordActivity.TAG_FORGET_PASSWORD_ACTIVITY);
    }

    private TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            switch (v.getId()) {
                case R.id.text_input_password:
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        if (login != null && login.isEnabled()) {
                            login();
                        }
                    }
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
            if (textClear != null) {
                textClear.setVisibility(!TextUtils.isEmpty(s) ? View.VISIBLE : View.INVISIBLE);
            }
            updateLoginStatus();//当密码已输入，登录按钮可点击
        }
    };

    private void updateLoginStatus() {
        if (editPhone != null && editPassword != null && login != null) {
            login.setEnabled(!TextUtils.isEmpty(editPassword.getText())
                    && editPhone.getText().length() == PHONE_NUMBER_LENGHT
                    && !TextUtils.isEmpty(editPhone.getText()));
        }
    }

    private TextWatcher passWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            updateLoginStatus();
        }
    };
}
