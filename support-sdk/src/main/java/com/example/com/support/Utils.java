package com.example.com.support;

import android.content.Context;

/**
 * Created by rhm on 2017/12/5.
 */

public class Utils {
    public static int px2dip(Context context, float pxValue) {
        try {
            /**获得屏幕分辨率**/
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (pxValue / scale + 0.5f);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (int) pxValue;
    }

    public static int dip2px(Context context, float dpValue) {
        try {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (int) dpValue;
    }
}
