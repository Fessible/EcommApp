package com.example.com.ecommapp.network.http;

import com.example.com.ecommapp.listener.CommonJsonCallback;
import com.example.com.ecommapp.listener.DisposeDataHandle;
import com.example.com.ecommapp.listener.DisposeDataListener;
import com.example.com.ecommapp.network.okhttp.CommonOkHttpClient;
import com.example.com.ecommapp.network.okhttp.request.CommRequest;

/**
 * Created by rhm on 2017/12/3.
 */

public class HttpRequest {
    //首页请求
    public static void requestRecommendData(DisposeDataListener listener) {
        CommonOkHttpClient.sendRequest(CommRequest.createGetRequest(HttpConstants.HOME, null), new CommonJsonCallback(new DisposeDataHandle(listener)));
    }
}
