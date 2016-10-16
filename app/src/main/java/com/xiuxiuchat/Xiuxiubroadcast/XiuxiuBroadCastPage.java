package com.xiuxiuchat.Xiuxiubroadcast;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.xiuxiuchat.CommonLib;
import com.xiuxiuchat.R;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.base.BaseActivity;
import com.xiuxiuchat.utils.ToastUtil;
import com.xiuxiuchat.utils.XiuxiuUtils;
import com.xiuxiuchat.widget.HeixiuHeadImgLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huzhi on 16-4-24.
 */
public class XiuxiuBroadCastPage extends BaseActivity implements View.OnClickListener{

    private static String TAG = "XiuxiuBroadCastPage";

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
        setUpViews();
        initData();
    }

    private Handler mUiHandler = new Handler();

    private HeixiuHeadImgLayout mHeixiuHeadImgLayout;

    private EditText mEdit;

    private void setUpViews(){
        mHeixiuHeadImgLayout = (HeixiuHeadImgLayout) findViewById(R.id.xiuxiu_head_img_layout);
        findViewById(R.id.submit).setOnClickListener(this);
        mEdit = (EditText) findViewById(R.id.edit);
    }

    private List<XiuxiuUserInfoResult> mData;

    private void initData(){
        XiuxiuUtils.showProgressDialog(XiuxiuBroadCastPage.this);
        new Thread(new Runnable() {
            @Override
            public void run() {
                mData = XiuxiuBroadcastManager.getInstance().getXiuxiuBroadcastUserList();
                mUiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        XiuxiuUtils.dismisslProgressDialog();
                        if(mData==null){
                            return;
                        }
                        List<String> urls = new ArrayList<String>();
                        for(XiuxiuUserInfoResult info:mData){
                            urls.add(info.getPic());
                        }
                        mHeixiuHeadImgLayout.setData(urls);
                    }
                });

            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XiuxiuUtils.dismisslProgressDialog();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.submit:
                if(TextUtils.isEmpty(mEdit.getText().toString())){
                    ToastUtil.showMessage(XiuxiuBroadCastPage.this,"咻广播内容不能为空");
                    return;
                }
                XiuxiuUtils.showProgressDialog(XiuxiuBroadCastPage.this);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if(XiuxiuUtils.getXiuxiuBroadcastTimes()>0 && XiuxiuUtils.costXiuxiuBroadcastTimes()){
                            mUiHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showMessage(XiuxiuBroadCastPage.this,"咻广播已经发出");
                                    XiuxiuBroadcastManager.getInstance().sendXiuxiuBroadcast(mData, mEdit.getText().toString());
                                    XiuxiuUtils.dismisslProgressDialog();
                                    finish();
                                }
                            });
                        }else{
                            mUiHandler.post(new Runnable() {
                                @Override
                                public void run() {
//                                    ToastUtil.showMessage(XiuxiuBroadCastPage.this, "今天咻广播次数已经用完");
                                    XiuxiuUtils.dismisslProgressDialog();
                                    showConfirmDialog(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.showMessage(XiuxiuBroadCastPage.this,"咻广播已经发出");
                                            finish();
                                        }
                                    }, XiuxiuBroadCastPage.this);
                                }
                            });
                        }
                    }
                }).start();
                break;
        }
    }



    // ============================================================================================
    // 确认付费对话框
    // ============================================================================================
    private void showConfirmDialog(final Runnable callback, final FragmentActivity ac) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ac);
        builder.setMessage("咻咻广播次数已达到上限,确认付费购买吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                XiuxiuUtils.showProgressDialog(ac);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean isSccess = XiuxiuUtils.costUserCoin("3", "3");
                        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (isSccess) {
                                    if (callback != null) {
                                        callback.run();
                                    }
                                } else {
                                    if (CommonLib.isNetworkConnected(ac)) {
                                        ToastUtil.showMessage(ac, "可能您的余额不够,支付失败,请您充值!");
                                    } else {
                                        ToastUtil.showMessage(ac, "网络连接错误!");
                                    }
                                }
                                XiuxiuUtils.dismisslProgressDialog();
                            }
                        });
                    }
                }).start();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
