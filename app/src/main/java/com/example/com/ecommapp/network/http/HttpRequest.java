package com.example.com.ecommapp.network.http;

import android.app.DownloadManager;

import com.example.com.ecommapp.module.recommand.RecommendModel;
import com.example.com.ecommapp.module.version.VersionModel;
import com.example.com.support.okhttp.CommOkhttpClient;
import com.example.com.support.okhttp.listener.DisposableHandler;
import com.example.com.support.okhttp.listener.DisposeListener;
import com.example.com.support.okhttp.request.CommonRequest;

import okhttp3.Request;

/**
 * Created by rhm on 2017/12/3.
 */

public class HttpRequest {

    //请求首页
    public static void HomeRequest(DisposeListener listener) {
        Request request = CommonRequest.getInstance().createGet(HttpConstants.HOME);
        CommOkhttpClient.getInstance().sendRequest(request, new DisposableHandler(RecommendModel.class, listener));
    }

    //版本更新请求
    public static void versionRequest(DisposeListener listener) {
        Request request = CommonRequest.getInstance().createGet(HttpConstants.VERSION);
        CommOkhttpClient.getInstance().sendRequest(request, new DisposableHandler(VersionModel.class, listener));

    }
}
