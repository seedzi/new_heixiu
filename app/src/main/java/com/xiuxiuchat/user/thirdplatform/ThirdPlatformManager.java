package com.xiuxiuchat.user.thirdplatform;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.controller.listener.SocializeListeners;
import com.umeng.socialize.exception.SocializeException;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.easeim.ImManager;
import com.xiuxiuchat.user.login.LoginUserDataEditPage;
import com.xiuxiuchat.utils.Md5Util;
import com.xiuxiuchat.utils.XiuxiuUtils;

import java.util.Map;
import java.util.Set;

/**
 * Created by huzhi on 16-5-31.
 */
public class ThirdPlatformManager {

    private static String TAG = ThirdPlatformManager.class.getSimpleName();

    private static ThirdPlatformManager mInstance;

    private ThirdPlatformManager(){
    }

    public static ThirdPlatformManager getInstance(){
        if(mInstance == null){
            mInstance = new ThirdPlatformManager();
        }
        return mInstance;
    }

    private FragmentActivity mAc;

    private String openId;
    private String nickname;
    private String city;
    private String headimgpath;
    private String sex;

    private ProgressDialog mProgressDialog;


    private Handler mUiHandler = new Handler();

    public void setActivity(FragmentActivity ac){
        mAc = ac;
        //参数1为当前Activity， 参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mAc, "1105329971",
                "QYEP6efwoIapKsMx");
        qqSsoHandler.addToSocialSDK();

        // 添加微信平台
        UMWXHandler wxHandler = new UMWXHandler(mAc,"wxd9dc87e781c9202a","2ceb6c986762f065e44a90fdc8f9cd0a");
        wxHandler.addToSocialSDK();

    }

//    UMShareAPI  mShareAPI;
    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.login");
    // =============================================================================================
    // 微信登录
    // =============================================================================================
    public void thirdLoginWechat(){
        SocializeListeners.UMAuthListener umAuthListener = new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Toast.makeText(mAc, "开始授权", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                Toast.makeText(mAc, "授权完成", Toast.LENGTH_SHORT).show();
                mController.getPlatformInfo(mAc, SHARE_MEDIA.WEIXIN, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(int i, Map<String, Object> map) {
                        if(map!=null){
                            Set<String> keys = map.keySet();
//                            for (String key : keys) {
//                                android.util.Log.d("77777", "key = " + key + ",value = " + map.get(key));
//                            }
                            nickname = (String) map.get("nickname");
                            city = (String) map.get("province");
                            openId = (String) map.get("openid");
                            headimgpath = (String) map.get("headimgurl");
                            sex = String.valueOf(map.get("sex"));
                        }
                        login();
                    }
                });
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {
                Toast.makeText(mAc, "授权失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                Toast.makeText(mAc, "授权取消", Toast.LENGTH_SHORT).show();
            }
        };
        mController.doOauthVerify(mAc, SHARE_MEDIA.WEIXIN,umAuthListener);

    }

    // =============================================================================================
    // QQ登录
    // =============================================================================================

    public void thirdLoginQQ(){

        SocializeListeners.UMAuthListener umAuthListener = new SocializeListeners.UMAuthListener() {
            @Override
            public void onStart(SHARE_MEDIA share_media) {
                Toast.makeText(mAc, "开始授权", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete(Bundle bundle, SHARE_MEDIA share_media) {
                Toast.makeText(mAc, "授权完成", Toast.LENGTH_SHORT).show();
                mController.getPlatformInfo(mAc, SHARE_MEDIA.QQ, new SocializeListeners.UMDataListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onComplete(int i, Map<String, Object> map) {
                        if (map != null) {
                            Set<String> keys = map.keySet();
//                            for (String key : keys) {
//                                android.util.Log.d("77777", "key = " + key + ",value = " + map.get(key));
//                            }
                            nickname = (String) map.get("screen_name");
                            city = (String) map.get("province");
                            openId = (String) map.get("openid");
                            headimgpath = (String) map.get("profile_image_url");
                            String sextxt = (String) map.get("gender");
                            if(sextxt.equals("男")){
                                sex = "1";
                            }else{ //其它都是女性
                                sex = "0";
                            }
                        }
                        login();
                    }
                });
            }

            @Override
            public void onError(SocializeException e, SHARE_MEDIA share_media) {
                Toast.makeText(mAc, "授权失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA share_media) {
                Toast.makeText(mAc, "授权取消", Toast.LENGTH_SHORT).show();
            }
        };
        mController.doOauthVerify(mAc, SHARE_MEDIA.QQ,umAuthListener);
    }

    // ========================================= QQ微信登录结果返回 ===================================//
    public void onActivityResult4ThirdPlatform(int requestCode, int resultCode, Intent data){
//        mShareAPI.onActivityResult(requestCode, resultCode, data);
        UMSsoHandler ssoHandler = mController.getConfig().getSsoHandler(requestCode);
        if(ssoHandler != null){
            ssoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
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
