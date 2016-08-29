package com.xiuxiuchat.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.xiuxiuchat.R;
import com.xiuxiuchat.base.BaseActivity;
import com.xiuxiuchat.easeim.Constant;
import com.xiuxiuchat.main.chat.ChatFragment;
import com.xiuxiuchat.main.chat.ConversationListManager;

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
