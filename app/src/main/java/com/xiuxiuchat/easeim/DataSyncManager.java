package com.xiuxiuchat.easeim;

import android.net.Uri;

import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuGetFriendsResult;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.api.XiuxiuResult;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.api.XiuxiuUserQueryResult;
import com.xiuxiuchat.utils.Md5Util;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * 好友联系人相关的数据同步类
 * Created by huzhi on 16-6-5.
 */
public class DataSyncManager {

    private static String TAG = DataSyncManager.class.getSimpleName();

    private static DataSyncManager mInstance;

    private DataSyncManager(){
    }

    public static DataSyncManager getInstance(){
        if(mInstance == null){
            mInstance = new DataSyncManager();
        }
        return mInstance;
    }

    /**
     * 获取对应用户的好友
     */
    public List<String> getAllContactsFromServer(){
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getFriendListUrl(XiuxiuLoginResult.getInstance().getXiuxiu_id()), future, future));
        try {
            String response = future.get();
            android.util.Log.d(TAG,"getAllContactsFromServer()  response = " + response);
            Gson gson = new Gson();
            XiuxiuGetFriendsResult res = gson.fromJson(response, XiuxiuGetFriendsResult.class);
            return res.getResult();
        } catch (Exception e) {
            android.util.Log.d(TAG,"e = " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String getFriendListUrl(String xiuxiuId) {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.GET_FRIENDS)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("xiuxiu_id", xiuxiuId)
                .appendQueryParameter("user_id", xiuxiuId)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG,"URL = " + url);
        return url;
    }

    // ===============================================================================================
    // 获取用户信息
    // ===============================================================================================
    public void asyncFetchContactInfosFromServer(final List<String> xiuxiuIds,final Callback callback){
        android.util.Log.d(TAG,"asyncFetchContactInfosFromServer");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(xiuxiuIds==null || xiuxiuIds.size()==0){
                    android.util.Log.d(TAG,"asyncFetchContactInfosFromServer return");
                    return;
                }
                String params = "";
                int position = 0;
                for(String s:xiuxiuIds){
                    if(position==0){
                        params = s;
                    }else{
                        params = params + "," + s;
                    }
                    position ++;
                }
                XiuxiuUserQueryResult result = null;
                RequestFuture<String> future = RequestFuture.newFuture();
                XiuxiuApplication.getInstance().getQueue()
                        .add(new StringRequest(getQueryUserListUrl(params), future, future));
                try {
                    String response = future.get();
                    android.util.Log.d(TAG,"queryUserInfos() response = " + response);
                    Gson gson = new Gson();
                    result = gson.fromJson(response, XiuxiuUserQueryResult.class);
                } catch (Exception e) {
                    android.util.Log.d(TAG,"queryUserInfos() e = " + e.getMessage());
                    e.printStackTrace();
                }
                if(result!=null && result.getUserinfos()!=null){
                    callback.onSuccess(result.getUserinfos());
                }else{
                    callback.onError();
                }
            }
        }).start();
    }

    private String getQueryUserListUrl(String xiuxiuIds){
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_BATCH_USERINFOS)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("xiuxiu_ids", xiuxiuIds)
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("offset", "0")
                .appendQueryParameter("count", "100")
                .build().toString();
        android.util.Log.d(TAG,"url = " + url);
        return url;
    }

    public interface Callback{
        public void onSuccess(List<XiuxiuUserInfoResult> list);
        public void onError();
    }

    // ===============================================================================================
    // 成为好友
    // ===============================================================================================
    /**
     * 成为好友
     * return true 成功　false 失败
     */
    public boolean beFriends(String remoteId){
        boolean isSuccess = false;
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getBeFriendUrl(remoteId), future, future));
        try {
            String response = future.get();
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            android.util.Log.d(TAG,"beFriends() response = " + response + ",res = " + res);
            if(res.isSuccess()){
                android.util.Log.d(TAG,"beFriends() succrss " );
                isSuccess = true;
            }
        } catch (InterruptedException e) {
            android.util.Log.d(TAG,"beFriends() e = " + e.toString() );
            e.printStackTrace();
            isSuccess = false;
        } catch (ExecutionException e) {
            android.util.Log.d(TAG,"beFriends() e = " + e.toString() );
            e.printStackTrace();
            isSuccess = false;
        }
        return isSuccess;
    }

    private String getBeFriendUrl(String remoteId) {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.BE_FRIENDS)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("remote_id", remoteId)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG,"url = " + url);
        return url;
    }



}
