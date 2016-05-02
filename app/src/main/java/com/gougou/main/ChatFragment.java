package com.gougou.main;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.gougou.R;
import com.gougou.bean.Friend;
import com.gougou.chat.FriendsAdapter;
import com.gougou.chat.MessageAdapter;
import com.gougou.discover.CharmAdapter;
import com.gougou.discover.OnLineAdapter;
import com.gougou.discover.WealthAdapter;
import com.gougou.widget.LayoutPagerAdapter;
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

    private MessageAdapter mMessageAdapter;
    private FriendsAdapter mFriendsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        new MediaPlayer();
        if(mRootView ==null){
            mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_chat, null);
            setUpView();
            initData();
        } else {
            try {
                ((ViewGroup)mRootView.getParent()).removeView(mRootView);
            } catch (Exception e) {
            }
        }
        return mRootView;
    }

    private void setUpView(){
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewpager);
        mDiscoverAdapter = new DiscoverPagerAdapter();
        mMessageAdapter = new MessageAdapter(getActivity());
        mFriendsAdapter = new FriendsAdapter(getActivity());
        mViewPager.setAdapter(mDiscoverAdapter);
        PageIndicator indicator = (PageIndicator) mRootView.findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setOnPageChangeListener(this);
    }

    private void initData() {
        List<Friend> list = new ArrayList<Friend>();
        for(int i=0;i<10;i++){
            list.add(new Friend());
        }
        mMessageAdapter.addAll(list);
        mFriendsAdapter.addAll(list);
    }

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

    // ===============================================================================================
    // Volley callback
    // ===============================================================================================
    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            /*
            try {
                Gson gson = new Gson();
                BizResult res = gson.fromJson(response, BizResult.class);
                if (res.isSuccess()) {
                    mAreaAdapter.clear();
                    mAreaAdapter.addAll(gson.fromJson(res.getMessage(), Area[].class));
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
            mPullView.onRefreshComplete();
            */
        }
    };

    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError arg0) {
//            mPullView.onRefreshComplete();
        }
    };

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
            Context context = container.getContext();
            View itemView = LayoutInflater.from(context).inflate(R.layout.list_layout, container, false);

            View headLayout = LayoutInflater.from(context).inflate(R.layout.friend_list_head_layout,null);

            ViewHolder holder = new ViewHolder(itemView);
            itemView.setTag(holder);
            PullToRefreshListView pullView = holder.mPullToRefreshListView;
            pullView.setOnRefreshListener(ChatFragment.this);
            pullView.setOnLastItemVisibleListener(ChatFragment.this);
            ListAdapter adapter = null;
            switch (position){
                case POSITION_MESSAGE:
                    adapter = mMessageAdapter;
                    break;
                case POSITION_FRIENDS:
                    pullView.getRefreshableView().addHeaderView(headLayout);
                    adapter = mFriendsAdapter;
                    break;
            }
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
}
