package com.example.com.support.video;

import java.util.Map;

/**
 * 视频播放抽象接口
 * Created by rhm on 2018/1/15.
 */

public interface INiceVideoPlayer {
    /**
     * 设置视频的url，以及headers
     *
     * @param url     视频地址，可以是本地，也可以是网络
     * @param headers 请求header
     */
    void setUp(String url, Map<String, String> headers);

    /**
     * 开始播放
     */
    void start();

    /**
     * 从指定位置开始播放
     *
     * @param position 播放位置
     */
    void start(long position);

    /**
     * 重新播放，播放器被暂停，播放错误，播放完成后需要调用这个方法重新播放
     */
    void restart();

    /**
     * 暂停播放
     */
    void pause();

    /**
     * seek到指定的位置继续播放
     *
     * @param pos
     */
    void seekTo(long pos);

    /**
     * IjkPlayer有效果，原生Mediaplayer不支持
     * 播放速度
     *
     * @param speed
     */
    void setSpeed(float speed);

    /**
     * 开始播放时，是否重上一次的位置继续播放
     * @param continueFromLastPosition
     */
    void continueFromLastPostion(boolean continueFromLastPosition);

    /******************************
     * 以下方法时播放器当前的播放状态
     ******************************/
    boolean isIdle();//是否空闲
    boolean isPreparing();
    boolean isPrepared();
    boolean isBufferingPlaying();
    boolean isBufferingPaused();
    boolean isPlaying();
    boolean isPaused();
    boolean isError();
    boolean isCompleted();

    /*************************
     * 播放器模式
     *************************/
    boolean isFullScreen();
    boolean isTinyWindow();
    boolean isNormal();

    /**
     * 设置音量
     * @param volume 音量值
     */
    void setVolume(int volume);

    /**
     * 获取最大音量
     * @return
     */
    int getMaxVolume();

    /**
     * 获取当前的音量
     * @return
     */
    int getVolume();

    /**
     * 获取总时长
     * @return
     */
    long getDuration();

    /**
     * 获取当前的播放位置
     * @return
     */
    long getCurrentPostion();

    /**
     * 获取视频缓冲百分比
     * @return
     */
    int getBufferPercentage();

    /**
     * 获取播放速度
     * @param speed
     * @return
     */
    float getSpeed(float speed);

    /**
     * 获取网络加载速度
     * @return
     */
    long getTcpSpeeed();

    /**
     * 进入全屏模式
     */
    void enterFullScreen();

    /**
     * 退出全屏模式
     * @return
     */
    boolean exitFullScreen();

    /**
     * 进入小窗口模式
     */
    void enterTinyWindow();

    /**
     * 退出小窗口模式
     * @return
     */
    boolean exitTinyWindow();

    void releasePlayer();

    /**
     * 释放videoPlayer
     */
    void  release();
}
