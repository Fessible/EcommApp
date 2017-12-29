package com.example.com.ecommapp.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.com.ecommapp.fragment.mine.LoginFragment;

/**
 * 负责处理文件的下载和线程通信
 * Created by rhm on 2017/12/28.
 */

public class UpdateDownloadRequest implements Runnable {
    private String downloadUrl;//下载地址
    private String localFilePath;//文件存储路径

    private UpdateDownloadListener downloadListener;
    private boolean isDownloading = false;
    private long currentLength;//当前下载长度

    private DownloadHandler downloadHandler;

    public UpdateDownloadRequest(String downloadUrl, String localFilePath, UpdateDownloadListener listener) {
        this.downloadListener = listener;
        this.downloadUrl = downloadUrl;
        this.localFilePath = localFilePath;
        this.isDownloading = true;
        downloadHandler = new DownloadHandler();
    }


    @Override
    public void run() {

    }

    public class DownloadHandler {
        public static final int SUCCESS_MESSAGE = 0;
        public static final int FAILURE_MESSAGE = 1;
        public static final int START_MESSAGE = 2;
        public static final int FINISH_MESSAGE = 3;
        public static final int NETWORK_OFF = 4;
        public static final int PROCESS_CHANGED = 5;

        private float completeSize = 0;
        private int progress = 0;

        private Handler handler;

        public DownloadHandler() {
            handler = new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    handleSelfMessage(msg);
                }
            };
        }

        private void handleSelfMessage(Message msg) {
            Object[] response;
            switch (msg.what) {
                case FAILURE_MESSAGE:
                    response = (Object[]) msg.obj;
                    sendFailureMessage(response[0]);
                    break;
                case PROCESS_CHANGED:
                    response = (Object[]) msg.obj;
                    handleProgressChangedMessage(((Integer) response[0]).intValue());
                    break;
                case FINISH_MESSAGE:
                    onFinish();
                    break;
            }

        }

        private void onFinish() {
            downloadListener.onFinished(completeSize, "");
        }

        private void handleProgressChangedMessage(int value) {
            downloadListener.onProgressChanged(progress, downloadUrl);
        }

        private void sendFailureMessage(Object o) {

        }

        private void sendFinishMessage() {
        }

    }
}
