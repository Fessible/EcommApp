package com.example.com.ecommapp.util;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;

import com.example.com.ecommapp.activity.TemplateActivity;

/**
 * Created by rhm on 2017/12/23.
 */

public class IntentUtil {
    public static final String KEY_ARGS = "args";

    public static void startActivity(Fragment fragment, Class<? extends Activity> activityClazz, String tag) {
        startActivity(fragment,activityClazz,null,tag);
    }

    public static void startActivity(Fragment fragment, Class<? extends Activity> activityClazz, Bundle args, String tag) {
        Intent intent = new Intent(fragment.getContext(), activityClazz);
        if (args != null) {
            intent.putExtra(KEY_ARGS, args);
        }
        fragment.startActivity(intent);

    }

    public static void startActivityForResult(Fragment fragment, Class<? extends Activity> activityClazz, String tag, int requestCode) {
        startActivityForResult(fragment,activityClazz,null,tag,requestCode);
    }

    public static void startActivityForResult(Fragment fragment, Class<? extends Activity> activityClazz, Bundle args, String tag, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), activityClazz);
        if (args != null) {
            intent.putExtra(KEY_ARGS, args);
        }
        fragment.startActivityForResult(intent,requestCode);
    }


    public static void startTemplateActivity(Fragment fragment, Class<? extends Fragment> fragmentClazz, String tag) {
        IntentUtil.startTemplateActivity(fragment, fragmentClazz, null, tag);
    }

    public static void startTemplateActivity(Fragment fragment, Class<? extends Fragment> fragmentClazz, Bundle args, String tag) {
        Intent intent = new Intent(fragment.getContext(), TemplateActivity.class);
        intent.putExtra(TemplateActivity.KEY_FRAGMENT_CLAZZ, fragmentClazz.getName());
        if (args != null) {
            intent.putExtra(TemplateActivity.KEY_FRAGMENT_ARGS, args);
        }
        intent.putExtra(TemplateActivity.KEY_FRAGMENT_TAG, tag);
        fragment.startActivity(intent);
    }

    public static void startTemplateActivityForResult(Fragment fragment, Class<? extends Fragment> fragmentClazz, String tag, int requstCode) {
        IntentUtil.startTemplateActivityForResult(fragment, fragmentClazz, null, tag, requstCode);
    }

    public static void startTemplateActivityForResult(Fragment fragment, Class<? extends Fragment> fragmentClazz, Bundle args, String tag, int requstCode) {
        Intent intent = new Intent(fragment.getContext(), TemplateActivity.class);
        intent.putExtra(TemplateActivity.KEY_FRAGMENT_CLAZZ, fragmentClazz.getName());
        if (args != null) {
            intent.putExtra(TemplateActivity.KEY_FRAGMENT_ARGS, args);
        }
        intent.putExtra(TemplateActivity.KEY_FRAGMENT_TAG, tag);
        fragment.startActivityForResult(intent, requstCode);
    }
}
