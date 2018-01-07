package com.example.com.ecommapp.network.http;

import android.sax.RootElement;

/**
 * 所有请求的url地址
 * Created by rhm on 2017/11/30.
 */

public class HttpConstants {
    private final static String ROOT_URL = "http://rap.taobao.org/mockjs/29850/ecom";
    /**
     * 首页请求接口
     */
    public final static String HOME = ROOT_URL + "/home";

    /**
     * 版本更新
     */
    public final static String VERSION = ROOT_URL + "/version";

    /**
     * 用户登录
     */
    public final  static String LOGIN = ROOT_URL+"/login";

    /**
     * 获取验证码
     */
    public final static String SMSCODE = ROOT_URL + "/recode";

    public final static String PASSWORD = ROOT_URL + "/password";

}
