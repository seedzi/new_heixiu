package com.xiuxiu.api;

import android.text.TextUtils;

import com.xiuxiu.SharePreferenceWrap;

/**
 * Created by huzhi on 16-7-4.
 */
public class XiuxiuWalletCoinResult{

    private static String TAG = XiuxiuWalletCoinResult.class.getSimpleName();

    public static final String SHARE_PREFERENCE_NAME = "xiuxiuwalletcoinresult";

    public static final String RECHARGE_COIN = "recharge_coin";

    public static final String EARN_COIN = "earn_coin";

    public int recharge_coin = -1;

    public int earn_coin = -1;


    public int getRecharge_coin() {
        return recharge_coin;
    }

    public int getEarn_coin() {
        return earn_coin;
    }


    public void setRecharge_coin(int recharge_coin) {
        this.recharge_coin = recharge_coin;
    }

    public void setEarn_coin(int earn_coin) {
        this.earn_coin = earn_coin;
    }


    /**
     * @param user
     */
    public static void save(XiuxiuWalletCoinResult info){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        if(info.getRecharge_coin()!=-1) {
            android.util.Log.d(TAG,"info.getRecharge_coin() = " + info.getRecharge_coin());
            sharePreferenceWrap.putInt(RECHARGE_COIN, info.getRecharge_coin());
        }
        if(info.getEarn_coin()!=-1) {
            android.util.Log.d(TAG,"info.getEarn_coin() = " + info.getEarn_coin());
            sharePreferenceWrap.putInt(EARN_COIN, info.getEarn_coin());
        }
    }

    /**
     * @return
     */
    public static XiuxiuWalletCoinResult getFromShareP(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        XiuxiuWalletCoinResult info = new XiuxiuWalletCoinResult();
        info.setRecharge_coin(sharePreferenceWrap.getInt(RECHARGE_COIN, 0));
        info.setEarn_coin(sharePreferenceWrap.getInt(EARN_COIN, 0));
        android.util.Log.d(TAG,"getFromShareP info = " + info);
        return info;
    }


    @Override
    public String toString() {
        return "XiuxiuWalletCoinResult{" +
                "recharge_coin='" + recharge_coin + '\'' +
                ", earn_coin='" + earn_coin + '\'' +
                '}';
    }
}
