package com.example.com.support.video;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by rhm on 2018/1/15.
 * 控制器抽象类,控制音量，亮度，进度
 */

public abstract class NiceVideoPlayerController extends FrameLayout implements View.OnTouchListener {
    private Context mContext;
    protected INiceVideoPlayer mNiceVidePlayer;

    private float mDownX;
    private float mDOwnY;
    private boolean mNeedChangeVolume;
    private boolean mNeedChangePosition;
    private boolean mNeedChangeBrightness;
    private static final int THRESHOLD = 80;
    private long mGestureDownPosition;
    private float mGestureDownBrightness;
    private int mGestureDownVolume;
    private long mNewPosition;

    //计时
    private Timer mUpdateProgressTimer;
    private TimerTask mUpdateProgressTimerTask;

    public NiceVideoPlayerController(@NonNull Context context) {
        super(context);
        mContext = context;
        this.setOnTouchListener(this);
        ViewConfiguration configuration = ViewConfiguration.get(context);
    }

    public void setNiceVideoPlayer(INiceVideoPlayer niceVideoPlayer) {
        mNiceVidePlayer = niceVideoPlayer;
    }

    /**
     * 设置播放的视频的标题
     *
     * @param title 视频标题
     */
    public abstract void setTitle(String title);

    /**
     * 视频底图
     *
     * @param resId 视频底图资源
     */
    public abstract void setImage(@DrawableRes int resId);

    /**
     * 视频底图ImageVie控件，提供给外部用图片加载工具来加载网络图片
     *
     * @return 底图ImageView
     */
    public abstract ImageView imageView();

    /**
     * 设置总时长
     *
     * @param length 时长
     */
    public abstract void setLength(long length);

    /**
     * @param playState 播放状态
     */
    protected abstract void onPlayStateChanged(int playState);

    /**
     * 播放模式改变
     *
     * @param playMode 播放模式
     */
    protected abstract void onPlayModeChanged(int playMode);

    /**
     * 重置控制器，将控制器恢复到初始状态
     */
    protected abstract void reset();

    /**
     * 开启更新进度的计时器
     */
    protected void startUpdateProgressTimer() {
        cancelUpdateProgressTimer();
        if (mUpdateProgressTimer == null) {
            mUpdateProgressTimer = new Timer();
        }
        if (mUpdateProgressTimerTask == null) {
            mUpdateProgressTimerTask = new TimerTask() {
                @Override
                public void run() {
                    NiceVideoPlayerController.this.post(new Runnable() {
                        @Override
                        public void run() {
                            updateProgress();
                        }
                    });
                }
            };
        }
        mUpdateProgressTimer.schedule(mUpdateProgressTimerTask, 0, 1000);

    }

    /**
     * 取消更新进度的计时器
     */
    protected void cancelUpdateProgressTimer() {
        if (mUpdateProgressTimer != null) {
            mUpdateProgressTimer.cancel();
            mUpdateProgressTimer = null;
        }
        if (mUpdateProgressTimerTask != null) {
            mUpdateProgressTimerTask.cancel();
            mUpdateProgressTimerTask = null;
        }
    }

    /**
     * 更新进度，包括进度条进度，展示当前播放位置时长，总时长
     */
    protected abstract void updateProgress();


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        //只有全屏的时候才能拖动位置，亮度，时间
        if (!mNiceVidePlayer.isFullScreen()) {
            return false;
        }
        //只有在播放，暂停，缓冲的时候能够拖动改变位置。亮度和声音
        if (mNiceVidePlayer.isIdle()
                || mNiceVidePlayer.isError()
                || mNiceVidePlayer.isPreparing()
                || mNiceVidePlayer.isPrepared()
                || mNiceVidePlayer.isCompleted()) {

            return false;
        }
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //记录按下时的位置
                mDownX = x;
                mDOwnY = y;
                mNeedChangePosition = false;
                mNeedChangeVolume = false;
                mNeedChangeBrightness = false;
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = x - mDownX;
                float deltaY = y - mDOwnY;
                float absDeltaX = Math.abs(deltaX);
                float absDeltaY = Math.abs(deltaY);
                if (!mNeedChangePosition && !mNeedChangeVolume && !mNeedChangeBrightness) {
                    //在播放，暂停，缓冲时能够拖动改变播放位置，声音，亮度
                    if (absDeltaX >= THRESHOLD) {//播放位置调节
                        cancelUpdateProgressTimer();
                        mNeedChangePosition = true;
                        mGestureDownPosition = mNiceVidePlayer.getCurrentPostion();
                    } else if (absDeltaY >= THRESHOLD) {
                        if (mDownX < getWidth() * 0.5f) {
                            //左侧改变亮度
                            mNeedChangeBrightness = true;
                            //获取窗口亮度属性
                            mGestureDownBrightness = NiceUtil.scanForActivity(mContext).getWindow().getAttributes().screenBrightness;
                        } else {
                            //右侧改变声音
                            mNeedChangeVolume = true;
                            mGestureDownVolume = mNiceVidePlayer.getVolume();
                        }
                        if (mNeedChangePosition) {
                            long duration = mNiceVidePlayer.getDuration();
                            //当前距离+屏幕滑动距离
                            long toPosition = (long) (mGestureDownPosition + deltaX / getWidth() * duration);
                            mNewPosition = Math.max(0, Math.min(duration, toPosition));
                            int newPositionProgress = (int) (100f * mNewPosition / duration);//百分比
                            showChangePosition(duration, newPositionProgress);

                        }
                        //窗口亮度值 为 0-1
                        if (mNeedChangeBrightness) {
                            deltaY = -deltaY;//由于是向下滑时，最终距离比开始距离大，为正数
                            float detaBrightness = deltaY / getHeight();//滑动的距离除以屏幕高度的比例，可以乘以一定的倍率，来加大滑动效果。  deltaY/getHeight *3
                            float newBrightness = mGestureDownBrightness + detaBrightness;
                            newBrightness = Math.max(0, Math.min(newBrightness, 1));//限制范围
                            WindowManager.LayoutParams params = NiceUtil.scanForActivity(mContext).getWindow().getAttributes();
                            params.screenBrightness = newBrightness;
                            //设置亮度
                            NiceUtil.scanForActivity(mContext).getWindow().setAttributes(params);
                            int newBrightnessProgress = (int) (100f * newBrightness);
                            showChangeBrightness(newBrightnessProgress);


                        }
                        //设置音量
                        if (mNeedChangeVolume) {
                            deltaY = -deltaY;
                            int maxVolume = mNiceVidePlayer.getMaxVolume();
                            int deltaVolume = (int) (deltaY / getHeight() * maxVolume);
                            int newVolume = mGestureDownVolume + deltaVolume;
                            newVolume = Math.max(0, Math.min(maxVolume, newVolume));
                            mNiceVidePlayer.setVolume(newVolume);
                            int newVolumeProgress = (int) (newVolume / maxVolume * 100f);
                            showChangeVolume(newVolumeProgress);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //隐藏状态图
                if (mNeedChangePosition) {
                    mNiceVidePlayer.seekTo(mNewPosition);
                    startUpdateProgressTimer();
                    hideChangePosition();
                    return true;
                }
                if (mNeedChangeBrightness) {
                    hideChangeBrightness();
                    return true;
                }
                if (mNeedChangeVolume) {
                    hideChangeVolume();
                    return true;
                }
                break;

        }
        return false;
    }

    /**
     * 手势滑动显示进度
     *
     * @param duration            播放总时长
     * @param newPositionProgress 新的位置
     */
    protected abstract void showChangePosition(long duration, int newPositionProgress);

    /**
     * 手势左右滑动改变播放器位置后，手势up或者cancel时隐藏空间中间的播放器位置变化视图
     */
    protected abstract void hideChangePosition();

    /**
     * 音量变化显示
     *
     * @param newVolumeProgress
     */
    protected abstract void showChangeVolume(int newVolumeProgress);

    /**
     * 音量变化显示
     */
    protected abstract void hideChangeVolume();

    /**
     * 亮度变化显示
     *
     * @param newBrightnessProgress 亮度值
     */
    protected abstract void showChangeBrightness(int newBrightnessProgress);

    /**
     * 隐藏亮度条
     */
    protected abstract void hideChangeBrightness();
}
