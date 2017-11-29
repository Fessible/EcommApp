package com.example.com.ecommapp.listener;

/**
 * 自定义监听
 * Created by rhm on 2017/11/7.
 */

public interface DisposeDataListener {
    /**
     * 请求成功回调事件处理
     * @param responseObj
     */
    public void onSuccess(Object responseObj);

    /**
     * 请求失败回调事件处理
     * @param responObj
     */
    public void onFailure(Object responObj);
}
