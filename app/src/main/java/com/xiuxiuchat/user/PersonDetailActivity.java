package com.xiuxiuchat.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.xiuxiuchat.CommonLib;
import com.xiuxiuchat.R;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.api.XiuxiuPerson;
import com.xiuxiuchat.api.XiuxiuResult;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.api.XiuxiuUserQueryResult;
import com.xiuxiuchat.base.BaseActivity;
import com.xiuxiuchat.chat.ChatPage;
import com.xiuxiuchat.easeim.Constant;
import com.xiuxiuchat.easeim.EaseUserCacheManager;
import com.xiuxiuchat.easeim.ImHelper;
import com.xiuxiuchat.easeim.XiuxiuSayHelloManager;
import com.xiuxiuchat.user.invitation.InviteMessgeDao;
import com.xiuxiuchat.user.invitation.UserDao;
import com.xiuxiuchat.user.voice.VoicePlayManager;
import com.xiuxiuchat.utils.DateUtils;
import com.xiuxiuchat.utils.DialogActivity;
import com.xiuxiuchat.utils.Md5Util;
import com.xiuxiuchat.utils.ScreenUtils;
import com.xiuxiuchat.utils.ToastUtil;
import com.xiuxiuchat.utils.UiUtil;
import com.xiuxiuchat.utils.XiuxiuUtils;

import java.net.URLDecoder;
import java.util.List;
import java.util.Map;

/**
 * 第三方用户的个人页面
 * Created by huzhi on 16-6-16.
 */
public class PersonDetailActivity extends BaseActivity implements View.OnClickListener{

    private static String TAG = PersonDetailActivity.class.getSimpleName();

    public static void startActivity(Context context,String xiuxiu_id,boolean isShowBottomLayout){
        Intent intent = new Intent(context,PersonDetailActivity.class);
        intent.putExtra("xiuxiu_id", xiuxiu_id);
        intent.putExtra("isShowBottomLayout", isShowBottomLayout);
        context.startActivity(intent);
    }

    public static int MORE_BT_REQUEST_CODE = 1;

    private ViewGroup mLayout;
    private GridView mPhotoWall;

    private int mScreenWidth;
    private int mPhotoItemWidth;
    private int mPhotoItemHeight;
    /*个人签名*/
    private TextView mSignTv;
    /*标题*/
    private TextView mTitleTv;
    /*年纪*/
    private TextView mAgTv;
    /*城市*/
    private TextView mCityTv;
    /*xiuxiu id*/
    private TextView mXiuxiuIdTv;
    /*在线时间*/
    private TextView mOnLineTime;
    /*魅力值*/
    private ImageView mCharmIv;
    /*url list*/
    private List<String> mUrlList;
    /*照片墙*/
    PhotoAdpater mPhotoAdpater;
    /*用户信息*/
    private XiuxiuUserInfoResult mXiuxiuUserInfoResult;
    /*xiuxiu id*/
    private String xiuxiuId;
    /**
     * 免费打招呼次数
     */
    private int mCallTimes = -1;

    private Handler mUiHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        mScreenWidth = ScreenUtils.getScreenWidth(getApplication());
        setContentView(R.layout.activity_user_detail_page);
        setupViews();
        initData();
        searchBecomplainUsers();
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
        mXiuxiuIdTv = (TextView) findViewById(R.id.heixiu_hao_value);
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

    /**
     * 获取用户被投诉标签
     */
    private void searchBecomplainUsers(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                XiuxiuUtils.searchBecomplainUsers(xiuxiuId);
            }
        }).start();
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
        android.util.Log.d("AAAA", "height = " + height);
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(width, height);
        ll.setMargins(ScreenUtils.dip2px(getApplicationContext(), 7), 0, ScreenUtils.dip2px(getApplicationContext(), 7), 0);
        mPhotoWall.setLayoutParams(ll);
    }

    private void initData(){
        findViewById(R.id.more).setVisibility(View.VISIBLE);
        xiuxiuId= getIntent().getStringExtra("xiuxiu_id");
        android.util.Log.d(TAG, "xiuxiu_id = " + xiuxiuId);

        refreshUserData(EaseUserCacheManager.getInstance().getBeanById(xiuxiuId));
        queryUserInfo(xiuxiuId);

        findViewById(R.id.say_hello_layout).setVisibility(View.VISIBLE);
        findViewById(R.id.xiuxiu_ta_layout).setVisibility(View.VISIBLE);

        boolean isShowBottomLayout = getIntent ().getBooleanExtra("isShowBottomLayout",false);
        if(isShowBottomLayout){
            findViewById(R.id.bottom_layout).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.bottom_layout).setVisibility(View.GONE);
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
            findViewById(R.id.yuyin_txt_no).setVisibility(View.VISIBLE);
            ((TextView)findViewById(R.id.yuyin_txt_no)).setText("暂无语音介绍");
            findViewById(R.id.yuyin_bt).setVisibility(View.GONE);
        }else{
            findViewById(R.id.yuyin_bt).setVisibility(View.VISIBLE);
            findViewById(R.id.yuyin_txt_no).setVisibility(View.GONE);
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
            mXiuxiuIdTv.setText(URLDecoder.decode(mXiuxiuUserInfoResult.getXiuxiu_id()));
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

        if(TextUtils.isEmpty(mXiuxiuUserInfoResult.getGet_gift())){
            findViewById(R.id.gift_layout).setVisibility(View.GONE);
            findViewById(R.id.gift_layout_line).setVisibility(View.GONE);
        }

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

        if(TextUtils.isEmpty(XiuxiuUserInfoResult.getInstance().getGet_gift())){
            findViewById(R.id.gift_layout).setVisibility(View.GONE);
            findViewById(R.id.gift_layout_line).setVisibility(View.GONE);
        }else{
            findViewById(R.id.gift_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.gift_layout_line).setVisibility(View.VISIBLE);
            setGifts(XiuxiuUserInfoResult.getInstance().getGiftList());
        }

        setupPhotoWall();

        findViewById(R.id.say_hello_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ImHelper.getInstance().getContactList().get(xiuxiuId)!=null) { //是好友
                    enterConversationPage(false);
                    return;
                }
                if(mCallTimes<=0){
                    showConfirmDialog();
                }else{
                    enterConversationPage(false);
                }
            }
        });

        findViewById(R.id.xiuxiu_ta_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterConversationPage(true);
            }
        });
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
        switch (gift.type){
            case 0:
                giftImg.setImageResource(R.drawable.gift_01);
                giftName.setText("鲜花");
                break;
            case 1:
                giftImg.setImageResource(R.drawable.gift_02);
                giftName.setText("香蕉");
                break;
            case 2:
                giftImg.setImageResource(R.drawable.gift_03);
                giftName.setText("套套");
                break;
            case 3:
                giftImg.setImageResource(R.drawable.gift_04);
                giftName.setText("蛋糕");
                break;
            case 4:
                giftImg.setImageResource(R.drawable.gift_05);
                giftName.setText("红心");
                break;
            case 5:
                giftImg.setImageResource(R.drawable.gift_06);
                giftName.setText("钻戒");
                break;
            case 6:
                giftImg.setImageResource(R.drawable.gift_07);
                giftName.setText("香水");
                break;
            case 7:
                giftImg.setImageResource(R.drawable.gift_08);
                giftName.setText("跑车");
                break;
        }
    }

    private void enterConversationPage(boolean enterXiuxiu){
        if(mXiuxiuUserInfoResult!=null) {
            ChatPage.startActivity(PersonDetailActivity.this,
                    mXiuxiuUserInfoResult.getXiuxiu_id(), mXiuxiuUserInfoResult.getXiuxiu_name(),enterXiuxiu);
            EaseUserCacheManager.getInstance().add(mXiuxiuUserInfoResult);
        }else{
            ChatPage.startActivity(PersonDetailActivity.this,
                    xiuxiuId, xiuxiuId,enterXiuxiu);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        refreshUserData(null);
        refreshXiuxiuTimes();
    }

    private void refreshXiuxiuTimes(){
        //1.是好友  2.已经打过招呼
        if(ImHelper.getInstance().getContactList().get(xiuxiuId)!=null || XiuxiuSayHelloManager.getInstance().isCanSayHell(xiuxiuId)){
            ((TextView)((ViewGroup)findViewById(R.id.say_hello_layout)).getChildAt(0)).setText("发消息");
            ((ViewGroup)findViewById(R.id.say_hello_layout)).getChildAt(1).setVisibility(View.GONE);
            ((ViewGroup)findViewById(R.id.xiuxiu_ta_layout)).getChildAt(1).setVisibility(View.GONE);
        }else{//非好友
            int times = XiuxiuSayHelloManager.getInstance().getCallTimes();
            if(times>=0){
                mCallTimes = times;
                TextView tv = (TextView) findViewById(R.id.say_hello_txt);
                tv.setText("今天还有" + mCallTimes + "次免费机会");
                if(times==3){//如果３次times　相当于重置招呼数
                    XiuxiuSayHelloManager.getInstance().clear();
                }
            }else{
                TextView tv = (TextView) findViewById(R.id.say_hello_txt);
                tv.setText("今天还有3次免费机会");
            }
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.more:
                DialogActivity.startActivity(PersonDetailActivity.this,MORE_BT_REQUEST_CODE,xiuxiuId);
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
            int type = data.getIntExtra("type",0);
            if(type == DialogActivity.OPERATION_DELETE_FRIEND)
                releaseFriend(xiuxiuId);
            else if(type == DialogActivity.OPERATION_PULL_BACK){
                //拉黑并且举报
                PullBlackReportActivity.startActivity(PersonDetailActivity.this,xiuxiuId);
            }
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
                ((ImageView)convertView).setScaleType(ImageView.ScaleType.CENTER_CROP);
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
            final XiuxiuUserQueryResult res = gson.fromJson(response, XiuxiuUserQueryResult.class);
            if(res!=null && res.getUserinfos()!=null && res.getUserinfos().size()>0){
                refreshUserData(res.getUserinfos().get(0));
                /*取完用户信息后　更新用户信息*/
                EaseUserCacheManager.getInstance().add(res.getUserinfos().get(0));
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

    // ============================================================================================
    // 确认付费对话框
    // ============================================================================================
    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonDetailActivity.this);
        builder.setMessage("确认要花费咻币和Ta打招呼吗？");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enterConversationPage(false);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

}
