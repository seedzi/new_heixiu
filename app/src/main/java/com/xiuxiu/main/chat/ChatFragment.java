package com.xiuxiu.main.chat;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.xiuxiu.R;
import com.xiuxiu.bean.Friend;
import com.xiuxiu.chat.FriendsAdapter;
import com.xiuxiu.chat.MessageAdapter;
import com.xiuxiu.widget.LayoutPagerAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import viewpagerindicator.PageIndicator;

/**
 * Created by huzhi on 16-3-21.
 */
public class ChatFragment extends Fragment implements ViewPager.OnPageChangeListener ,PullToRefreshBase.OnRefreshListener, PullToRefreshBase.OnLastItemVisibleListener {

    private ViewGroup mRootView;
    private ViewPager mViewPager;
    private DiscoverPagerAdapter mDiscoverAdapter;

//    private MessageAdapter mMessageAdapter;
//    private FriendsAdapter mFriendsAdapter;

    /**
     * 会话管理类
     */
    ConversationListManager mConversationListManager;

    /**
     * 联系人管理
     */
    ContactListManager mContactListManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new MediaPlayer();
        if(mRootView ==null){
            mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, null);
            setUpView();
//            initData();
            initListManager();
        } else {
            try {
                ((ViewGroup)mRootView.getParent()).removeView(mRootView);
            } catch (Exception e) {
            }
        }


        return mRootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mConversationListManager!=null){
            mConversationListManager.refresh();
        }
        if(mContactListManager!=null){
            mContactListManager.refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mContactListManager!=null){
            mContactListManager.onDestroy();
        }
    }

    private void setUpView(){
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewpager);
        mDiscoverAdapter = new DiscoverPagerAdapter();
//        mMessageAdapter = new MessageAdapter(getActivity());
//        mFriendsAdapter = new FriendsAdapter(getActivity());
        mViewPager.setAdapter(mDiscoverAdapter);
        PageIndicator indicator = (PageIndicator) mRootView.findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setOnPageChangeListener(this);
    }
    /*
    private void initData() {
        List<Friend> list = new ArrayList<Friend>();
        for(int i=0;i<10;i++){
            list.add(new Friend());
        }
        mMessageAdapter.addAll(list);
        mFriendsAdapter.addAll(list);

        initListManager();
    }*/

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onLastItemVisible() {
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {

    }

    private void initListManager(){
        mConversationListManager = ConversationListManager.getInstance();
        mConversationListManager.init(getActivity().getApplication());

        mContactListManager = ContactListManager.getInstance();
        mContactListManager.init(getActivity().getApplication());

    }

    // ===============================================================================================
    // page adpter
    // ===============================================================================================
    private class DiscoverPagerAdapter extends LayoutPagerAdapter {

        private static final int PAGE_COUNT = 2;
        public static final int POSITION_MESSAGE = 0;
        public static final int POSITION_FRIENDS = 1;

        public DiscoverPagerAdapter() {
            super(PAGE_COUNT);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            switch (position){
                case POSITION_MESSAGE:
                    container.addView(mConversationListManager.getLayout());
                    return mConversationListManager.getLayout();
                case POSITION_FRIENDS:
                    container.addView(mContactListManager.getLayout());
                    return mContactListManager.getLayout();
                    /*
                    Context context = container.getContext();
                    View itemView = LayoutInflater.from(context).inflate(R.layout.list_layout, container, false);

                    View headLayout = LayoutInflater.from(context).inflate(R.layout.friend_list_head_layout,null);

                    ViewHolder holder = new ViewHolder(itemView);
                    itemView.setTag(holder);
                    PullToRefreshListView pullView = holder.mPullToRefreshListView;
                    pullView.setOnRefreshListener(ChatFragment.this);
                    pullView.setOnLastItemVisibleListener(ChatFragment.this);
                    ListAdapter adapter = null;
                    pullView.getRefreshableView().addHeaderView(headLayout);
                    adapter = mFriendsAdapter;
                    pullView.getRefreshableView().setAdapter(adapter);

                    View emptyView = holder.mEmptyView;
                    emptyView.findViewById(R.id.reload_button).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                    initData(position);
                        }
                    });
                    container.addView(itemView);
                    return itemView;
                    */
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int res = -1;
            switch (position){
                case POSITION_MESSAGE:
                    res = R.string.chat_message;
                    break;
                case POSITION_FRIENDS:
                    res = R.string.chat_friends;
                    break;
            }
            return getString(res);
        }

        private class ViewHolder{

            public ViewHolder(View itemView) {
                mPullToRefreshListView = (PullToRefreshListView) itemView.findViewById(R.id.list);
                mEmptyView = itemView.findViewById(R.id.empty_view);
                mLoadingView = itemView.findViewById(R.id.loading_layout);
                mPullToRefreshListView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.GONE);
                mLoadingView.setVisibility(View.GONE);
            }

            private PullToRefreshListView mPullToRefreshListView;
            private View mLoadingView;
            private View mEmptyView;
        }
    }

    private void test(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    List<String> usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    android.util.Log.d("ccc","e = " +e.getMessage());
                }
            }
        });
    }
}
