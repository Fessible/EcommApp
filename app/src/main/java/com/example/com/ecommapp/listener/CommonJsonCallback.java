package com.example.com.ecommapp.listener;

import android.os.Looper;

import java.io.IOException;

import android.os.Handler;

import com.example.com.ecommapp.ResponseEntityToModule;
import com.example.com.ecommapp.exception.OkHttpException;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Callback;
import okhttp3.Response;

/**
 * 专门处理Json的回调响应
 * Created by rhm on 2017/11/7.
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

    private Handler mDeliveryHandler;//进行消息的转发，将子线程的数据转发到UI线程
    private DisposeDataListener mListner;//回调
    private Class<?> mClass;//字节码

    public CommonJsonCallback(DisposeDataHandle handle) {
        this.mListner = handle.mListener;
        this.mClass = handle.mClass;
        //Looper.getMainLooper()表示Handler放到主线程中去执行
        this.mDeliveryHandler = new Handler(Looper.getMainLooper());
    }

    //请求失败处理
    @Override
    public void onFailure(okhttp3.Call call, final IOException e) {
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                mListner.onFailure(new OkHttpException(NETWORK_ERROR, e));
            }
        });

    }

    //请求成功处理,真正的响应处理
    @Override
    public void onResponse(okhttp3.Call call, Response response) throws IOException {
        final String result = response.body().string();
        //转发到主线程中
        mDeliveryHandler.post(new Runnable() {
            @Override
            public void run() {
                handlerResponse(result);
            }
        });


    }

    //处理响应数据
    private void handlerResponse(Object responseObj) {
        if (responseObj == null && responseObj.toString().trim().equals("")) {

            mListner.onFailure(new OkHttpException(NETWORK_ERROR, EMPTY_MSG));
            return;
        }
        try {
            //得到Json实体
            JSONObject result = new JSONObject(responseObj.toString());

            if (result.has(RESULT_CODE)) {
                //从json对象中取出我们的响应码，若为0则正确
                if (result.getInt(RESULT_CODE) == RESULT_CODE_VALUE) {
                    //说明我们不需要将我们的json对象转化为实体对象
                    if (mClass == null) {
                        mListner.onSuccess(responseObj);
                    } else {
                        //需要我们将json转化为实体对象
                        Object object = ResponseEntityToModule.parseJsonObjectToModule(result, mClass);
                        //说明正确转化为了实体对象
                        if (object != null) {
                            mListner.onSuccess(object);
                        } else {
                            mListner.onFailure(new OkHttpException(JSON_ERROR, EMPTY_MSG));
                        }
                    }
                }
            } else {//将服务器返回的异常回调到应用处理
                mListner.onFailure(new OkHttpException(OTHER_ERROR, result.get(RESULT_CODE)));
            }
        } catch (JSONException e) {
            mListner.onFailure(new OkHttpException(OTHER_ERROR, e.getMessage()));
        }
    }
}
