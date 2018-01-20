package com.example.com.ecommapp.jpush;

import java.io.Serializable;

/**
 * 极光推送消息实体，包含所有的数据字段
 * Created by rhm on 2018/1/20.
 */

public class PushMessage implements Serializable {
    //消息类型，
    public String messageType = null;
    //连接，要打开的url地址
    public String  messageUrl = null;
    //详情内容
    public String messageContent=null;

}
