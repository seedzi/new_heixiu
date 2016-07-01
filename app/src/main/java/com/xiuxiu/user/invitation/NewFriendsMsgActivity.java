package com.xiuxiu.user.invitation;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.xiuxiu.R;

import java.util.List;

/**
 * Created by huzhi on 16-6-5.
 */
public class NewFriendsMsgActivity extends FragmentActivity{

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.em_activity_new_friends_msg);

        listView = (ListView) findViewById(R.id.list);
        InviteMessgeDao dao = new InviteMessgeDao(this);
        List<InviteMessage> msgs = dao.getMessagesList();
        //设置adapter
        NewFriendsMsgAdapter adapter = new NewFriendsMsgAdapter(this, 1, msgs);
        listView.setAdapter(adapter);
        dao.saveUnreadMessageCount(0);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
