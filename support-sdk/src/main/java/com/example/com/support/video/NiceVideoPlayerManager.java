package com.example.com.support.video;

/**
 * 视频播放管理器
 * Created by rhm on 2018/1/16.
 */

public class NiceVideoPlayerManager {
    private NiceVideoPlayer mVideoPlayer;

    private NiceVideoPlayerManager() {
    }

    //单例模式
    private static NiceVideoPlayerManager sInstance;

    public static synchronized NiceVideoPlayerManager getsInstance() {
        if (sInstance == null) {
            sInstance = new NiceVideoPlayerManager();
        }
        return sInstance;
    }

    public NiceVideoPlayer getCurrentNiceVideoPlayer() {
        return mVideoPlayer;
    }

    public void setCurrentNiceVideoPlayer(NiceVideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            releaseNiceVideoPlayer();
            mVideoPlayer = videoPlayer;
        }
    }

    public void suspendNiceVideoPlayer() {
        if (mVideoPlayer != null && (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying())) {
            mVideoPlayer.pause();
        }
    }


    public void resumeNiceVideoPlayer() {
        //手动暂停或者缓存暂停时 恢复播放
        if (mVideoPlayer != null && (mVideoPlayer.isPaused() || mVideoPlayer.isBufferingPaused())) {
            mVideoPlayer.restart();
        }
    }

    public void releaseNiceVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    public boolean onBackPressed() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isFullScreen()) {
                return mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isTinyWindow()) {
                return mVideoPlayer.exitTinyWindow();
            }
        }
        return false;
    }
}
