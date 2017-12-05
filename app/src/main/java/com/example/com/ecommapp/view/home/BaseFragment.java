package com.example.com.ecommapp.view.home;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

/**
 * Created by rhm on 2017/10/31.
 * 提供公共的行为和事件
 */

public abstract class BaseFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(getFragmentLayout(),container,false);
    }

    //获取布局
    protected abstract int getFragmentLayout();


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view,savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
    }
    //初始化View
    protected abstract void initView(View view, Bundle savedInstanceState);

    protected void showShortToast(String msg) {
        Toast.makeText(getActivity(),msg,Toast.LENGTH_SHORT).show();
    }
}
