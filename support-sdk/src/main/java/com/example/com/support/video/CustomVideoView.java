package com.example.com.support.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.com.glide.R;
import com.example.com.support.util.SDKConstant;
import com.example.com.support.util.Utils;

import java.io.IOException;

/**
 * Created by rhm on 2017/12/17.
 */
//
//public class CustomVideoView extends RelativeLayout implements View.OnClickListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, OnBufferingUpdateListener, TextureView.SurfaceTextureListener {
//    private static final String TAG = "CustomVideoView";
//    private static final int TIME_MSG = 0x01;
//    private static final int TIME_INTERVAL = 1000;//时间间隔
//    private static final int STATE_ERROR = -1;
//    private static final int STATE_IDLE = 0;//空闲状态
//    private static final int STATE_PLAYING = 1;
//    private static final int STATE_PAUSING = 2;
//    private static final int LOAD_TOTAL_COUNT = 3;//三个线程加载视频
//    private String url;
//    private int currentCount;//当前加载的次数
//
//    private ViewGroup mParentContainer;
//    private RelativeLayout mPlayerView;
//    private TextureView mVideoView;
//    private ImageView imgLoading;//加载
//    private ImageView imgFrame;
//    private ImageView imgEnlarge;//扩大
//    private Button btnPlay;//播放按钮
//
//    private AudioManager audioManager;
//    private Surface videoSurface;
//    private MediaPlayer mediaPlayer;//视频播放器
//    private ScreenEventReceiver screenEventReceiver;//锁屏广播接收器
//    private boolean isRealPause; //手动点击的暂停
//    private boolean isComplete;//是否播放完成
//    private Context context;
//    private Uri uri;
//
//
//    private VideoPlayerListener videoPlayerListener;
//
//
//    //状态
//    private int playerState = STATE_IDLE;
//
//    private int screenWidth;
//    private int destationHeight;
//
//    //播中检测 用于向服务器返回 当前播放的位置  ,在播放和load 时使用，其他情况下remove
//    private Handler mHanlder = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what) {
//                case TIME_MSG:
//                    if (isPlaying()) {
//                        //回调缓存监听
//                        sendEmptyMessageDelayed(TIME_MSG, TIME_INTERVAL);
//                        videoPlayerListener.onBufferUpdate(getCurrentPosition());
//                    }
//                    break;
//            }
//        }
//    };
//
//    public CustomVideoView(Context context, ViewGroup parent) {
//        super(context);
//        mParentContainer = parent;
//        initView();
//        initData();
//        registerBroadcastReceiver();
//    }
//
//    private void initView() {
//        LayoutInflater inflater = LayoutInflater.from(this.getContext());
//        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.xadsdk_video_player, this);
//        mVideoView = mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
//        imgEnlarge = mPlayerView.findViewById(R.id.xadsdk_to_full_view);
//        imgFrame = mPlayerView.findViewById(R.id.framing_view);
//        imgLoading = mPlayerView.findViewById(R.id.loading_bar);
//        btnPlay = mPlayerView.findViewById(R.id.xadsdk_small_play_btn);
//    }
//
//    private void initData() {
//        mVideoView.setOnClickListener(this);
//        mVideoView.setKeepScreenOn(true);
//        mVideoView.setSurfaceTextureListener(this);
//        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
//        imgEnlarge.setOnClickListener(this);
//        btnPlay.setOnClickListener(this);
//        //初始化 宽度 和高度
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        WindowManager manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
//        manager.getDefaultDisplay().getMetrics(displayMetrics);
//        screenWidth = displayMetrics.widthPixels;
//        destationHeight = (int) (screenWidth *
//                SDKConstant.VIDEO_HEIGHT_PERCENT); //宽度乘 9/16
//    }
//
//    /**
//     * 注册广播
//     */
//    private void registerBroadcastReceiver() {
//        if (screenEventReceiver == null) {
//            screenEventReceiver = new ScreenEventReceiver();
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
//            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
//            getContext().registerReceiver(screenEventReceiver, intentFilter);
//        }
//    }
//
//    /**
//     * 设置当前的状态
//     *
//     * @param state
//     */
//    private void setCurrentState(int state) {
//        playerState = state;
//    }
//
//    /**
//     * 获取当前播放位置
//     * @return
//     */
//    public int getCurrentPosition() {
//        if (mediaPlayer != null) {
//            return mediaPlayer.getCurrentPosition();
//        }
//        return 0;
//    }
//
//    /**
//     * 获取总的播放时间
//     * @return
//     */
//    public int getDuration(){
//        if (mediaPlayer != null) {
//            return mediaPlayer.getDuration();
//        } else {
//            return 0;
//        }
//    }
//
//    public boolean isComplete(){
//        return isComplete;
//    }
//
//    /**
//     * 设置URL
//     *
//     * @param url
//     */
//    public void setDataSource(String url) {
//        this.url = url;
//    }
//
//    public void setDataSource(Context context, Uri uri) {
//        this.context = context;
//        this.uri = uri;
//    }
//
//    /**
//     * 加载视频url
//     */
//    public void load() {
//        //不处于空闲状态 直接返回
//        if (playerState != STATE_IDLE) {
//            return;
//        }
//        //下载过程中显示加载界面
//        showLoadingView();
//        try {
//            setCurrentState(STATE_IDLE);
//            checkMediaPlayer();//完成播放器的创建工作
//            if (context != null) {
//                mediaPlayer.setDataSource(context, uri);
//            } else {
//                mediaPlayer.setDataSource(url);
//            }
//            mediaPlayer.prepareAsync();//异步加载视频
//
//        } catch (Exception e) {
//            stop();
//        }
//    }
//
//    /**
//     * 创建唯一一个mediaPlayer
//     */
//    private synchronized void checkMediaPlayer() {
//        if (mediaPlayer == null) {
//            mediaPlayer = createMediaPlayer();
//        }
//    }
//
//    /**
//     * 创建MediaPlayer
//     *
//     * @return
//     */
//    private MediaPlayer createMediaPlayer() {
//        mediaPlayer = new MediaPlayer();
//        mediaPlayer.reset();
//        mediaPlayer.setOnPreparedListener(this);
//        mediaPlayer.setOnErrorListener(this);
//        mediaPlayer.setOnBufferingUpdateListener(this);
//        mediaPlayer.setOnCompletionListener(this);
//        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
//        if (videoSurface != null && videoSurface.isValid()) {
//            mediaPlayer.setSurface(videoSurface);
//        } else {
//            stop();
//        }
//        return mediaPlayer;
//    }
//
//    /**
//     * 显示加载图
//     */
//    private void showLoadingView() {
//        //隐藏组件
//        imgEnlarge.setVisibility(GONE);
//        imgFrame.setVisibility(GONE);
//        btnPlay.setVisibility(GONE);
//        imgLoading.setVisibility(VISIBLE);
//        AnimationDrawable animationDrawable = (AnimationDrawable) imgLoading.getBackground();
//        animationDrawable.start();
//        loadFrameImg();
//    }
//
//    /**
//     * 异步加载定帧图
//     */
//    private void loadFrameImg() {
//    }
//
//    /**
//     * 清空mediaplayer，并重启load
//     */
//    public void stop() {
//        if (mediaPlayer != null) {
//            mediaPlayer.reset();
//            mediaPlayer.setOnSeekCompleteListener(null);
//            mediaPlayer.stop();
//            mediaPlayer.release();
//            mediaPlayer = null;
//        }
//        setCurrentState(STATE_IDLE);
//        mHanlder.removeCallbacksAndMessages(null);
//        //重新加载视频
//        if (currentCount < LOAD_TOTAL_COUNT) {
//            currentCount += 1;
//            load();
//        } else {//停止重试
//            showPauseView(true);//显示暂停
//        }
//    }
//
//    private void showPauseView(boolean isPause) {
//        imgEnlarge.setVisibility(isPause ? GONE : VISIBLE);
//        imgLoading.setVisibility(GONE);
//        imgLoading.clearAnimation();
//        btnPlay.setVisibility(isPause ? VISIBLE : GONE);
//        mVideoView.setVisibility(isPause?GONE:VISIBLE);
//        if (isPause) {
//            imgFrame.setVisibility(VISIBLE);
//            loadFrameImg();
//        } else {
//            imgFrame.setVisibility(GONE);
//        }
//    }
//
//    /**
//     * 播放器处于就绪状态，才能使用start
//     *
//     * @param mp
//     */
//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        mediaPlayer = mp;
//        if (mediaPlayer != null) {
//            mediaPlayer.setOnBufferingUpdateListener(this);
//            currentCount = 0;//重试次数设为0
//            //listener回调loadSuccess
//            if (videoPlayerListener != null) {
//                videoPlayerListener.loadSuccess();
//            }
//            //准备好后，根据具体条件判断是否可播放
//            decideCanPlay();
//        }
//    }
//
//    /**
//     * 1.是否大于视频播放屏幕的50%
//     * 2.是否和用户设置的网络状态一致
//     */
//    private void decideCanPlay() {
//        //来回切换页面时，只有 >50,且满足自动播放条件才自动播放
//        if (Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT) {
//            resume();
//        } else {
//            pause();
//        }
//    }
//
//    /**
//     * 播放视频
//     */
//    public void resume() {
//        //不为暂停状态直接返回
//        if (playerState != STATE_PAUSING) {
//            return;
//        }
//        if (!isPlaying()) {
//            mediaPlayer.start();
//            setCurrentState(STATE_PLAYING);//更新状态
//            mHanlder.sendEmptyMessage(TIME_MSG);
//        }
//    }
//
//    private boolean isPlaying() {
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            return true;
//        }
//        return false;
//    }
//
//    /**
//     * 播放器产生异常回调
//     *
//     * @param mp
//     * @param what
//     * @param extra
//     * @return
//     */
//    @Override
//    public boolean onError(MediaPlayer mp, int what, int extra) {
//        setCurrentState(STATE_ERROR);
//        if (currentCount >= LOAD_TOTAL_COUNT) {
//            //失败回调
//            if (videoPlayerListener != null) {
//                videoPlayerListener.loadFail();
//            }
//            showPauseView(true);
//        }
//        stop();//清空内容
//        return true;//表示用户自己处理，false表示系统处理
//    }
//
//    @Override
//    public void onCompletion(MediaPlayer mp) {
//        //成功回调
//        if (videoPlayerListener != null) {
//            videoPlayerListener.loadComplete();
//        }
//        //回到初始状态
//        playBack();
//    }
//
//    /**
//     * 回到初始状态
//     */
//    private void playBack() {
//        setCurrentState(STATE_PAUSING);
//        if (mediaPlayer != null) {
//            mediaPlayer.setOnSeekCompleteListener(null);
//            mediaPlayer.seekTo(0);
//            mediaPlayer.pause();
//        }
//        mHanlder.removeCallbacksAndMessages(null);
//        showPauseView(true);
//    }
//
//    /**
//     * 暂停视频
//     */
//    public void pause() {
//        if (playerState != STATE_PAUSING) {
//            return;
//        }
//        setCurrentState(STATE_PAUSING);
//        if (isPlaying()) {
//            mediaPlayer.pause();
//        }
//        mHanlder.removeCallbacksAndMessages(null);
//        showPauseView(true);
//    }
//
//
//    /**
//     * 可见状态变化，控制视频播放和暂停状态
//     *
//     * @param changedView
//     * @param visibility
//     */
//    @Override
//    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
//        super.onVisibilityChanged(changedView, visibility);
//        if (visibility == VISIBLE && playerState == STATE_PAUSING) {
//            if (isComplete||isRealPause) {//播放完成，则继续暂停 或者点击了暂停
//                pause();
//            } else {
//                decideCanPlay();//根据屏幕判断是否可播放
//            }
//        } else {
//            pause();
//        }
//
//    }
//
//    /**
//     * 设置为true，默认处理点击事件
//     *
//     * @param event
//     * @return
//     */
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        return true;
//    }
//
//    @Override
//    public void onClick(View v) {
//        int i = v.getId();
//        if (v == mVideoView && videoPlayerListener != null) {
//            videoPlayerListener.onClickVideoView();
//        } else if (v == btnPlay) {
//            if (playerState == STATE_PAUSING) {
//                if (Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT) {
//                    resume();
//                    if (videoPlayerListener != null) {
//                        videoPlayerListener.onClickPlay();
//                    }
//                }
//            }
//
//        } else if (v == imgEnlarge && videoPlayerListener != null) {
//            videoPlayerListener.onClickFullButton();
//        }
//
//    }
//
//    @Override
//    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//
//    }
//
//    /**
//     * 表明我们的TextureView可用
//     * 在Surface可用时，load我们的视频
//     *
//     * @param surface
//     * @param width
//     * @param height
//     */
//    @Override
//    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
//        videoSurface = new Surface(surface);
//        load();
//    }
//
//    @Override
//    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
//
//    }
//
//    @Override
//    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
//        return false;
//    }
//
//    @Override
//    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
//
//    }
//
//    /**
//     * 监听锁屏事件的广播
//     * Intent.ACTION_SCREEN_ON ： 屏幕点亮
//     * Intent.ACTION_SCREEN_OFF ：屏幕关闭
//     * Intent.ACTION_USER_PRESENT： 用户解锁
//     */
//    private class ScreenEventReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            switch (intent.getAction()) {//主动锁屏时暂停pause，主动解锁时播放 resume
//                case Intent.ACTION_USER_PRESENT: // 解锁
//                    if (playerState == STATE_PAUSING) {
//                        //手动点的暂停 回来后仍显示暂停
//                        if (isRealPause) {
//                            pause();
//                        }
//                    } else {
//                        decideCanPlay(); //根据视频显示状态来决定是否播放
//                    }
//                    break;
//                case Intent.ACTION_SCREEN_OFF: //屏幕关闭
//                    if (isPlaying()) {
//                        pause();
//                    }
//                    break;
//            }
//        }
//    }
//
//    public void setVideoPlayerListener(VideoPlayerListener listener) {
//        this.videoPlayerListener = listener;
//    }
//
//
//    public  interface VideoPlayerListener {
//        void onBufferUpdate(int time);//当前更新的时间
//
//        void onClickVideoView();//点击VideoView
//
//        void onClickFullButton();//点击放大按钮
//
//        void onClickPlay();
//
//        void loadSuccess();
//
//        void loadFail();
//
//        void loadComplete();
//    }

/**
 * 自定义VideoView,负责视频的播放，暂停，错误处理
 * Created by rhm on 2017/12/20.
 */

public class CustomVideoView extends RelativeLayout implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, OnBufferingUpdateListener {
    public final static int STATE_PAUSE = 0x00;
    public final static int STATE_PLAYING = 0x01;
    public final static int STATE_ERROR = 0x02;
    public final static int STATE_IDLE = 0x03;

    public final static int TIME_MSG = 0x04;
    public final static int TIME_INTERVAL = 1000;//间隔时间
    public final static int TOTAL_COUNT = 3;//重新加载次数

    private ViewGroup mParentContainer;
    private TextureView mVideoView;
    private ImageView imgLoading;//加载
    private ImageView imgFrame;
    private ImageView imgEnlarge;//扩大
    private Button btnPlay;//播放按钮

    private Surface mSurface;
    private MediaPlayer mediaPlayer;
    private Context context;
    private String url;
    private Uri uri;

    private int currentCount;
    private int playState = STATE_IDLE;
    private VideoPlayerListener listener;
    private boolean isRealPause = false;//手动暂停
    private ScreenEventReceiver receiver;
    private boolean isComplete;
    private AudioManager audioManager;


    //播中检测 用于向服务器返回 当前播放的位置  ,在播放时使用，其他情况下remove
    private Handler mHanlder = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_MSG:
                    if (isPlaying()) {
                        //回调缓存监听
                        sendEmptyMessageDelayed(TIME_MSG, TIME_INTERVAL);
                        if (listener != null) {
                            listener.onBufferUpdate(getCurrentPosition());
                        }
                    }
                    break;
            }
        }
    };

    public CustomVideoView(Context context, ViewGroup parent) {
        super(context);
        mParentContainer = parent;
        audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        initView();
        registerReceiver();
    }

    private void registerReceiver() {
        if (receiver == null) {
            receiver = new ScreenEventReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Intent.ACTION_USER_PRESENT);
            intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
            getContext().registerReceiver(receiver, intentFilter);
        }
    }

    private void initView() {
        LayoutInflater inflater = LayoutInflater.from(this.getContext());
        RelativeLayout mPlayerView;
        mPlayerView = (RelativeLayout) inflater.inflate(R.layout.xadsdk_video_player, this);
        mVideoView = mPlayerView.findViewById(R.id.xadsdk_player_video_textureView);
        imgEnlarge = mPlayerView.findViewById(R.id.xadsdk_to_full_view);
        imgFrame = mPlayerView.findViewById(R.id.framing_view);
        imgLoading = mPlayerView.findViewById(R.id.loading_bar);
        btnPlay = mPlayerView.findViewById(R.id.xadsdk_small_play_btn);

        mVideoView.setSurfaceTextureListener(this);
        mVideoView.setOnClickListener(videoClick);
        btnPlay.setOnClickListener(btnPlayClick);
        imgEnlarge.setOnClickListener(enlargeClick);
    }

    private OnClickListener btnPlayClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mediaPlayer != null) {
                mediaPlayer.seekTo(getCurrentPosition());
                mediaPlayer.start();
                if (listener != null) {
                    listener.onClickPlay();
                }
            }
            showPauseView(false);
        }
    };

    private OnClickListener enlargeClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (listener != null) {
                listener.onClickFullButton();
            }
        }
    };

    private OnClickListener videoClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                isRealPause = true;
                if (listener != null) {
                    listener.onClickVideoView();
                }
                pause();
            }
        }
    };

    /**
     * 当TextureView可用时 初始化mediaplayer
     *
     * @param surface
     * @param width
     * @param height
     */
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
        load();//开始初始化mediaplayer进入准备状态
    }

    private void load() {
        if (playState != STATE_IDLE) {
            return;
        }
        showLoadingView();
        if (mediaPlayer == null) {
            initMediaPlayer();
        }
        try {
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            playState = STATE_IDLE;
        } catch (IOException e) {
            stop();
        }
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
        AnimationDrawable animationDrawable = (AnimationDrawable) imgLoading.getBackground();
        animationDrawable.start();
        //        loadFrameImg();
    }

    private synchronized void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.reset();
        mediaPlayer.setSurface(mSurface);//绘制到surface上
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        //添加监听事件
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnBufferingUpdateListener(this);
    }

    /**
     * 清空mediaplayer，并重启load
     */
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        playState = STATE_IDLE;
        mHanlder.removeCallbacksAndMessages(null);

        //重新加载视频
        if (currentCount < TOTAL_COUNT) {
            currentCount += 1;
            load();
        } else {//停止重试
            showPauseView(true);//显示暂停
        }
    }

    private void showPauseView(boolean isPause) {
        imgEnlarge.setVisibility(isPause ? GONE : VISIBLE);
        imgLoading.setVisibility(GONE);
        imgLoading.clearAnimation();
        btnPlay.setVisibility(isPause ? VISIBLE : GONE);
        if (isPause) {
            //            imgFrame.setVisibility(VISIBLE);
            //            loadFrameImg();
        } else {
            //            imgFrame.setVisibility(GONE);
        }
    }


    /**
     * 可见状态变化，控制视频播放和暂停状态
     *
     * @param changedView
     * @param visibility
     */
    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE && playState == STATE_PAUSE) {
            if (isComplete || isRealPause) {//播放完成，则继续暂停 或者点击了暂停
                pause();
            } else {
                decideCanPlay();//根据屏幕判断是否可播放
            }
        } else {
            pause();
        }

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

    public void setVideoViewListener(VideoPlayerListener listener) {
        this.listener = listener;
    }

    public void setDataSource(String path) {
        this.url = path;
    }

    public void setDataSource(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
    }

    public void setPlayerState(int state) {
        this.playState = state;
    }

    public int getPlayerState() {
        return playState;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isComplete() {
        return isComplete;
    }

    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public boolean isRealPause() {
        return isRealPause;
    }

    public void setIsRealPause(boolean isRealPause) {
        this.isRealPause = isRealPause;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mediaPlayer = mp;
        if (mp != null) {
            mediaPlayer.setOnBufferingUpdateListener(this);
            currentCount = 0;//重试次数设为0
            //listener回调loadSuccess
            if (listener != null) {
                listener.loadSuccess();
            }
            //准备好后，根据具体条件判断是否可播放
            decideCanPlay();
        }
    }

    /**
     * 1.是否大于视频播放屏幕的50%
     * 2.是否和用户设置的网络状态一致
     */
    private void decideCanPlay() {
        //来回切换页面时，只有 >50,且满足自动播放条件才自动播放
        if (Utils.getVisiblePercent(mParentContainer) > SDKConstant.VIDEO_SCREEN_PERCENT) {
            resume();
        } else {
            pause();
        }
    }

    public void mute(boolean isMute) {
        if (mediaPlayer != null) {
            float volume = isMute ? 0.0f : 1.0f;
            mediaPlayer.setVolume(volume, volume);
        }
    }

    /**
     * 播放视频
     */
    public void resume() {
        if (!isPlaying()) {
            mediaPlayer.start();
            showPauseView(false);
            playState = STATE_PLAYING;
        }
        mHanlder.sendEmptyMessage(TIME_MSG);
    }

    /**
     * 跳到指定点播放视频
     *
     * @param position
     */
    public void seekAndResume(int position) {
        if (mediaPlayer != null) {
            showPauseView(false);
            //            entryResumeState();
            mediaPlayer.seekTo(position);
            mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mediaPlayer.start();
                    mHanlder.sendEmptyMessage(TIME_MSG);
                }
            });
        }

    }

    /**
     * 暂停
     */
    public void pause() {
        if (playState != STATE_PLAYING) {
            return;
        }
        isRealPause = false;
        playState = STATE_PAUSE;
        if (isPlaying()) {
            mediaPlayer.pause();
        }
        mHanlder.removeCallbacksAndMessages(null);
        showPauseView(true);
    }

    public void setBtnPlayVisible(boolean isVisible) {
        btnPlay.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void setParentContainer(ViewGroup parentContainer) {
        this.mParentContainer = parentContainer;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        playState = STATE_ERROR;
        if (currentCount > TOTAL_COUNT) {
            if (listener != null) {
                listener.loadFail();
            }
            showPauseView(true);
        }
        stop();//清空内容
        return true;//表示用户自己处理，false表示系统处理
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (listener != null) {
            listener.loadComplete();
        }
        isComplete = true;
        //回到初始状态
        playBack();

    }

    public void playBack() {
        playState = STATE_PAUSE;
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
        mHanlder.removeCallbacksAndMessages(null);
        showPauseView(true);
    }

    public void setFullButton(boolean isVisible) {
        imgEnlarge.setVisibility(isVisible ? VISIBLE : GONE);
    }

    public void setVideoVisible(boolean isVisible) {
        mVideoView.setVisibility(isVisible ? VISIBLE : GONE);
    }

    /**
     * 销毁
     */
    public void destroy() {
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        playState = STATE_IDLE;
        currentCount = 0;
        unRegisterBroadcastReceiver();
        mHanlder.removeCallbacksAndMessages(null);
        showPauseView(true);
    }

    /**
     * 取消广播
     */
    private void unRegisterBroadcastReceiver() {
        if (receiver != null) {
            getContext().unregisterReceiver(receiver);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }


    public interface VideoPlayerListener {
        void onBufferUpdate(int time);//当前更新的时间

        void onClickVideoView();//点击VideoView

        void onClickFullButton();//点击放大按钮

        void onClickPlay();

        void loadSuccess();

        void loadFail();

        void loadComplete();
    }

    /**
     * 监听锁屏事件的广播
     * Intent.ACTION_SCREEN_ON ： 屏幕点亮
     * Intent.ACTION_SCREEN_OFF ：屏幕关闭
     * Intent.ACTION_USER_PRESENT： 用户解锁
     */
    private class ScreenEventReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Intent.ACTION_SCREEN_OFF://屏幕关闭
                    if (isPlaying()) {
                        pause();
                    }
                    break;
                case Intent.ACTION_USER_PRESENT://用户解锁
                    if (playState == STATE_PAUSE) {
                        if (isRealPause) {
                            pause();
                        }
                    } else {
                        decideCanPlay();
                    }
                    break;
            }
        }
    }
}
