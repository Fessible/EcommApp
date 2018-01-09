package com.example.com.support.video;

import com.example.com.support.util.SDKConstant.AutoPlaySetting;

/**
 * Created by rhm on 2017/12/24.
 */

public class VideoManagerParameters {
    private static AutoPlaySetting playSetting = AutoPlaySetting.AUTO_PLAY_3G_4G_WIFI;

    public static AutoPlaySetting getPlaySetting() {
        return playSetting;
    }

    public static void setPlaySetting(AutoPlaySetting autoPlaySetting) {
        playSetting = autoPlaySetting;
    }
}
