package com.xiuxiu.user;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiu.R;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuPerson;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.user.voice.VoicePlayManager;
import com.xiuxiu.utils.ScreenUtils;
import com.xiuxiu.utils.UiUtil;

import java.net.URLDecoder;
import java.util.List;

/**
 * 用户资料详情页
 * Created by huzhi on 16-4-8.
 */
public class UserDetailActivity extends BaseActivity implements View.OnClickListener{

    private static String TAG = UserDetailActivity.class.getSimpleName();

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

    private ImageView mCharmIv;
    private List<String> mUrlList;

    PhotoAdpater mPhotoAdpater;

    private final int REQUEST_CODE_USER_EDIT = 101;

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
        mPhotoAdpater = new PhotoAdpater();
        mPhotoWall.setHorizontalSpacing(4);
        mPhotoWall.setVerticalSpacing(4);
        mPhotoWall.setAdapter(mPhotoAdpater);

        mPhotoWall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserImagePageActivity.startActivity(UserDetailActivity.this, mUrlList, position);
            }
        });

        findViewById(R.id.edit).setOnClickListener(this);
        findViewById(R.id.edit).setVisibility(View.VISIBLE);
        findViewById(R.id.more).setVisibility(View.GONE);

        mSignTv = (TextView)findViewById(R.id.user_sign_value);
        mTitleTv = (TextView)findViewById(R.id.title);
        mAgTv = (TextView)findViewById(R.id.user_age);
        mCityTv = (TextView)findViewById(R.id.city_value);
        mHeixiuIdTv = (TextView) findViewById(R.id.heixiu_hao_value);

        mCharmIv = (ImageView) findViewById(R.id.charm_value);

        VoicePlayManager.getInstance().init(this, (ImageView) findViewById(R.id.yuyin_bt),
                R.drawable.list_item_stop, R.drawable.list_item_play);

        findViewById(R.id.back).setOnClickListener(this);

        findViewById(R.id.bottom_layout).setVisibility(View.GONE);

        findViewById(R.id.charm_layout).setOnClickListener(this);
    }

    /**
     * 刷新用户信息
     */
    private void refreshUserData(){
        android.util.Log.d(TAG,"XiuxiuUserInfoResult.getInstance() = " + XiuxiuUserInfoResult.getInstance());
        mUrlList = XiuxiuUserInfoResult.getInstance().getPics();
        android.util.Log.d(TAG, "mUrlList = " + mUrlList);
        mSignTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getSign()));
        mTitleTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getXiuxiu_name()));
        mAgTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getAge()));
        mCityTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getCity()));
        mHeixiuIdTv.setText(URLDecoder.decode(XiuxiuUserInfoResult.getInstance().getXiuxiu_id()));
        XiuxiuPerson.setCharmValue(mCharmIv, XiuxiuUserInfoResult.getInstance().getCharm());
        mPhotoAdpater.notifyDataSetChanged();

        if(TextUtils.isEmpty(XiuxiuUserInfoResult.getInstance().getVoice())){
            findViewById(R.id.yuyin_bt).setVisibility(View.GONE);
            findViewById(R.id.yuyin_txt_no).setVisibility(View.VISIBLE);
            findViewById(R.id.yuyin_layout).setOnClickListener(null);
        }else{
            findViewById(R.id.yuyin_bt).setVisibility(View.VISIBLE);
            findViewById(R.id.yuyin_txt_no).setVisibility(View.GONE);
            findViewById(R.id.yuyin_layout).setOnClickListener(this);
        }

        if(XiuxiuUserInfoResult.isMale(XiuxiuUserInfoResult.getInstance().getSex())){
            ((TextView)findViewById(R.id.charm_txt)).setText("财富等级");
            XiuxiuPerson.setWealthValue(mCharmIv, XiuxiuUserInfoResult.getInstance().getFortune());
            mAgTv.setBackgroundResource(R.drawable.male_age_bg);
        }else{
            ((TextView)findViewById(R.id.charm_txt)).setText("魅力等级");
            XiuxiuPerson.setCharmValue(mCharmIv, XiuxiuUserInfoResult.getInstance().getCharm());
            mAgTv.setBackgroundResource(R.drawable.female_age_bg);
        }

        if(TextUtils.isEmpty(XiuxiuUserInfoResult.getInstance().getGet_gift())){
            findViewById(R.id.gift_layout).setVisibility(View.GONE);
            findViewById(R.id.gift_layout_line).setVisibility(View.GONE);
        }else{
            findViewById(R.id.gift_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.gift_layout_line).setVisibility(View.VISIBLE);
            setGifts(XiuxiuUserInfoResult.getInstance().getGiftList());
        }

        setUpWalletHeight();

        setUpBuliangjilv();
    }

    private void setGifts(List<XiuxiuUserInfoResult.Gift> list){
        if(list==null){
            return;
        }
        int i = 0;
        for(XiuxiuUserInfoResult.Gift gift: list){
            if(i==0){
                setGiftItem(gift,(ViewGroup)findViewById(R.id.gift_item_1));
            }else if(i==1){
                setGiftItem(gift,(ViewGroup)findViewById(R.id.gift_item_2));
            }else if(i==2){
                setGiftItem(gift,(ViewGroup)findViewById(R.id.gift_item_3));
            }else if(i==3){
                setGiftItem(gift,(ViewGroup)findViewById(R.id.gift_item_4));
            }
            i++;
        }
    }

    private void setGiftItem(XiuxiuUserInfoResult.Gift gift,ViewGroup layout){
        TextView sizeTv = (TextView) layout.findViewById(R.id.gift_size);
        sizeTv.setText("x "+gift.size);
        ImageView giftImg = (ImageView) layout.findViewById(R.id.gift_img);
        TextView giftName = (TextView) layout.findViewById(R.id.gift_name);
    }


    //设置不良记录
    private void setUpBuliangjilv(){
        if(TextUtils.isEmpty(XiuxiuUserInfoResult.getInstance().getSpam())){
            findViewById(R.id.bu_liang_ji_lu_layout).setVisibility(View.GONE);
            findViewById(R.id.bu_liang_ji_lu_line).setVisibility(View.GONE);
        }
    }

    // 设置照片墙宽度
    private void setUpWalletHeight(){
        int width = mScreenWidth - ScreenUtils.dip2px(getApplicationContext(),14);
        mPhotoItemWidth = (width - ScreenUtils.dip2px(getApplicationContext(),12))/4;
        mPhotoItemHeight = mPhotoItemWidth;
        int height = 0;
        if(mUrlList!=null && mUrlList.size()>4) {
            height = mPhotoItemHeight*2 + ScreenUtils.dip2px(getApplicationContext(),4);
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(width, height);
            ll.setMargins(ScreenUtils.dip2px(getApplicationContext(), 7), 0, ScreenUtils.dip2px(getApplicationContext(), 7), 0);
            mPhotoWall.setLayoutParams(ll);
        }else if(mUrlList!=null && mUrlList.size()<=4){
            height = mPhotoItemHeight + ScreenUtils.dip2px(getApplicationContext(),4);
            LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(width, height);
            ll.setMargins(ScreenUtils.dip2px(getApplicationContext(), 7), 0, ScreenUtils.dip2px(getApplicationContext(), 7), 0);
            mPhotoWall.setLayoutParams(ll);
        } else {
            mPhotoWall.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.edit:
                UserEditDetailActivity.startActivity4Result(UserDetailActivity.this, REQUEST_CODE_USER_EDIT);
                break;
            case R.id.yuyin_layout:
                if(VoicePlayManager.getInstance().isPlaying()){
                    VoicePlayManager.getInstance().pause();
                }else{
                    VoicePlayManager.getInstance().play(XiuxiuUserInfoResult.getUrlVoice4Qiniu(XiuxiuUserInfoResult.getInstance().getVoice()));
                }
                break;
            case R.id.charm_layout:
                if(XiuxiuUserInfoResult.isMale(XiuxiuUserInfoResult.getInstance().getSex())){
                    WealthLevelActivity.startActivity(UserDetailActivity.this);
                }else{
                    CharmLevelActivity.startActivity(UserDetailActivity.this);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_USER_EDIT && resultCode == RESULT_OK){
            refreshUserData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoicePlayManager.getInstance().release();
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
            return mUrlList==null?0: mUrlList.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridView.LayoutParams gl = null;
            if(convertView == null){
                gl = new GridView.LayoutParams(mPhotoItemWidth,mPhotoItemHeight);
                convertView = new ImageView(getApplicationContext());
                convertView.setLayoutParams(gl);
                ((ImageView)convertView).setScaleType(ImageView.ScaleType.CENTER_CROP);
            }
            ((ImageView)convertView).setImageDrawable(new ColorDrawable(Color.parseColor("#a6a6a6")));
            if(mUrlList!=null && position<mUrlList.size()){
                ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST + XiuxiuUserInfoResult.getInstance().getPics().get(position),
                        (ImageView)convertView);
            }
            return convertView;
        }
    }
}
