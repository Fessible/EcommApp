package com.example.com.support.video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

import java.util.Formatter;
import java.util.Locale;

/**
 * 工具类
 * Created by rhm on 2018/1/15.
 */

public class NiceUtil {

    /**
     * 获取Activity
     *
     * @param context 上下文
     * @return
     */
    public static Activity scanForActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            return scanForActivity(((ContextWrapper) context).getBaseContext());//递归寻找子类的是否为Activity
        }
        return null;
    }

    /**
     * 得到AppCompactActivity
     *
     * @param context 上下文
     * @return
     */
    public static AppCompatActivity getAppCompatActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof AppCompatActivity) {
            return (AppCompatActivity) context;
        } else if (context instanceof ContextThemeWrapper) {
            return getAppCompatActivity(((ContextThemeWrapper) context).getBaseContext());
        }
        return null;
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }


    /**
     * 获取屏幕的高度
     *
     * @param context 上下文
     * @return
     */
    public static int getScreenHeigh(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * 将dp转化为px
     *
     * @param context 上下文
     * @param dpVal   dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());
    }

    /**
     * 将毫秒数格式转化为 "##:##"的时间
     *
     * @param milliseconds 毫秒数
     * @return
     */
    public static String formatTime(long milliseconds) {
        if (milliseconds <= 0 || milliseconds >= 24 * 60 * 60 * 1000) {
            return "00:00";
        }
        long tolalSeconds = milliseconds / 1000;
        long hours = tolalSeconds / 3600;//时
        long minutes = (tolalSeconds / 60) % 60;//分
        long seconds = tolalSeconds % 60;//秒
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder, Locale.getDefault());
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 保存播放位置，以便下一次播放时继续播放
     *
     * @param context  上下文
     * @param url      视频连接url
     * @param position 播放位置
     */
    public static void savedPlayPosition(Context context, String url, long position) {
        context.getSharedPreferences("NICE_VIDEO_PLAYER", Context.MODE_PRIVATE).edit().putLong(url, position).apply();
    }

    public static long getSavedPlayPosition(Context context, String url) {
        return
                context.getSharedPreferences("NICE_VIDEO_PLAYER", Context.MODE_PRIVATE).getLong(url
                        , 0);

    }

    @SuppressLint("RestrictedApi")
    public static void showActionBar(Context context) {
        android.support.v7.app.ActionBar actionBar = getAppCompatActivity(context).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setShowHideAnimationEnabled(false);
            actionBar.show();
        }
        //清除全屏
        scanForActivity(context)
                .getWindow()
                .clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @SuppressLint("RestrictedApi")
    public static void hideActionBar(Context context) {
        android.support.v7.app.ActionBar actionBar = getAppCompatActivity(context).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setShowHideAnimationEnabled(false);
            actionBar.hide();
        }

        scanForActivity(context)
                .getWindow()
                .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }


}
