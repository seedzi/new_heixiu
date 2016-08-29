package com.xiuxiuchat.Xiuxiubroadcast;

import android.net.Uri;
import android.text.TextUtils;

import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuActiveUserResult;
import com.xiuxiuchat.api.XiuxiuApi;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.api.XiuxiuQueryActiveUserResult;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.api.XiuxiuUserQueryResult;
import com.xiuxiuchat.bean.XiuxiuBroadcastMsg;
import com.xiuxiuchat.db.XiuxiuBroadcastMsgTable;
import com.xiuxiuchat.easeim.EaseUserCacheManager;
import com.xiuxiuchat.utils.Md5Util;
import com.xiuxiuchat.utils.XiuxiuUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by huzhi on 16-7-25.
 */
public class XiuxiuBroadcastManager {

    private static final String TAG = "XiuxiuBroadcastManager";

    private static XiuxiuBroadcastManager mInstance;

    public static XiuxiuBroadcastManager getInstance(){
        if(mInstance == null){
            mInstance = new XiuxiuBroadcastManager();
        }
        return mInstance;
    }

    private XiuxiuBroadcastManager(){}

    /**
     * 发送咻咻广播
     * @param list
     * @param txt
     */
    public static void sendXiuxiuBroadcast(List<XiuxiuUserInfoResult> list,String txt){
        if(list==null){
            return;
        }
        for(XiuxiuUserInfoResult user:list){
            EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
            String action = EaseConstant.MESSAGE_ATTR_XIUXIU_BROADCAST_ACTION;
            EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
            cmdMsg.setReceipt(user.getXiuxiu_id());
            cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_BROADCAST, true);
            cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_BROADCAST_CONTENT,txt);
            cmdMsg.addBody(cmdBody);
            EMClient.getInstance().chatManager().sendMessage(cmdMsg);
        }
    }

    public List<XiuxiuUserInfoResult> getXiuxiuBroadcastUserList() {
        String params = getActiveXiuxiuIds();
        if(!TextUtils.isEmpty(params)){
            return queryUserList(params);
        }
        return null;
    }

    // =============================================================================================
    // 获取活跃用户id
    // =============================================================================================
    private String getActiveXiuxiuIds(){
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getActiveUserIdsUrl(), future, future));
        try {
            String response = future.get();
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
                    return params;
                }
            }catch (Exception e){}
        } catch (Exception e) {
            android.util.Log.d(TAG,"queryUserInfoList() e = " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    private String getActiveUserIdsUrl() {
        return Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_ACTIVE_USER)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("offset", "0")
                .appendQueryParameter("count", "10")
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("user_id",XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .build().toString();
    }



    // =============================================================================================
    // 获取用户信息
    // =============================================================================================
    private List<XiuxiuUserInfoResult> queryUserList(String xiuxiuIds){
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getQueryUserListUrl(xiuxiuIds), future, future));
        try {
            String response = future.get();
            Gson gson = new Gson();
            XiuxiuUserQueryResult xiuxiuUserInfoResult = gson.fromJson(response, XiuxiuUserQueryResult.class);
            if(xiuxiuUserInfoResult!=null && xiuxiuUserInfoResult.getUserinfos()!=null){
                Collections.sort(xiuxiuUserInfoResult.getUserinfos(), new Comparator<XiuxiuUserInfoResult>() {
                    public int compare(XiuxiuUserInfoResult arg0, XiuxiuUserInfoResult arg1) {
                        return (int) (arg1.getActive_time() - arg0.getActive_time());
                    }
                });
                return xiuxiuUserInfoResult.getUserinfos();
            }
        }catch (Exception e){}
        return null;
    }

    private String getQueryUserListUrl(String xiuxiuIds){
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_BATCH_USERINFOS)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("cookie",XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("user_id",XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_ids", xiuxiuIds)
                .build().toString();
        return url;
    }

    // =============================================================================================
    // 处理接受到的咻咻广播消息
    // =============================================================================================
    public void saveXiuxiuBroadcastMsg(final EMMessage message){
        if(message==null){
            return;
        }
        XiuxiuUtils.runInNewThread(new Runnable() {
            @Override
            public void run() {
                //1.保持咻咻
                XiuxiuBroadcastMsgTable.insert(XiuxiuBroadcastMsg.fromEmMessage2Bean(message));
                //2.获取用户信息
                if(EaseUserCacheManager.getInstance().getBeanById(message.getFrom())==null){
                    XiuxiuApi.queryUserInfoSyn(message.getUserName());
                }
                XiuxiuUtils.saveXiuxiuBroadcastPrompt(true);
                //3.通知刷新
                notifyBroadcastMsg(XiuxiuBroadcastMsg.fromEmMessage2Bean(message));
            }
        });

    }
    // =============================================================================================
    // 咻咻消息的广播接受者
    // =============================================================================================

    private List<XiuxiuBroadcastMsgObserver> mAppUpdateObservers = new ArrayList<XiuxiuBroadcastMsgObserver>();

    public void notifyBroadcastMsg(XiuxiuBroadcastMsg msg) {
        for (XiuxiuBroadcastMsgObserver observer : mAppUpdateObservers) {
            observer.onBroadcastMsgNofify(msg);
        }
    }

    public void registObserver(XiuxiuBroadcastMsgObserver observer) {
        if (observer == null || mAppUpdateObservers.contains(observer)) {
            return;
        }
        mAppUpdateObservers.add(observer);
    }

    public void unregistObserver(XiuxiuBroadcastMsgObserver observer) {
        mAppUpdateObservers.remove(observer);
    }

    public void cleanAllObserver() {
        mAppUpdateObservers.clear();
    }

    public static interface XiuxiuBroadcastMsgObserver {
        void onBroadcastMsgNofify(XiuxiuBroadcastMsg msg);
    }

    // =============================================================================================
    // 咻咻消息的广播的sharedprefenece
    // =============================================================================================
    public boolean isBroadCastPrompt(){
        return false;
    }

}
