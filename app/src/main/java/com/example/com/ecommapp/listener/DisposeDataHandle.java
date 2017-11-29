package com.example.com.ecommapp.listener;

/**
 * Created by rhm on 2017/11/7.
 */

public class DisposeDataHandle {

    //转化的字节码
    public Class<?> mClass = null;
    public DisposeDataListener mListener = null;

    public DisposeDataHandle(DisposeDataListener listener) {
        this.mListener = listener;
    }

    public DisposeDataHandle(DisposeDataListener listener, Class<?> clazz) {
        this.mListener = listener;
        this.mClass = clazz;
    }
}
