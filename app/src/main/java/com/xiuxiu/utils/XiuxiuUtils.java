package com.xiuxiu.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.util.PathUtil;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuResult;
import com.xiuxiu.api.XiuxiuTimsResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.api.XiuxiuUserQueryResult;
import com.xiuxiu.db.XiuxiuUserInfoTable;
import com.xiuxiu.easeim.EaseUserCacheManager;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgTable;
import com.xiuxiu.qupai.QuPaiManager;
import com.xiuxiu.server.UpdateActiveUserManager;

/**
 * Created by huzhi on 16-6-18.
 */
public class XiuxiuUtils {

    private static String TAG = XiuxiuUtils.class.getSimpleName();

    public static void onAppStart(Context context) {
        doSomthingOnAppStart(context);
        doSomthingOnAppStartInBackground(context);
    }



    private static void doSomthingOnAppStart(final Context context) {
        //1.获取用户信息
        queryUserInfo();
        //2.更新活跃用户
        UpdateActiveUserManager.getInstance().start();
        //3.获取所有群组信息
        EMClient.getInstance().groupManager().loadAllGroups();
        //4.获取所有会话信息
        EMClient.getInstance().chatManager().loadAllConversations();
        //5.初始化用户信息管理类以及数据库
        EaseUserCacheManager.getInstance().init();
        if(XiuxiuUserInfoTable.queryCount()>1000) {
            XiuxiuUserInfoTable.deleteByExceedLimit();
        }
        //6.初始化咻咻action消息管理类以及数据库
        XiuxiuActionMsgManager.getInstance().init();
        if(XiuxiuActionMsgTable.queryCount()>1000) {
            XiuxiuActionMsgTable.deleteByExceedLimit();
        }
        //7.因为不知道 PathUtil init()方法调用时机  目前这样手动调用 TODO　huzhi
        PathUtil.getInstance().initDirs("xiuxiu", "b", context);
        //8.趣拍初始化
        QuPaiManager.getInstance().init();
    }

    private static void doSomthingOnAppStartInBackground(final Context context) {
        /*
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            }
        }).start();*/
    }

    // ============================================================================================
    // 获取用户信息
    // ============================================================================================
    private static void queryUserInfo() {
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getQueryUserInfoUrl(), future, future));
        try {
            String response = future.get();
            android.util.Log.d(TAG,"user = " + response);
            Gson gson = new Gson();
            XiuxiuUserQueryResult res = gson.fromJson(response, XiuxiuUserQueryResult.class);
            if(res!=null && res.getUserinfos()!=null && res.getUserinfos().size()>0){
                XiuxiuUserInfoResult info = res.getUserinfos().get(0);
                if(info!=null && !TextUtils.isEmpty(info.getBirthday())){
                    info.setBirthday(DateUtils.time2Date(info.getBirthday()));
                }
                XiuxiuUserInfoResult.save(info);
            }
        }catch (Exception error){
            android.util.Log.d(TAG,"error = " + error);
        }
    }
    private static String getQueryUserInfoUrl() {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_USER_INFO)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "url = " + url);
        return url;
    }

    // ============================================================================================
    //　消耗打招呼
    // ============================================================================================

    private static Response.Listener<String> mCostXiuxiuTimesListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            android.util.Log.d(TAG,"消费咻咻招呼 response = " + response);
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            if(res!=null&&res.isSuccess()){
            }
        }
    };
    private static Response.ErrorListener mCostXiuxiuTimesErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.d(TAG,"error = " + error);
        }
    };

    public static void costXiuxiuCallTimes() {
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getCostXiuxiuTimesUrl("call"), mCostXiuxiuTimesListener, mCostXiuxiuTimesErroListener));
    }

    // ============================================================================================
    //　消耗羞咻广播
    // ============================================================================================
    public static boolean costXiuxiuBroadcastTimes() {
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getCostXiuxiuTimesUrl("broadcast"), future, future));
        try {
            String response = future.get();
            android.util.Log.d(TAG,"costXiuxiuBroadcastTimes()  response = " + response);
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            if(res!=null&&res.isSuccess()){
                return true;
            }
        }catch (Exception e){}
        return false;
    }

    private static String getCostXiuxiuTimesUrl(String type) {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.COST_CALL_LIMIT_BY_TYPE)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("limitType", type)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "getCostXiuxiuTimesUrl() url = " + url);
        return url;
    }

    // ============================================================================================
    //　扣除咻咻b
    // ============================================================================================
    public static void costUserCoinAsyn(final String costType,final String costCoin,final CallBack callBack){
        if(callBack!=null){
            callBack.onPre();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(costUserCoin(costType,costCoin)){
                    if(callBack!=null){
                        callBack.onSccusee();
                    }
                }else{
                    if(callBack!=null){
                        callBack.onFailure();
                    }
                }
            }
        }).start();
    }


    public static boolean costUserCoin(String costType,String costCoin){
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getCostUserCoinUrl(costType, costCoin), future, future));
        try {
            String response = future.get();
            android.util.Log.d(TAG,"costUserCoin() response = " + response);
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            if(res!=null && res.isSuccess()) {
                return true;
            }
        } catch (Exception e) {
            android.util.Log.d(TAG, "costUserCoin() e = " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param costType 话费类型
     * @param costCoin 话费的咻咻钱币
     * @return
     */
    private static String getCostUserCoinUrl(String costType,String costCoin) {
        String url = Uri.parse(HttpUrlManager.weixinPayUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.COST_USER_COIN)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("costCoin",costCoin)
                .appendQueryParameter("costType",costType)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "getCostUserCoinUrl() url = " + url);
        return url;
    }

    // ============================================================================================
    //　扣除咻咻b
    // ============================================================================================
    public static void transferCoinAsyn(final String toXiuxiuId,final String costCoin,final CallBack callBack){
        if(callBack!=null){
            callBack.onPre();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(transferCoin(toXiuxiuId, costCoin)){
                    if(callBack!=null){
                        callBack.onSccusee();
                    }
                }else{
                    if(callBack!=null){
                        callBack.onFailure();
                    }
                }
            }
        }).start();
    }


    public static boolean transferCoin(String toXiuxiuid,String costCoin){
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getTransferCoinUrl(toXiuxiuid, costCoin), future, future));
        try {
            String response = future.get();
            android.util.Log.d(TAG,"transferCoin() response = " + response);
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            if(res!=null && res.isSuccess()) {
                return true;
            }
        } catch (Exception e) {
            android.util.Log.d(TAG, "transferCoin() e = " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param costType 话费类型
     * @param costCoin 话费的咻咻钱币
     * @return
     */
    private static String getTransferCoinUrl(String toXiuxiuId,String costCoin) {
        String url = Uri.parse(HttpUrlManager.weixinPayUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.TRANSFER_COIN)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("from", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("to",toXiuxiuId)
                .appendQueryParameter("transferCoin",costCoin)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "getTransferCoinUrl() url = " + url);
        return url;
    }

    // ============================================================================================
    //　获取咻咻广播次数
    // ============================================================================================
    public static int getXiuxiuBroadcastTimes() {
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getXiuxiuTimesUrl("broadcast"), future, future));
        try {
            String response = future.get();
            android.util.Log.d(TAG,"getXiuxiuBroadcastTimes()  response = " + response);
            Gson gson = new Gson();
            XiuxiuTimsResult res = gson.fromJson(response, XiuxiuTimsResult.class);
            if(res!=null&&res.isSuccess()){
                int callTimes = res.getTimes();
                return callTimes;
            }
        }catch (Exception e){}
        return 0;
    }
    private static String getXiuxiuTimesUrl(String type) {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.GET_XX_TIMES)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("limitType", type)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "getXiuxiuTimesUrl() url = " + url);
        return url;
    }


    public interface CallBack{

        void onPre();

        void onSccusee();

        void onFailure();

    }

    // ============================================================================================
    //　礼物
    // ============================================================================================
    public static boolean sendGift(String type,String toId){
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getGiftUrl(type, toId), future, future));
        try {
            String response = future.get();
            android.util.Log.d(TAG,"sendGift()  response = " + response);
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            if(res!=null&&res.isSuccess()){
                return true;
            }

        }catch (Exception e){}
        return false;
    }
    private static String getGiftUrl(String type,String toId) {
        String url = Uri.parse(HttpUrlManager.weixinPayUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.SEND_GIFT)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("giftType", type)
                .appendQueryParameter("toId", toId)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "getGiftUrl() url = " + url);
        return url;
    }


    // ============================================================================================
    //　查看被投诉用户与标签理由
    // ============================================================================================
    public static boolean searchBecomplainUsers(String xiuxiuId){
        RequestFuture<String> future = RequestFuture.newFuture();
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getSearchBecomplainUsersUrl(xiuxiuId), future, future));
        try {
            String response = future.get();
            android.util.Log.d(TAG,"searchBecomplainUsers()  response = " + response);
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            if(res!=null&&res.isSuccess()){
                return true;
            }

        }catch (Exception e){}
        return false;
    }
    private static String getSearchBecomplainUsersUrl(String xiuxiuId) {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.SEARCH_BECOMPLAIN_USERS)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .appendQueryParameter("xiuxiuid", xiuxiuId)
//                .appendQueryParameter("offset", "0")
//                .appendQueryParameter("count", "100")
                .build().toString();
        android.util.Log.d(TAG, "getSearchBecomplainUsersUrl() url = " + url);
        return url;
    }
    // ============================================================================================
    //　对话框
    // ============================================================================================
    private static ProgressDialog mProgressDialog;

    public static void showProgressDialog(Context context,String txt){
        mProgressDialog = ProgressDialog.show(context, "提示", txt);
    }

    public static void showProgressDialog(Context context){
        mProgressDialog = ProgressDialog.show(context, "提示", "正在上传中...");
    }

    public static void dismisslProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

}
