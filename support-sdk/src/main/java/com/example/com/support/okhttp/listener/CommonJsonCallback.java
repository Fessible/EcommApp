package com.example.com.support.okhttp.listener;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.com.support.okhttp.exception.HttpException;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by rhm on 2017/12/4.
 * 将Callbac执行的内容切换到主线程
 */

public class CommonJsonCallback implements Callback {

    //与服务器返回的字段的一个对应关系
    //有返回则对于http请求来说是成功的
    protected final String RESULT_CODE = "ecode";
    protected final int RESULT_CODE_VALUE = 0;
    protected final String ERROR_MSG = "emsg";
    protected final String EMPTY_MSG = "";

    /**
     * 自定义异常类型
     */
    protected final int NETWORK_ERROR = -1;//与网络相关的错误
    protected final int JSON_ERROR = -2;//与JSON相关的错误
    protected final int OTHER_ERROR = -3;//其他类型的错误

    private DisposeListener mListerner;
    private DisposableHandler mDisHandler;
    private Handler mDeliveryHandler;
    private Class<?> mClass;

    public CommonJsonCallback(DisposableHandler handler) {
        this.mDisHandler = handler;
        this.mListerner = handler.getListener();
        this.mClass = handler.getClazz();
        //切换到主线程，写接口listener的目的
        mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(Call call, final IOException e) {
        mDeliveryHandler.post(new Runnable() {//切换主线程
            @Override
            public void run() {
                mListerner.onFailure(new HttpException(NETWORK_ERROR, e).getMessage());
            }
        });

    }

    @Override
    public void onResponse(Call call, final Response response) throws IOException {
        final String res = response.body().string();
        Log.d("http", res);
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handlerResponse(res);
            }
        });
    }

    private void handlerResponse(String response) {
        if (response != null) {
            if (mClass != null) {
                Gson gson = new Gson();
                mListerner.onSuccess(gson.fromJson(response, mClass));
            }
        } else {
            mListerner.onFailure(new HttpException(JSON_ERROR, EMPTY_MSG).getMessage());
        }

    }
}
