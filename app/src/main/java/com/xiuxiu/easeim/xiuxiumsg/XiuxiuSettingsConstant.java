package com.xiuxiu.easeim.xiuxiumsg;


import com.xiuxiu.SharePreferenceWrap;
import com.xiuxiu.api.XiuxiuLoginResult;

/**
 * Created by huzhi on 16-8-2.
 */
public class XiuxiuSettingsConstant {

    public static final String SHARE_PREFERENCE_NAME = "XiuxiuSettingsConstant" + "_" + XiuxiuLoginResult.getInstance().getXiuxiu_id();

    public static final String XIUXIU_YUYIN_PRICE_KEY = "xiuxiu_yuyin_price";

    public static final String XIUXIU_IMG_PRICE_KEY = "xiuxiu_img_price";

    public static final String XIUXIU_VIDEO_PRICE_KEY = "xiuxiu_video_price";

    public static final int XIUXIU_YUYIN_PRICE_DEFAULT = 10;

    public static final int XIUXIU_IMG_PRICE_DEFAULT = 20;

    public static final int XIUXIU_VIDEO_PRICE_DEFAULT = 20;

    public static int getXiuxiuYuyinPrice(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        return sharePreferenceWrap.getInt(XIUXIU_YUYIN_PRICE_KEY, XIUXIU_YUYIN_PRICE_DEFAULT);
    }

    public static void saveXiuxiuYuyinPrice(int price){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putInt(XIUXIU_YUYIN_PRICE_KEY,price);
    }

    public static int getXiuxiuImgPrice(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        return sharePreferenceWrap.getInt(XIUXIU_IMG_PRICE_KEY, XIUXIU_IMG_PRICE_DEFAULT);
    }

    public static void saveXiuxiuImgPrice(int price){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putInt(XIUXIU_IMG_PRICE_KEY,price);
    }

    public static int getXiuxiuVideoPrice(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        return sharePreferenceWrap.getInt(XIUXIU_VIDEO_PRICE_KEY, XIUXIU_VIDEO_PRICE_DEFAULT);
    }

    public static void saveXiuxiuVideoPrice(int price){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putInt(XIUXIU_VIDEO_PRICE_KEY,price);
    }
}
