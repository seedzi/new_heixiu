package com.gougou.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gougou.R;
import com.gougou.utils.ScreenUtils;
import com.gougou.utils.UiUtil;

/**
 * Created by huzhi on 16-4-8.
 */
public class UserDetailActivity extends FragmentActivity{

    public static void startActivity(Context context){
        Intent intent = new Intent(context,UserDetailActivity.class);
        context.startActivity(intent);
    }

    private ViewGroup mLayout;
    private GridView mPhotoWall;

    private int mScreenWidth;
    private int mPhotoItemWidth;
    private int mPhotoItemHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        mScreenWidth = ScreenUtils.getScreenWidth(getApplication());
        setContentView(R.layout.activity_user_detail_page);
        setupViews();
    }

    private void setupViews(){
        mLayout = (ViewGroup)findViewById(R.id.root_layout);
        mPhotoWall = (GridView)UiUtil.findViewById(mLayout, R.id.photo_wall);
        //设置照片墙大小
        int width = mScreenWidth - ScreenUtils.dip2px(getApplicationContext(),14);
        mPhotoItemWidth = (width - ScreenUtils.dip2px(getApplicationContext(),12))/4;
        mPhotoItemHeight = mPhotoItemWidth;
        int height = mPhotoItemHeight*2 + ScreenUtils.dip2px(getApplicationContext(),4);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(width,height);
        ll.setMargins(ScreenUtils.dip2px(getApplicationContext(),7),0,ScreenUtils.dip2px(getApplicationContext(),7),0);
        mPhotoWall.setLayoutParams(ll);
        mPhotoWall.setHorizontalSpacing(4);
        mPhotoWall.setVerticalSpacing(4);
        mPhotoWall.setAdapter(new PhotoAdpater());
    }


    private class PhotoAdpater extends BaseAdapter{

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridView.LayoutParams gl = null;
            if(convertView == null){
                gl = new GridView.LayoutParams(mPhotoItemWidth,mPhotoItemHeight);
                convertView = new ImageView(getApplicationContext());
                convertView.setLayoutParams(gl);
                ((ImageView)convertView).setScaleType(ImageView.ScaleType.FIT_XY);
            }
            ((ImageView)convertView).setBackgroundColor(Color.parseColor("#a6a6a6"));
            return convertView;
        }
    }
}
