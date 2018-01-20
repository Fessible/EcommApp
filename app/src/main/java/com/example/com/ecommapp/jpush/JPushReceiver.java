package com.example.com.ecommapp.jpush;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.com.ecommapp.fragment.home.HomeFragment;
import com.example.com.ecommapp.fragment.mine.LoginFragment;
import com.example.com.ecommapp.manager.UserManager;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.ui.PushActivity;

import static com.example.com.ecommapp.jpush.JPushActivity.PUSH_MESSAGE;

/**
 * 自定义的接受极光推送的广播服务
 * Created by rhm on 2018/1/20.
 */

public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = JPushReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[JPushReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
        //接受到消息
        if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            int notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[JPushReceiver] 接收到推送下来的通知的ID" + notificationId);

            //收到消息后打开界面
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            /**
             * 1.应用是否已经启动
             * 2. 是否需要登录
             * 3.是否已经登录
             * 4.消息显示页面
             */

            //将收到的数据转换为pushMessage
            String message = bundle.getString(JPushInterface.EXTRA_EXTRA);
            PushMessage pushMessage = new Gson().fromJson(message, PushMessage.class);
            if (getCurrentTask(context)) {
                //应用已经启动
                Intent pushIntent = new Intent();
                pushIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                pushIntent.putExtra(PUSH_MESSAGE, pushMessage);
                //需要登录，且未登录
                if (pushMessage.messageType != null && pushMessage.messageType.equals("2") && !UserManager.getInstance().hasLogined()) {
                    pushIntent.setClass(context, LoginFragment.class);
                    pushIntent.putExtra("fromPush", true);

                } else {
                    //直接跳转到显示界面
                    pushIntent.setClass(context, PushActivity.class);
                }
                context.startActivity(pushIntent);

            } else {
                //应用未启动
                //启动应用
                Intent mainIntent = new Intent(context, HomeFragment.class);
                mainIntent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                if (pushMessage != null && pushMessage.messageType.equals("2")) {
                    Intent loginIntent = new Intent(context, LoginFragment.class);
                    loginIntent.putExtra("fromPush", true);
                    loginIntent.putExtra(PUSH_MESSAGE, pushMessage);

                    context.startActivities(new Intent[]{mainIntent, loginIntent});
                } else {
                    Intent pushIntent = new Intent(context, PushActivity.class);
                    pushIntent.putExtra(PUSH_MESSAGE, pushIntent);
                    context.startActivities(new Intent[]{mainIntent, pushIntent});
                }
            }
        }
    }

    //判断指定应用是否已经启动
    private boolean getCurrentTask(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取当前系统中所有的运行程序
        List<ActivityManager.RunningTaskInfo> appProcessInfos = activityManager.getRunningTasks(Integer.MAX_VALUE);
        for (ActivityManager.RunningTaskInfo process : appProcessInfos) {
            //top 或者 base中有当前的包名，说明正在运行中
            if (process.baseActivity.getPackageName().equals(context.getPackageName()) || process.topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    //打印所有的intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + ", value: " + bundle.get(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + ", value: " + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.d(TAG, "This message has no Extra data");
                    continue;
                }
                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();
                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey: " + key + ", value: [" + json.optString(myKey) + " ]");
                    }

                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }
            } else {
                sb.append("\nkey:" + key + ", value: " + bundle.getString(key));
            }
        }
        return sb.toString();
    }
}
