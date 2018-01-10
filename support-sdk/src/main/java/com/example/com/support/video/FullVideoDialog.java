package com.example.com.support.video;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.com.glide.R;

/**
 * 全屏显示视频
 * Created by rhm on 2017/12/22.
 */

public class FullVideoDialog extends Dialog implements CustomVideoView.VideoPlayerListener {

    private RelativeLayout rootView;
    private RelativeLayout parentView;
    private ImageView imgLoad;
    private Button btnPlay;
    private ImageView imgBack;

    private FullToSmallListener listener;
    private int position;
    private boolean isFirst = true;
    private CustomVideoView videoView;
    private boolean isFocus;

    public FullVideoDialog(@NonNull Context context, CustomVideoView videoView) {
        super(context, R.style.dialog_full_screen);//保证dialog全屏
        this.videoView = videoView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.xadsdk_dialog_video_layout);
        initVideoView();
    }

    private void initVideoView() {
        rootView = findViewById(R.id.root_view);
        parentView = findViewById(R.id.content_layout);
        btnPlay = findViewById(R.id.xadsdk_big_play_btn);
        imgLoad = findViewById(R.id.loading_bar);
        imgBack = findViewById(R.id.xadsdk_player_close_btn);

        videoView.setVideoViewListener(this);//设置监听为当前对象
        parentView.addView(videoView);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBackBtn();
            }
        });
    }

    private void clickBackBtn() {
        dismiss();
        if (listener != null) {
            listener.getCurrentPosition(videoView.getCurrentPosition());
        }
    }

    /**
     * back键监听
     */
    @Override
    public void onBackPressed() {
        clickBackBtn();
        super.onBackPressed();
    }

    /**
     * 焦点状态改变时回调
     *
     * @param hasFocus
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isFocus) {//未获得焦点
            position = videoView.getCurrentPosition();
            videoView.pause();
        } else {
            if (isFirst) {//首次创建
                videoView.seekAndResume(position);
            } else {
                videoView.resume();
            }
            isFirst = false;
        }
    }

    /**
     * 窗口消失时调用
     */
    @Override
    public void dismiss() {
        parentView.removeView(videoView);
        super.dismiss();
    }

    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickVideoView() {

    }

    @Override
    public void onClickFullButton() {

    }

    @Override
    public void onClickPlay() {

    }

    @Override
    public void loadSuccess() {

    }

    @Override
    public void loadFail() {

    }

    @Override
    public void loadComplete() {
        dismiss();
        if (listener != null) {
            listener.playComplete();
        }
    }

    public void setListener(FullToSmallListener listener) {
        this.listener = listener;
    }

    /**
     * 与业务逻辑层通信
     */
    public interface FullToSmallListener {
        public void getCurrentPosition(int position);

        public void playComplete();
    }
}
