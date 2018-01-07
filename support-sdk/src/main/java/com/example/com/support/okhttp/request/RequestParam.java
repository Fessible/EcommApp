package com.example.com.support.okhttp.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设置请求参数
 * Created by rhm on 2017/12/4.
 */

public class RequestParam {
    //普通参数
    public ConcurrentHashMap<String, String> urlParam = new ConcurrentHashMap<>();
    //文件参数
    public ConcurrentHashMap<String, Object> fileParam = new ConcurrentHashMap<>();

    public RequestParam() {
        this(null);
    }

    public RequestParam(String key, Object value) {
        put(key, value);
    }

    public RequestParam(Map<String, Object> source) {
        if (source != null) {
            for (Map.Entry<String, Object> entry : source.entrySet()) {
                //设置
                put(entry.getKey(), entry.getValue());
            }
        }

    }

    /**
     * 设置普通参数
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        if (key != null && value != null) {
            urlParam.put(key, value);
        }

    }

    /**
     * 设置文件参数
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        if (key != null) {
            fileParam.put(key, value);
        }
    }


}
