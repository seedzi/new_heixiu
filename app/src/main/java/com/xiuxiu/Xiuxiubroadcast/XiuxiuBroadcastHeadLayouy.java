package com.xiuxiu.Xiuxiubroadcast;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiuxiu.R;

/**
 * Created by huzhi on 16-8-11.
 */
public class XiuxiuBroadcastHeadLayouy extends LinearLayout{

    private TextView mDescriptionTv;

    private TextView mOnlineTimeTv;

    public XiuxiuBroadcastHeadLayouy(Context context) {
        super(context);
    }

    public XiuxiuBroadcastHeadLayouy(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public XiuxiuBroadcastHeadLayouy(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mDescriptionTv = (TextView) findViewById(R.id.description);
        mOnlineTimeTv = (TextView) findViewById(R.id.online_time);
    }

    public void update(String descriptionTxt,String onlineTime){
        if(!TextUtils.isEmpty(descriptionTxt)){
            mDescriptionTv.setText(descriptionTxt);
        }
        if(!TextUtils.isEmpty(onlineTime)){
            mOnlineTimeTv.setText(onlineTime);
        }
    }

}
