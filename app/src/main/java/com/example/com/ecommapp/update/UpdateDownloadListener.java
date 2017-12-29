package com.example.com.ecommapp.update;

/**
 * 事件监听回调
 * Created by rhm on 2017/12/28.
 */

public interface UpdateDownloadListener {

    /**
     * 开始下载
     */
    public void onStart();

    /**
     * 进度更新
     */
    public void onProgressChanged(int processs, String downloadUrl);

    /**
     * 下载完成
     */
    public void onFinished(float completeSize, String downloadUrl);

    /**
     * 下载失败
     */
    public void onFailure();


}
