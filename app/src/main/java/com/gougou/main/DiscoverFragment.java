package com.gougou.main;

import android.content.Context;
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
public class DiscoverFragment extends Fragment implements ViewPager.OnPageChangeListener ,PullToRefreshBase.OnRefreshListener, PullToRefreshBase.OnLastItemVisibleListener {

    private ViewGroup mRootView;
    private ViewPager mViewPager;
    private DiscoverPagerAdapter mDiscoverAdapter;

    private OnLineAdapter mOnLineAdapter;
    private CharmAdapter mCharmAdapter;
    private WealthAdapter mWealthAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(mRootView ==null){
            mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_discover, null);
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
        mOnLineAdapter = new OnLineAdapter(getActivity());
        mCharmAdapter = new CharmAdapter(getActivity());
        mWealthAdapter = new WealthAdapter(getActivity());
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
        mOnLineAdapter.addAll(list);
        mCharmAdapter.addAll(list);
        mWealthAdapter.addAll(list);
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
    private class DiscoverPagerAdapter extends LayoutPagerAdapter{

        private static final int PAGE_COUNT = 3;
        public static final int POSITION_ONLINE = 0;
        public static final int POSITION_CHARM = 1;
        public static final int POSITION_WEALTH = 2;

        public DiscoverPagerAdapter() {
            super(PAGE_COUNT);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Context context = container.getContext();
            View itemView = LayoutInflater.from(context).inflate(R.layout.list_layout, container, false);

            ViewHolder holder = new ViewHolder(itemView);
            itemView.setTag(holder);
            PullToRefreshListView pullView = holder.mPullToRefreshListView;
            pullView.setOnRefreshListener(DiscoverFragment.this);
            pullView.setOnLastItemVisibleListener(DiscoverFragment.this);
            ListAdapter adapter = null;
            switch (position){
                case POSITION_ONLINE:
                    adapter = mOnLineAdapter;
                    break;
                case POSITION_CHARM:
                    adapter = mCharmAdapter;
                    pullView.getRefreshableView().addHeaderView(LayoutInflater.from(context).inflate(R.layout.discover_charm_head_layout,null));
                    break;
                case POSITION_WEALTH:
                    adapter = mWealthAdapter;
                    pullView.getRefreshableView().addHeaderView(LayoutInflater.from(context).inflate(R.layout.discover_wealth_head_layout,null));
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
                case POSITION_ONLINE:
                    res = R.string.discover_online;
                    break;
                case POSITION_CHARM:
                    res = R.string.discover_charm;
                    break;
                case POSITION_WEALTH:
                    res = R.string.discover_wealth;
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
