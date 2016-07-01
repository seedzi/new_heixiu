package com.xiuxiu.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.xiuxiu.R;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.xiuxiu.chat.im.ChatFragment;

/**
 * Created by zhihu on 16-4-17.
 */
public class ChatPage extends FragmentActivity{

    public static final String EXTRA_USERID = "userId";

    public static final String EXTRA_USERNAME = "userName";

    String toChatUserId;

    public static void startActivity(FragmentActivity ac,String userId,String userName){
        Intent intent = new Intent(ac,ChatPage.class);
        intent.putExtra(EXTRA_USERID,userId);
        intent.putExtra(EXTRA_USERNAME,userName);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_chat_page);
        //聊天人或群id
        toChatUserId = getIntent().getExtras().getString(EXTRA_USERID);
        //可以直接new EaseChatFratFragment使用
        ChatFragment chatFragment = new ChatFragment();
        //传入参数
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, chatFragment).commit();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // 点击notification bar进入聊天页面，保证只有一个聊天页面
        String userid = intent.getStringExtra(EXTRA_USERID);
        if (toChatUserId.equals(userid))
            super.onNewIntent(intent);
        else {
            finish();
            startActivity(intent);
        }

    }
}
