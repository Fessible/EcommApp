package com.example.com.ecommapp.view.home.fragment;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.view.home.BaseFragment;

/**
 * Created by rhm on 2017/10/31.
 */

public class HomeFragment extends BaseFragment {
    private Context mContext;
    private View mContentView;
    private ImageView animTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mContentView = inflater.inflate(R.layout.fragment_home_layout, container, false);
        animTv = mContentView.findViewById(R.id.loading_view);
        AnimationDrawable animationDrawable = (AnimationDrawable) animTv.getDrawable();
        animationDrawable.start();

        return mContentView;
    }
}
