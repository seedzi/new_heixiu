package com.xiuxiu.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.xiuxiu.R;
import com.xiuxiu.base.BaseActivity;

/**
 * Created by zhihu on 16-5-2.
 */
public class SetupPage extends BaseActivity{

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,SetupPage.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_setup_page);
    }
}
