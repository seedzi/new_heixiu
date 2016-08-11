package com.xiuxiu.Xiuxiubroadcast;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiuxiu.R;


public class LoadMoreLayout extends FrameLayout {

    private ProgressBar mRotateView;

    private TextView mPromptTv;

    public LoadMoreLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public LoadMoreLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadMoreLayout(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.xiuxiu_broadcast_list_loading_more_layout, this);
        mRotateView = (ProgressBar) findViewById(R.id.pull_to_refresh_image);
        mPromptTv = (TextView) findViewById(R.id.no_more_data_prompt);
    }
    
    public void showProgress(){
        mRotateView.setVisibility(View.VISIBLE);
        mPromptTv.setVisibility(View.GONE);
    }
    
    public void showPrompt(){
        mRotateView.setVisibility(View.GONE);
        mPromptTv.setVisibility(View.VISIBLE);
    }

    public void showPrompt(String txt){
        mRotateView.setVisibility(View.GONE);
        mPromptTv.setText(txt);
        mPromptTv.setVisibility(View.VISIBLE);
    }

    public boolean isPromptShowing(){
        return mPromptTv.getVisibility() == View.VISIBLE;
    }

    public void setPromptTvClickListener(View.OnClickListener listener){
        mPromptTv.setOnClickListener(listener);
    }
}
