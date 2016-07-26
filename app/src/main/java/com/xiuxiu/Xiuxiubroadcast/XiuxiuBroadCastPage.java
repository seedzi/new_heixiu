package com.xiuxiu.Xiuxiubroadcast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.XiuxiuUtils;
import com.xiuxiu.widget.HeixiuHeadImgLayout;

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
                            android.util.Log.d(TAG,"mData==null");
                            return;
                        }
                        List<String> urls = new ArrayList<String>();
                        for(XiuxiuUserInfoResult info:mData){
                            android.util.Log.d(TAG,"info.getPic() = " + info.getPic());
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
                                    ToastUtil.showMessage(XiuxiuBroadCastPage.this,"今天咻广播已经发出");
                                    XiuxiuBroadcastManager.getInstance().sendXiuxiuBroadcast(mData, mEdit.getText().toString());
                                    XiuxiuUtils.dismisslProgressDialog();
                                }
                            });
                        }else{
                            mUiHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    ToastUtil.showMessage(XiuxiuBroadCastPage.this, "今天咻广播次数已经用完");
                                    XiuxiuUtils.dismisslProgressDialog();
                                }
                            });
                        }
                    }
                }).start();
                break;
        }

    }
}
