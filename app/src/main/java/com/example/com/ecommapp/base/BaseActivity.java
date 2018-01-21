package com.example.com.ecommapp.base;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.com.ecommapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by rhm on 2017/10/31.
 * 为我们所有的Activity提供公共的行为和方法
 */

public abstract class BaseActivity extends AppCompatActivity {
    private Unbinder unbinder;
    public final static int WRITE_READ_EXTERNAL_CODE =1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getActivityResId());
        unbinder = ButterKnife.bind(this);
        onViewCreated(savedInstanceState);
    }

    protected abstract int getActivityResId();

    protected abstract void onViewCreated(Bundle savedInstanceState);

    protected void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    /**
     * 寻找Fragment
     *
     * @param tag
     * @param <F>
     * @return
     */
    public final <F extends Fragment> F findFragment(@NonNull String tag) {
        FragmentManager fm = getSupportFragmentManager();
        return (F) fm.findFragmentByTag(tag);
    }

    /**
     * 添加Fragment
     */
    public final <F extends Fragment> void addFragment(@IdRes int containerViewId, F fragment, @Nullable String tag) {
        if (fragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.add(containerViewId, fragment, tag);
            ft.commit();
            fm.executePendingTransactions();//立刻执行
        }
    }

    public final <F extends Fragment> void removeFragmentsIfExits(F... fragments) {
        if (fragments != null && fragments.length > 0) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            for (Fragment fragment : fragments) {
                if (fragment != null) {
                    ft.remove(fragment);
                }
            }
        }
    }

    //申请指定的权限
    public void requestPermission(int code, String... permissions) {
        ActivityCompat.requestPermissions(this, permissions, code);
    }

    //判断是否具有指定权限
    public boolean hasPermission(String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case WRITE_READ_EXTERNAL_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    doSDCardPermission();
                }
                break;
        }
    }

    public void doSDCardPermission(){}
}
