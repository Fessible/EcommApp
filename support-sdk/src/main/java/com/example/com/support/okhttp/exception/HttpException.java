package com.example.com.support.okhttp.exception;

/**
 * Created by rhm on 2017/12/4.
 */

public class HttpException extends Exception {
    private static final long serializeID = 1L;
    private int ecode;
    private Object emsg;

    public HttpException(int ecode, Object emsg) {
        this.ecode = ecode;
        this.emsg = emsg;
    }

    public int getEcode() {
        return ecode;
    }

    public Object getEmsg() {
        return emsg;
    }
}
