package com.gougou.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.gougou.R;


/**
 * @author huzhi
 */
public class DotTextView extends TextView implements SharedPreferences.OnSharedPreferenceChangeListener{

   private Paint mPaint;

    private int mRadius;

    private int mXCenter;

    private int mYCenter;

    private boolean mShowDot;

    public DotTextView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        init(arg0);
    }

    public DotTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DotTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.dot_color));
        mPaint.setAntiAlias(true); //去锯齿
        mPaint.setStyle(Paint.Style.FILL);
        mRadius = (int) context.getResources().getDimension(R.dimen.dot_radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 如果当前home页面不是处于奇闻页面
        if(mShowDot){
            int textWidth = (int) getPaint().measureText(getText().toString());
            mXCenter = (getWidth() + textWidth)/2 + mRadius;
            mYCenter = mRadius;
            canvas.drawCircle(mXCenter, mYCenter, mRadius, mPaint);// 小圆
        }
    }

    private void updateShowDote(boolean show){
        mShowDot = show;
        invalidate();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences arg0, String arg1) {
        /*
        if ("anecdote__has_important".equals(arg1)){
            boolean isShow = arg0.getBoolean(arg1, false);
            if(isShow && isCurrentHomePageAnecdote()){
                AnecdoteApi.saveHasImportant(BrowserApp.getApplication(), false);
            }else{
                updateShowDote(isShow);
            }
        }*/
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        /*
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        updateShowDote(AnecdoteApi.getHasImportant(getContext()));
        preferences.registerOnSharedPreferenceChangeListener(this);
        if(isCurrentHomePageAnecdote()){
            AnecdoteApi.saveHasImportant(BrowserApp.getApplication(), false);
        }*/
    }

    @Override
    protected void onDetachedFromWindow() {
        /*
        super.onDetachedFromWindow();
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        preferences.unregisterOnSharedPreferenceChangeListener(this);
        */
    }
}
