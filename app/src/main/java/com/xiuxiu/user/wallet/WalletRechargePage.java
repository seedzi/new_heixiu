package com.xiuxiu.user.wallet;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuPayConf;
import com.xiuxiu.api.XiuxiuPayConfResult;
import com.xiuxiu.api.XiuxiuWechatResult;
import com.xiuxiu.payment.WeiXinPayManager;
import com.xiuxiu.user.WalletCashPage;
import com.xiuxiu.utils.Md5Util;
import com.xiuxiu.utils.NetUtils;
import com.xiuxiu.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzhi on 16-6-28.
 */
public class WalletRechargePage extends FragmentActivity implements View.OnClickListener{


    public static void startActivity(Context context){
        Intent intent = new Intent(context,WalletRechargePage.class);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_wallet_recharge_page);
        setUpViews();
        initData();
    }

    private void setUpViews(){
        findViewById(R.id.back).setOnClickListener(this);
        mContainerLayout = (ViewGroup) findViewById(R.id.container);
        findViewById(R.id.wx_pay_bt).setOnClickListener(this);
    }

    private void initData(){
        getPayConf();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.wx_pay_bt:
                int position = 0;
                boolean hasChecked = false;
                for(CheckBox checkBox:mCheckBoxList){
                    if(checkBox.isChecked()){
                        hasChecked = true;
                        break;
                    }
                    position ++;
                }
                if(hasChecked){
                    XiuxiuPayConf xiuxiuPayConf = mListData.get(position);
                    pay(xiuxiuPayConf.getXiuxiuB(), xiuxiuPayConf.getCash() + "");
                }
                showProgressDialog();
                break;
        }
    }

    private ViewGroup mContainerLayout;

    private List<CheckBox> mCheckBoxList = new ArrayList<CheckBox>();

    private void setListData(){
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        if(mListData!=null){
            int position = 0;
            for(XiuxiuPayConf info :mListData){
                ViewGroup itemLayout = (ViewGroup) inflater.inflate(R.layout.recharge_item_layout, null);
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                itemLayout.setLayoutParams(ll);
                mContainerLayout.addView(itemLayout);
                ((TextView)itemLayout.findViewById(R.id.xiu_bi)).setText(info.getXiuxiuB());
                ((TextView)itemLayout.findViewById(R.id.rmb)).setText("￥" + info.getCash());
                if(position==mListData.size()-1){
                    itemLayout.findViewById(R.id.line).setVisibility(View.GONE);
                }
                mCheckBoxList.add((CheckBox)itemLayout.findViewById(R.id.checked));
                itemLayout.findViewById(R.id.checked).setTag(info.getCash());
                itemLayout.findViewById(R.id.checked).setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        /*
                        if(((CheckBox)v).isChecked()){
                            ((CheckBox)v).setChecked(false);
                        }else{
                            for(CheckBox c:mCheckBoxList){
                                c.setChecked(false);
                            }
                            ((CheckBox)v).setChecked(true);
                        }
                        */
                        if(((CheckBox)v).isChecked()){
                            for(CheckBox c:mCheckBoxList){
                                c.setChecked(false);
                            }
                            ((CheckBox)v).setChecked(true);
                        }else{
                        }
                    }
                });
                position ++;
            }
        }
    }


    // ============================================================================================
    // 获取支付配置
    // ============================================================================================
    private List<XiuxiuPayConf> mListData;
    public void getPayConf() {
        Response.Listener<String> mPayConfListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                android.util.Log.d("AAAA","response = " + response);
                Gson gson = new Gson();
                XiuxiuPayConfResult res = gson.fromJson(response, XiuxiuPayConfResult.class);
                if(res!=null){
                    mListData = res.getResult();
                }
                setListData();
            }
        };
        Response.ErrorListener mPayCOnfErroListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                android.util.Log.d("AAAA","error = " + error);
            }
        };
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getPayConfUrl(), mPayConfListener, mPayCOnfErroListener));
    }
    private static String getPayConfUrl() {
        String url = Uri.parse(HttpUrlManager.weixinPayUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.GET_PAY_CONF)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        return url;
    }

    // ============================================================================================
    // 对话框
    // ============================================================================================
    private ProgressDialog mProgressDialog;

    private void showProgressDialog(){
        mProgressDialog = ProgressDialog.show(this, "提示", "正在上传中...");
    }

    private void dismisslProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    // ============================================================================================
    // 支付
    // ============================================================================================
    public void pay(String orderCode,String total_fee) {
        Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                android.util.Log.d("AAAA","response = " + response);
                XiuxiuWechatResult res = gson.fromJson(response, XiuxiuWechatResult.class);
                if(res!=null){
                    WeiXinPayManager.getInstance().pay(res.result,WalletRechargePage.this);
                }
                dismisslProgressDialog();
            }
        };
        Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                android.util.Log.d("AAAA","error = " + error);
                dismisslProgressDialog();
                ToastUtil.showMessage(WalletRechargePage.this,"支付失败!");
            }
        };
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(payWeiXinUrl(orderCode, total_fee), mRefreshListener, mRefreshErroListener));
    }
    private static String payWeiXinUrl(String orderCode,String total_fee) {
        String url = Uri.parse(HttpUrlManager.weixinPayUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.WEIXIN_PAY)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("orderCode", orderCode)
                .appendQueryParameter("client_ip", NetUtils.getWifiIpAddress(XiuxiuApplication.getInstance()))
                .appendQueryParameter("total_fee", total_fee)
                .build().toString();
        android.util.Log.d("AAAA","url = " + url);
        return url;
    }
}
