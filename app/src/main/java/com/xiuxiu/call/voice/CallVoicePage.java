package com.xiuxiu.call.voice;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuPerson;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.api.XiuxiuUserQueryResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.call.CallManager;
import com.xiuxiu.chat.TimeBar;
import com.xiuxiu.easeim.EaseUserCacheManager;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.CharmLevelActivity;
import com.xiuxiu.utils.Md5Util;
import com.xiuxiu.utils.ToastUtil;

import java.net.URLDecoder;

/**
 * Created by huzhi on 16-6-26.
 */
public class CallVoicePage extends BaseActivity implements View.OnClickListener{


    private static String TAG = CallVoicePage.class.getSimpleName();

    public static void startActivity(Context ac,String from,boolean isActionCall){
        Intent intent = new Intent(ac,CallVoicePage.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("from",from);
        intent.putExtra("is_action_call",isActionCall);
        ac.startActivity(intent);
    }


    private Handler mHandler = new Handler();

    EMCallStateChangeListener listener = new EMCallStateChangeListener() {
        @Override
        public void onCallStateChanged(EMCallStateChangeListener.CallState
                                               callState, EMCallStateChangeListener.CallError error) {
            switch (callState) {
                case CONNECTING: // 正在连接对方
                    android.util.Log.d(TAG,"CONNECTING");
                    mHander.post(new Runnable() {
                        @Override
                        public void run() {
                            /*if(isActionCall) {
                                mStatusView.setText("拨号中...");
                            }else{
                                mStatusView.setText("正在连接对方...");
                            }*/
                            mStatusView.setText("正在连接对方...");
                            mStatusView.setVisibility(View.VISIBLE);
                            mTimeView.setVisibility(View.GONE);
                        }
                    });
                    break;
                case CONNECTED: // 双方已经建立连接
                    android.util.Log.d(TAG,"CONNECTED");
                    mHander.post(new Runnable() {
                        @Override
                        public void run() {
                            /*
                            if(isActionCall) {
                                mStatusView.setText("等待对方接听...");
                            }*/
                            mStatusView.setText("已经和对方建立连接，等待对方接受...");
                            mStatusView.setVisibility(View.VISIBLE);
                            mTimeView.setVisibility(View.GONE);
                        }
                    });
                    break;
                case ACCEPTED: // 电话接通成功
                    android.util.Log.d(TAG,"ACCEPTED");
                    mHander.post(new Runnable() {
                        @Override
                        public void run() {
                            mBeforeCallLayout.setVisibility(View.GONE);
                            mAfterCallLayout.setVisibility(View.VISIBLE);
                            mStatusView.setVisibility(View.GONE);
                            mTimeView.setVisibility(View.VISIBLE);
                            mTimeView.start();
                            mHandFreeLayout.setVisibility(View.VISIBLE);
                            mMutLayout.setVisibility(View.VISIBLE);
                        }
                    });
                    break;
                case DISCONNNECTED: // 电话断了
                    ToastUtil.showMessage(CallVoicePage.this,"通话已中断");
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    },1000);
                    android.util.Log.d(TAG,"DISCONNNECTED");
                    break;
                case NETWORK_UNSTABLE: //网络不稳定
                    android.util.Log.d(TAG,"NETWORK_UNSTABLE");
                    if(error == EMCallStateChangeListener.CallError.ERROR_NO_DATA){
                        //无通话数据
                    }else{
                    }
                    ToastUtil.showMessage(CallVoicePage.this,"网络不稳定");
                    break;
                case NETWORK_NORMAL: //网络恢复正常
                    android.util.Log.d(TAG,"NETWORK_NORMAL");
//                    ToastUtil.showMessage(CallVoicePage.this, "网络恢复正常");
                    break;
                default:
                    break;
            }

        }
    };


    private String mFrom;
    /*是否主动进入拨打页面*/
    private boolean isActionCall = false;

    protected AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_call_voice_page);
        mFrom = getIntent().getStringExtra("from");
        isActionCall = getIntent().getBooleanExtra("is_action_call", false);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setUpViews();
        initCall();
        initData(mFrom);
        android.util.Log.d(TAG,"mFrom = " + mFrom);
    }


    private Handler mHander = new Handler();

    private View answerCall;
    private View endCall;

    private View mBeforeCallLayout;
    private View mAfterCallLayout;

    private TextView mStatusView;
    private TimeBar mTimeView;

    private boolean isMuteState;
    private boolean isHandsfreeState;

    private TextView mSignatureTv;
    private TextView muserAge;
    private ImageView mVipGradeIv;
    /*免提按钮*/
    private ImageView mHandFreeBt;
    /*静音按钮*/
    private ImageView mMuteBt;

    private View mHandFreeLayout;
    private View mMutLayout;

    private void setUpViews(){
        answerCall = findViewById(R.id.answer_call);
        endCall = findViewById(R.id.end_call);
        mBeforeCallLayout = findViewById(R.id.before_call_layout);
        mAfterCallLayout = findViewById(R.id.after_call_layout);
        mStatusView = (TextView)findViewById(R.id.status);
        mTimeView = (TimeBar)findViewById(R.id.time);

        mHandFreeBt = (ImageView) findViewById(R.id.hands_free);
        mMuteBt = (ImageView) findViewById(R.id.mute);

        mHandFreeLayout = findViewById(R.id.hands_free_layout);
        mMutLayout = findViewById(R.id.mute_layout);

        if(isActionCall){
            mBeforeCallLayout.setVisibility(View.GONE);
            mAfterCallLayout.setVisibility(View.VISIBLE);
            mHandFreeLayout.setVisibility(View.GONE);
            mMutLayout.setVisibility(View.GONE);
        }else{
            mBeforeCallLayout.setVisibility(View.VISIBLE);
            mAfterCallLayout.setVisibility(View.GONE);
        }
        mStatusView.setVisibility(View.VISIBLE);
        mTimeView.setVisibility(View.GONE);

        answerCall.setOnClickListener(this);
        findViewById(R.id.end_call).setOnClickListener(this);
        findViewById(R.id.end_call_2).setOnClickListener(this);
        findViewById(R.id.hands_free).setOnClickListener(this);
        findViewById(R.id.mute).setOnClickListener(this);

        //用户信息相关
        mHeadImg = (CircleImageView) findViewById(R.id.head_img);
        mNameTv = (TextView) findViewById(R.id.name);
        mSignatureTv = (TextView) findViewById(R.id.signature);
        muserAge = (TextView) findViewById(R.id.user_age);
        mVipGradeIv = (ImageView) findViewById(R.id.vip_grade);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.answer_call:
                CallManager.getInstance(CallVoicePage.this).answserCall();
                break;
            case R.id.end_call:
                CallManager.getInstance(CallVoicePage.this).endCall();
                break;
            case R.id.end_call_2:
                CallManager.getInstance(CallVoicePage.this).endCall();
                break;
            case R.id.hands_free:
                if (isHandsfreeState) {
                    // 关闭免提
                    mHandFreeBt.setImageResource(R.drawable.call_voice_speaker_normal);
                    closeSpeakerOn();
                    isHandsfreeState = false;
                } else {
                    mHandFreeBt.setImageResource(R.drawable.call_voice_speaker_off);
                    openSpeakerOn();
                    isHandsfreeState = true;
                }
                break;
            case R.id.mute:
                if (isMuteState) {
                    // 关闭静音
                    mMuteBt.setImageResource(R.drawable.call_voice_mute_normal);
                    EMClient.getInstance().callManager().resumeVoiceTransfer();
                    isMuteState = false;
                } else {
                    // 打开静音
                    mMuteBt.setImageResource(R.drawable.call_voice_mute_on);
                    EMClient.getInstance().callManager().pauseVoiceTransfer();
                    isMuteState = true;
                }
                break;
        }
    }

    private void initCall(){
        EMClient.getInstance().callManager().addCallStateChangeListener(listener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeView.stop();
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setMicrophoneMute(false);
        EMClient.getInstance().callManager().removeCallStateChangeListener(listener);
        CallManager.getInstance(CallVoicePage.this).endCall();
    }



    // 打开扬声器
    protected void openSpeakerOn() {
        try {
            if (!audioManager.isSpeakerphoneOn())
                audioManager.setSpeakerphoneOn(true);
            audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭扬声器
    protected void closeSpeakerOn() {
        try {
            if (audioManager != null) {
                // int curVolume =
                // audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
                if (audioManager.isSpeakerphoneOn())
                    audioManager.setSpeakerphoneOn(false);
                audioManager.setMode(AudioManager.MODE_IN_COMMUNICATION);
                // audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,
                // curVolume, AudioManager.STREAM_VOICE_CALL);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ============================================================================================
    // 获取用户数据
    // ============================================================================================
    private CircleImageView mHeadImg;
    private TextView mNameTv;

    private void initData(String xiuxiuId){
        XiuxiuUserInfoResult xiuxiuUserInfoResult = EaseUserCacheManager.getInstance().getBeanById(xiuxiuId);

        if(xiuxiuUserInfoResult!=null){
            refreshUserInfoData(xiuxiuUserInfoResult);
        }else{
            queryUserInfo(xiuxiuId);
        }
    }

    private void refreshUserInfoData(XiuxiuUserInfoResult xiuxiuUserInfoResult){
        ImageLoader.getInstance().displayImage(HttpUrlManager.QI_NIU_HOST
                + xiuxiuUserInfoResult.getPic(), mHeadImg);
        mNameTv.setText(xiuxiuUserInfoResult.getXiuxiu_name());
        mSignatureTv.setText(xiuxiuUserInfoResult.getSign());

        if(XiuxiuUserInfoResult.isMale(xiuxiuUserInfoResult.getSex())){
            XiuxiuPerson.setWealthValue(mVipGradeIv, xiuxiuUserInfoResult.getFortune());
            muserAge.setBackgroundResource(R.drawable.male_age_bg);
        }else{
            XiuxiuPerson.setCharmValue(mVipGradeIv, xiuxiuUserInfoResult.getCharm());
            muserAge.setBackgroundResource(R.drawable.female_age_bg);
        }
        if(!TextUtils.isEmpty(xiuxiuUserInfoResult.getAge())) {
            muserAge.setText(URLDecoder.decode(xiuxiuUserInfoResult.getAge()));
        }
    }


    // ============================================================================================
    // 获取用户信息
    // ============================================================================================
    private void queryUserInfo(String xiuxiuId) {
        Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                android.util.Log.d(TAG,"user = " + response);
                XiuxiuUserQueryResult res = gson.fromJson(response, XiuxiuUserQueryResult.class);
                if(res!=null && res.getUserinfos()!=null && res.getUserinfos().size()>0){
                    EaseUserCacheManager.getInstance().add(res.getUserinfos().get(0));
                    refreshUserInfoData(res.getUserinfos().get(0));
                }
            }
        };
        Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                android.util.Log.d(TAG,"error = " + error);
            }
        };
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getQueryUserInfoUrl(xiuxiuId), mRefreshListener, mRefreshErroListener));
    }
    private String getQueryUserInfoUrl(String xiuxiuId) {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.QUERY_USER_INFO)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", xiuxiuId)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "url = " + url);
        return url;
    }
}
