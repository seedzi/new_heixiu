package com.xiuxiuchat.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiuchat.R;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.base.BaseActivity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by huzhi on 16-6-16.
 */
public class UserImagePageActivity extends BaseActivity implements ViewPager.OnPageChangeListener{

    private static String TAG = UserImagePageActivity.class.getSimpleName();

    public static void startActivity(FragmentActivity ac,List<String> imgs,int position){
        if(imgs==null||imgs.size()==0){
            return;
        }
        if(position>=imgs.size()){
            return;
        }
        Intent intent = new Intent(ac,UserImagePageActivity.class);
        intent.putExtra("imgs", (Serializable)imgs);
        intent.putExtra("position",position);
        ac.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_user_image_page);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        initData();
    }

    /**
     * 装ImageView数组
     */
    private ImageView[] mImageViews;

    private List<String> mImgs;

    private int mCurrentPosition;

    private ViewPager mViewPager;

    private TextView mPositionTv;

    private void initData(){
        mImgs = (List<String>)getIntent().getSerializableExtra("imgs");
        mCurrentPosition = getIntent().getIntExtra("position",0);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        android.util.Log.d(TAG,"mImgs = " + mImgs + ",mCurrentPosition = " + mCurrentPosition);

        /*
        for(String str ; mImgs){
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10,10));

            tips[i] = imageView;
            if(i == 0){
                tips[i].setBackgroundResource(R.drawable.page_indicator_focused);
            }else{
                tips[i].setBackgroundResource(R.drawable.page_indicator_unfocused);
            }

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            layoutParams.leftMargin = 5;
            layoutParams.rightMargin = 5;
            group.addView(imageView, layoutParams);
        }
        */

        //将图片装载到数组中
        mImageViews = new ImageView[mImgs.size()];
        for(int i=0; i<mImageViews.length; i++){
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mImageViews[i] = imageView;
//            imageView.setBackgroundResource(imgIdArray[i]);
        }


        //设置Adapter
        mViewPager.setAdapter(new MyAdapter());
        //设置监听，主要是设置点点的背景
        mViewPager.setOnPageChangeListener(this);
        //设置ViewPager的默认项, 设置为长度的100倍，这样子开始就能往左滑动
//        mViewPager.setCurrentItem((mImageViews.length) * 100);

        mViewPager.setCurrentItem(mCurrentPosition);

        mPositionTv = (TextView) findViewById(R.id.position);
        mPositionTv.setText(""+(mCurrentPosition+1)+"/"+mImgs.size());


        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     *
     * @author xiaanming
     *
     */
    public class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImgs.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager)container).removeView(mImageViews[position % mImageViews.length]);

        }

        /**
         * 载入图片进去，用当前的position 除以 图片数组长度取余数是关键
         */
        @Override
        public Object instantiateItem(View container, int position) {
            android.util.Log.d(TAG,"instantiateItem");
                    ((ViewPager) container).addView(mImageViews[position], 0);
            ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST + mImgs.get(position), mImageViews[position]);
            return mImageViews[position];
        }




    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        android.util.Log.d(TAG, "onPageSelected");
        ImageView iv = mImageViews[arg0];
        ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST + mImgs.get(arg0), iv);
        try {
            mPositionTv.setText("" + (arg0 + 1) + "/" + mImgs.size());
        } catch (Exception e){}
    }
}
