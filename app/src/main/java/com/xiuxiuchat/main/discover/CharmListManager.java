package com.xiuxiuchat.main.discover;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xiuxiuchat.R;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuActiveUserResult;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.api.XiuxiuQueryActiveUserResult;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.api.XiuxiuUserQueryResult;
import com.xiuxiuchat.main.MainActivity;
import com.xiuxiuchat.user.PersonDetailActivity;
import com.xiuxiuchat.utils.Md5Util;
import com.xiuxiuchat.utils.ToastUtil;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by huzhi on 16-5-23.
 */
public class CharmListManager implements  PullToRefreshBase.OnRefreshListener {

    private static final String TAG = "CharmListManager";

    private static CharmListManager mInstance;

    public static CharmListManager getInstance(FragmentActivity ac){
        if(mInstance == null){
            mInstance = new CharmListManager(ac);
        }
        return mInstance;
    }

    private CharmListManager(FragmentActivity ac){
        mAc = ac;
        init(ac);
    }

    public ViewGroup getLayout(){
        return mLayout;
    }

    private FragmentActivity mAc;

    private ViewGroup mLayout;

    private PullToRefreshListView mPullToRefreshListView;

    private View mEmptyView;

    private View mLoadingView;

    private CharmAdapter adapter;

    private CharmHeadLayout mCharmHeadLayout;

    private void init(FragmentActivity context){
        setUpViews(context);
        initData(context);
    }

    private void setUpViews(Context context){
        mLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.list_layout, null);
        mPullToRefreshListView = (PullToRefreshListView) mLayout.findViewById(R.id.list);
        mEmptyView = mLayout.findViewById(R.id.empty_view);
        mLoadingView = mLayout.findViewById(R.id.loading_layout);
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mCharmHeadLayout = new CharmHeadLayout(context);
        mPullToRefreshListView.getRefreshableView().
                addHeaderView(mCharmHeadLayout);
        mPullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    android.util.Log.d(TAG,"position = " + position);
                    XiuxiuUserInfoResult xiuxiuUser = adapter.getItem(position-2);
                    PersonDetailActivity.startActivity(MainActivity.getInstance(), xiuxiuUser.getXiuxiu_id(), false);
                }catch (Exception e){
                    android.util.Log.d(TAG,"exception = " + e.toString());
                }
            }
        });

        adapter = new CharmAdapter(context);
        mCharmHeadLayout.setActivity(mAc);
        mPullToRefreshListView.getRefreshableView().setAdapter(adapter);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                test();
            }
        });
    }

    private void initData(Context context){
        test();
    }


    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        test();
    }

    // ===============================================================================================
    // 获取在线用户
    // ===============================================================================================
    // ===============================================================================================
    // 获取活跃用户id
    // ===============================================================================================

    private void test(){
        queryCharmUsers();
    }
    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            android.util.Log.d(TAG," 获取魅力排行用户id  response = " + response);
            Gson gson = new Gson();
            try {
                XiuxiuQueryActiveUserResult result = gson.fromJson(response, XiuxiuQueryActiveUserResult.class);
                android.util.Log.d(TAG, "onResponse result.activeUsers = " + result.activeUsers);
                if(result!=null && result.activeUsers!=null){
                    String params = "";
                    int position = 0;
                    for(XiuxiuActiveUserResult item:result.activeUsers){
                        if(position==0){
                            params = item.getXiuxiu_id();
                        }else{
                            params = params + "," + item.getXiuxiu_id();
                        }
                        position ++;
                    }
                    queryUserInfos(params);
                }else{
                    mPullToRefreshListView.onRefreshComplete();
                }
            }catch (Exception e){

            }
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.d(TAG,"onErrorResponse error = " + error.getMessage() +","
                    + error.toString() + ",error.getCause() =" + error.getCause());
            ToastUtil.showMessage(MainActivity.getInstance(), "获取数据失败!");
            mPullToRefreshListView.onRefreshComplete();
        }
    };

    private void queryCharmUsers() {
        android.util.Log.d(TAG, "getTopicListUrl() = " + getTopicListUrl());
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getTopicListUrl(), mRefreshListener, mRefreshErroListener));
    }
    private String getTopicListUrl() {
        return Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_CHARM_USER)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("offset", "0")
                .appendQueryParameter("count", "100")
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("user_id",XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .build().toString();
    }

    // ===============================================================================================
    // 获取在线用户
    // ===============================================================================================

    private Response.Listener<String> mUserInfoListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            android.util.Log.d(TAG,"获取在线用户 response = " + response);
            Gson gson = new Gson();
            XiuxiuUserQueryResult xiuxiuUserInfoResult = gson.fromJson(response, XiuxiuUserQueryResult.class);
            if(xiuxiuUserInfoResult!=null && xiuxiuUserInfoResult.getUserinfos()!=null){
                Collections.sort(xiuxiuUserInfoResult.getUserinfos(), new Comparator<XiuxiuUserInfoResult>() {
                    public int compare(XiuxiuUserInfoResult arg0, XiuxiuUserInfoResult arg1) {
                        return (int) (arg1.getActive_time() - arg0.getActive_time());
                    }
                });
                setHeadItem(xiuxiuUserInfoResult.getUserinfos(), 0);
                setHeadItem(xiuxiuUserInfoResult.getUserinfos(),1);
                setHeadItem(xiuxiuUserInfoResult.getUserinfos(),2);
                adapter.clear();
                adapter.addAll(xiuxiuUserInfoResult.getUserinfos());
                adapter.notifyDataSetChanged();
            }
            mPullToRefreshListView.onRefreshComplete();
        }
    };

    private void setHeadItem(List<XiuxiuUserInfoResult> list,int position){
        if(list!=null && list.size()>0){
            XiuxiuUserInfoResult xiuxiuInfo = list.remove(0);
            mCharmHeadLayout.setItem(xiuxiuInfo, position);
        }
    }


    private Response.ErrorListener mUserInfoErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.d(TAG,"error = " + error.getMessage());
            ToastUtil.showMessage(MainActivity.getInstance(), "获取数据失败!");
            mPullToRefreshListView.onRefreshComplete();
        }
    };

    private void queryUserInfos(String xiuxiuIds){
        android.util.Log.d(TAG,"queryUserInfos() = " + getQueryUserListUrl(xiuxiuIds));
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getQueryUserListUrl(xiuxiuIds), mUserInfoListener, mUserInfoErroListener));
    }

    private String getQueryUserListUrl(String xiuxiuIds){
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_BATCH_USERINFOS)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("cookie",/*"test"*/XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("user_id",/*"test"*/XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_ids", xiuxiuIds)
                .build().toString();
        return url;
    }

}
