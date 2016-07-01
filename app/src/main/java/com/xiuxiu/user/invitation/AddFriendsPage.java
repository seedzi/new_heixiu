package com.xiuxiu.user.invitation;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.exceptions.HyphenateException;
import com.xiuxiu.R;
import com.xiuxiu.chat.ChatPage;
import com.xiuxiu.easeim.ImHelper;
import com.xiuxiu.utils.ToastUtil;

/**
 * Created by huzhi on 16-6-2.
 */
public class AddFriendsPage extends FragmentActivity implements View.OnClickListener{


    public static void startActivity(Context context,String userId){
        Intent intent = new Intent(context,AddFriendsPage.class);
        intent.putExtra(ChatPage.EXTRA_USERID,userId);
        context.startActivity(intent);
    }

    private EditText mEdit;

    String toChatUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friends_page_layout);

        toChatUserId = getIntent().getExtras().getString(ChatPage.EXTRA_USERID);
        setupViews();
    }

    private void setupViews(){
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.next).setOnClickListener(this);
        mEdit = (EditText) findViewById(R.id.edit);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.next:
                addContact();
                /*
                //参数为要添加的好友的username和添加理由
                try {
                    EMClient.getInstance().contactManager().addContact(toChatUserId, mEdit.getText().toString());
                    ToastUtil.showMessage(this,"已经发送");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    ToastUtil.showMessage(this, "发送失败");
                }*/
                break;
            case R.id.back:
                finish();
                break;
        }
    }


    private ProgressDialog progressDialog;
    /**
     *  添加contact
     * @param view
     */
    public void addContact(){
        if(EMClient.getInstance().getCurrentUser().equals(toChatUserId)){
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }
        /*
        if(ImHelper.getInstance().getContactList().containsKey(nameText.getText().toString())){
            //提示已在好友列表中(在黑名单列表里)，无需添加
            if(EMClient.getInstance().contactManager().getBlackListUsernames().contains(nameText.getText().toString())){
                new EaseAlertDialog(this, R.string.user_already_in_contactlist).show();
                return;
            }
            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
            return;
        }*/

        progressDialog = new ProgressDialog(this);
        String stri = getResources().getString(R.string.Is_sending_a_request);
        progressDialog.setMessage(stri);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        new Thread(new Runnable() {
            public void run() {

                try {
                    String s = mEdit.getText().toString();
                    if(TextUtils.isEmpty(s)){
                        s = getResources().getString(R.string.Add_a_friend);
                    }
                    EMClient.getInstance().contactManager().addContact(toChatUserId, s);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s1 = getResources().getString(R.string.send_successful);
                            Toast.makeText(getApplicationContext(), s1, 1).show();
                            finish();
                        }
                    });
                } catch (final Exception e) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                            Toast.makeText(getApplicationContext(), s2 + e.getMessage(), 1).show();
                        }
                    });
                }
            }
        }).start();
    }
}
