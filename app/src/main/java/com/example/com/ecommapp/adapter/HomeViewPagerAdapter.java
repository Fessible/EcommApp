package com.example.com.ecommapp.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.com.ecommapp.R;
import com.example.com.support.imageloader.ImageLoaderManager;

import java.util.List;

/**
 * Created by rhm on 2017/12/7.
 */

public class HomeViewPagerAdapter extends PagerAdapter {
    List<String> list;
    Context context;
    ImageLoaderManager imageLoaderManager;

    HomeViewPagerAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
        imageLoaderManager = ImageLoaderManager.getInstance(context);
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        int curPosition = position % list.size();
        View view = LayoutInflater.from(context).inflate(R.layout.item_adapter_view_pager, null);
        ImageView imageView = view.findViewById(R.id.img);
        imageLoaderManager.displayImage(imageView, list.get(curPosition));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
