package com.xiuxiu.user.thirdplatform;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.api.XiuxiuUserQueryResult;
import com.xiuxiu.easeim.ImManager;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.login.LoginUserDataEditPage;
import com.xiuxiu.utils.Md5Util;
import com.xiuxiu.utils.XiuxiuUtils;

import java.util.Map;

/**
 * Created by huzhi on 16-5-31.
 */
public class ThirdPlatformManager {

    private static String TAG = ThirdPlatformManager.class.getSimpleName();

    private static ThirdPlatformManager mInstance;

    private ThirdPlatformManager(){
        PlatformConfig.setWeixin("wxd9dc87e781c9202a", "2ceb6c986762f065e44a90fdc8f9cd0a");
    }

    public static ThirdPlatformManager getInstance(){
        if(mInstance == null){
            mInstance = new ThirdPlatformManager();
        }
        return mInstance;
    }

    private FragmentActivity mAc;
    private UMShareAPI mShareAPI;
    private SHARE_MEDIA platform;

    private String openId;
    private String nickname;
    private String city;
    private String headimgpath;
    private String sex;

    private ProgressDialog mProgressDialog;


    private Handler mUiHandler = new Handler();

    public void setActivity(FragmentActivity ac){
        mAc = ac;
    }

    /**
     * 微信登录
     */
    public void thirdLoginWechat(){
//        showProgressDialog();
        if(mShareAPI == null) {
            mShareAPI = UMShareAPI.get(mAc);
        }
        platform = SHARE_MEDIA.WEIXIN;
        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
//                Toast.makeText(mAc, "Authorize succeed", Toast.LENGTH_SHORT).show();
                for (String key : data.keySet()) {
                    android.util.Log.d("123456","data key = " + key);
                }
                mShareAPI.getPlatformInfo(mAc, platform, new UMAuthListener(){
                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//                        Toast.makeText(mAc, "get info succeed", Toast.LENGTH_SHORT).show();
                        nickname = map.get("nickname");
                        city = map.get("province");
                        openId = map.get("openid");
                        headimgpath = map.get("headimgurl");
                        sex = map.get("sex");
                        for (String key : map.keySet()) {
                            android.util.Log.d("123456","key = " + key);
                        }
                        login();
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        Toast.makeText( mAc, "get info fail", Toast.LENGTH_SHORT).show();
//                        dismisslProgressDialog();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        Toast.makeText( mAc, "onCancel cancel", Toast.LENGTH_SHORT).show();
//                        dismisslProgressDialog();
                    }
                });
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText( mAc, "Authorize fail", Toast.LENGTH_SHORT).show();
//                dismisslProgressDialog();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText( mAc, "onCancel cancel", Toast.LENGTH_SHORT).show();
//                dismisslProgressDialog();
            }
        };
        mShareAPI.doOauthVerify(mAc, platform, umAuthListener);
    }

    /**
     * 微信登录结果返回
     */
    public void onActivityResult4Wechat(int requestCode, int resultCode, Intent data){
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }

    // ========================================= Volley  login ===================================//
    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            android.util.Log.d(TAG,"response = " + response);
            Gson gson = new Gson();
            final XiuxiuLoginResult res = gson.fromJson(response, XiuxiuLoginResult.class);
            if (res.isSuccess()) {
                XiuxiuLoginResult.save(res);
                ImManager.getInstance().login(res.getXiuxiu_id(), res.getPasswordForYX(), new Runnable() {
                    @Override
                    public void run() {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                XiuxiuUtils.onAppStart(mAc);
                                mUiHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        boolean isEnterFirstLoginPage = false;
                                        XiuxiuUserInfoResult xiuxiuUserQueryResult = XiuxiuUserInfoResult.getInstance();
                                        if (xiuxiuUserQueryResult != null) {
                                            if (TextUtils.isEmpty(xiuxiuUserQueryResult.getSex())
                                                    || "unknown".equals(xiuxiuUserQueryResult.getSex())) {
                                                isEnterFirstLoginPage = true;
                                            }
                                        }
                                        LoginUserDataEditPage.startActivity(mAc, nickname, headimgpath, city, sex);
                                        /*
                                        if(res.getIsFirstLogin()|| isEnterFirstLoginPage){
                                            LoginUserDataEditPage.startActivity(mAc,nickname,headimgpath,city,sex);
                                        }else {
                                            MainActivity.startActivity(mAc);
                                            mAc.finish();
                                        }*/
                                    }
                                });
                            }
                        }).start();

                    }
                });
            }
            dismisslProgressDialog();
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.d(TAG,"error = " + error.getMessage());
            dismisslProgressDialog();
        }
    };

    /**
     * 登录
     */
    private void login() {
        showProgressDialog();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getTopicListUrl(),
                        mRefreshListener, mRefreshErroListener));
    }
    private String getTopicListUrl() {
        android.util.Log.d(TAG,"openId = " + openId + ",nickname = " + nickname);
        return Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.LOGIN_BY_PLAT)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("weixin_token", openId)
                .appendQueryParameter("xiuxiu_name", nickname)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
    }

    private void showProgressDialog(){
        mProgressDialog = ProgressDialog.show(mAc, "提示", "正在加载中...");
    }

    private void dismisslProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }
}
