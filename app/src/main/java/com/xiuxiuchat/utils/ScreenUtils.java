package com.xiuxiuchat.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class ScreenUtils {
	
	
	public static int[] getScreenSize(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics  dm = new DisplayMetrics();     
		wm.getDefaultDisplay().getMetrics(dm);     
		int width = dm.widthPixels;               
		int height = dm.heightPixels;
		int[] size = new int[]{width,height};
		return size;
	}
	
	public static int getScreenWidth(Context context) {
		return getScreenSize(context)[0];
	}

	public static int getScreenHeight(Context context) {
		return getScreenSize(context)[1];
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier(
				"status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}
}
