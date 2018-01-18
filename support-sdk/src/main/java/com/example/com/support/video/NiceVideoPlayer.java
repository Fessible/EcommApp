package com.example.com.support.video;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.io.IOException;
import java.util.Map;

import static android.media.MediaPlayer.MEDIA_INFO_BUFFERING_END;
import static android.media.MediaPlayer.MEDIA_INFO_BUFFERING_START;
import static android.media.MediaPlayer.MEDIA_INFO_NOT_SEEKABLE;
import static android.media.MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START;

/**
 * Created by rhm on 2018/1/15.
 */

public class NiceVideoPlayer extends FrameLayout implements INiceVideoPlayer, TextureView.SurfaceTextureListener {
    /****************
     * 播放状态
     ****************/
    /**
     * 播放错误
     */
    public static final int STATE_ERROR = -1;
    /**
     * 播放未开始
     */
    public static final int STATE_IDLE = 0;
    /**
     * 播放准备中
     */
    public static final int STATE_PREPARING = 1;
    /**
     * 播放准备就绪
     */
    public static final int STATE_PREPARED = 2;
    /**
     * 正在播放
     */
    public static final int STATE_PLAYING = 3;
    /**
     * 暂停播放
     */
    public static final int STATE_PAUSED = 4;
    /**
     * 正在缓冲（播放器正在播放时，缓冲区数据不足，进行缓冲，缓冲区数据足够后恢复播放）
     */
    public static final int STATE_BUFFERING_PLAYING = 5;
    /**
     * 正在缓冲(播放器正在播放时，缓冲区数据不足，进行缓冲，此时暂停播放器，继续缓冲，缓冲区数据足够后恢复暂停
     **/
    public static final int STATE_BUFFERING_PAUSED = 6;
    /**
     * 播放完成
     */
    public static final int STATE_COMPLETED = 7;

    /*************
     * 播放模式
     *************/
    /**
     * 普通模式
     */
    public static final int MODE_NORMAL = 10;
    /**
     * 全屏模式
     */
    public static final int MODE_FULL_SCREEN = 11;
    /**
     * 小窗口模式
     */
    public static final int MODE_TINY_WINDOW = 12;

    private Context mContext;
    private AudioManager mAudioManager;
    private FrameLayout mContainer;
    private String mUrl;
    private Map<String, String> mHeaders;
    private NiceVideoPlayerController mController;
    private boolean continueFromlastPosition;
    private int mCurrentState = STATE_IDLE;
    private int mCurrentMode = MODE_NORMAL;
    private MediaPlayer mediaPlayer;
    private NiceTextureView mTextureView;
    private long skipToPosition;
    private Surface mSurface;
    private SurfaceTexture mSurfaceTexture;
    private int mBufferPercentage;


    public NiceVideoPlayer(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    public NiceVideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        mContainer = new FrameLayout(mContext);
        mContainer.setBackgroundColor(Color.BLACK);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mContainer, params);
    }

    @Override
    public void setUp(String url, Map<String, String> headers) {
        mUrl = url;
        mHeaders = headers;
    }

    public void setController(NiceVideoPlayerController controller) {
        if (controller != null) {
            mContainer.removeView(controller);
            mController = controller;
            mController.reset();
            mController.setNiceVideoPlayer(this);
            //全屏显示
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mContainer.addView(mController, params);
        }
    }

    //初始化AudioManager
    private void initAudioManager() {
        if (mAudioManager == null) {
            mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            mAudioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        }
    }

    private void initMediaPlayer() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new NiceTextureView(mContext);
            mTextureView.setSurfaceTextureListener(this);
        }
    }

    private void addTextureView() {
        mContainer.removeView(mTextureView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER);
        mContainer.addView(mTextureView, 0, params);
    }

    @Override
    public void start() {
        //只有在空闲状态才能开始
        if (mCurrentState == STATE_IDLE) {
            NiceVideoPlayerManager.getsInstance().setCurrentNiceVideoPlayer(this);
            initAudioManager();
            initMediaPlayer();
            initTextureView();
            addTextureView();

        } else {
            LogUtil.d("NiceVideoPlayer只有在mCurrentState==STATE_IDLE时才能调用");
        }

    }

    @Override
    public void start(long position) {
        skipToPosition = position;
        start();
    }

    @Override
    public void restart() {
        //如果当前状态为暂停
        if (mCurrentState == STATE_PAUSED) {
            mediaPlayer.start();
            mCurrentState = STATE_PLAYING;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("STATE_PLAYING");
        } else if (mCurrentState == STATE_BUFFERING_PAUSED) {
            mediaPlayer.start();
            mCurrentState = STATE_BUFFERING_PLAYING;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("STATE_BUFFERING_PLAYING");
            //当完成或者播放错误
        } else if (mCurrentState == STATE_COMPLETED || mCurrentState == STATE_ERROR) {
            mediaPlayer.reset();//重置
            openMediaPlayer();//打开mediaplayer
        } else {
            LogUtil.d("NiceVideoPlayer在mCurrenState==" + mCurrentState + "时不能调用restart()");
        }

    }

    //初始化mediaplayer
    private void openMediaPlayer() {
        //设置屏幕常亮
        mContainer.setKeepScreenOn(true);
        //设置监听
        mediaPlayer.setOnPreparedListener(onPreparedListener);
        mediaPlayer.setOnVideoSizeChangedListener(videoSizeChangedListener);
        mediaPlayer.setOnCompletionListener(completionListener);
        mediaPlayer.setOnErrorListener(onErrorListener);
        mediaPlayer.setOnInfoListener(onInfoListener);
        mediaPlayer.setOnBufferingUpdateListener(onBufferingUpdateListener);
        //设置dataSource
        try {
            mediaPlayer.setDataSource(mContext.getApplicationContext(), Uri.parse(mUrl), mHeaders);
            if (mSurface == null) {
                mSurface = new Surface(mSurfaceTexture);
            }
            mediaPlayer.setSurface(mSurface);
            mediaPlayer.prepareAsync();//准备
            mCurrentState = STATE_PREPARING;//准备状态
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("STATE_PREPARING");
        } catch (IOException e) {
            e.printStackTrace();
            LogUtil.d("打开播放器发生错误");
        }
    }

    @Override
    public void pause() {
        if (mCurrentState == STATE_PLAYING) {
            mediaPlayer.pause();
            mCurrentState = STATE_PAUSED;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("STATE_PAUSED");
        }
        if (mCurrentState == STATE_BUFFERING_PLAYING) {
            mediaPlayer.pause();
            mCurrentState = STATE_BUFFERING_PAUSED;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("STATE_BUFFERING_PAUSED");
        }

    }

    @Override
    public void seekTo(long pos) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo((int) pos);
        }
    }

    @Override
    public void setSpeed(float speed) {

    }

    @Override
    public void continueFromLastPostion(boolean continueFromLastPosition) {
        this.continueFromlastPosition = continueFromLastPosition;
    }

    @Override
    public boolean isIdle() {
        return mCurrentState == STATE_IDLE;
    }

    @Override
    public boolean isPreparing() {
        return mCurrentState == STATE_PREPARING;
    }

    @Override
    public boolean isPrepared() {
        return mCurrentState == STATE_PREPARED;
    }

    @Override
    public boolean isBufferingPlaying() {
        return mCurrentState == STATE_BUFFERING_PLAYING;
    }

    @Override
    public boolean isBufferingPaused() {
        return mCurrentState == STATE_BUFFERING_PAUSED;
    }

    @Override
    public boolean isPlaying() {
        return mCurrentState == STATE_PLAYING;
    }

    @Override
    public boolean isPaused() {
        return mCurrentState == STATE_PAUSED;
    }

    @Override
    public boolean isError() {
        return mCurrentState == STATE_ERROR;
    }

    @Override
    public boolean isCompleted() {
        return mCurrentState == STATE_COMPLETED;
    }

    @Override
    public boolean isFullScreen() {
        return mCurrentMode == MODE_FULL_SCREEN;
    }

    @Override
    public boolean isTinyWindow() {
        return mCurrentMode == MODE_TINY_WINDOW;
    }

    @Override
    public boolean isNormal() {
        return mCurrentMode == MODE_NORMAL;
    }

    @Override
    public void setVolume(int volume) {
        if (mAudioManager != null) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        }

    }

    @Override
    public int getMaxVolume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public int getVolume() {
        if (mAudioManager != null) {
            return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        }
        return 0;
    }

    @Override
    public long getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    @Override
    public long getCurrentPostion() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public int getBufferPercentage() {
        return mBufferPercentage;
    }

    @Override
    public float getSpeed(float speed) {
        return 0;
    }

    @Override
    public long getTcpSpeeed() {
        return 0;
    }

    //全屏状态
    @Override
    public void enterFullScreen() {
        if (mCurrentMode == MODE_FULL_SCREEN) return;
        //隐藏ActionBar、状态栏、并横屏
        NiceUtil.hideActionBar(mContext);
        //设置横屏
        NiceUtil.scanForActivity(mContext)
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        ViewGroup contentView = NiceUtil.scanForActivity(mContext).findViewById(android.R.id.content);
        if (mCurrentMode == MODE_TINY_WINDOW) {
            contentView.removeView(mContainer);
        } else {
            this.removeView(mContainer);
        }
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        contentView.addView(mContainer, params);
        mCurrentMode = MODE_FULL_SCREEN;
        mController.onPlayModeChanged(mCurrentMode);
        LogUtil.d("MODE_FULL_SCREEN");


    }

    //退出全屏
    @Override
    public boolean exitFullScreen() {
        if (mCurrentMode == MODE_FULL_SCREEN) {
            NiceUtil.showActionBar(mContext);
            NiceUtil.scanForActivity(mContext)
                    .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            ViewGroup contentView = NiceUtil.scanForActivity(mContext).findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);
            mCurrentMode = MODE_NORMAL;
            mController.onPlayModeChanged(mCurrentMode);
            LogUtil.d("MODE_NORMAL");
            return true;
        }

        return false;
    }

    //进入小窗口
    @Override
    public void enterTinyWindow() {
        if (mCurrentMode == MODE_TINY_WINDOW) return;
        this.removeView(mContainer);
        ViewGroup contentView = NiceUtil.scanForActivity(mContext).findViewById(android.R.id.content);
        // 小窗口的宽度为屏幕宽度的60%，长宽比默认为16:9，右边距、下边距为8dp。
        LayoutParams params = new LayoutParams((int) (NiceUtil.getScreenWidth(mContext) * 0.6f), (int) (NiceUtil.getScreenHeigh(mContext) * 0.6f * 9f / 16f));
        params.gravity = Gravity.BOTTOM | Gravity.END;
        params.rightMargin = NiceUtil.dp2px(mContext, 8f);
        params.bottomMargin = NiceUtil.dp2px(mContext, 8f);
        contentView.addView(mContainer, params);
        mCurrentMode = MODE_TINY_WINDOW;
        mController.onPlayModeChanged(mCurrentMode);
        LogUtil.d("MODE_TINY_WINDOW");
    }

    //退出小窗口
    @Override
    public boolean exitTinyWindow() {
        if (mCurrentMode == MODE_TINY_WINDOW) {
            ViewGroup contentView = NiceUtil.scanForActivity(mContext).findViewById(android.R.id.content);
            contentView.removeView(mContainer);
            LayoutParams params =
                    (LayoutParams) new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            this.addView(mContainer, params);
            mCurrentMode = MODE_NORMAL;
            mController.onPlayModeChanged(mCurrentMode);
            LogUtil.d("MODE_NORMAL");
            return true;
        }
        return false;
    }

    @Override
    public void releasePlayer() {
        if (mAudioManager != null) {
            mAudioManager.abandonAudioFocus(null);
            mAudioManager = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mContainer.removeView(mTextureView);
        if (mSurface != null) {
            mSurface.release();
            mSurface = null;
        }
        if (mSurfaceTexture != null) {
            mSurfaceTexture.release();
            mSurfaceTexture = null;
        }
        mCurrentState = STATE_IDLE;
    }

    @Override
    public void release() {
        //保存播放位置
        if (isPlaying() || isBufferingPlaying() || isBufferingPaused() || isPaused()) {
            NiceUtil.savedPlayPosition(mContext, mUrl, getCurrentPostion());
        } else if (isCompleted()) {
            NiceUtil.savedPlayPosition(mContext, mUrl, 0);
        }
        //退出全屏
        if (isFullScreen()) {
            exitFullScreen();
        }
        //退出小窗口
        if (isTinyWindow()) {
            exitTinyWindow();
        }
        mCurrentMode = MODE_NORMAL;
        //释放播放器
        releasePlayer();
        //恢复控制器
        if (mController != null) {
            mController.reset();
        }
        Runtime.getRuntime().gc();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mSurfaceTexture == null) {
            mSurfaceTexture = surface;
            openMediaPlayer();//mediaPlayer开始准备
        } else {
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }

    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return mSurfaceTexture == null;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            mCurrentState = STATE_PREPARED;//就绪状态
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("onprepared——》STATE_PREPARED");
            mp.start();//开始播放
            //从上次的保存位置播放
            if (continueFromlastPosition) {
                long savePlayPosition = NiceUtil.getSavedPlayPosition(mContext, mUrl);
                mp.seekTo((int) savePlayPosition);//跳转到指定位置
            }
            //跳转到指定位置
            if (skipToPosition != 0) {
                mp.seekTo((int) skipToPosition);
            }
        }
    };

    private MediaPlayer.OnVideoSizeChangedListener videoSizeChangedListener = new MediaPlayer.OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
            mTextureView.adaptVideoSize(width, height);
            LogUtil.d("onVideoSizeChanged ——> width：" + width + "， height：" + height);

        }
    };

    private MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mCurrentState = STATE_COMPLETED;
            mController.onPlayStateChanged(mCurrentState);
            LogUtil.d("onCompletion ——> STATE_COMPLETED");
            //清除屏幕常亮
            mContainer.setKeepScreenOn(false);
        }
    };

    private MediaPlayer.OnErrorListener onErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            // 直播流播放时去调用mediaPlayer.getDuration会导致-38和-2147483648错误，忽略该错误
            if (what != -38 && what != -2147483648 && extra != -38 && extra != -2147483648) {
                mCurrentState = STATE_ERROR;
                mController.onPlayStateChanged(mCurrentState);
                LogUtil.d("onError ——> STATE_ERROR ———— what：" + what + ", extra: " + extra);
            }
            return true;
        }
    };

    int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;


    private MediaPlayer.OnInfoListener onInfoListener = new MediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(MediaPlayer mp, int what, int extra) {
            if (what == MEDIA_INFO_VIDEO_RENDERING_START) {
                //播放器开始渲染
                mCurrentState = STATE_PLAYING;
                mController.onPlayStateChanged(mCurrentState);
                LogUtil.d("onInfo ——> MEDIA_INFO_VIDEO_RENDERING_START：STATE_PLAYING");
            } else if (what == MEDIA_INFO_BUFFERING_START) {
                //mediaplayer暂时不播放，以缓冲更多的数据
                if (mCurrentState == STATE_PAUSED || mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_BUFFERING_PAUSED;
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PAUSED");
                } else {
                    mCurrentState = STATE_BUFFERING_PLAYING;
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_START：STATE_BUFFERING_PLAYING");
                }
                mController.onPlayStateChanged(mCurrentState);
            } else if (what == MEDIA_INFO_BUFFERING_END) {
                // 填充缓冲区后，MediaPlayer恢复播放/暂停
                if (mCurrentState == STATE_BUFFERING_PLAYING) {
                    mCurrentState = STATE_PLAYING;
                    mController.onPlayStateChanged(mCurrentState);
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PLAYING");
                }
                if (mCurrentState == STATE_BUFFERING_PAUSED) {
                    mCurrentState = STATE_PAUSED;
                    mController.onPlayStateChanged(mCurrentState);
                    LogUtil.d("onInfo ——> MEDIA_INFO_BUFFERING_END： STATE_PAUSED");
                }
            } else if (what == MEDIA_INFO_VIDEO_ROTATION_CHANGED) {
                // 视频旋转了extra度，需要恢复
                if (mTextureView != null) {
                    mTextureView.setRotation(extra);
                    LogUtil.d("视频旋转角度：" + extra);
                }
            } else if (what == MEDIA_INFO_NOT_SEEKABLE) {
                LogUtil.d("视频不能seekTo，为直播视频");
            } else {
                LogUtil.d("onInfo ——> what：" + what);
            }
            return true;

        }
    };

    private MediaPlayer.OnBufferingUpdateListener onBufferingUpdateListener = new MediaPlayer.OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(MediaPlayer mp, int percent) {

        }
    };

}
