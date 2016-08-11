package com.xiuxiu.Xiuxiubroadcast;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.xiuxiu.R;
import com.xiuxiu.SharePreferenceWrap;
import com.xiuxiu.utils.XiuxiuUtils;

/**
 * @author huzhi
 */
public class DotView extends View implements SharedPreferences.OnSharedPreferenceChangeListener{

   private Paint mPaint;

    private int mRadius;

    private int mXCenter;

    private int mYCenter;

    private boolean mShowDot = true;

    public DotView(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
        init(arg0);
    }

    public DotView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DotView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.xiuxiu_broadcast_dot_color));
        mPaint.setAntiAlias(true); //去锯齿
        mPaint.setStyle(Paint.Style.FILL);
        mRadius = (int) context.getResources().getDimension(R.dimen.dot_radius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 如果当前home页面不是处于奇闻页面
        if(mShowDot){
            mXCenter = getWidth()/2;
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
        if ("has_xiuxiu_broadcast".equals(arg1)){
            boolean isShow = arg0.getBoolean(arg1, false);
            updateShowDote(isShow);
            /*
            if(isShow && isCurrentHomePageAnecdote()){

            }else{
                updateShowDote(isShow);
            }
            */
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        updateShowDote(XiuxiuUtils.isXiuxiuBroadcastPrompt());
        XiuxiuUtils.getDefaultSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XiuxiuUtils.getDefaultSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
}
