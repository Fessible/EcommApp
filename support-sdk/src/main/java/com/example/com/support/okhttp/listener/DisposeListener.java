package com.example.com.support.okhttp.listener;

/**
 * 接口实现成功和失败时候的回调,为了在内部实现线程切换至主线程
 * Created by rhm on 2017/12/4.
 */

public interface DisposeListener {
    void onSuccess(Object responseObj);

    void onFailure(String msg);
}
