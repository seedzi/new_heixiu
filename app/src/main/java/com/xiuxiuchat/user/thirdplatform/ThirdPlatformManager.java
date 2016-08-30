package com.xiuxiuchat.user.thirdplatform;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.easeim.ImManager;
import com.xiuxiuchat.user.login.LoginUserDataEditPage;
import com.xiuxiuchat.utils.Md5Util;
import com.xiuxiuchat.utils.XiuxiuUtils;

import java.util.Map;

/**
 * Created by huzhi on 16-5-31.
 */
public class ThirdPlatformManager {

    private static String TAG = ThirdPlatformManager.class.getSimpleName();

    private static ThirdPlatformManager mInstance;

    private ThirdPlatformManager(){
        //1.微信初始化
        PlatformConfig.setWeixin("wxd9dc87e781c9202a", "2ceb6c986762f065e44a90fdc8f9cd0a");
        //2.qq初始化
        PlatformConfig.setQQZone("1105329971","QYEP6efwoIapKsMx");
        //APP ID                  1105329971   APP KEY         QYEP6efwoIapKsMx
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

    // =============================================================================================
    // 微信登录
    // =============================================================================================
    public void thirdLoginWechat(){
        if(mShareAPI == null) {
            mShareAPI = UMShareAPI.get(mAc);
        }
        platform = SHARE_MEDIA.WEIXIN;
        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                mShareAPI.getPlatformInfo(mAc, platform, new UMAuthListener(){
                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        if(map!=null){
                            nickname = map.get("nickname");
                            city = map.get("province");
                            openId = map.get("openid");
                            headimgpath = map.get("headimgurl");
                            sex = map.get("sex");
                        }else{

                        }
                        login();
                    }
                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        Toast.makeText( mAc, "get info fail", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        Toast.makeText( mAc, "onCancel cancel", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                Toast.makeText( mAc, "Authorize fail", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                Toast.makeText( mAc, "onCancel cancel", Toast.LENGTH_SHORT).show();
            }
        };
        mShareAPI.doOauthVerify(mAc, platform, umAuthListener);
    }

    // =============================================================================================
    // QQ登录
    // =============================================================================================

    public void thirdLoginQQ(){
        android.util.Log.d("77777","thirdLoginQQ()");
        if(mShareAPI == null) {
            mShareAPI = UMShareAPI.get(mAc);
        }
        platform = SHARE_MEDIA.QQ;
        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                android.util.Log.d("77777","onComplete()");
                mShareAPI.getPlatformInfo(mAc, platform, new UMAuthListener(){
                    @Override
                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
                        android.util.Log.d("77777","nei onComplete()");
                        for (Map.Entry<String, String> entry : map.entrySet()) {
                            android.util.Log.d("77777","Key = " + entry.getKey() + ", Value = " + entry.getValue());
                        }
                        if(map!=null){
                            nickname = map.get("nickname");
                            city = map.get("province");
                            openId = map.get("openid");
                            headimgpath = map.get("headimgurl");
                            sex = map.get("sex");
                        }
                        login();
                    }

                    @Override
                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
                        android.util.Log.d("77777","onError()");
                        Toast.makeText( mAc, "get info fail", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancel(SHARE_MEDIA share_media, int i) {
                        android.util.Log.d("77777","onCancel()");
                        Toast.makeText( mAc, "onCancel cancel", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                android.util.Log.d("77777","onError()");
                Toast.makeText( mAc, "Authorize fail", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                android.util.Log.d("77777","onCancel()");
                Toast.makeText( mAc, "onCancel cancel", Toast.LENGTH_SHORT).show();
            }
        };
        mShareAPI.doOauthVerify(mAc, platform, umAuthListener);
    }

    // ========================================= 微信登录结果返回 ===================================//
    public void onActivityResult4ThirdPlatform(int requestCode, int resultCode, Intent data){
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XiuxiuUtils.queryUserInfo(); //1.获取用户信息
                        boolean isEnterFirstLoginPage = XiuxiuUtils.isEnterFirstLoginPage();//2.是否进入首次登录编辑页面
                        if (res.getIsFirstLogin() || isEnterFirstLoginPage) { //如果进入登录编辑页面
                            mUiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    LoginUserDataEditPage.startActivity(mAc, nickname, headimgpath, city, sex);
                                }
                            });
                        } else {//如果直接登录
                            ImManager.getInstance().login(XiuxiuLoginResult.getInstance().getXiuxiu_id(),
                                    XiuxiuLoginResult.getInstance().getPasswordForYX(),
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            XiuxiuUtils.initAndEnterMainPage(mAc);
                                        }
                                    });
                        }
                        mUiHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                dismisslProgressDialog();
                            }
                        });
                    }
                }).start();
            }else{
                dismisslProgressDialog();
                Toast.makeText( mAc, "连接错误,登录失败", Toast.LENGTH_SHORT).show();
            }
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
