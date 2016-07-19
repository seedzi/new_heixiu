package com.xiuxiu.Xiuxiubroadcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.xiuxiu.R;
import com.xiuxiu.base.BaseActivity;

/**
 * Created by huzhi on 16-4-24.
 */
public class XiuxiuBroadCastPage extends BaseActivity{

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,XiuxiuBroadCastPage.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_heixiu_broadcast_page);
    }
}
