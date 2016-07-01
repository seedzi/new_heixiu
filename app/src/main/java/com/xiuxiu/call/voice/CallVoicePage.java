package com.xiuxiu.call.voice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.xiuxiu.R;
import com.xiuxiu.call.CallManager;
import com.xiuxiu.chat.TimeBar;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.utils.ToastUtil;

/**
 * Created by huzhi on 16-6-26.
 */
public class CallVoicePage extends FragmentActivity implements View.OnClickListener{


    private static String TAG = CallVoicePage.class.getSimpleName();

    public static void startActivity(Context ac){
        Intent intent = new Intent(ac,CallVoicePage.class);
        ac.startActivity(intent);
    }

    public static void startActivity(Context ac,String type,String from,boolean isActionCall){
        Intent intent = new Intent(ac,CallVoicePage.class);
        intent.putExtra("type",type);
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
                            if(isActionCall) {
                                mStatusView.setText("拨号中...");
                            }
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
                            if(isActionCall) {
                                mStatusView.setText("等待对方接听...");
                            }
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
                            mStatusView.setVisibility(View.GONE);
                            mTimeView.setVisibility(View.VISIBLE);
                            mTimeView.start();
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


    private String mType;
    private String mFrom;
    /*是否主动进入拨打页面*/
    private boolean isActionCall = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_call_voice_page);
        mType = getIntent().getStringExtra("type");
        mFrom = getIntent().getStringExtra("from");
        isActionCall = getIntent().getBooleanExtra("is_action_call",false);
        setUpViews();
        initCall();
    }


    private Handler mHander = new Handler();

    private View answerCall;
    private View endCall;

    private View mBeforeCallLayout;
    private View mAfterCallLayout;

    private TextView mStatusView;
    private TimeBar mTimeView;

    private void setUpViews(){
        answerCall = findViewById(R.id.answer_call);
        endCall = findViewById(R.id.end_call);
        mBeforeCallLayout = findViewById(R.id.before_call_layout);
        mAfterCallLayout = findViewById(R.id.after_call_layout);
        mStatusView = (TextView)findViewById(R.id.status);
        mTimeView = (TimeBar)findViewById(R.id.time);

        if(isActionCall){
            mBeforeCallLayout.setVisibility(View.GONE);
            mAfterCallLayout.setVisibility(View.VISIBLE);
        }else{
            mBeforeCallLayout.setVisibility(View.VISIBLE);
            mAfterCallLayout.setVisibility(View.GONE);
        }
        mStatusView.setVisibility(View.VISIBLE);
        mTimeView.setVisibility(View.GONE);

        answerCall.setOnClickListener(this);
        endCall.setOnClickListener(this);
        findViewById(R.id.end_call_2).setOnClickListener(this);
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
        }
    }

    private void initCall(){
        EMClient.getInstance().callManager().addCallStateChangeListener(listener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimeView.stop();
        EMClient.getInstance().callManager().removeCallStateChangeListener(listener);
        CallManager.getInstance(CallVoicePage.this).endCall();
    }
}
