package com.example.com.support.okhttp.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * get和post的请求
 * Created by rhm on 2017/12/4.
 */

public class CommonRequest {

    private CommonRequest() {
    }

    private static CommonRequest instance;

    public static CommonRequest getInstance() {
        if (instance == null) {
            synchronized (CommonRequest.class) {
                if (instance == null) {
                    instance = new CommonRequest();
                }
            }
        }
        return instance;
    }

    /**
     * 无参数的get请求
     *
     * @param url 网址
     * @return request 请求
     */
    public Request createGet(String url) {
        return createGet(url, null);
    }

    /**
     * 带参数的get请求
     *
     * @param url 网址
     * @param param 参数
     * @return 请求
     */
    public Request createGet(String url, RequestParam param) {
        StringBuilder stringBuilder = new StringBuilder(url).append("?");
        if (param != null) {
            for (Map.Entry<String, String> entry : param.urlParam.entrySet()) {
                stringBuilder.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("&");
            }
        }
        return new Request.Builder().url(stringBuilder.substring(0, stringBuilder.length() - 1)).build();
    }

    public Request createPost(String url, RequestParam param) {
        FormBody.Builder formBuilder = new FormBody.Builder();
        if (param != null) {
            for (Map.Entry<String, String> entry : param.urlParam.entrySet()) {
                formBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody body = formBuilder.build();
        return new Request.Builder().url(url).post(body).build();
    }


}
