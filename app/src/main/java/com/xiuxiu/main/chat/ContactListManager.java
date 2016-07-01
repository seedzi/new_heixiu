package com.xiuxiu.main.chat;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.domain.EaseUser;
import com.xiuxiu.R;
import com.xiuxiu.easeim.ImHelper;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.PersonDetailActivity;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by huzhi on 16-5-24.
 */
public class ContactListManager {

    private static String TAG = ContactListManager.class.getSimpleName();

    private ContactListManager(){
    }

    private static ContactListManager mInstance;

    public static ContactListManager getInstance(){
        if(mInstance == null){
            mInstance = new ContactListManager();
        }
        return mInstance;
    }

    private Context mContext;

    private ViewGroup mLayout;

    private PullToRefreshListView mPullToRefreshListView;

    private View mEmptyView;

    private View mLoadingView;

    private Map<String, EaseUser> contactsMap;

    protected List<EaseUser> contactList;

    private ContactInfoSyncListener contactInfoSyncListener;


    private ContactSyncListener contactSyncListener;

    private ContactListAdapter adapter;

    protected boolean isConflict;

    static final int MSG_UPDATE_LIST = 0;

    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_LIST:
                    if(adapter != null){
                        adapter.clear();
                        adapter.addAll(new ArrayList<EaseUser>(contactList));
                        adapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public View getLayout(){
        return mLayout;
    }

    public void init(Context context){
        initViews(context);
        setUpViews(context);
    }

    private void initViews(Context context){
        mContext = context;
        mLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.list_layout, null);
        mPullToRefreshListView = (PullToRefreshListView) mLayout.findViewById(R.id.list);
        mEmptyView = mLayout.findViewById(R.id.empty_view);
        mLoadingView = mLayout.findViewById(R.id.loading_layout);
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
    }

    public void refresh() {
        android.util.Log.d("cccc","refresh()");
        Map<String, EaseUser> m = ImHelper.getInstance().getContactList();
        if (m instanceof Hashtable<?, ?>) {
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>)m).clone();
        }
        setContactsMap(m);

        getContactList();
        refreshListView();
    }

    private void setUpViews(Context context){
        //设置联系人数据
        Map<String, EaseUser> m = ImHelper.getInstance().getContactList();
        if (m instanceof Hashtable<?, ?>) {
            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>)m).clone();
        }
        setContactsMap(m);

        EMClient.getInstance().addConnectionListener(connectionListener);
        contactList = new ArrayList<EaseUser>();
        // 获取设置contactlist
        getContactList();
        //init list
        adapter = new ContactListAdapter(context, 0,new ArrayList<EaseUser>(contactList));
//        adapter.setPrimaryColor(primaryColor).setPrimarySize(primarySize).setInitialLetterBg(initialLetterBg)
//                .setInitialLetterColor(initialLetterColor);
        mPullToRefreshListView.getRefreshableView().setAdapter(adapter);

        mPullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                android.util.Log.d("TAG","onItemClick  position = " + position);
                PersonDetailActivity.startActivity(MainActivity.getInstance(), adapter.getItem(position-1).getUsername(), true);
            }
        });

        contactSyncListener = new ContactSyncListener();
        ImHelper.getInstance().addSyncContactListener(contactSyncListener);
        contactInfoSyncListener = new ContactInfoSyncListener();
        ImHelper.getInstance().getUserProfileManager().addSyncContactInfoListener(contactInfoSyncListener);
    }

    public void onDestroy() {
        EMClient.getInstance().removeConnectionListener(connectionListener);
        if (contactSyncListener != null) {
            ImHelper.getInstance().removeSyncContactListener(contactSyncListener);
            contactSyncListener = null;
        }
        /*
        if(contactInfoSyncListener != null){
            DemoHelper.getInstance().getUserProfileManager().removeSyncContactInfoListener(contactInfoSyncListener);
        }
        */
    }

    public void refreshListView(){
        Message msg = handler.obtainMessage(MSG_UPDATE_LIST);
        handler.sendMessage(msg);
    }


    /**
     * 设置需要显示的数据map，key为环信用户id
     * @param contactsMap
     */
    public void setContactsMap(Map<String, EaseUser> contactsMap){
        this.contactsMap = contactsMap;
    }


    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    protected void getContactList() {
        contactList.clear();
        //获取联系人列表
        if(contactsMap == null){
            return;
        }
        synchronized (this.contactsMap) {
            Iterator<Map.Entry<String, EaseUser>> iterator = contactsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, EaseUser> entry = iterator.next();
                EaseUser user = entry.getValue();
                contactList.add(user);
            }
        }
    }


    class ContactSyncListener implements ImHelper.DataSyncListener {
        @Override
        public void onSyncComplete(final boolean success) {
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    if(success){
                        refresh();
                    }
                }
            });
        }
    }

    class ContactInfoSyncListener implements ImHelper.DataSyncListener {

        @Override
        public void onSyncComplete(final boolean success) {
            MainActivity.getInstance().runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    android.util.Log.d(TAG,"ContactInfoSyncListener  RUN");
//                    loadingView.setVisibility(View.GONE);
                    if (success) {
                        android.util.Log.d(TAG,"ContactInfoSyncListener  refresh(); ");
                        refresh();
                    }
                }
            });
        }

    }


    protected EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
                isConflict = true;
            } else {
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    public void run() {
                        onConnectionDisconnected();
                    }

                });
            }
        }

        @Override
        public void onConnected() {
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                public void run() {
                    onConnectionConnected();
                }

            });
        }
    };


    protected void onConnectionDisconnected() {

    }

    protected void onConnectionConnected() {

    }
}
