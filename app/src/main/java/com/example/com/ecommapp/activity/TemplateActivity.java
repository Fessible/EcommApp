package com.example.com.ecommapp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseActivity;
import com.example.com.ecommapp.base.BaseFragment;

import org.w3c.dom.Text;

/**
 * Created by rhm on 2017/12/23.
 */

public class TemplateActivity extends BaseActivity {
    public static final String KEY_FRAGMENT_TAG = "fragment_tag";
    public static final String KEY_FRAGMENT_CLAZZ = "fragment_clazz";
    public static final String KEY_FRAGMENT_ARGS = "fragment_args";

    private String fragmentTag;
    private String fragmentClazz;
    private Fragment fragment;

    @Override
    protected int getActivityResId() {
        return R.layout.activity_template_layout;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            fragmentClazz = extras.getString(KEY_FRAGMENT_CLAZZ);
            fragmentTag = extras.getString(KEY_FRAGMENT_TAG);
            if (!TextUtils.isEmpty(fragmentClazz) && !TextUtils.isEmpty(fragmentTag)) {
                try {
                    //通过反射找到Fragment
                    fragment = (Fragment) Class.forName(fragmentClazz).newInstance();
                    fragment.setArguments(extras.getBundle(KEY_FRAGMENT_ARGS));
                    addFragment(R.id.container, fragment, fragmentTag);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment != null && fragment instanceof BaseFragment) {
            if (((BaseFragment) fragment).onKeyDown(keyCode, event)) {
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_FRAGMENT_CLAZZ, fragmentClazz);
        outState.putString(KEY_FRAGMENT_TAG, fragmentTag);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fragmentClazz = savedInstanceState.getString(KEY_FRAGMENT_CLAZZ);
        fragmentTag = savedInstanceState.getString(KEY_FRAGMENT_TAG);
        if (!TextUtils.isEmpty(fragmentClazz) && !TextUtils.isEmpty(fragmentTag)) {
            fragment = findFragment(fragmentTag);
        }
    }
}
