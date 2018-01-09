package com.example.com.support.video;

import android.content.Context;
import android.view.ViewGroup;

/**
 * Created by rhm on 2017/12/23.
 */

public class VideoManager {
    private ViewGroup parentView;
    private VideoSlot videoSlot;
    private String path;
    private Context context;


    public VideoManager(Context context,ViewGroup viewGroup, String path) {
        this.parentView = viewGroup;
        this.path = path;
        this.context = context;
        load();
    }

    private void load() {
        videoSlot = new VideoSlot(context,parentView,path);
    }

    public void destroy() {
        videoSlot.destroy();
    }
}
