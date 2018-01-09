package com.example.com.ecommapp.share;

import android.content.Context;

import com.mob.commons.SHARESDK;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by rhm on 2018/1/8.
 */

public class ShareManager {
    private static ShareManager manager = null;

    /**
     * 要分享的平台
     */
    private Platform currentPlatform;

    private ShareManager() {
    }

    public static ShareManager getManager() {
        if (manager == null) {
            synchronized (ShareManager.class) {
                if (manager == null) {
                    manager = new ShareManager();
                }
            }
        }
        return manager;
    }

    /**
     * 分享数据到不同平台
     */
    public void shareData(ShareData shareData, PlatformActionListener listener) {
        switch (shareData.mPlatformType) {
            case QQ:
                currentPlatform = ShareSDK.getPlatform(QQ.NAME);
                break;
            case Qzone:
                currentPlatform = ShareSDK.getPlatform(QZone.NAME);
                break;
            case Weibo:
                currentPlatform = ShareSDK.getPlatform(SinaWeibo.NAME);
                break;
            case WeChat:
                currentPlatform = ShareSDK.getPlatform(Wechat.NAME);
                break;
            case Comment:
                currentPlatform = ShareSDK.getPlatform(WechatMoments.NAME);
                break;
        }
        currentPlatform.setPlatformActionListener(listener);
        currentPlatform.share(shareData.mShareParams);

    }

    public enum PlatofrmType {
        WeChat, Weibo, QQ, Qzone, Comment
    }


}
