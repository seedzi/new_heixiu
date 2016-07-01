package com.xiuxiu.api;

import com.xiuxiu.SharePreferenceWrap;

/**
 * Created by huzhi on 16-6-18.
 */
public class XiuxiuTimsResult extends XiuxiuResult{


    public static final String SHARE_PREFERENCE_NAME = "xiuxiuTimes";

    public static final String TIMES = "times";

    private int times;

    public void setTimes(int times) {
        this.times = times;
    }

    public int getTimes() {
        return times;
    }

    public static void save(XiuxiuTimsResult result){
        if(result==null){
            return;
        }
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putInt(TIMES,result.getTimes());
    }

    public static int getXiuxiuTims(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        return sharePreferenceWrap.getInt(TIMES,0);
    }

}
