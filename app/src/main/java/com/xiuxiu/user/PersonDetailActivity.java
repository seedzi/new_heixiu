package com.xiuxiu.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hyphenate.easeui.domain.EaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuPerson;
import com.xiuxiu.api.XiuxiuResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.api.XiuxiuUserQueryResult;
import com.xiuxiu.chat.ChatPage;
import com.xiuxiu.easeim.Constant;
import com.xiuxiu.easeim.EaseUserCacheManager;
import com.xiuxiu.easeim.ImHelper;
import com.xiuxiu.user.invitation.InviteMessgeDao;
import com.xiuxiu.user.invitation.UserDao;
import com.xiuxiu.user.voice.VoicePlayManager;
import com.xiuxiu.utils.DateUtils;
import com.xiuxiu.utils.DialogActivity;
import com.xiuxiu.utils.Md5Util;
import com.xiuxiu.utils.ScreenUtils;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.UiUtil;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * Created by huzhi on 16-6-16.
 */
public class PersonDetailActivity extends FragmentActivity implements View.OnClickListener{

    private static String TAG = PersonDetailActivity.class.getSimpleName();

    public static void startActivity(Context context,String xiuxiu_id,boolean isFromContactList){
        Intent intent = new Intent(context,PersonDetailActivity.class);
        intent.putExtra("xiuxiu_id", xiuxiu_id);
        intent.putExtra("isFromContactList", isFromContactList);
        context.startActivity(intent);
    }

    public static int MORE_BT_REQUEST_CODE = 1;

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
    private TextView mOnLineTime;

    private ImageView mCharmIv;
    private List<String> mUrlList;

    PhotoAdpater mPhotoAdpater;

    private XiuxiuUserInfoResult mXiuxiuUserInfoResult;

    private String xiuxiuId;

    private boolean isFromContactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        mScreenWidth = ScreenUtils.getScreenWidth(getApplication());
        setContentView(R.layout.activity_user_detail_page);
        setupViews();
        initData();
//        refreshUserData(null);
    }

    private void setupViews(){
        mLayout = (ViewGroup)findViewById(R.id.root_layout);
        mPhotoWall = (GridView) UiUtil.findViewById(mLayout, R.id.photo_wall);

        setupPhotoWall();

        mPhotoAdpater = new PhotoAdpater();
        mPhotoWall.setHorizontalSpacing(4);
        mPhotoWall.setVerticalSpacing(4);
        mPhotoWall.setAdapter(mPhotoAdpater);
        mPhotoWall.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UserImagePageActivity.startActivity(PersonDetailActivity.this, mUrlList, position);
            }
        });

        findViewById(R.id.more).setOnClickListener(this);

        mSignTv = (TextView)findViewById(R.id.user_sign_value);
        mTitleTv = (TextView)findViewById(R.id.title);
        mAgTv = (TextView)findViewById(R.id.user_age);
        mCityTv = (TextView)findViewById(R.id.city_value);
        mHeixiuIdTv = (TextView) findViewById(R.id.heixiu_hao_value);
        mOnLineTime = (TextView) findViewById(R.id.online_time);

        findViewById(R.id.yuyin_layout).setOnClickListener(this);

        mCharmIv = (ImageView) findViewById(R.id.charm_value);

        VoicePlayManager.getInstance().init(this, (ImageView) findViewById(R.id.yuyin_bt),
                R.drawable.list_item_stop, R.drawable.list_item_play);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setupPhotoWall(){
        //设置照片墙大小
        int width = mScreenWidth - ScreenUtils.dip2px(getApplicationContext(),14);
        mPhotoItemWidth = (width - ScreenUtils.dip2px(getApplicationContext(),12))/4;
        mPhotoItemHeight = mPhotoItemWidth;
        int height = 0;
        if (mUrlList!=null && mUrlList.size()>4) {
            height = mPhotoItemHeight * 2 + ScreenUtils.dip2px(getApplicationContext(), 4);
        } else if(mUrlList!=null && mUrlList.size()>0 ){
            height = mPhotoItemHeight * 1 + ScreenUtils.dip2px(getApplicationContext(), 4);
        }
        android.util.Log.d("AAAA","height = " + height);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(width, height);
        ll.setMargins(ScreenUtils.dip2px(getApplicationContext(), 7), 0, ScreenUtils.dip2px(getApplicationContext(), 7), 0);
        mPhotoWall.setLayoutParams(ll);
    }

    private void initData(){
        isFromContactList = getIntent().getBooleanExtra("isFromContactList", false);
        xiuxiuId= getIntent().getStringExtra("xiuxiu_id");
        android.util.Log.d(TAG, "xiuxiu_id = " + xiuxiuId);
        queryUserInfo(xiuxiuId);

        findViewById(R.id.say_hello_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.xiuxiu_ta_layout).setVisibility(View.VISIBLE);

        if(isFromContactList){
            ((TextView)((ViewGroup)findViewById(R.id.say_hello_layout)).getChildAt(0)).setText("发消息");
            ((ViewGroup)findViewById(R.id.say_hello_layout)).getChildAt(1).setVisibility(View.GONE);
            ((ViewGroup)findViewById(R.id.xiuxiu_ta_layout)).getChildAt(1).setVisibility(View.GONE);
        }else{

        }
    }


    /**
     * 刷新用户信息
     */
    private void refreshUserData(final XiuxiuUserInfoResult result){
        if(result==null){
            return;
        }
        mXiuxiuUserInfoResult = result;

        if(TextUtils.isEmpty(mXiuxiuUserInfoResult.getVoice())){
            ((TextView)findViewById(R.id.yuyin_txt)).setText("暂无语音介绍");
            findViewById(R.id.yuyin_bt).setVisibility(View.GONE);
        }else{
            findViewById(R.id.yuyin_bt).setVisibility(View.VISIBLE);
        }

        mUrlList = mXiuxiuUserInfoResult.getPics();
        if(!TextUtils.isEmpty(result.getSign())) {
            mSignTv.setText(URLDecoder.decode(mXiuxiuUserInfoResult.getSign()));
        }
        if(!TextUtils.isEmpty(result.getXiuxiu_name())) {
            mTitleTv.setText(URLDecoder.decode(mXiuxiuUserInfoResult.getXiuxiu_name()));
        }
        if(!TextUtils.isEmpty(mXiuxiuUserInfoResult.getAge())) {
            mAgTv.setText(URLDecoder.decode(mXiuxiuUserInfoResult.getAge()));
        }
        if(!TextUtils.isEmpty(result.getCity())) {
            mCityTv.setText(URLDecoder.decode(mXiuxiuUserInfoResult.getCity()));
        }
        if(!TextUtils.isEmpty(result.getXiuxiu_id())) {
            mHeixiuIdTv.setText(URLDecoder.decode(mXiuxiuUserInfoResult.getXiuxiu_id()));
        }
        if(TextUtils.isEmpty(result.getSpam())){
            findViewById(R.id.bu_liang_ji_lu_layout).setVisibility(View.GONE);
            findViewById(R.id.bu_liang_ji_lu_line).setVisibility(View.GONE);
        }else{
            findViewById(R.id.bu_liang_ji_lu_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.bu_liang_ji_lu_line).setVisibility(View.VISIBLE);
        }
        String timeTxt = DateUtils.getTextByTime(
                PersonDetailActivity.this,
                result.getActive_time(),
                R.string.date_fromate_anecdote);
        mOnLineTime.setText(timeTxt);

        if(XiuxiuUserInfoResult.isMale(result.getSex())){
            ((TextView)findViewById(R.id.charm_txt)).setText("财富等级");
            XiuxiuPerson.setWealthValue(mCharmIv, mXiuxiuUserInfoResult.getFortune());
            findViewById(R.id.charm_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharmLevelActivity.startActivity(PersonDetailActivity.this);
                }
            });
            mAgTv.setBackgroundResource(R.drawable.male_age_bg);
        }else{
            ((TextView)findViewById(R.id.charm_txt)).setText("魅力等级");
            XiuxiuPerson.setCharmValue(mCharmIv, mXiuxiuUserInfoResult.getCharm());
            findViewById(R.id.charm_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CharmLevelActivity.startActivity(PersonDetailActivity.this);
                }
            });
            mAgTv.setBackgroundResource(R.drawable.female_age_bg);
        }
        mPhotoAdpater.notifyDataSetChanged();

        setupPhotoWall();

        findViewById(R.id.say_hello_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatPage.startActivity(PersonDetailActivity.this,
                        result.getXiuxiu_id(), result.getXiuxiu_name());
                android.util.Log.d(TAG,
                        ",xiuxiuUser.getXiuxiu_name() = " + result.getXiuxiu_name()
                                + ",xiuxiuUser.getXiuxiu_id() = " + result.getXiuxiu_id()
                );
                EaseUserCacheManager.getInstance().add(result);
            }
        });

        findViewById(R.id.xiuxiu_ta_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshUserData(null);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.more:
                DialogActivity.startActivity(PersonDetailActivity.this,MORE_BT_REQUEST_CODE);
                break;
            case R.id.yuyin_layout:
                if(VoicePlayManager.getInstance().isPlaying()){
                    VoicePlayManager.getInstance().pause();
                }else{
                    VoicePlayManager.getInstance().play(XiuxiuUserInfoResult.getUrlVoice4Qiniu(mXiuxiuUserInfoResult.getVoice()));
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == MORE_BT_REQUEST_CODE && resultCode == RESULT_OK){
//            showProgressDialog();
            releaseFriend(xiuxiuId);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VoicePlayManager.getInstance().release();
    }

    private class PhotoAdpater extends BaseAdapter {

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
            if(mUrlList == null){
                return 0;
            }else {
                return mUrlList.size();
            }
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
            ((ImageView)convertView).setImageDrawable(new ColorDrawable(Color.parseColor("#a6a6a6")));
            if(mUrlList!=null && position<mUrlList.size()){
                ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST + mXiuxiuUserInfoResult.getPics().get(position),
                        (ImageView)convertView);
            }
            return convertView;
        }
    }

    // ============================================================================================
    // 获取用户信息
    // ============================================================================================
    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            android.util.Log.d(TAG,"user = " + response);
            XiuxiuUserQueryResult res = gson.fromJson(response, XiuxiuUserQueryResult.class);
            if(res!=null && res.getUserinfos()!=null && res.getUserinfos().size()>0){
                refreshUserData(res.getUserinfos().get(0));
            }
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.d(TAG,"error = " + error);
        }
    };

    private void queryUserInfo(String xiuxiu_id) {
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getQueryUserInfoUrl(xiuxiu_id), mRefreshListener, mRefreshErroListener));
    }
    private String getQueryUserInfoUrl(String xiuxiu_id) {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_USER_INFO)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", xiuxiu_id)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "url = " + url);
        return url;
    }

    // ============================================================================================
    // 删除好友
    // ============================================================================================

    private ProgressDialog mProgressDialog;

    private void showProgressDialog(){
        mProgressDialog = ProgressDialog.show(this, "提示", "正在加载中...");
    }

    private void dismisslProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }


    private Response.Listener<String> mReleaseFriendListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            android.util.Log.d(TAG,"response = " + response);
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            if(res!=null && res.isSuccess()){
                ToastUtil.showMessage(PersonDetailActivity.this, "删除好友成功!");
                // 被删除
                Map<String, EaseUser> localUsers = ImHelper.getInstance().getContactList();
                localUsers.remove(xiuxiuId);
                UserDao dao = new UserDao(getApplicationContext());
                dao.deleteContact(xiuxiuId);
                InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getApplicationContext());
                inviteMessgeDao.deleteMessage(xiuxiuId);
                //发送好友变动广播
                LocalBroadcastManager broadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
            }
        }
    };
    private Response.ErrorListener mErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.d(TAG,"error = " + error);
        }
    };

    private void releaseFriend(String xiuxiu_id) {
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getReleaseFriendUrl(xiuxiu_id), mReleaseFriendListener, mErroListener));
    }
    private String getReleaseFriendUrl(String xiuxiu_id) {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.RELEASE_FRIENDS_RELATION)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("remote_id", xiuxiu_id)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "url = " + url);
        return url;
    }
}
