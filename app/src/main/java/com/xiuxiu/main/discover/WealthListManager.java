package com.xiuxiu.main.discover;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuActiveUserResult;
import com.xiuxiu.api.XiuxiuAllUserResult;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuPerson;
import com.xiuxiu.api.XiuxiuQueryActiveUserResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.api.XiuxiuUserQueryResult;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.PersonDetailActivity;
import com.xiuxiu.utils.Md5Util;
import com.xiuxiu.utils.ToastUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by huzhi on 16-5-23.
 */
public class WealthListManager implements PullToRefreshBase.OnRefreshListener {

    private static final String TAG = "WealthListManager";

    private static WealthListManager mInstance;

    public static WealthListManager getInstance(FragmentActivity ac){
        if(mInstance == null){
            mInstance = new WealthListManager(ac);
        }
        return mInstance;
    }

    private WealthListManager(FragmentActivity ac){
        mAc = ac;
        setUpViews(ac);
        initData(ac);
    }

    public ViewGroup getLayout(){
        return mLayout;
    }

    private ViewGroup mLayout;

    private PullToRefreshListView mPullToRefreshListView;

    private View mEmptyView;

    private View mLoadingView;

    private FragmentActivity mAc;

    private WealthAdapter adapter;

    private WealthHeadLayout mWealthHeadLayout;

    private void setUpViews(Context context){
        mLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.list_layout, null);
        mPullToRefreshListView = (PullToRefreshListView) mLayout.findViewById(R.id.list);
        mEmptyView = mLayout.findViewById(R.id.empty_view);
        mLoadingView = mLayout.findViewById(R.id.loading_layout);
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mWealthHeadLayout = new WealthHeadLayout(context);
        mPullToRefreshListView.getRefreshableView().
                addHeaderView(mWealthHeadLayout);
        mPullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try{
                    XiuxiuUserInfoResult xiuxiuUser = adapter.getItem(position-2);
                    PersonDetailActivity.startActivity(MainActivity.getInstance(), xiuxiuUser.getXiuxiu_id(), false);
                }catch (Exception e){}
            }
        });
        mPullToRefreshListView.setOnRefreshListener(this);
        adapter = new WealthAdapter(context);
        mWealthHeadLayout.setActivity(mAc);
        mPullToRefreshListView.getRefreshableView().setAdapter(adapter);

    }


    private void initData(Context context){
        test();
    }

    @Override
    public void onRefresh(PullToRefreshBase refreshView) {
        test();
    }


    // ===============================================================================================
    // 获取活跃用户id
    // ===============================================================================================

    private void test(){
        queryCharmUsers();
    }
    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            android.util.Log.d(TAG," 获取财富排行用户id  response = " + response);
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
                .appendQueryParameter("m", HttpUrlManager.QUERY_FORTUNR_USER)
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
            android.util.Log.d(TAG,"获取财富排行用户 response = " + response);
            Gson gson = new Gson();
            XiuxiuUserQueryResult xiuxiuUserInfoResult = gson.fromJson(response, XiuxiuUserQueryResult.class);
            if(xiuxiuUserInfoResult!=null && xiuxiuUserInfoResult.getUserinfos()!=null){
                Collections.sort(xiuxiuUserInfoResult.getUserinfos(), new Comparator<XiuxiuUserInfoResult>() {
                    public int compare(XiuxiuUserInfoResult arg0, XiuxiuUserInfoResult arg1) {
                        return (int) (arg1.getActive_time() - arg0.getActive_time());
                    }
                });
                setHeadItem(xiuxiuUserInfoResult.getUserinfos(), 0);
                setHeadItem(xiuxiuUserInfoResult.getUserinfos(), 1);
                setHeadItem(xiuxiuUserInfoResult.getUserinfos(), 2);
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
            mWealthHeadLayout.setItem(xiuxiuInfo, position);
        }
    }

    private Response.ErrorListener mUserInfoErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            ToastUtil.showMessage(MainActivity.getInstance(),"获取数据失败!");
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
