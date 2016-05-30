package com.xiuxiu;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.hyphenate.easeui.controller.EaseUI;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.xiuxiu.db.XiuxiuDatabaseHelper;
import com.xiuxiu.easeim.ImHelper;
import com.xiuxiu.easeim.ImManager;
import com.umeng.socialize.PlatformConfig;


/**
 * Created by huzhi on 16-4-7.
 */
public class XiuxiuApplication extends MultiDexApplication{

    private RequestQueue mQueue;

    private Handler mUiHandler;


    private static XiuxiuApplication instance;

    public synchronized static XiuxiuApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mUiHandler = new Handler();
        mQueue = Volley.newRequestQueue(this.getApplicationContext());
        //环信初始化
        ImHelper.getInstance().init(getApplicationContext());
        //初始化第三方登陆
        initThirdLogin();
        //init Volley
        mQueue = Volley.newRequestQueue(this.getApplicationContext());
        //初始化数据库
        XiuxiuDatabaseHelper.getInstance(getApplicationContext());
        // 初始化图片加载器
        initImageLoader(getApplicationContext());
    }

    private void initImageLoader(Context context) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .writeDebugLogs() // Remove for release app
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCacheExtraOptions(480, 1600)
                .build();
        ImageLoader.getInstance().init(config);
    }

    private void initThirdLogin(){
        PlatformConfig.setWeixin("wxe684fcac0ecbcc99", "e968b57d7e3d5ad48f76ffb6c03dcbfb");
    }

    public Handler getUIHandler(){
        return mUiHandler;
    }

    public RequestQueue getQueue() {
        return mQueue;
    }
}
