package com.xiuxiu.user.invitation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.xiuxiu.R;

/**
 * Created by huzhi on 16-4-24.
 */
public class InvitationPage extends FragmentActivity{

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