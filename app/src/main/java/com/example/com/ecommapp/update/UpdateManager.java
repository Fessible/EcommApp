package com.example.com.ecommapp.update;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 下载管理器，调用UpdateDownloadRequest
 * Created by rhm on 2017/12/28.
 */

public class UpdateManager {
    private static UpdateManager manager;
    private ThreadPoolExecutor threadPoolExecutor;//线程池
    private UpdateDownloadRequest request;

    private UpdateManager() {
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
    }

    static {
        manager = new UpdateManager();
    }

    public static UpdateManager getInstance() {
        return manager;
    }

    public void startDownloads(String downloadUrl, String localPath, UpdateDownloadListener listener) {
        if (request != null) {
            return;
        }
        chekLocalFilePaht(localPath);//检测文件路径
        request = new UpdateDownloadRequest(downloadUrl, localPath, listener);
        Future<?> future = threadPoolExecutor.submit(request);

    }

    //检查文件路径是否已经存在
    private void chekLocalFilePaht(String path) {
        File dir = new File(path.substring(0, path.lastIndexOf("/") + 1));
        //如果文件不存在
        if (!dir.exists()) {
            dir.mkdir();
        }

        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
