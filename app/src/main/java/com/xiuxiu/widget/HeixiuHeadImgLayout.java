package com.xiuxiu.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xiuxiu.R;
import com.xiuxiu.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhihu on 16-5-2.
 */
public class HeixiuHeadImgLayout extends LinearLayout{


    private List<ImageView> mList = new ArrayList<ImageView>();

    private Context mContext;

    public HeixiuHeadImgLayout(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public HeixiuHeadImgLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public HeixiuHeadImgLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    private void init(){
        calculationItemSize();
        LinearLayout.LayoutParams ll = null;
        for(int i=0;i<8;i++){
            ll = new LinearLayout.LayoutParams(mItemWidth
                    ,mItemHeight);
            ll.setMargins(0,0,mItemMargin,0);
            if(i==7){
                ll.setMargins(0,0,0,0);
            }
            ImageView iv = new ImageView(mContext);
            iv.setLayoutParams(ll);
            iv.setImageResource(R.drawable.head_default);
            addView(iv);
        }
    }

    private int mItemMargin;

    private int mItemHeight;

    private int mItemWidth;

    private void calculationItemSize(){
        mItemMargin = 26;
        mItemWidth = (ScreenUtils.getScreenWidth(mContext) - mItemMargin*7 - 2*ScreenUtils.dip2px(mContext,16))/8;
        mItemHeight = mItemWidth;
    }
}
