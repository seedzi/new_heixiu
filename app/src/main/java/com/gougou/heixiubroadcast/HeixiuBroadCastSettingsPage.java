package com.gougou.heixiubroadcast;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.gougou.R;

/**
 * Created by huzhi on 16-4-24.
 */
public class HeixiuBroadCastSettingsPage extends FragmentActivity{

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,HeixiuBroadCastSettingsPage.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_heixiu_broadcast_setting_page);
    }
}
