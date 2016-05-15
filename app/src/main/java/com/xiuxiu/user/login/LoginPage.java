package com.xiuxiu.user.login;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.xiuxiu.GougouApplication;
import com.xiuxiu.R;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuResult;
import com.xiuxiu.api.XiuxiuUser;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.register.RegisterPage;
import com.xiuxiu.utils.Md5Util;
import com.xiuxiu.utils.UiUtil;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.Map;

/**
 * Created by zhihu on 16-4-17.
 */
public class LoginPage extends FragmentActivity implements View.OnClickListener{

    private ViewGroup mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_login_page);
        mRootLayout = (ViewGroup)findViewById(R.id.root_layout);
        UiUtil.findViewById(mRootLayout,R.id.qq_login_bt).setOnClickListener(this);
        UiUtil.findViewById(mRootLayout,R.id.wechat_login_bt).setOnClickListener(this);

        mShareAPI = UMShareAPI.get(this);
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.qq_login_bt:
                /*
                finish();
                MainActivity.startActivity(this);
                */
                enterRegister();
                break;
            case R.id.wechat_login_bt:
                /*
                HeixiuApi heixiuApi = new HeixiuApi();
                thirdLoginWechat();*/
//                login();

                finish();
                MainActivity.startActivity(this);
                break;
        }
    }

    UMShareAPI mShareAPI;
    SHARE_MEDIA platform;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }


    public void thirdLoginWechat(){
        platform = SHARE_MEDIA.WEIXIN;
        UMAuthListener umAuthListener = new UMAuthListener() {
            @Override
            public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
                android.util.Log.d("aaa","完成");
                Toast.makeText(LoginPage.this, "Authorize succeed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(SHARE_MEDIA platform, int action, Throwable t) {
                android.util.Log.d("aaa","出错了");
                Toast.makeText( LoginPage.this, "Authorize fail", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(SHARE_MEDIA platform, int action) {
                android.util.Log.d("aaa","取消了");
                Toast.makeText( LoginPage.this, "onCancel cancel", Toast.LENGTH_SHORT).show();
            }
        };
        mShareAPI.doOauthVerify(LoginPage.this, platform, umAuthListener);
    }

    // ========================================= Volley  login ================================================//
    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            android.util.Log.d("ccc","response = " + response);
            XiuxiuResult res = gson.fromJson(response, XiuxiuUser.class);
            if (res.isSuccess()) {
                Toast.makeText(getApplication(),"登录成功",0).show();
                finish();
                MainActivity.startActivity(LoginPage.this);
            }
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };

    /**
     * 登录
     */
    private void login() {
        GougouApplication.getInstance().getQueue()
                .add(new StringRequest(getTopicListUrl(), mRefreshListener, mRefreshErroListener));
    }
    private String getTopicListUrl() {
        return Uri.parse(HttpUrlManager.loginUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.LOGIN_BY_PLAT)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("xiuxiu_id", String.valueOf("1234560"))
                .appendQueryParameter("xiuxiu_name", String.valueOf("huzhi1"))
                .appendQueryParameter("attrs", String.valueOf("huzhi"))
                .build().toString();
    }

    // ========================================= Volley ================================================//

    // ========================================= test ================================================//

    private void enterRegister(){
        RegisterPage.startActivity(LoginPage.this);
    }
}
