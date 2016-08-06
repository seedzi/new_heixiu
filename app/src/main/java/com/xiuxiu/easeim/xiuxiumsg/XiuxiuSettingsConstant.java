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

    public static final String VOICE_ON_KEY = "voice_on_key"; //声音

    public static final String VIBRATION_ON_KEY = "vibration_on_key";  //振动

    public static final String NEW_MESSAGE_PROMPT_ON_KEY = "new_message_prompt_on_key";  //接受新消息提示

    public static final String XIUXIU_PROMPT_ON_KEY = "xiuxiu_prompt_on_key";  //接受咻咻提示

    public static final String XIUXIU_BROADCAST_ON_KEY = "xiuxiu_broadcast_on_key";  //接受咻广播提示

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

    public static boolean isVoicOn(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        return sharePreferenceWrap.getBoolean(VOICE_ON_KEY, true);
    }

    public static void setVoicOn(boolean value){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putBoolean(VOICE_ON_KEY, value);
    }


    public static boolean isVibrationOn(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        return sharePreferenceWrap.getBoolean(VIBRATION_ON_KEY, true);
    }

    public static void setVibrationOn(boolean value){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putBoolean(VIBRATION_ON_KEY, value);
    }


    public static boolean isNewMessagePromptOn(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        return sharePreferenceWrap.getBoolean(NEW_MESSAGE_PROMPT_ON_KEY, true);
    }

    public static void setNewMessagePromptOn(boolean value){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putBoolean(NEW_MESSAGE_PROMPT_ON_KEY, value);
    }

    public static boolean isXiuxiuPromptOn(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        return sharePreferenceWrap.getBoolean(XIUXIU_PROMPT_ON_KEY, true);
    }

    public static void setXiuxiuPromptOn(boolean value){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putBoolean(XIUXIU_PROMPT_ON_KEY, value);
    }


    public static boolean isXiuxiuBroadcastOn(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        return sharePreferenceWrap.getBoolean(XIUXIU_BROADCAST_ON_KEY, true);
    }

    public static void setXiuxiuBroadcastOn(boolean value){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putBoolean(XIUXIU_BROADCAST_ON_KEY, value);
    }
}
