package com.xiuxiu.qiniu;


import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuQiniuTokenResult;
import com.xiuxiu.api.XiuxiuResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.utils.Md5Util;

import org.json.JSONObject;

import java.io.File;
import java.util.concurrent.ExecutionException;

/**
 * Created by huzhi on 16-5-29.
 */
public class QiniuManager {

    private static String TAG = "QiniuManager";
    private static QiniuManager mQiniuManager;

    private QiniuManager(){
        mUploadManager = new UploadManager();
    }

    public static QiniuManager getInstance(){
        if(mQiniuManager == null){
            mQiniuManager = new QiniuManager();
        }
        return mQiniuManager;
    }

    private UploadManager mUploadManager;

    /**
     * 获取token
     * return true 成功　false 失败
     */
    public String getToken(String filename){
        String token = null;
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getTokenUrl(filename), future, future));
        try {
            String response = future.get();
            Gson gson = new Gson();
            XiuxiuQiniuTokenResult res = gson.fromJson(response, XiuxiuQiniuTokenResult.class);
            if(res.isSuccess()){
                token = res.qiniuToken;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return token;
    }

    private String getTokenUrl(String fileName) {
        return Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.GET_QINIU_TOKEN)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("key", fileName)
                .appendQueryParameter("isinsertOnly", "0")
                .build().toString();
    }

    /**
     * 上传文件
     * @param filePath
     * @param key
     * @param token
     * @return false:参数错误;true:参数有效
     */
    public boolean uploadFile(String filePath,String key,String token, final UpCompletionHandler callback){
        android.util.Log.d(TAG,"uploadFile()");
        if(TextUtils.isEmpty(filePath)||TextUtils.isEmpty(key)||TextUtils.isEmpty(token)){
            return false;
        }
        File data = new File(filePath);
        if(!data.exists()){
            return false;
        }
        mUploadManager.put(data, key, token,callback, null);
        return true;
    }

    /**
     * 上传文件
     * @param key
     * @param token
     * @return false:参数错误;true:参数有效
     */
    public boolean uploadFile(byte[] fileByte,String key,String token, final UpCompletionHandler callback){
        if(fileByte == null||TextUtils.isEmpty(key)||TextUtils.isEmpty(token)){
            return false;
        }
        mUploadManager.put(fileByte, key, token,callback, null);
        return true;
    }

}
