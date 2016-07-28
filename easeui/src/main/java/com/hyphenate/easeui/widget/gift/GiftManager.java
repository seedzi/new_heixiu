package com.hyphenate.easeui.widget.gift;

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

}
