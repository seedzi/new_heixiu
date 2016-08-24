package com.xiuxiu.main.chat;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.util.EMLog;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.Xiuxiubroadcast.XiuxiuBroadcastHeadLayouy;
import com.xiuxiu.Xiuxiubroadcast.XiuxiuBroadcastListPage;
import com.xiuxiu.Xiuxiubroadcast.XiuxiuBroadcastManager;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.bean.XiuxiuBroadcastMsg;
import com.xiuxiu.chat.ChatPage;
import com.xiuxiu.db.XiuxiuBroadcastMsgTable;
import com.xiuxiu.easeim.EaseUserCacheManager;
import com.xiuxiu.easeim.ImHelper;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.main.chat.widget.ContactItemView;
import com.xiuxiu.user.invitation.InviteMessgeDao;
import com.xiuxiu.user.invitation.NewFriendsMsgActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 会话列表页面管理类
 */
public class ConversationListManager {
    /**
     *
     */
    private ContactSyncListener contactSyncListener;

    private static ConversationListManager mInstance;

    public static ConversationListManager getInstance(){
        if(mInstance == null){
            mInstance = new ConversationListManager();
        }
        return mInstance;
    }

    public View getLayout(){
        return mLayout;
    }

    private ViewGroup mLayout;

    private PullToRefreshListView mPullToRefreshListView;

    private View mEmptyView;

    private View mLoadingView;

    private ConversationListAdapter adapter;

    private InviteMessgeDao inviteMessgeDao;

    private Context mContext;
    /**
     * 列表头部好友申请控件
     */
    private ContactItemView applicationItem;
    /**
     * 列表头部咻咻广播提示
     */
    private XiuxiuBroadcastHeadLayouy mXiuxiuBroadcastItem;

    // ===================================== 刷新罗辑 start =========================================//
    protected Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    onConnectionDisconnected();
                    break;
                case 1:
                    onConnectionConnected();
                    break;

                case 2:
                {
                    passedListRef.clear();
                    passedListRef = loadConversationList();
                    if (adapter != null) {
                        adapter.clear();
                        conversationList.clear();
                        conversationList.addAll(passedListRef);
                        adapter.notifyDataSetChanged();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * 连接到服务器
     */
    protected void onConnectionConnected(){
//        errorItemContainer.setVisibility(View.GONE);
    }

    /**
     * 连接断开
     */
    protected void onConnectionDisconnected(){
//        errorItemContainer.setVisibility(View.VISIBLE);
    }

    /**
     * 刷新页面
     */
    public void refresh() {
        /**
         * １刷新信息列表
         */
        if(!handler.hasMessages(2)){
            handler.sendEmptyMessage(2);
        }
        /**
         * 刷新加好友列表
         */
        if(inviteMessgeDao == null){
            inviteMessgeDao = new InviteMessgeDao(mContext);
        }
        if(inviteMessgeDao.getUnreadMessagesCount() > 0){
            applicationItem.showUnreadMsgView();
        }else{
            applicationItem.hideUnreadMsgView();
        }
    }
// ======================================== 刷新罗辑 end ============================================//

    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();

    protected List<EMConversation> passedListRef = null;

    public void init(Context context){
        setUpViews(context);
        initData(context);
    }

    private void setUpViews(Context context){
        mContext = context;
        mLayout = (ViewGroup)LayoutInflater.from(context).inflate(R.layout.list_layout, null);
        mPullToRefreshListView = (PullToRefreshListView) mLayout.findViewById(R.id.list);
        mEmptyView = mLayout.findViewById(R.id.empty_view);
        mLoadingView = mLayout.findViewById(R.id.loading_layout);
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mPullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    EMConversation conversation = adapter.getItem(position - 2);
                    String username = conversation.getUserName();
                    if (username.equals(EMClient.getInstance().getCurrentUser()))
                        Toast.makeText(MainActivity.getInstance(), R.string.Cant_chat_with_yourself, 0).show();
                    else {
                        // 进入聊天页面
                        ChatPage.startActivity(MainActivity.getInstance(), username, username, false);
                    }
                } catch (Exception e) {
                }
            }
        });

        // 添加咻咻广播提示　和　好友申请
        applicationItem = new ContactItemView(mContext);
        applicationItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // 进入申请与通知页面
                Intent intent = new Intent(mContext, NewFriendsMsgActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        mXiuxiuBroadcastItem = (XiuxiuBroadcastHeadLayouy) LayoutInflater.from(mContext).inflate(R.layout.xiuxiu_broadcast_head_layout,null);
        mXiuxiuBroadcastItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进入申请与通知页面
                Intent intent = new Intent(mContext, XiuxiuBroadcastListPage.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        });
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ll.addView(mXiuxiuBroadcastItem);
        ll.addView(applicationItem);
        mPullToRefreshListView.getRefreshableView().addHeaderView(ll);

        XiuxiuBroadcastManager.getInstance().registObserver(observer);

        // 初始化数据
        Cursor cursor =XiuxiuBroadcastMsgTable.queryCursor(1);
        if(cursor!=null && cursor.moveToFirst()){
            XiuxiuBroadcastMsg msg = XiuxiuBroadcastMsg.fromCursor2Bean(cursor);
            setupXiuxiuBroadcastLayout(msg);
        }
    }

    private void initData(Context context){
        passedListRef = loadConversationList();
        if(passedListRef!=null)
        conversationList.addAll(passedListRef);
        adapter = new ConversationListAdapter(context, conversationList);
        mPullToRefreshListView.getRefreshableView().setAdapter(adapter);
    }

    /**
     * 获取会话列表
     *
     * @param context
     * @return
    +    */
    protected List<EMConversation> loadConversationList(){
        // 获取所有会话，包括陌生人
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param usernames
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {
                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }
        });
    }

    class ContactSyncListener implements ImHelper.DataSyncListener {
        @Override
        public void onSyncComplete(final boolean success) {
//            EMLog.d(TAG, "on contact list sync success:" + success);
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    if (success) {
//                        loadingView.setVisibility(View.GONE);
                        refresh();
                    } else {
                        /*
                        String s1 = getResources().getString(R.string.get_failed_please_check);
                        Toast.makeText(getActivity(), s1, 1).show();
                        loadingView.setVisibility(View.GONE);
                        */
                    }
                }
            });
        }
    }



    XiuxiuBroadcastManager.XiuxiuBroadcastMsgObserver observer = new XiuxiuBroadcastManager.XiuxiuBroadcastMsgObserver() {
        @Override
        public void onBroadcastMsgNofify(final XiuxiuBroadcastMsg msg) {
            if(msg!=null){
                XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        setupXiuxiuBroadcastLayout(msg);
                    }
                });
            }
        }
    };

    private void setupXiuxiuBroadcastLayout(XiuxiuBroadcastMsg msg){
        if(msg==null){
            return;
        }
        String content = "";
        String timeTxt = "";
        XiuxiuUserInfoResult info = EaseUserCacheManager.getInstance().getBeanById(msg.fromXiuxiuId);
        if (info != null) {
            content = info.getXiuxiu_name() + ": " + msg.content;
        } else {
            content = msg.fromXiuxiuId + ": " + msg.content;
        }
        timeTxt = com.xiuxiu.utils.DateUtils.getTextByTime(
                XiuxiuApplication.getInstance().getApplicationContext(),
                msg.updateTime,
                R.string.date_fromate_anecdote);
        mXiuxiuBroadcastItem.update(content, timeTxt);
    }

    public void onDestroy() {
        XiuxiuBroadcastManager.getInstance().unregistObserver(observer);
    }
}
