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
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by huzhi on 16-5-23.
 */
public class OnLineListManager implements  PullToRefreshBase.OnRefreshListener {

    private static String TAG = "OnLineListManager";

    private static OnLineListManager mInstance;

    public static OnLineListManager getInstance(FragmentActivity ac){
        if(mInstance == null){
            mInstance = new OnLineListManager(ac);
        }
        return mInstance;
    }

    private OnLineListManager(FragmentActivity ac){
        mAc = ac;
        init(ac);
    }

    public ViewGroup getLayout(){
        return mLayout;
    }

    private ViewGroup mLayout;

    private PullToRefreshListView mPullToRefreshListView;

    private View mEmptyView;

    private View mLoadingView;

    private OnLineAdapter adapter;

    FragmentActivity mAc;

    private void init(FragmentActivity context){
        setUpViews(context);
        initData(context);
        startLoop();
    }

    private void setUpViews(Context context){
        mLayout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.list_layout,null);
        mPullToRefreshListView = (PullToRefreshListView) mLayout.findViewById(R.id.list);
        mEmptyView = mLayout.findViewById(R.id.empty_view);
        mLoadingView = mLayout.findViewById(R.id.loading_layout);
        mPullToRefreshListView.setVisibility(View.VISIBLE);
        mEmptyView.setVisibility(View.GONE);
        mLoadingView.setVisibility(View.GONE);
        mPullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                XiuxiuUserInfoResult xiuxiuUser = adapter.getItem(position-1);
                PersonDetailActivity.startActivity(MainActivity.getInstance(), xiuxiuUser.getXiuxiu_id(),false);
            }
        });
        adapter = new OnLineAdapter(mAc);
        mPullToRefreshListView.getRefreshableView().setAdapter(adapter);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                test();
            }
        });
    }
    private void initData(Context context){
        android.util.Log.d(TAG,"initData");
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
        queryActiveUsers();
    }
    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            android.util.Log.d(TAG," 获取活跃用户id  response = " + response);
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
            ToastUtil.showMessage(MainActivity.getInstance(),"获取数据失败!");
            mPullToRefreshListView.onRefreshComplete();
        }
    };

    private void queryActiveUsers() {
        android.util.Log.d(TAG, "getTopicListUrl() = " + getTopicListUrl());
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getTopicListUrl(), mRefreshListener, mRefreshErroListener));
    }
    private String getTopicListUrl() {
        return Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_ACTIVE_USER)
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
                        return (int)(arg1.getActive_time() - arg0.getActive_time());
                    }
                });
                adapter.clear();
                adapter.addAll(xiuxiuUserInfoResult.getUserinfos());
                adapter.notifyDataSetChanged();
            }
            mPullToRefreshListView.onRefreshComplete();
        }
    };
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

    // ===============================================================================================
    // 每隔5分钟刷新一次列表
    // ===============================================================================================
    private static final long LOOP_TIME = 5*60*1000;
    private Timer timer= new Timer();
    private TimerTask task = new TimerTask(){
        public void run(){
            // 在此处添加执行的代码 
            XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                @Override
                public void run() {
                    mPullToRefreshListView.setRefreshing();
                }
            });
        }
    };
    public void startLoop(){
        timer.schedule(task, LOOP_TIME, LOOP_TIME);//开启定时器
    }

    public void destory(){
        if(timer!=null) {
            timer.cancel();
        }
    }
}
