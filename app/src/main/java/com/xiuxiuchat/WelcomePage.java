package com.xiuxiuchat;

import android.content.Intent;
import android.os.Bundle;

import com.xiuxiuchat.base.BaseActivity;
import com.xiuxiuchat.easeim.ImHelper;
import com.xiuxiuchat.user.login.LoginPage;
import com.xiuxiuchat.utils.XiuxiuUtils;

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
            XiuxiuUtils.initAndEnterMainPage(WelcomePage.this);
        }else{
            //进入登陆页面
            LoginPage.startActivity(this);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
