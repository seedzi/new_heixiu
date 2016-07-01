package com.xiuxiu.utils;

import android.content.Context;
import android.net.Uri;
import android.os.Process;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HeixiuApi;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuTimsResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.api.XiuxiuUserQueryResult;
import com.xiuxiu.call.CallManager;
import com.xiuxiu.server.UpdateActiveUserManager;

/**
 * Created by huzhi on 16-6-18.
 */
public class XiuxiuUtils {

    private static String TAG = XiuxiuUtils.class.getSimpleName();

    public static void onAppStart(Context context) {
        doSomthingOnAppStart(context);
        doSomthingOnAppStartInBackground(context);
    }



    private static void doSomthingOnAppStart(final Context context) {
        //1.获取用户信息
        queryUserInfo();
        //1.更新活跃用户
        UpdateActiveUserManager.getInstance().start();
        //2.获取咻咻招呼次数
        getXXTimes();
        //3.初始化CallManager
        CallManager.getInstance(context);
    }

    private static void doSomthingOnAppStartInBackground(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            }
        }).start();
    }


    // ============================================================================================
    // 获取用户信息
    // ============================================================================================
    private static void queryUserInfo() {
        Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                android.util.Log.d(TAG,"user = " + response);
                XiuxiuUserQueryResult res = gson.fromJson(response, XiuxiuUserQueryResult.class);
                if(res!=null && res.getUserinfos()!=null && res.getUserinfos().size()>0){
                    XiuxiuUserInfoResult.save(res.getUserinfos().get(0));
                }
            }
        };
        Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                android.util.Log.d(TAG,"error = " + error);
            }
        };
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getQueryUserInfoUrl(), mRefreshListener, mRefreshErroListener));
    }
    private static String getQueryUserInfoUrl() {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_USER_INFO)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "url = " + url);
        return url;
    }

    // ============================================================================================
    // 获取咻咻招呼次
    // ============================================================================================
    private static int getXXTimes(){
        Response.Listener<String> mRefreshXXListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                XiuxiuTimsResult res = gson.fromJson(response, XiuxiuTimsResult.class);
                XiuxiuTimsResult.save(res);
            }
        };
        Response.ErrorListener mRefreshXXErroListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                android.util.Log.d(TAG,"error = " + error);
            }
        };
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getXXTimesUrl(), mRefreshXXListener, mRefreshXXErroListener));
        return 0;
    }

    private static String getXXTimesUrl(){
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.GET_XX_TIMES)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("limitType","call")
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        return url;
    }
}
