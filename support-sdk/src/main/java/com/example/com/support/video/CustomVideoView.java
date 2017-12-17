package com.example.com.support.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.com.glide.R;
import com.example.com.support.util.SDKConstant;

/**
 * Created by rhm on 2017/12/17.
 */

public class CustomVideoView extends RelativeLayout implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener, TextureView.SurfaceTextureListener {
    private static final String TAG = "CustomVideoView";
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 1000;
    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;//空闲状态
    private static final int STATE_PLAYING = 1;
    private static final int STATE_PAUSING = 2;
    private static final int LOAD_TOTAL_COUNT = 3;//三个线程加载视频

    private ViewGroup mParentContainer;
    private RelativeLayout mPlayerView;
    private TextureView mVideoView;
    private ImageView imgLoading;//加载
    private ImageView imgFrame;
    private ImageView imgEnlarge;//扩大
    private Button btnPlay;//播放按钮

    private AudioManager audioManager;
    private Surface videoSurface;
    private MediaPlayer mediaPlayer;//视频播放器
    private ScreenEventReceiver screenEventReceiver;//监听锁屏事件


    //状态
    private int playerState = STATE_IDLE;

    private int screenWidth;
    private int destationHeight;

    //播中检测 用于向服务器返回 当前播放的位置
    private Handler mHanlder = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    public CustomVideoView(Context context, ViewGroup parent) {
        super(context);
        mParentContainer = parent;
        initView();
        initData();
        registerBroadcastReceiver();
    }


    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.xadsdk_video_player, this);
        mVideoView = mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        imgEnlarge = mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        imgFrame = mPlayerView.findViewById(R.id.framing_view);
        imgLoading = mPlayerView.findViewById(R.id.loading_bar);
        btnPlay = mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
    }

    private void initData() {
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        imgEnlarge.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        //初始化 宽度 和高度
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        destationHeight = (int) (screenWidth *
                SDKConstant.VIDEO_HEIGHT_PERCENT); //宽度乘 9/16

    }

    /**
     * 注册广播
     */
    private void registerBroadcastReceiver() {
    }

    /**
     * 设置当前的状态
     *
     * @param state
     */
    private void setCurrentState(int state) {
        playerState = state;
    }

    /**
     * 加载视频url
     */
    public void load() {
        //不处于空闲状态 直接返回
        if (playerState != STATE_IDLE) {
            return;
        }
        //下载过程中显示加载界面
        showLoadingView();
        try {
            setCurrentState(STATE_IDLE);
            checkMediaPlayer();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建唯一一个mediaPlayer
     */
    private synchronized void checkMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = createMediaPlayer();
        }
    }

    /**
     * 创建MediaPlayer
     *
     * @return
     */
    private MediaPlayer createMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        if (videoSurface != null && videoSurface.isValid()) {
            mediaPlayer.setSurface(videoSurface);
        } else {
            stop();
        }
        return mediaPlayer;
    }


    /**
     * 显示加载图
     */
    private void showLoadingView() {
        //隐藏组件
        imgEnlarge.setVisibility(GONE);
        imgFrame.setVisibility(GONE);
        btnPlay.setVisibility(GONE);
        imgLoading.setVisibility(VISIBLE);
        AnimationDrawable animationDrawable = (AnimationDrawable) imgLoading.getDrawable();
        animationDrawable.start();
        loadFrameImg();
    }

    /**
     * 异步加载定帧图
     */
    private void loadFrameImg() {
    }

    /**
     * 暂停视频
     */
    public void pause() {

    }

    /**
     * 恢复视频
     */
    public void resume() {
    }

    public void stop() {
    }


    /**
     * 可见状态变化
     *
     * @param changedView
     * @param visibility
     */
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
    }

    /**
     * 设置为true，默认处理点击事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 播放器处于就绪状态，才能使用start
     *
     * @param mp
     */
    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    /**
     * 播放器产生异常回调
     *
     * @param mp
     * @param what
     * @param extra
     * @return
     */
    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }

    /**
     * 表明我们的TextureView可用
     *
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    /**
     * 监听锁屏事件的广播
     */
    private class ScreenEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

        }
    }
}
