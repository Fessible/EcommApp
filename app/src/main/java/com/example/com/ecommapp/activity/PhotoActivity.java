package com.example.com.ecommapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.adapter.PhotoViewAdapter;
import com.example.com.ecommapp.base.BaseActivity;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

import butterknife.BindView;

/**
 * Created by rhm on 2017/12/19.
 */

public class PhotoActivity extends BaseActivity {

    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private PhotoViewAdapter viewAdapter;
    private List<String> photoList;
    public final static String PHOTO_VIEW = "photo_view";

    @Override
    protected int getActivityResId() {
        return R.layout.activity_photoview_layout;
    }

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        Intent intent = getIntent();
        photoList = intent.getStringArrayListExtra(PHOTO_VIEW);
        viewAdapter = new PhotoViewAdapter(this, photoList);
        viewPager.setAdapter(viewAdapter);
        //点击图片退出
        viewAdapter.setPhotoTapListener(new PhotoViewAdapter.PhotoViewClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }
}
