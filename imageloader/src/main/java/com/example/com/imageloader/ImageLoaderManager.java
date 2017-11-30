package com.example.com.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * Created by rhm on 2017/11/30.
 * 使用单例模式，对ImageLoader进行配置
 */

public class ImageLoaderManager {

    private final static int THREAD_COUNT = 4;//线程数
    private final static int PROPRITY = 2;//优先级
    private final static int DISK_CACHE_SIZE = 50 * 1024;//硬盘缓存大小
    private final static int CONNECTION_TIME_OUT = 5 * 1000;//连接超时时间
    private final static int READ_TIME_OUT = 20 * 1000;//读取超时时间

    private static ImageLoaderManager mInstance = null;
    private ImageLoader mImageLoader;

    /**
     * 单例模式
     *
     * @return
     */
    public static ImageLoaderManager getInstatnce(Context context) {
        if (mInstance == null) {
            synchronized (ImageLoaderManager.class) {
                if (mInstance == null) {
                    mInstance = new ImageLoaderManager(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 进行参数配置
     *
     * @param context
     */
    private ImageLoaderManager(Context context) {
        ImageLoaderConfiguration configuration =
                new ImageLoaderConfiguration.Builder(context)
                        .diskCacheSize(DISK_CACHE_SIZE)//配置图片下载的最大线程
                        .diskCacheFileNameGenerator(new Md5FileNameGenerator())//使用MD5命名文件
                        .threadPoolSize(THREAD_COUNT)//线程池中的线程数
                        .threadPriority(Thread.NORM_PRIORITY - PROPRITY)
                        .memoryCache(new WeakMemoryCache())//使用弱引用内存缓存
                        .denyCacheImageMultipleSizesInMemory()//防止缓存多尺寸的图片到内存中
                        .tasksProcessingOrder(QueueProcessingType.FIFO)//图片下载顺序
                        .defaultDisplayImageOptions(getDefaultOptions())//默认图片加载options
                        .imageDownloader(new BaseImageDownloader(context, CONNECTION_TIME_OUT, READ_TIME_OUT))//设置图片下载器
                        .writeDebugLogs()//debug环境下输出日志
                        .build();
        ImageLoader.getInstance().init(configuration);
        mImageLoader = ImageLoader.getInstance();
    }

    /**
     * 图片操作设置
     *
     * @return
     */
    private DisplayImageOptions getDefaultOptions() {
        DisplayImageOptions options =
                new DisplayImageOptions.Builder()
                        .showImageForEmptyUri(R.drawable.error)//地址为空时显示
                        .showImageOnFail(R.drawable.error)//下载错误时显示
                        .cacheInMemory(true)//可缓存在内存中
                        .cacheOnDisk(true)//可缓存在硬盘中
                        .bitmapConfig(Bitmap.Config.RGB_565)//使用图片的解码类型
                        .decodingOptions(new BitmapFactory.Options())//图片解码配置
                        .build();
        return options;
    }

    /**
     * 显示图片
     *
     * @param imageView
     * @param url
     * @param options
     * @param listener
     */
    public void displayImage(ImageView imageView, String url, DisplayImageOptions options, ImageLoadingListener listener) {
        if (mImageLoader != null) {
            mImageLoader.displayImage(url, imageView, options, listener);
        }
    }

    public void displayImage(ImageView imageView, String url, ImageLoadingListener listener) {
        this.displayImage(imageView, url, null, listener);
    }

    public void displayImage(ImageView imageView, String url) {
        this.displayImage(imageView, url, null);
    }
}
