package com.xiuxiuchat.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class Tab extends TextView {
	
	
	 private String tab_name;

     private int tab_index;

     public Tab(Context context) {
         super(context);
     }

     public Tab(Context context, AttributeSet attrs) {
         super(context, attrs);
     }

     public Tab(Context context, AttributeSet attrs, int defStyleAttr) {
         super(context, attrs, defStyleAttr);
     }

     public void setTabName(String name){
         tab_name = name;
         setText(name);
     }

     public void setTabIndex(int index){
         tab_index = index;
     }
}
