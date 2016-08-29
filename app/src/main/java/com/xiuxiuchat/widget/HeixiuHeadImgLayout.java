package com.xiuxiuchat.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hyphenate.easeui.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiuchat.R;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhihu on 16-5-2.
 */
public class HeixiuHeadImgLayout extends LinearLayout{


    private List<ImageView> mList = new ArrayList<ImageView>();

    private Context mContext;

    private boolean isInit = false;

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
        if(isInit){
            return;
        }
        calculationItemSize();
        LinearLayout.LayoutParams ll = null;
        for(int i=0;i<8;i++){
            ll = new LinearLayout.LayoutParams(mItemWidth
                    ,mItemHeight);
            ll.setMargins(0,0,mItemMargin,0);
            if(i==7){
                ll.setMargins(0,0,0,0);
            }
            CircleImageView iv = new CircleImageView(mContext);
            iv.setLayoutParams(ll);
            iv.setImageResource(R.drawable.head_default);
            addView(iv);
            mList.add(iv);
        }
        isInit = true;
    }

    private int mItemMargin;

    private int mItemHeight;

    private int mItemWidth;

    private void calculationItemSize(){
        mItemMargin = 26;
        mItemWidth = (ScreenUtils.getScreenWidth(mContext) - mItemMargin*7 - 2*ScreenUtils.dip2px(mContext,16))/8;
        mItemHeight = mItemWidth;
    }

    public void setData(List<String> imgurls){
        if(imgurls==null){
            return;
        }
        int position = 0;
        for(ImageView iv:mList){
           if(position < imgurls.size()){
               if(!TextUtils.isEmpty(imgurls.get(position))){
                   ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST + imgurls.get(position),iv);
               }
               position++;
           }else{
               return;
           }
        }
    }
}
