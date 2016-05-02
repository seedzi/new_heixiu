package com.hyphenate.easeui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.hyphenate.easeui.R;
import com.hyphenate.util.DensityUtil;

/**
 * Created by huzhi on 16-4-29.
 */
public class EaseChatExtendMenu2 extends RelativeLayout{

    protected Context context;

    public EaseChatExtendMenu2(Context context) {
        super(context);
        init(context);
    }

    public EaseChatExtendMenu2(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EaseChatExtendMenu2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context){
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.ease_widget_chat_extend_menu2,this);
    }
}
