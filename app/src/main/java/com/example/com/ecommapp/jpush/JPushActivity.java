package com.example.com.ecommapp.jpush;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseActivity;

import butterknife.BindView;

/**
 * 显示推送的消息界面
 * Created by rhm on 2018/1/20.
 */

public class JPushActivity extends BaseActivity {
    public final static String PUSH_MESSAGE = "pushMessage";

    @BindView(R.id.message_type_view)
    TextView mTypeView;

    @BindView(R.id.message_type_value_view)
    TextView mTypeValueView;

    @BindView(R.id.message_content_view)
    TextView mContentView;

    @BindView(R.id.message_content_value_view)
    TextView mContentValueView;

    private PushMessage mPushMessage;

    @Override
    protected int getActivityResId() {
        return R.layout.activity_jupsh_layout;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        //初始化推送过来的数据
        Intent intent = getIntent();
        mPushMessage = (PushMessage) intent.getSerializableExtra(PUSH_MESSAGE);

        mTypeValueView.setText(mPushMessage.messageType);
        mContentValueView.setText(mPushMessage.messageContent);
        if (!TextUtils.isEmpty(mPushMessage.messageUrl)) {
            //跳转到web页面
            gotoWebView();
        }
    }

    private void gotoWebView() {
    }
}
