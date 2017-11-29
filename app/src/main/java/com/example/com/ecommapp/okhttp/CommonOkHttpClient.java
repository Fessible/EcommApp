package com.example.com.ecommapp.okhttp;

import com.example.com.ecommapp.listener.CommonJsonCallback;
import com.example.com.ecommapp.okhttp.ssl.HttpsUtils;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 请求的发送，请求参数的配置，https支持
 * Created by rhm on 2017/11/7.
 */

public class CommonOkHttpClient {

    //超时参数
    private static final int TIME_OUT = 30;
    private static OkHttpClient mOkhttpClient;
    //配置参数
    static{
        //创建我们的client对象的构建者
        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        okhttpBuilder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        okhttpBuilder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        //允许重定向
        okhttpBuilder.followRedirects(true);

        //https支持 所有类型的证书
        okhttpBuilder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        //支持SSL
        okhttpBuilder.socketFactory(HttpsUtils.getSslSocketFactory());

        //生成我们的client
        mOkhttpClient = okhttpBuilder.build();

    }

    /**
     * 发送okhttp/okhttps请求
     * @param request
     * @param callback
     * @return
     */
    public static Call sendRequest(Request request, CommonJsonCallback callback) {
        Call call = mOkhttpClient.newCall(request);
        call.enqueue(callback);
        return  call;
    }
}
