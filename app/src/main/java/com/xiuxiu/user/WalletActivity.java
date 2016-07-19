package com.xiuxiu.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuWalletCoinResult;
import com.xiuxiu.api.XiuxiuWalletCoinResultResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.user.wallet.WalletRechargePage;
import com.xiuxiu.utils.Md5Util;

/**
 * Created by huzhi on 16-4-8.
 */
public class WalletActivity extends BaseActivity implements View.OnClickListener{

    public static void startActivity(Context context){
        Intent intent = new Intent(context,WalletActivity.class);
        context.startActivity(intent);
    }

    private static String TAG = WalletActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_wallet_layout);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.ti_xian).setOnClickListener(this);
        findViewById(R.id.chong_zhi).setOnClickListener(this);
        refreshData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryUserWalletInfo();
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

    private void refreshData(){
        try {
            XiuxiuWalletCoinResult xiuxiu = XiuxiuWalletCoinResult.getFromShareP();
            android.util.Log.d(TAG,"xiuxiu = " +xiuxiu);
            if (xiuxiu != null) {
                ((TextView) findViewById(R.id.xiuxiu_coin_size)).setText(
                        xiuxiu.getRecharge_coin() + xiuxiu.getEarn_coin() + "");
            }
        } catch (Exception e){
            android.util.Log.d(TAG,"E = " + e.getMessage());

        }
    }

    // ============================================================================================
    // 获取用户信息
    // ============================================================================================
    private void queryUserWalletInfo() {
        Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                android.util.Log.d(TAG,"response = " + response);
                XiuxiuWalletCoinResultResult res = gson.fromJson(response, XiuxiuWalletCoinResultResult.class);
                android.util.Log.d(TAG,"res = " + res.getResult());
                if(res!=null && res.isSuccess() && res.getResult()!=null){
                    XiuxiuWalletCoinResult.save(res.getResult());
                    refreshData();
                }
            }
        };
        Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                android.util.Log.d(TAG,"error = " + error);
            }
        };
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getUserWalletUrl(), mRefreshListener, mRefreshErroListener));
    }
    private String getUserWalletUrl() {
        String url = Uri.parse(HttpUrlManager.weixinPayUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.GET_USER_XIUXIU_COIN)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "url = " + url);
        return url;
    }
}
