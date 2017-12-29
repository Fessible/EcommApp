package com.example.com.support.download;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

/**
 * Created by rhm on 2017/12/29.
 */

public class DownloadManagerCompact {
    public static final long INVALID_DOWNLOAD_ID = -1;
    public final static int STATUS_PENDING = DownloadManager.STATUS_PENDING;//下载延迟
    public final static int STATUS_RUNNING = DownloadManager.STATUS_RUNNING;//正在下载
    public final static int STATUS_PAUSED = DownloadManager.STATUS_PAUSED;
    public final static int STATUS_SUCCESSFUL = DownloadManager.STATUS_SUCCESSFUL;
    public final static int STATUS_FAILED = DownloadManager.STATUS_FAILED;

    private DownloadManager downloadManager;

    private DownloadManagerCompact(Context context) {
        downloadManager = (DownloadManager) context.getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public DownloadManager.Request newRequest(CharSequence title, CharSequence description, String url, Uri destinationUri) {
        DownloadManager.Request request = null;
        if (!TextUtils.isEmpty(url)) {
            request = new DownloadManager.Request(Uri.parse(url));
            if (!TextUtils.isEmpty(title)) {//设置标题
                request.setTitle(title);
            }

            if (!TextUtils.isEmpty(description)) { //描述
                request.setDescription(description);
            }

            //设置用于下载时的网络状态
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            //漫游状态下是否可以下
            request.setAllowedOverRoaming(false);
            //设置文件类型
            request.setMimeType(MimeTypeMap.getSingleton().getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url)));

        }

        return request;
    }


}
