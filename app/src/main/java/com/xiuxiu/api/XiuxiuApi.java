package com.xiuxiu.api;

import android.net.Uri;

import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.db.XiuxiuUserInfoTable;
import com.xiuxiu.easeim.EaseUserCacheManager;
import com.xiuxiu.utils.Md5Util;

/**
 * Created by huzhi on 16-5-4.
 */
public class XiuxiuApi {

    private static final String TAG = "XiuxiuApi";

    //==============================================================================================
    // 获取用户信息 并将信息存入缓存和数据库中
    //==============================================================================================
    public static void queryUserInfoSyn(String xiuxiuid){
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getQueryUserInfoUrl(xiuxiuid), future, future));
        try {
            String response = future.get();
            android.util.Log.d(TAG,"queryUserInfos() response = " + response);
            Gson gson = new Gson();
            XiuxiuUserQueryResult result = gson.fromJson(response, XiuxiuUserQueryResult.class);
            if(result!=null && result.getUserinfos()!=null){
                for(XiuxiuUserInfoResult info:result.getUserinfos()){
                    if(info!=null) {
                        EaseUserCacheManager.getInstance().add(info);
                    }
                }
            }
        } catch (Exception e) {
            android.util.Log.d(TAG,"queryUserInfos() e = " + e.getMessage());
            e.printStackTrace();
        }

    }
    private static String getQueryUserInfoUrl(String xiuxiuid) {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_USER_INFO)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", xiuxiuid)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "url = " + url);
        return url;
    }

}
