package com.xiuxiuchat.server;

import android.net.Uri;
import android.os.Message;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.utils.Md5Util;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by huzhi on 16-6-17.
 */
public class UpdateActiveUserManager {

    private static String TAG = UpdateActiveUserManager.class.getSimpleName();

    private static UpdateActiveUserManager mInstance;

    private UpdateActiveUserManager(){}

    public static UpdateActiveUserManager getInstance(){
        if(mInstance == null){
            mInstance = new UpdateActiveUserManager();
        }
        return mInstance;
    }

    private Timer timer;

    private TimerTask task;

    public void start(){
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                updataActiveUser();
                Message message = new Message();
                message.what = 1;
                XiuxiuApplication.getInstance().getUIHandler().sendMessage(message);
            }
        };
        timer.schedule(task,0, 5*60*1000);//五分钟更新一次
    }

    public void stop(){
        if(timer!=null) {
            timer.cancel();
        }
    }

    // ===============================================================================================
    // 更新活跃用户
    // ===============================================================================================

    private Response.Listener<String> mSuccessListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            android.util.Log.d(TAG, "更新活跃用户 response = " + response);
        }
    };
    private Response.ErrorListener mErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };

    private void updataActiveUser(){
        android.util.Log.d(TAG,"getUrl() = " + getUrl());
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getUrl(), mSuccessListener, mErroListener));
    }

    private String getUrl(){
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.UPDATE_ACTIVE_USER)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .build().toString();
        return url;
    }
}
