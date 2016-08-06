package com.xiuxiu;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;

import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.easeim.ImHelper;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.login.LoginPage;
import com.xiuxiu.user.login.LoginUserDataEditPage;
import com.xiuxiu.utils.XiuxiuUtils;

/**
 * Created by huzhi on 16-8-6.
 */
public class WelcomePage extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_layout);
        init();
    }


    /**
     * 如果登录了直接进入
     */
    private void init(){
        if(ImHelper.getInstance().isLoggedIn()){
            boolean isEnterFirstLoginPage = false;
            XiuxiuUserInfoResult xiuxiuUserQueryResult = XiuxiuUserInfoResult.getInstance();
            if(xiuxiuUserQueryResult!=null ){
                if(TextUtils.isEmpty(xiuxiuUserQueryResult.getSex())
                        || "unknown".equals(xiuxiuUserQueryResult.getSex())){
                    isEnterFirstLoginPage = true;
                }
            }
            if(isEnterFirstLoginPage){
                LoginUserDataEditPage.startActivity(WelcomePage.this);
            }else{
                initAndEnterMainPage();
            }
        }else{
            //进入登陆页面
            LoginPage.startActivity(this);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LoginUserDataEditPage.REQUEST_CODE ){
            if(resultCode == RESULT_OK){
                initAndEnterMainPage();
            }
        }
    }

    private void initAndEnterMainPage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                XiuxiuUtils.onAppStart(getApplicationContext());
                XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.startActivity(WelcomePage.this);
                        finish();
                    }
                });
            }
        }).start();
    }
}
