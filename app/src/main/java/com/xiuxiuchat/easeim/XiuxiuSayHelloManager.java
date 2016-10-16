package com.xiuxiuchat.easeim;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.xiuxiuchat.CommonLib;
import com.xiuxiuchat.SharePreferenceWrap;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.utils.ToastUtil;
import com.xiuxiuchat.utils.XiuxiuUtils;

import org.json.JSONArray;
import org.json.JSONException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by huzhi on 16-8-15.
 * 咻咻招呼Manager类
 */
public class XiuxiuSayHelloManager {

    private static String TAG = XiuxiuSayHelloManager.class.getSimpleName();

    public final String SHARE_PREFERENCE_NAME = "XiuxiuSayHelloManager_" + XiuxiuLoginResult.getInstance().getXiuxiu_id();

    private static XiuxiuSayHelloManager mInstance;

    public static XiuxiuSayHelloManager getInstance(){
        if(mInstance==null){
            mInstance = new XiuxiuSayHelloManager();
        }
        return mInstance;
    }

    private int mCallTimes;

    public int getCallTimes(){
        android.util.Log.d(TAG,"getCallTimes() times = " + mCallTimes);
        return mCallTimes;
    }

    public void setCallTimes(int times){
        android.util.Log.d(TAG,"setCallTimes() times = " + times);
        mCallTimes = times;
    }

    private XiuxiuSayHelloManager(){
        mData = new HashMap<String,String>();
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap();
        String str = sharePreferenceWrap.getString(SHARE_PREFERENCE_NAME,"");
        JSONArray jsonArray = null;
        android.util.Log.d(TAG,"str = " + str);
        if(TextUtils.isEmpty(str)){
            jsonArray = new JSONArray();
        }else{
            try {
                jsonArray = new JSONArray(str);
            } catch (JSONException e) {
                e.printStackTrace();
                jsonArray = new JSONArray();
            }
        }
        try {
            for(int i=0;i<jsonArray.length();i++){
                mData.put(jsonArray.getString(i),jsonArray.getString(i));
            }
        }catch (Exception e){
        }
        mCallTimes = -1;
    }

    public void clear(){
        android.util.Log.d(TAG,"clear()");
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap();
        sharePreferenceWrap.putString(SHARE_PREFERENCE_NAME,"");
        mData.clear();
    }

    /**
     * 已经打过招呼的用户数据
     */
    private Map<String,String> mData;

    /**
     * 根据咻羞id判断是否能打招呼
     */
    public boolean isCanSayHell(String xiuxiuId){
        //1.判断是否是女性
        if(!XiuxiuUserInfoResult.isMale(XiuxiuUserInfoResult.getInstance().getSex())){
            return true;
        }
        //２.判断是否在免费招呼的数据中(每天有三个)
        if(mData.get(xiuxiuId)!=null){
            return true;
        }
        //３.判断是不是好友
        if(ImHelper.getInstance().getContactList().get(xiuxiuId)!=null){
            return true;
        }
        return false;
    }

    /**
     * 将能大打招呼的咻羞id保存
     */
    public void saveXiuxiuId(String xiuxiuId){
        mData.put(xiuxiuId,xiuxiuId);
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap();
        String str = sharePreferenceWrap.getString(SHARE_PREFERENCE_NAME,"");
        JSONArray jsonArray = null;
        if(TextUtils.isEmpty(str)){
            jsonArray = new JSONArray();
        }else{
            try {
                jsonArray = new JSONArray(str);
            } catch (JSONException e) {
                e.printStackTrace();
                jsonArray = new JSONArray();
            }
        }
        jsonArray.put(xiuxiuId);
        sharePreferenceWrap.putString(SHARE_PREFERENCE_NAME, jsonArray.toString());
    }


    public void sayHello(final String toXiuxiuId, final Callback callback, final FragmentActivity ac){
        android.util.Log.d(TAG,"sayHello" );
        if(TextUtils.isEmpty(toXiuxiuId)){
            return;
        }
        if(isCanSayHell(toXiuxiuId)){
            callback.onSuccess();
        }else{
            XiuxiuUtils.showProgressDialog(ac);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(mCallTimes==-1){
                        mCallTimes = XiuxiuUtils.getXiuxiuTimes();
                    }
                    mCallTimes = mCallTimes -1;
                    android.util.Log.d(TAG,"mCallTimes = " + mCallTimes);
                    XiuxiuUtils.costXiuxiuCallTimes();
                    if(mCallTimes>=0){
                        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                saveXiuxiuId(toXiuxiuId);
                                XiuxiuUtils.dismisslProgressDialog();
                                callback.onSuccess();
                            }
                        });
                    }else{
                        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                XiuxiuUtils.dismisslProgressDialog();
                                showConfirmDialog(callback, ac, toXiuxiuId);
                            }
                        });
                    }
                }
            }).start();
        }

    }


    // ============================================================================================
    // 确认付费对话框
    // ============================================================================================
    private void showConfirmDialog(final Callback callback, final FragmentActivity ac, final String xiuxiuId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ac);
        builder.setMessage("招呼已达到上限,确认付费购买吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                XiuxiuUtils.showProgressDialog(ac);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean isSccess = XiuxiuUtils.costUserCoin("1", "1");
                        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if (isSccess) {
                                    saveXiuxiuId(xiuxiuId);
                                    if (callback != null) {
                                        callback.onSuccess();
                                    }
                                } else {
                                    if (CommonLib.isNetworkConnected(ac)) {
                                        ToastUtil.showMessage(ac, "可能您的余额不够,支付失败,请您充值!");
                                    } else {
                                        ToastUtil.showMessage(ac, "网络连接错误!");
                                    }
                                }
                                XiuxiuUtils.dismisslProgressDialog();
                            }
                        });
                    }
                }).start();

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }


    public interface Callback{
        public void onSuccess();
    }

}
