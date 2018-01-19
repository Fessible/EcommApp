package com.example.com.support.video;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.CountDownTimer;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import com.example.com.glide.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.example.com.support.video.NiceVideoPlayer.MODE_FULL_SCREEN;
import static com.example.com.support.video.NiceVideoPlayer.MODE_NORMAL;
import static com.example.com.support.video.NiceVideoPlayer.MODE_TINY_WINDOW;
import static com.example.com.support.video.NiceVideoPlayer.STATE_BUFFERING_PAUSED;
import static com.example.com.support.video.NiceVideoPlayer.STATE_BUFFERING_PLAYING;
import static com.example.com.support.video.NiceVideoPlayer.STATE_COMPLETED;
import static com.example.com.support.video.NiceVideoPlayer.STATE_ERROR;
import static com.example.com.support.video.NiceVideoPlayer.STATE_IDLE;
import static com.example.com.support.video.NiceVideoPlayer.STATE_PAUSED;
import static com.example.com.support.video.NiceVideoPlayer.STATE_PLAYING;
import static com.example.com.support.video.NiceVideoPlayer.STATE_PREPARED;
import static com.example.com.support.video.NiceVideoPlayer.STATE_PREPARING;

/**
 * Created by rhm on 2018/1/18.
 */

public class TxVideoPlayerController extends NiceVideoPlayerController implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Context mContext;
    private ImageView mImage;
    private ImageView mCenterStart;

    private LinearLayout mTop;
    private ImageView mBack;
    private TextView mTitle;
    private LinearLayout mBatteryTime;
    private ImageView mBattery;
    private TextView mTime;

    private LinearLayout mBottom;
    private ImageView mRestartPause;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private TextView mClarity;
    private ImageView mFullScreen;

    private TextView mLength;

    private LinearLayout mLoading;
    private TextView mLoadText;

    private LinearLayout mChangePositon;
    private TextView mChangePositionCurrent;
    private ProgressBar mChangePositionProgress;

    private LinearLayout mChangeBrightness;
    private ProgressBar mChangeBrightnessProgress;

    private LinearLayout mChangeVolume;
    private ProgressBar mChangeVolumeProgress;

    private LinearLayout mError;
    private TextView mRetry;

    private LinearLayout mCompleted;
    private TextView mReplay;
    private TextView mShare;

    //链接
    private String url;

    private CountDownTimer mDismissTopBottomCountDownTimer;
    private boolean topBottomVisible;
    private boolean hasRegisterBatteryReceiver; // 是否已经注册了电池广播


    public TxVideoPlayerController(@NonNull Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.tx_video_player_controller, this, true);
        mCenterStart = findViewById(R.id.center_start);//中间播放按钮
        mImage = findViewById(R.id.image);//底图
        mTop = findViewById(R.id.top);
        mBack = findViewById(R.id.back);
        mTitle = findViewById(R.id.title);
        mBatteryTime = findViewById(R.id.battery_time);
        mBattery = findViewById(R.id.battery);
        mTime = findViewById(R.id.time);
        mBottom = findViewById(R.id.bottom);//底部控制区
        mRestartPause = findViewById(R.id.restart_or_pause);
        mPosition = findViewById(R.id.position);
        mDuration = findViewById(R.id.duration);
        mSeek = findViewById(R.id.seek);
        mFullScreen = findViewById(R.id.full_screen);
        mClarity = findViewById(R.id.clarity);
        mLength = findViewById(R.id.length);
        mLoading = findViewById(R.id.loading);
        mLoadText = findViewById(R.id.load_text);
        mChangePositon = findViewById(R.id.change_position);
        mChangePositionProgress = findViewById(R.id.change_position_progress);
        mChangePositionCurrent = findViewById(R.id.change_position_current);
        mChangeBrightness = findViewById(R.id.change_brightness);
        mChangeBrightnessProgress = findViewById(R.id.change_brightness_progress);
        mChangeVolume = findViewById(R.id.change_volume);
        mChangeVolumeProgress = findViewById(R.id.change_volume_progress);
        mError = findViewById(R.id.error);
        mRetry = findViewById(R.id.retry);
        mCompleted = findViewById(R.id.completed);
        mReplay = findViewById(R.id.replay);
        mShare = findViewById(R.id.share);
        mCenterStart.setOnClickListener(this);
        mBack.setOnClickListener(this);
        mRestartPause.setOnClickListener(this);
        mFullScreen.setOnClickListener(this);
        mClarity.setOnClickListener(this);
        mRetry.setOnClickListener(this);
        mReplay.setOnClickListener(this);
        mShare.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        this.setOnClickListener(this);

    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    //设置底图
    @Override
    public void setImage(@DrawableRes int resId) {
        mImage.setImageResource(resId);
    }

    @Override
    public ImageView imageView() {
        return mImage;
    }

    @Override
    public void setLength(long length) {
        mLength.setText(NiceUtil.formatTime(length));
    }

    @Override
    public void setNiceVideoPlayer(INiceVideoPlayer niceVideoPlayer) {
        super.setNiceVideoPlayer(niceVideoPlayer);//初始化niceVideoPlayer
        if (url != null) {
            //设置视频播放链接
            mNiceVidePlayer.setUp(url, null);
        }
    }

    //设置视频连接
    public void setUrl(String url) {
        if (url != null) {
            this.url = url;
            //个播放器设置视频连接
            if (mNiceVidePlayer != null) {
                mNiceVidePlayer.setUp(url, null);
            }
        }
    }

    //播放状态的改变
    @Override
    protected void onPlayStateChanged(int playState) {
        switch (playState) {
            case STATE_IDLE:
                break;
            case STATE_PREPARING://准备中
                mImage.setVisibility(GONE);//底图
                mLoading.setVisibility(VISIBLE);//加载图
                mLoadText.setText("正在准备...");
                mError.setVisibility(GONE);
                mCompleted.setVisibility(GONE);
                mTop.setVisibility(GONE);
                mBottom.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                mLength.setVisibility(GONE);
                break;
            case STATE_PREPARED://就绪状态
                startUpdateProgressTimer();
                break;
            case STATE_PLAYING://播放状态
                mImage.setVisibility(GONE);
                mError.setVisibility(GONE);
                mCompleted.setVisibility(GONE);
                mTop.setVisibility(GONE);
                mBottom.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                mLength.setVisibility(GONE);
                mLoading.setVisibility(GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                startDismissTopBottomTimer();//显示并自动消失控制栏
                break;
            case STATE_PAUSED:
                mLoading.setVisibility(GONE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                cancelDismissTopBottomTimer();//显示控制栏
                break;
            case STATE_BUFFERING_PLAYING://缓冲状态
                mLoading.setVisibility(VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_pause);
                mLoadText.setText("正在缓冲中...");
                startDismissTopBottomTimer();
                break;
            case STATE_BUFFERING_PAUSED:
                mLoading.setVisibility(VISIBLE);
                mRestartPause.setImageResource(R.drawable.ic_player_start);
                mLoadText.setText("正在缓冲中...");
                cancelDismissTopBottomTimer();
                break;
            case STATE_ERROR://错误
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mTop.setVisibility(VISIBLE);
                mError.setVisibility(VISIBLE);
                break;
            case STATE_COMPLETED://完成
                cancelUpdateProgressTimer();
                setTopBottomVisible(false);
                mImage.setVisibility(VISIBLE);
                mCompleted.setVisibility(VISIBLE);
                break;
        }
    }


    //开启top bottom 自动消失的timer
    private void startDismissTopBottomTimer() {
        cancelDismissTopBottomTimer();
        if (mDismissTopBottomCountDownTimer == null) {
            mDismissTopBottomCountDownTimer = new CountDownTimer(8000, 8000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    setTopBottomVisible(false);
                }
            };
        }
        mDismissTopBottomCountDownTimer.start();
    }

    /**
     * 设置top、bottom的显示和隐藏
     *
     * @param visible true显示，false隐藏.
     */
    private void setTopBottomVisible(boolean visible) {
        mTop.setVisibility(visible ? VISIBLE : GONE);
        mBottom.setVisibility(visible ? VISIBLE : GONE);
        topBottomVisible = visible;
        if (visible) {
            if (!mNiceVidePlayer.isPaused() && !mNiceVidePlayer.isBufferingPaused()) {
                startDismissTopBottomTimer();
            }
        } else {
            cancelDismissTopBottomTimer();
        }


    }

    //取消top，bottom自动消失的timer
    private void cancelDismissTopBottomTimer() {
        if (mDismissTopBottomCountDownTimer != null) {
            mDismissTopBottomCountDownTimer.cancel();
        }
    }

    @Override
    protected void onPlayModeChanged(int playMode) {
        switch (playMode) {
            case MODE_NORMAL://正常模式
                mBack.setVisibility(GONE);
                mFullScreen.setImageResource(R.drawable.ic_player_enlarge);
                mFullScreen.setVisibility(VISIBLE);
                mBatteryTime.setVisibility(GONE);
                mClarity.setVisibility(GONE);
                if (hasRegisterBatteryReceiver) {
                    mContext.unregisterReceiver(mBatterReceiver);
                    hasRegisterBatteryReceiver = false;
                }
                break;
            //全屏模式
            case MODE_FULL_SCREEN:
                mBack.setVisibility(VISIBLE);
                mFullScreen.setVisibility(GONE);
                mFullScreen.setImageResource(R.drawable.ic_player_shrink);
                mBatteryTime.setVisibility(VISIBLE);
                if (!hasRegisterBatteryReceiver) {
                    mContext.registerReceiver(mBatterReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    hasRegisterBatteryReceiver = true;
                }
                break;
            case MODE_TINY_WINDOW:
                mBatteryTime.setVisibility(VISIBLE);
                mClarity.setVisibility(GONE);
                break;
        }
    }

    @Override
    protected void reset() {
        topBottomVisible = false;
        cancelUpdateProgressTimer();
        cancelDismissTopBottomTimer();
        mSeek.setProgress(0);
        mSeek.setSecondaryProgress(0);

        mCenterStart.setVisibility(VISIBLE);
        mImage.setVisibility(VISIBLE);

        mBottom.setVisibility(GONE);
        mFullScreen.setImageResource(R.drawable.ic_player_enlarge);

        mLength.setVisibility(VISIBLE);

        mTop.setVisibility(VISIBLE);
        mBack.setVisibility(GONE);

        mLoading.setVisibility(GONE);
        mError.setVisibility(GONE);
        mCompleted.setVisibility(GONE);
    }

    @Override
    protected void updateProgress() {
        long position = mNiceVidePlayer.getCurrentPostion();
        long duration = mNiceVidePlayer.getDuration();
        int bufferPercentage = mNiceVidePlayer.getBufferPercentage();
        //缓冲进度
        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration);
        mSeek.setProgress(progress);
        mPosition.setText(NiceUtil.formatTime(position));
        mDuration.setText(NiceUtil.formatTime(duration));
        mTime.setText(new SimpleDateFormat("HH:mm", Locale.CHINA).format(new Date()));

    }

    @Override
    protected void showChangePosition(long duration, int newPositionProgress) {
        mChangePositon.setVisibility(VISIBLE);
        long newPosition = (long) (duration * newPositionProgress / 100f);
        mChangePositionCurrent.setText(NiceUtil.formatTime(newPosition));
        mChangePositionProgress.setProgress(newPositionProgress);
        mSeek.setProgress(newPositionProgress);
        mPosition.setText(NiceUtil.formatTime(newPosition));
    }

    @Override
    protected void hideChangePosition() {
        mChangePositon.setVisibility(GONE);
    }

    @Override
    protected void showChangeVolume(int newVolumeProgress) {
        mChangeVolume.setVisibility(View.VISIBLE);
        mChangeVolumeProgress.setProgress(newVolumeProgress);
    }


    @Override
    protected void hideChangeVolume() {
        mChangeVolume.setVisibility(View.GONE);
    }

    @Override
    protected void showChangeBrightness(int newBrightnessProgress) {
        mChangeBrightness.setVisibility(View.VISIBLE);
        mChangeBrightnessProgress.setProgress(newBrightnessProgress);
    }

    @Override
    protected void hideChangeBrightness() {
        mChangeBrightness.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        if (v == mCenterStart) {
            if (mNiceVidePlayer.isIdle()) {
                mNiceVidePlayer.start();
            }
        } else if (v == mBack) {
            if (mNiceVidePlayer.isFullScreen()) {
                mNiceVidePlayer.exitFullScreen();
            } else if (mNiceVidePlayer.isTinyWindow()) {
                mNiceVidePlayer.exitTinyWindow();
            }
        } else if (v == mRestartPause) {
            //播放变暂停，暂停变播放
            if (mNiceVidePlayer.isPlaying() || mNiceVidePlayer.isBufferingPlaying()) {
                mNiceVidePlayer.pause();
            } else if (mNiceVidePlayer.isPaused() || mNiceVidePlayer.isBufferingPaused()) {
                mNiceVidePlayer.restart();
            }
        } else if (v == mFullScreen) {
            if (mNiceVidePlayer.isNormal() || mNiceVidePlayer.isTinyWindow()) {
                mNiceVidePlayer.enterFullScreen();
            } else if (mNiceVidePlayer.isFullScreen()) {
                mNiceVidePlayer.exitFullScreen();
            }
        } else if (v == mRetry) {
            mNiceVidePlayer.restart();
        } else if (v == mReplay) {
            mRetry.performClick();
        } else if (v == mShare) {
            Toast.makeText(mContext, "分享", Toast.LENGTH_SHORT).show();
        } else if (v == this) { // 点击屏幕时
            if (mNiceVidePlayer.isPlaying() || mNiceVidePlayer.isPaused() || mNiceVidePlayer.isBufferingPlaying() || mNiceVidePlayer.isBufferingPaused()) {
                setTopBottomVisible(!topBottomVisible);
            }

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mNiceVidePlayer.isBufferingPaused() || mNiceVidePlayer.isPaused()) {
            mNiceVidePlayer.restart();
        }
        long position = (long) (mNiceVidePlayer.getDuration() * seekBar.getProgress() / 100f);
        mNiceVidePlayer.seekTo(position);
        startDismissTopBottomTimer();

    }

    //电池状态变化广播接收器
    private BroadcastReceiver mBatterReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
            if (status == BatteryManager.BATTERY_STATUS_CHARGING) {
                //充电中
                mBattery.setImageResource(R.drawable.battery_charging);
            } else if (status == BatteryManager.BATTERY_STATUS_FULL) {
                //充电完成
                mBattery.setImageResource(R.drawable.battery_full);
            } else {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
                int percentage = (level / scale) * 100;
                if (percentage <= 10) {
                    mBattery.setImageResource(R.drawable.battery_10);
                } else if (percentage <= 20) {
                    mBattery.setImageResource(R.drawable.battery_20);
                } else if (percentage <= 50) {
                    mBattery.setImageResource(R.drawable.battery_50);
                } else if (percentage <= 80) {
                    mBattery.setImageResource(R.drawable.battery_80);
                } else if (percentage <= 100) {
                    mBattery.setImageResource(R.drawable.battery_100);
                }
            }
        }
    };
}
