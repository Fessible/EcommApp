package com.example.com.ecommapp.network.okhttp.request;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;

/**
 * 接受请求参数，返回Request对象
 * Created by rhm on 2017/11/7.
 */

public class CommRequest {

    /**
     * 通过传入参数返回一个Post类型的请求
     * @param url
     * @param params
     * @return
     */
    public static  Request createPostRequest(String url, RequestParams params) {
        FormBody.Builder formBodyBuilder = new FormBody.Builder();
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                formBodyBuilder.add(entry.getKey(), entry.getValue());
            }
        }
        FormBody formBody = formBodyBuilder.build();
        return new Request.Builder().url(url).post(formBody).build();
    }

    /**
     * 通过传入参数返回一个Get类型的请求
     * @param url
     * @param params
     * @return
     */
    public static Request createGetRequest(String url, RequestParams params) {
        StringBuilder stringBuilder = new StringBuilder(url).append("?");
        if (params != null) {
            for (Map.Entry<String, String> entry : params.urlParams.entrySet()) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        return new Request.Builder().url(stringBuilder.substring(0, stringBuilder.length() - 1)).get().build();
    }
}
