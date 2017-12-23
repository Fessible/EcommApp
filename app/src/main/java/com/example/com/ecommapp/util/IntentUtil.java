package com.example.com.ecommapp.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.example.com.ecommapp.activity.TemplateActivity;

/**
 * Created by rhm on 2017/12/23.
 */

public class IntentUtil {

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
