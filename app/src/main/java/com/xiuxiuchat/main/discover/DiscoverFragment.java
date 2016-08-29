package com.xiuxiuchat.main.discover;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiuxiuchat.R;
import com.xiuxiuchat.bean.Friend;
import com.xiuxiuchat.Xiuxiubroadcast.XiuxiuBroadCastPage;
import com.xiuxiuchat.widget.LayoutPagerAdapter;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import viewpagerindicator.PageIndicator;


/**
 * Created by huzhi on 16-3-21.
 */
public class DiscoverFragment extends Fragment implements
        ViewPager.OnPageChangeListener ,PullToRefreshBase.OnRefreshListener, PullToRefreshBase.OnLastItemVisibleListener ,View.OnClickListener{

    private ViewGroup mRootView;
    private ViewPager mViewPager;
    private DiscoverPagerAdapter mDiscoverAdapter;

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
        mViewPager.setAdapter(mDiscoverAdapter);
        PageIndicator indicator = (PageIndicator) mRootView.findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
        indicator.setOnPageChangeListener(this);
        mRootView.findViewById(R.id.heixiu_broadcast).setOnClickListener(this);
    }

    private void initData() {
        List<Friend> list = new ArrayList<Friend>();
        for(int i=0;i<10;i++){
            list.add(new Friend());
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        if(position==0){
            mRootView.findViewById(R.id.heixiu_broadcast).setVisibility(View.VISIBLE);
        }else{
            mRootView.findViewById(R.id.heixiu_broadcast).setVisibility(View.GONE);
        }
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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.heixiu_broadcast: //咻咻
                XiuxiuBroadCastPage.startActivity(getActivity());
                break;
        }
    }

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
            switch (position){
                case POSITION_ONLINE:
                    container.addView(OnLineListManager.getInstance(getActivity()).getLayout());
                    return OnLineListManager.getInstance(getActivity()).getLayout();
                case POSITION_CHARM:
                    container.addView(CharmListManager.getInstance(getActivity()).getLayout());
                    return CharmListManager.getInstance(getActivity()).getLayout();
                case POSITION_WEALTH:
                    container.addView(WealthListManager.getInstance(getActivity()).getLayout());
                    return WealthListManager.getInstance(getActivity()).getLayout();
            }
            container.addView(null);
            return null;
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
