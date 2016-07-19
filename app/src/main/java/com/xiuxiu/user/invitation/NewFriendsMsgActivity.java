package com.xiuxiu.user.invitation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xiuxiu.R;
import com.xiuxiu.base.BaseActivity;

import java.util.List;

/**
 * Created by huzhi on 16-6-5.
 */
public class NewFriendsMsgActivity extends BaseActivity{

    private ListView listView;

    private NewFriendsMsgAdapter adapter;

    private InviteMessgeDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_new_friends_msg);

        listView = (ListView) findViewById(R.id.list);
        dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        //设置adapter
        adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        listView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                showDeleteItemDialog(position);
                return true;
            }
        });
    }


    // ============================================================================================
    // 删除对话框
    // ============================================================================================
    private void showDeleteItemDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewFriendsMsgActivity.this);
        builder.setMessage("确认删除这条消息吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                InviteMessage inviteMessage = adapter.getItem(position);
                if (inviteMessage == null) {
                    return;
                }
                String from = inviteMessage.getFrom();
                if (TextUtils.isEmpty(from)){
                    return;
                }
                dao.deleteMessage(from);
                List<InviteMessage> msgs = dao.getMessagesList();
                adapter.clear();
                adapter.addAll(msgs);
                adapter.notifyDataSetChanged();
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
