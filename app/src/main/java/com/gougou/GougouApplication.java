package com.gougou;

import android.app.Application;
import android.os.Handler;
import android.support.multidex.MultiDexApplication;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.gougou.im.ImManager;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;


/**
 * Created by huzhi on 16-4-7.
 */
public class GougouApplication extends MultiDexApplication{

    private RequestQueue mQueue;

    private static Handler mUiHandler;

    @Override
    public void onCreate() {
        super.onCreate();

        mUiHandler = new Handler();
        mQueue = Volley.newRequestQueue(this.getApplicationContext());
        ImManager.getInstance().init(getApplicationContext());
    }

    public static Handler getUIHandler(){
        return mUiHandler;
    }

}
