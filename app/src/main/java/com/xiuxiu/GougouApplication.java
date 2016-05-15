package com.xiuxiu;

import android.os.Handler;
import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.xiuxiu.im.ImManager;
import com.umeng.socialize.PlatformConfig;


/**
 * Created by huzhi on 16-4-7.
 */
public class GougouApplication extends MultiDexApplication{

    private RequestQueue mQueue;

    private static Handler mUiHandler;


    private static GougouApplication instance;

    public synchronized static GougouApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        mUiHandler = new Handler();
        mQueue = Volley.newRequestQueue(this.getApplicationContext());
        //Im初始化
        ImManager.getInstance().init(getApplicationContext());
        //初始化第三方登陆
        initThirdLogin();
        //init Volley
        mQueue = Volley.newRequestQueue(this.getApplicationContext());
    }

    private void initThirdLogin(){
        PlatformConfig.setWeixin("wxe684fcac0ecbcc99", "e968b57d7e3d5ad48f76ffb6c03dcbfb");
    }

    public static Handler getUIHandler(){
        return mUiHandler;
    }

    public RequestQueue getQueue() {
        return mQueue;
    }
}
