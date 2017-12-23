package com.example.com.support;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;

import com.example.com.support.util.SDKConstant;

/**
 * 业务逻辑层
 * Created by rhm on 2017/12/21.
 */

public class VideoSlot implements CustomVideoView.VideoPlayerListener {
    private Context context;
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;
    private String path;

    public VideoSlot(Context context,ViewGroup viewGroup, String path) {
        mParentView = viewGroup;
        this.context = context;
        this.path = path;
        initVideoView();
    }

    private void initVideoView() {
        mVideoView = new CustomVideoView(context, mParentView);
        mVideoView.setDataSource(path);
        mVideoView.setVideoViewListener(this);
        mParentView.addView(mVideoView);
    }


    /**
     * 当前位置的毫秒
     *
     * @return
     */
    public int getCurrPosition() {
        return mVideoView.getCurrentPosition() / SDKConstant.MILLION_UNIT;
    }

    /**
     * 总时间的毫秒数
     *
     * @return
     */
    public int getDuration() {
        return mVideoView.getDuration() / SDKConstant.MILLION_UNIT;
    }

    public void destroy() {
        mVideoView.destroy();
        mVideoView = null;
        context = null;
    }

    public boolean isPlaying() {
        return mVideoView.isPlaying();
    }

    @Override
    public void onBufferUpdate(int time) {

    }

    @Override
    public void onClickVideoView() {

    }

    @Override
    public void onClickFullButton() {
        //将播放器从当前view树中移除
        mParentView.removeView(mVideoView);
        FullVideoDialog dialog = new FullVideoDialog(context, mVideoView);
        dialog.setListener(new FullVideoDialog.FullToSmallListener() {
            @Override
            public void getCurrentPosition(int position) {
                //全屏返回到小屏幕
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {
                mParentView.addView(mVideoView);
                mVideoView.playBack();
            }
        });

        //显示出来
        dialog.show();

    }

    //全屏返回到小屏幕
    private void backToSmallMode(int position) {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        //显示全屏按钮
        mVideoView.setFullButton(true);
        //小屏幕静音
        mVideoView.mute(true);
        mVideoView.setVideoViewListener(this);
        mVideoView.seekAndResume(position);
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

    }

    public interface VideoSlotListener {
        public ViewGroup getVideoGroup();

        public void onVideoLoadSuccess();

        public void onVideoLoadFailed();

        public void onVideoLoadComplete();

        public void onClickVideo(String url);
    }
}
