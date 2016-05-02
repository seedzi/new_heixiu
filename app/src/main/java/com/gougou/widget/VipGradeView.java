package com.gougou.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.gougou.R;

public class VipGradeView extends View {
	
	private int mGrade = 1;

	private final int GRADE_COUNT = 8;
	
	private Paint mPaint;
	
	private Rect mRect;
	
	private static Bitmap mNode;
	
	private static Bitmap mGrayNode;
	
	private int mItemWidth;
	
	public VipGradeView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public VipGradeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public VipGradeView(Context context) {
		super(context);
		init();
	}
	
	private void init(){
		if(mNode  == null)
			mNode = BitmapFactory.decodeResource(getResources(), R.drawable.user_upgrade);
		if(mGrayNode == null)
			mGrayNode = BitmapFactory.decodeResource(getResources(), R.drawable.user_upgrade_gary);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, mNode.getHeight());
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int lineWidth =   getWidth()-mNode.getWidth();
		if(GRADE_COUNT==0||GRADE_COUNT==1)
			mItemWidth = lineWidth;
		else
			mItemWidth = lineWidth/(GRADE_COUNT-1);
		
		mPaint = new Paint();
		//画灰线
		mPaint.setColor(getResources().getColor(R.color.color_user_info_line_gray));
		mRect = new Rect(mNode.getWidth()/2, mNode.getHeight()*5/12   , getWidth()-mNode.getWidth()/2, mNode.getHeight()*7/12);
		canvas.drawRect(mRect, mPaint);		
		//画亮线
		mPaint.setColor(getResources().getColor(R.color.color_user_info_line));
		mRect = new Rect(mNode.getWidth()/2, mNode.getHeight()*5/12   , mItemWidth*mGrade, mNode.getHeight()*7/12);
		canvas.drawRect(mRect, mPaint);
		//画点
		mPaint = new Paint();
		for(int i=0;i<GRADE_COUNT;i++){
			if(i==mGrade){
				canvas.drawBitmap(mNode, mItemWidth*i, 0, mPaint);
			}
			/*
			if(i<=mGrade){
				canvas.drawBitmap(mNode, mItemWidth*i, 0, mPaint);
			}else{
				canvas.drawBitmap(mGrayNode, mItemWidth*i, 0, mPaint);
			}*/
		}
	}

	public void setGrade(int grade){
		if(grade<0)
			mGrade = 0;
		else if(grade>=GRADE_COUNT)
			mGrade = GRADE_COUNT -1;
		mGrade = grade;
	}
	
}
