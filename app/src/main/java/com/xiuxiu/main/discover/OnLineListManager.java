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
import com.xiuxiu.api.XiuxiuAllUserResult;
import com.xiuxiu.api.XiuxiuPerson;
import com.xiuxiu.bean.ChatNickNameAndAvatarBean;
import com.xiuxiu.chat.ChatPage;
import com.xiuxiu.discover.OnLineAdapter;
import com.xiuxiu.easeim.ChatNickNameAndAvatarCacheManager;
import com.xiuxiu.utils.Md5Util;

/**
 * Created by huzhi on 16-5-23.
 */
public class OnLineListManager implements  PullToRefreshBase.OnRefreshListener {

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
                XiuxiuPerson xiuxiuUser = adapter.getItem(position-1);
                ChatPage.startActivity(mAc, xiuxiuUser.getXiuxiu_id(), xiuxiuUser.getXiuxiu_name());
                ChatNickNameAndAvatarBean info = new ChatNickNameAndAvatarBean();
                info.setAvatar("");
                info.setXiuxiu_id(xiuxiuUser.getXiuxiu_id());
                info.setNickName(xiuxiuUser.getXiuxiu_name());
                ChatNickNameAndAvatarCacheManager.getInstance().add(info);
            }
        });
        adapter = new OnLineAdapter(mAc);
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
    // 获取在线用户
    // ===============================================================================================

    private void test(){
        queryAllUsers();
    }
    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            XiuxiuAllUserResult result = gson.fromJson(response, XiuxiuAllUserResult.class);
            if(result!=null && result.getUserinfos()!=null){
                adapter.clear();
                adapter.addAll(result.getUserinfos());
                adapter.notifyDataSetChanged();
            }
            mPullToRefreshListView.onRefreshComplete();
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            mPullToRefreshListView.onRefreshComplete();
        }
    };

    private void queryAllUsers() {
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getTopicListUrl(), mRefreshListener, mRefreshErroListener));
    }
    private String getTopicListUrl() {
        return Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_USER_INFO)
                .appendQueryParameter("password", Md5Util.md5())
                .build().toString();
    }
}
