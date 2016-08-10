package com.hyphenate.easeui.widget.gift;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by huzhi on 16-7-26.
 */
public class GiftManager {

    private static GiftManager mInstance;

    public static GiftManager getInstance(){
        if(mInstance==null){
            mInstance = new GiftManager();
        }
        return mInstance;
    }

    private GiftItemClickListener mListener;

    public void setListener(GiftItemClickListener l){
        mListener = l;
    }

    public GiftItemClickListener getListener(){
        return mListener;
    }


    // ===================================================================
    // 咻咻礼物配置
    // ===================================================================
    private Map<Integer,Integer> mGifts = new HashMap<Integer,Integer>();

    public Map<Integer,Integer> getGifts(){
        return  mGifts;
    }

}
