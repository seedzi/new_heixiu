package com.xiuxiu.user;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.xiuxiu.R;
import com.xiuxiu.user.wallet.WalletRechargePage;

/**
 * Created by huzhi on 16-4-8.
 */
public class WalletActivity extends FragmentActivity implements View.OnClickListener{

    public static void startActivity(Context context){
        Intent intent = new Intent(context,WalletActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_wallet_layout);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.ti_xian).setOnClickListener(this);
        findViewById(R.id.chong_zhi).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.ti_xian:
                WalletCashPage.startActivity(WalletActivity.this);
                break;
            case R.id.chong_zhi:
                WalletRechargePage.startActivity(WalletActivity.this);
                break;
            case R.id.back:
                finish();
                break;
        }
    }
}
