package com.example.com.support;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
 * 自定义VideoView,负责视频的播放，暂停，错误处理
 * Created by rhm on 2017/12/20.
 */

public class CustomVideoView extends RelativeLayout implements TextureView.SurfaceTextureListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener {
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
            mediaPlayer.seekTo(getCurrentPosition());
            mediaPlayer.start();
            if (listener != null) {
                listener.onClickPlay();
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
            if (isPlaying()) {
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
            if (context != null) {
                mediaPlayer.setDataSource(context, uri);
            } else {
                mediaPlayer.setDataSource(url);
            }
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

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
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

        playState = STATE_PAUSE;
        if (isPlaying()) {
            mediaPlayer.pause();
        }
        mHanlder.removeCallbacksAndMessages(null);
        showPauseView(true);
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

    private void playBack() {
        playState = STATE_PAUSE;
        if (mediaPlayer != null) {
            mediaPlayer.setOnSeekCompleteListener(null);
            mediaPlayer.seekTo(0);
            mediaPlayer.pause();
        }
        mHanlder.removeCallbacksAndMessages(null);
        showPauseView(true);
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
