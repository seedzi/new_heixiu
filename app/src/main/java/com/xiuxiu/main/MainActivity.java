package com.xiuxiu.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Window;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.api.XiuxiuUserQueryResult;
import com.xiuxiu.api.XiuxiuWechatBean;
import com.xiuxiu.api.XiuxiuWechatResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.easeim.Constant;
import com.xiuxiu.main.chat.ChatFragment;
import com.xiuxiu.main.chat.ConversationListManager;
import com.xiuxiu.payment.WeiXinPayManager;
import com.xiuxiu.server.UpdateActiveUserManager;
import com.xiuxiu.user.FileUploadManager;
import com.xiuxiu.utils.Md5Util;
import com.xiuxiu.utils.NetUtils;
import com.xiuxiu.utils.XiuxiuUtils;

public class MainActivity extends BaseActivity {

    private static String TAG = "MainActivity";

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,MainActivity.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    private static MainActivity mInstance;

    public static MainActivity getInstance(){
        return mInstance;
    }

    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager broadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_main);
        registerBroadcastReceiver();
    }

    // ============================================================================================
    // 注册事件
    // ============================================================================================

    private void registerBroadcastReceiver() {
        broadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.ACTION_CONTACT_CHANAGED);
        broadcastReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
//                updateUnreadLabel();
//                updateUnreadAddressLable();
                /*
                if (TabsFragmentManager.getInstance().getCurrentPosition() == 0) {
                } else if (TabsFragmentManager.getInstance().getCurrentPosition() == 1) {
                    // 当前页面如果为聊天历史页面，刷新此页面
                    if(TabsFragmentManager.getInstance().getFragment(1) != null) {
                        ConversationListManager.getInstance().refresh();

                    }
                }*/
                // 当前页面如果为聊天历史页面，刷新此页面
                if(TabsFragmentManager.getInstance().getCurrentFragment()instanceof ChatFragment){
                    ConversationListManager.getInstance().refresh();
                }
                /*
                String action = intent.getAction();
                if(action.equals(Constant.ACTION_GROUP_CHANAGED)){
                    if (EaseCommonUtils.getTopActivity(MainActivity.this).equals(GroupsActivity.class.getName())) {
                        GroupsActivity.instance.onResume();
                    }
                }*/
            }
        };
        broadcastManager.registerReceiver(broadcastReceiver, intentFilter);
    }
}
