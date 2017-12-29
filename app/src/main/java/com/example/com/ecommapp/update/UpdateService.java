package com.example.com.ecommapp.update;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * app更新下载后台
 * Created by rhm on 2017/12/28.
 */

public class UpdateService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 真正的下载文件，发送消息回调接口
     */
    public class DownloadResponseHandler {
    }
}
