package com.xiuxiuchat.user.invitation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.xiuxiuchat.R;
import com.xiuxiuchat.base.BaseActivity;

/**
 * 邀请朋友加入咻咻
 * Created by huzhi on 16-4-24.
 */
public class InvitationPage extends BaseActivity{

    public static void startActivity(Context context){
        Intent intent = new Intent(context,InvitationPage.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.invitation_layout);
    }
}
