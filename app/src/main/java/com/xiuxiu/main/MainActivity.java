package com.xiuxiu.main;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Window;

import com.xiuxiu.R;

public class MainActivity extends FragmentActivity {

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,MainActivity.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_main);
    }

    private void init(){
    }

}
