package com.xiuxiu.Xiuxiubroadcast;

import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuActiveUserResult;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuQueryActiveUserResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.api.XiuxiuUserQueryResult;
import com.xiuxiu.utils.Md5Util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by huzhi on 16-7-25.
 */
public class XiuxiuBroadcastManager {

    private static final String TAG = "XiuxiuBroadcastManager";

    private static XiuxiuBroadcastManager mInstance;

    public static XiuxiuBroadcastManager getInstance(){
        if(mInstance == null){
            mInstance = new XiuxiuBroadcastManager();
        }
        return mInstance;
    }

    private XiuxiuBroadcastManager(){}

    public void sendXiuxiuBroadcast(List<XiuxiuUserInfoResult> list,String txt){
        if(list==null){
            return;
        }
        for(XiuxiuUserInfoResult user:list){
            EMMessage message = EMMessage.createTxtSendMessage(txt, user.getXiuxiu_id());
            //发送消息
            EMClient.getInstance().chatManager().sendMessage(message);
        }

    }


    public List<XiuxiuUserInfoResult> getXiuxiuBroadcastUserList() {
        String params = getActiveXiuxiuIds();
        if(!TextUtils.isEmpty(params)){
            return queryUserList(params);
        }
        return null;
    }

    // ===============================================================================================
    // 获取活跃用户id
    // ===============================================================================================
    private String getActiveXiuxiuIds(){
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getActiveUserIdsUrl(), future, future));
        try {
            String response = future.get();
            Gson gson = new Gson();
            try {
                XiuxiuQueryActiveUserResult result = gson.fromJson(response, XiuxiuQueryActiveUserResult.class);
                android.util.Log.d(TAG, "onResponse result.activeUsers = " + result.activeUsers);
                if(result!=null && result.activeUsers!=null){
                    String params = "";
                    int position = 0;
                    for(XiuxiuActiveUserResult item:result.activeUsers){
                        if(position==0){
                            params = item.getXiuxiu_id();
                        }else{
                            params = params + "," + item.getXiuxiu_id();
                        }
                        position ++;
                    }
                    return params;
                }
            }catch (Exception e){}
        } catch (Exception e) {
            android.util.Log.d(TAG,"queryUserInfoList() e = " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String getActiveUserIdsUrl() {
        return Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_ACTIVE_USER)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("offset", "0")
                .appendQueryParameter("count", "10")
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("user_id",XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .build().toString();
    }



    // ===============================================================================================
    // 获取用户信息
    // ===============================================================================================
    private List<XiuxiuUserInfoResult> queryUserList(String xiuxiuIds){
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getQueryUserListUrl(xiuxiuIds), future, future));
        try {
            String response = future.get();
            Gson gson = new Gson();
            XiuxiuUserQueryResult xiuxiuUserInfoResult = gson.fromJson(response, XiuxiuUserQueryResult.class);
            if(xiuxiuUserInfoResult!=null && xiuxiuUserInfoResult.getUserinfos()!=null){
                Collections.sort(xiuxiuUserInfoResult.getUserinfos(), new Comparator<XiuxiuUserInfoResult>() {
                    public int compare(XiuxiuUserInfoResult arg0, XiuxiuUserInfoResult arg1) {
                        return (int) (arg1.getActive_time() - arg0.getActive_time());
                    }
                });
                return xiuxiuUserInfoResult.getUserinfos();
            }
        }catch (Exception e){}
        return null;
    }

    private String getQueryUserListUrl(String xiuxiuIds){
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_BATCH_USERINFOS)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("cookie",XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("user_id",XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_ids", xiuxiuIds)
                .build().toString();
        return url;
    }
}
