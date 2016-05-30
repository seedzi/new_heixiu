package com.xiuxiu.user;

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
import android.widget.TextView;

import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.utils.ScreenUtils;
import com.xiuxiu.utils.UiUtil;

import java.net.URLDecoder;

/**
 * Created by huzhi on 16-4-8.
 */
public class UserDetailActivity extends FragmentActivity implements View.OnClickListener{

    public static void startActivity(Context context){
        Intent intent = new Intent(context,UserDetailActivity.class);
        context.startActivity(intent);
    }

    private ViewGroup mLayout;
    private GridView mPhotoWall;

    private int mScreenWidth;
    private int mPhotoItemWidth;
    private int mPhotoItemHeight;

    private TextView mSignTv;
    private TextView mTitleTv;
    private TextView mAgTv;
    private TextView mCityTv;
    private TextView mHeixiuIdTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        mScreenWidth = ScreenUtils.getScreenWidth(getApplication());
        setContentView(R.layout.activity_user_detail_page);
        setupViews();
        refreshUserData();
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
        ll.setMargins(ScreenUtils.dip2px(getApplicationContext(), 7), 0, ScreenUtils.dip2px(getApplicationContext(), 7), 0);
        mPhotoWall.setLayoutParams(ll);
        mPhotoWall.setHorizontalSpacing(4);
        mPhotoWall.setVerticalSpacing(4);
        mPhotoWall.setAdapter(new PhotoAdpater());

        findViewById(R.id.edit).setOnClickListener(this);

        mSignTv = (TextView)findViewById(R.id.user_sign_value);
        mTitleTv = (TextView)findViewById(R.id.title);
        mAgTv = (TextView)findViewById(R.id.age);
        mCityTv = (TextView)findViewById(R.id.city_value);
        mHeixiuIdTv = (TextView) findViewById(R.id.heixiu_hao_value);
    }

    /**
     * 刷新用户信息
     */
    private void refreshUserData(){
        mSignTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getSign()));
        mTitleTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getXiuxiu_name()));
        mAgTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getAge()));
        mCityTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getCity()));
        mHeixiuIdTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getXiuxiu_id()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserData();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.edit:
                UserEditDetailActivity.startActivity(UserDetailActivity.this);
                break;
        }
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
