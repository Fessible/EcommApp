package com.example.com.support.okhttp.listener;

/**
 * 包装了解析的字节码class 和 listener
 * Created by rhm on 2017/12/4.
 */

public class DisposableHandler {
    private DisposeListener mListener;
    private Class<?> mClass;

    public DisposableHandler(DisposeListener listener) {
        this.mListener = listener;
    }

    public DisposableHandler(Class<?> clazz,DisposeListener listener ) {
        this.mListener = listener;
        this.mClass = clazz;
    }

    public Class<?> getClazz() {
        return mClass;
    }

    public DisposeListener getListener() {
        return mListener;
    }
}
