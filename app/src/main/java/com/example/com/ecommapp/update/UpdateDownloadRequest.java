package com.example.com.ecommapp.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.com.ecommapp.fragment.mine.LoginFragment;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;

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
        try {
            makeRequest();//建立连接
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //真正建立连接的方法
    private void makeRequest() {
        //当前线程未被打断，即当前线程在后台正确的运行
        if (!Thread.currentThread().isInterrupted()) {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.connect();//阻塞我们当前线程
                currentLength = connection.getContentLength();
                if (!Thread.currentThread().isInterrupted()) {
                    //真正的完成文件下载
                    downloadHandler.sendResponseMessage(connection.getInputStream());
                }


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


    }

    private String getTwoPointFloatStr(float value) {
        DecimalFormat fmat = new DecimalFormat("0.00");
        return fmat.format(value);
    }

    public enum FailureCode {
        UnknowHost, Socket, SocketTimeout, ConnectionTimeout, IO, HttpResponse, JSON, Interrupted
    }


    //真正的下载文件，发送消息 回调接口
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
                    progressChangedMessage(((Integer) response[0]).intValue());
                    break;
                case FINISH_MESSAGE:
                    onFinish();
                    break;
            }

        }

        //下载成功接口回调
        public void onFinish() {
            downloadListener.onFinished(completeSize, "");
        }

        //下载失败
        public void onFailure(FailureCode failureCode) {
            downloadListener.onFailure();
        }

        //文件下载
        public void sendResponseMessage(InputStream inputStream) {
            RandomAccessFile randomAccessFile = null;
            completeSize = 0;
            try {
                byte[] buffer = new byte[1024];
                int length = -1;
                int limit = 0;
                randomAccessFile = new RandomAccessFile(localFilePath, "rwd");
                while ((length = inputStream.read(buffer)) != -1) {
                    if (isDownloading) {
                        randomAccessFile.write(buffer, 0, length);
                        completeSize += length;
                        if (completeSize < currentLength) {
                            progress = (int) Float.parseFloat(getTwoPointFloatStr(completeSize / currentLength));
                            //限制notification的更新频率
                            if (limit / 30 == 0 || progress <= 100) {
                                progressChangedMessage(progress);
                            }
                            limit++;
                        }

                    }
                }
                sendFinishMessage();

            } catch (Exception e) {
                sendFailureMessage(FailureCode.IO);
            } finally {
                try {
                    if (inputStream != null) {

                        inputStream.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    sendFailureMessage(FailureCode.IO);

                }
            }

        }


        public void progressChangedMessage(int value) {
            downloadListener.onProgressChanged(progress, downloadUrl);
        }

        private void sendFailureMessage(Object o) {

        }

        private void sendFinishMessage() {
        }


    }
}
