package com.example.com.ecommapp.fragment.mine;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseFragment;
import com.example.com.ecommapp.util.SharedPreferenceManager;
import com.example.com.support.video.VideoManagerParameters;
import com.example.com.support.util.SDKConstant;

import butterknife.BindView;

/**
 * Created by rhm on 2017/12/23.
 */

public class SettingFragment extends BaseFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    public static final String TAG_SETTING_FRAGMENT = "SETTING_FRAGMENT";
    public static final int DEFAULT_ALWAY_STATE = 0;
    public static final int DEFAULT_WIFI_STATE = 1;
    public static final int DEFAULT_CLOSE_STATE = 2;

    @BindView(R.id.alway_layout)
    RelativeLayout alwayLayout;

    @BindView(R.id.alway_check_box)
    CheckBox checkAlway;

    @BindView(R.id.wifi_check_box)
    CheckBox checkWifi;

    @BindView(R.id.close_check_box)
    CheckBox checkClose;

    @BindView(R.id.wifi_layout)
    RelativeLayout wifiLayout;

    @BindView(R.id.close_layout)
    RelativeLayout closeLayout;

    @BindView(R.id.back_view)
    ImageView backView;

    @Override
    protected int getFragmentLayout() {

        return R.layout.fragment_setting_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        backView.setOnClickListener(this);
        wifiLayout.setOnClickListener(this);
        alwayLayout.setOnClickListener(this);
        closeLayout.setOnClickListener(this);
        checkAlway.setOnCheckedChangeListener(this);
        checkWifi.setOnCheckedChangeListener(this);
        checkClose.setOnCheckedChangeListener(this);
        backView.setOnClickListener(this);

        int currentSetting = SharedPreferenceManager.getInstance().getInt(SharedPreferenceManager.VIDEO_SETTING, DEFAULT_ALWAY_STATE);
        switch (currentSetting) {
            case DEFAULT_ALWAY_STATE:
                checkAlway.setChecked(true);
                break;
            case DEFAULT_WIFI_STATE:
                checkWifi.setChecked(true);
                break;
            case DEFAULT_CLOSE_STATE:
                checkClose.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alway_layout:
                checkAlway.setChecked(checkAlway.isChecked() ? false : true);
                break;
            case R.id.wifi_layout:
                checkWifi.setChecked(checkWifi.isChecked() ? false : true);
                break;
            case R.id.close_layout:
                checkClose.setChecked(checkClose.isChecked() ? false : true);
                break;
            case R.id.back_view:
                getActivity().finish();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.alway_check_box:
                if (isChecked) {
                    checkWifi.setChecked(false);
                    checkClose.setChecked(false);
                    SharedPreferenceManager.getInstance().putInt(SharedPreferenceManager.VIDEO_SETTING, DEFAULT_ALWAY_STATE);
                    VideoManagerParameters.setPlaySetting(SDKConstant.AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI);
                }
                break;
            case R.id.wifi_check_box:
                if (isChecked) {
                    checkAlway.setChecked(false);
                    checkClose.setChecked(false);
                    SharedPreferenceManager.getInstance().putInt(SharedPreferenceManager.VIDEO_SETTING, DEFAULT_WIFI_STATE);
                    VideoManagerParameters.setPlaySetting(SDKConstant.AutoPlaySetting.AUTO_PLAY_ONLY_WIFI);

                }
                break;
            case R.id.close_check_box:
                if (isChecked) {
                    SharedPreferenceManager.getInstance().putInt(SharedPreferenceManager.VIDEO_SETTING, DEFAULT_CLOSE_STATE);
                    checkAlway.setChecked(false);
                    checkWifi.setChecked(false);
                    VideoManagerParameters.setPlaySetting(SDKConstant.AutoPlaySetting.AUTO_PLAY_NEVER);
                }
                break;
        }

    }
}
