package com.example.com.ecommapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.com.ecommapp.R;
import com.example.com.support.imageloader.ImageLoaderManager;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

/**
 * Created by rhm on 2017/12/19.
 */

public class PhotoViewAdapter extends PagerAdapter {
    private Context mContext;
    private List<String> photoList;
    private ImageLoaderManager imageLoaderManager;
    private PhotoViewClickListener listener;

    public PhotoViewAdapter(Context context, List<String> photoList) {
        this.mContext = context;
        this.photoList = photoList;
        imageLoaderManager = ImageLoaderManager.getInstance(context);
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mContext);
        photoView.setOnPhotoTapListener(photoTapListener);
        imageLoaderManager.displayImage(photoView, photoList.get(position));
        container.addView(photoView);
        return photoView;
    }

    public void setPhotoTapListener(PhotoViewClickListener listener) {
        this.listener = listener;
    }

    private OnPhotoTapListener photoTapListener = new OnPhotoTapListener() {
        @Override
        public void onPhotoTap(ImageView view, float x, float y) {
            if (listener != null) {
                listener.onClick();
            }
        }
    };

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    //单击图片退出
    public interface PhotoViewClickListener {
        void onClick();
    }
}
