package com.example.com.ecommapp.network.http;

import android.app.DownloadManager;

import com.example.com.ecommapp.module.BaseModel;
import com.example.com.ecommapp.module.LoginModel;
import com.example.com.ecommapp.module.recommand.RecommendModel;
import com.example.com.ecommapp.module.version.VersionModel;
import com.example.com.support.okhttp.CommOkhttpClient;
import com.example.com.support.okhttp.listener.DisposableHandler;
import com.example.com.support.okhttp.listener.DisposeListener;
import com.example.com.support.okhttp.request.CommonRequest;
import com.example.com.support.okhttp.request.RequestParam;

import java.net.HttpURLConnection;

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

    //获取验证码
    public static void recodeRequest(String phone, DisposeListener listener) {
//        RequestParam requestParam = new RequestParam();
//        requestParam.put("phone", phone);
        Request request = CommonRequest.getInstance().createGet(HttpConstants.SMSCODE+"/"+phone);
        CommOkhttpClient.getInstance().sendRequest(request, new DisposableHandler(BaseModel.class, listener));
    }

    //登录
    public static void loginRequest(String phone, String password, DisposeListener listener) {
        RequestParam requestParam = new RequestParam();
        requestParam.put("phone", phone);
        requestParam.put("password", password);
        Request request = CommonRequest.getInstance().createPost(HttpConstants.LOGIN, requestParam);
        CommOkhttpClient.getInstance().sendRequest(request, new DisposableHandler(LoginModel.class, listener));
    }

    //重置密码
    public static void passwordRequest(String phone, String smsCode, String password, String newPassword, DisposeListener listener) {
        RequestParam requestParam = new RequestParam();
        requestParam.put("phone", phone);
        requestParam.put("passwrod", password);
        requestParam.put("smsCode", smsCode);
        requestParam.put("newPassword", newPassword);
        Request request = CommonRequest.getInstance().createPost(HttpConstants.PASSWORD, requestParam);
        CommOkhttpClient.getInstance().sendRequest(request, new DisposableHandler(BaseModel.class, listener));
    }
}
