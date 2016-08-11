package com.xiuxiu.Xiuxiubroadcast;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.xiuxiu.R;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.bean.XiuxiuBroadcastMsg;
import com.xiuxiu.chat.ChatPage;
import com.xiuxiu.db.XiuxiuBroadcastMsgTable;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgTable;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.utils.XiuxiuUtils;

/**
 * Created by huzhi on 16-8-11.
 */
public class XiuxiuBroadcastListPage extends BaseActivity implements View.OnClickListener,AbsListView.OnScrollListener {

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,XiuxiuBroadcastListPage.class);
        ac.startActivity(intent);
    }

    private ListView mList;

    private BroadcastListAdapter adapter;

    private static final int PAGE_SIZE = 10;

    private int mPageSize = 0;

    private LoadMoreLayout mRefreshLayout;

    XiuxiuBroadcastManager.XiuxiuBroadcastMsgObserver observer = new XiuxiuBroadcastManager.XiuxiuBroadcastMsgObserver() {
        @Override
        public void onBroadcastMsgNofify(XiuxiuBroadcastMsg msg) {
            int count = mPageSize + 1;
            initData(count,null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_list_page_layout);
        setUpViews();
        XiuxiuBroadcastManager.getInstance().registObserver(observer);
        initData(PAGE_SIZE,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XiuxiuBroadcastManager.getInstance().unregistObserver(observer);
    }

    private void setUpViews(){
        mList = (ListView) findViewById(R.id.list);
        adapter = new BroadcastListAdapter(getApplicationContext(),null);
        mList.setAdapter(adapter);
        mList.setOnScrollListener(this);
        findViewById(R.id.back).setOnClickListener(this);
        mRefreshLayout = new LoadMoreLayout(getApplicationContext());
        mList.addFooterView(mRefreshLayout);
        mRefreshLayout.setVisibility(View.GONE);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String username = adapter.getCursor().getString(adapter.getCursor().getColumnIndex(XiuxiuBroadcastMsgTable._FROM_XIUXIU_ID));
                    // 进入聊天页面
                    ChatPage.startActivity(MainActivity.getInstance(), username, username, false);
                } catch (Exception e) {
                }
            }
        });

    }

    private void initData(int size, BroadcastLoaderManager.Callback callback){
        if(callback==null){
            callback = new BroadcastLoaderManager.Callback() {
                @Override
                public void onSuccess(Cursor cursor) {
                    mPageSize = cursor.getCount();
                    Cursor olderCursor = adapter.swapCursor(cursor);
                    if(olderCursor!=null){
                        olderCursor.close();
                    }
                    adapter.notifyDataSetChanged();
                    XiuxiuUtils.saveXiuxiuBroadcastPrompt(false);
                }
            };
        }
        BroadcastLoaderManager.getInstance().syncData(size, callback);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(totalItemCount<=5){
            return;
        }
        if(view.getLastVisiblePosition() == totalItemCount -1){
            if(mRefreshLayout.getVisibility() == View.VISIBLE || !isScrollEnable){
                return;
            }
            mRefreshLayout.setVisibility(View.VISIBLE);
            mRefreshLayout.showProgress();
            int count = mPageSize + PAGE_SIZE;
            initData(count, new BroadcastLoaderManager.Callback() {
                @Override
                public void onSuccess(Cursor cursor) {
                    if (cursor != null && cursor.getCount() <= mPageSize) {
                        mRefreshLayout.setVisibility(View.VISIBLE);
                        mRefreshLayout.showPrompt();
                        isScrollEnable = false;
                        cursor.close();
                    } else if (cursor != null) {
                        mRefreshLayout.setVisibility(View.GONE);
                        mPageSize = cursor.getCount();
                        Cursor olderCursor = adapter.swapCursor(cursor);
                        if (olderCursor != null) {
                            olderCursor.close();
                        }
                    }
                }
            });
        }
    }

    private boolean isScrollEnable = true;
}
