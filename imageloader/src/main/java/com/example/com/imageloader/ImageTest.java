package com.example.com.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by rhm on 2017/11/29.
 */

public class ImageTest {
    public void testApi(Context context, ImageView imageView){
        /**
         * 为ImageLoader配置参数
         */
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(context).build();
        /**
         * 我们先来获取ImageLoader的一个实例
         */
        ImageLoader imageLoader = ImageLoader.getInstance();
        /**
         * 为我们的显示图片配置
         */
        DisplayImageOptions options = new DisplayImageOptions.Builder().build();
        imageLoader.displayImage("url",imageView,options,new SimpleImageLoadingListener(){
            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                super.onLoadingCancelled(imageUri, view);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
            }

            @Override
            public void onLoadingStarted(String imageUri, View view) {
                super.onLoadingStarted(imageUri, view);
            }
        });
    }



}
