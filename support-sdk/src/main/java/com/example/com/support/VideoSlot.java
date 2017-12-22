package com.example.com.support;

import android.content.Context;
import android.view.ViewGroup;

import com.example.com.support.module.Advalue;
import com.example.com.support.util.SDKConstant;

/**
 * 业务逻辑层
 * Created by rhm on 2017/12/21.
 */

public class VideoSlot implements CustomVideoView.VideoPlayerListener {
    private Context context;
    private CustomVideoView mVideoView;
    private ViewGroup mParentView;
    private Advalue advalue;

    /**
     * 当前位置的毫秒
     * @return
     */
    public int getCurrPosition() {
        return mVideoView.getCurrentPosition() / SDKConstant.MILLION_UNIT;
    }

    /**
     * 总时间的毫秒数
     * @return
     */
    public int getDuration() {
        return mVideoView.getDuration()/SDKConstant.MILLION_UNIT;
    }

    public void destroy() {
        mVideoView.destroy();
        mVideoView = null;
        context = null;
    }

    public boolean isPlaying(){
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
        FullVideoDialog dialog = new FullVideoDialog(context, mVideoView, advalue);
        dialog.setListener(new FullVideoDialog.FullToSmallListener() {
            @Override
            public void getCurrentPosition(int position) {
            //全屏返回到小屏幕
                backToSmallMode(position);
            }

            @Override
            public void playComplete() {

            }
        });

        //显示出来
        dialog.show();


    }

    private void backToSmallMode(int position) {
        if (mVideoView.getParent() == null) {
            mParentView.addView(mVideoView);
        }
        //显示全屏按钮
        //小屏幕静音
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
}
