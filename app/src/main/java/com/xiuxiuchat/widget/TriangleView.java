package com.xiuxiuchat.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.xiuxiuchat.utils.ScreenUtils;

/**
 * Created by huzhi on 16-8-12.
 */
public class TriangleView extends View {


    private final int TRIANGLE_LENGTH = ScreenUtils.dip2px(getContext(),20);

    private int mPointTopX;
    private int mPointTopY;

    private int mPointLeftX;
    private int mPointLeftY;

    private int mPointRightX;
    private int mPointRightY;

    public TriangleView(Context context) {
        super(context);
        android.util.Log.d("123456", "TriangleView");
    }

    public TriangleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        android.util.Log.d("123456", "TriangleView");
    }

    public TriangleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        android.util.Log.d("123456", "TriangleView");
    }

    private int mWidth,mHeight;

    private int mPosition = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mWidth = getWidth();
        mHeight = getHeight();

        if(mPosition == 0){
            mPointTopX = mWidth/6;
            mPointTopY = 0;
            mPointLeftX = mWidth/6 - TRIANGLE_LENGTH/2;
            mPointLeftY = mHeight;
            mPointRightX = mWidth/6 + TRIANGLE_LENGTH/2;
            mPointRightY = mHeight;
        }else if(mPosition == 1){
            mPointTopX = mWidth/2;
            mPointTopY = 0;
            mPointLeftX = mWidth/2 - TRIANGLE_LENGTH/2;
            mPointLeftY = mHeight;
            mPointRightX = mWidth/2 + TRIANGLE_LENGTH/2;
            mPointRightY = mHeight;
        }else if(mPosition == 2){
            mPointTopX = mWidth*5/6;
            mPointTopY = 0;
            mPointLeftX = mWidth*5/6 - TRIANGLE_LENGTH/2;
            mPointLeftY = mHeight;
            mPointRightX = mWidth*5/6 + TRIANGLE_LENGTH/2;
            mPointRightY = mHeight;
        }
        android.util.Log.d("123456","mPointTopX = " + mPointTopX
        +",mPointTopY = " + mPointTopY + ",mPointLeftX = " + mPointLeftX
                +",mPointLeftY = " + mPointLeftY + ",mPointRightX = " + mPointRightX
                + ", mPointRightY = " + mPointRightY
        );
        /*画一个实心三角形*/
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#55ffffff"));
            /*设置paint的 style 为STROKE：空心*/
        paint.setStyle(Paint.Style.FILL);
        /*去锯齿*/
        paint.setAntiAlias(true);
        Path path = new Path();
        path.moveTo(mPointTopX, mPointTopY);
        path.lineTo(mPointLeftX, mPointLeftY);
        path.lineTo(mPointRightX, mPointRightY);
        path.close();
        canvas.drawPath(path, paint);
    }

    public void setPosition(int position){
        mPosition = position;
        invalidate();
    }
}
