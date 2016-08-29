/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiuxiuchat.call.voice;

import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hyphenate.chat.EMCallStateChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.widget.CircleImageView;
import com.hyphenate.util.EMLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiuxiuchat.R;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.HttpUrlManager;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.api.XiuxiuPerson;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.api.XiuxiuUserQueryResult;
import com.xiuxiuchat.call.CallActivity;
import com.xiuxiuchat.easeim.EaseUserCacheManager;
import com.xiuxiuchat.easeim.ImHelper;
import com.xiuxiuchat.utils.Md5Util;
import com.xiuxiuchat.utils.ToastUtil;
import com.xiuxiuchat.utils.XiuxiuUtils;

import java.net.URLDecoder;
import java.util.UUID;

/**
 * 语音通话页面
 * 
 */
public class VoiceCallActivity extends CallActivity implements OnClickListener {
	private LinearLayout comingBtnContainer;
	private ImageView hangupBtn;
	private ImageView refuseBtn;
	private ImageView answerBtn;
	private ImageView muteImage;
	private ImageView handsFreeImage;

	private boolean isMuteState;
	private boolean isHandsfreeState;
	
	private TextView callStateTextView;
	private boolean endCallTriggerByMe = false;
//	private TextView nickTextView; //huzhi
//	private TextView durationTextView; //huzhi
	private Chronometer chronometer;
	String st1;
	private LinearLayout voiceContronlLayout;
    private TextView netwrokStatusVeiw;
    /**
     * 每分钟消耗的咻咻b
     */
    private int mCostXiuxiubPerMinute = 20;

    /**
     * 静音，扬声
     */
    private View mMuteLayout,mHandsFreelayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
        	finish();
        	return;
        }
//		setContentView(R.layout.em_activity_voice_call);

        setContentView(R.layout.activity_call_voice_page);

		ImHelper.getInstance().isVoiceCalling = true;
		callType = 0;

		comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
		refuseBtn = (ImageView) findViewById(R.id.btn_refuse_call);
		answerBtn = (ImageView) findViewById(R.id.btn_answer_call);
		hangupBtn = (ImageView) findViewById(R.id.btn_hangup_call);
		muteImage = (ImageView) findViewById(R.id.iv_mute);
		handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
		callStateTextView = (TextView) findViewById(R.id.tv_call_state);
//		nickTextView = (TextView) findViewById(R.id.tv_nick); //huzhi
//		durationTextView = (TextView) findViewById(R.id.tv_calling_duration); //huzhi
		chronometer = (Chronometer) findViewById(R.id.chronometer);
		voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);
		netwrokStatusVeiw = (TextView) findViewById(R.id.tv_network_status);

        mMuteLayout = findViewById(R.id.mute_layout);
        mHandsFreelayout = findViewById(R.id.hands_free_layout);

		refuseBtn.setOnClickListener(this);
		answerBtn.setOnClickListener(this);
		hangupBtn.setOnClickListener(this);
		muteImage.setOnClickListener(this);
		handsFreeImage.setOnClickListener(this);

		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
						| WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		// 注册语音电话的状态的监听
		addCallStateListener();
		msgid = UUID.randomUUID().toString();

		username = getIntent().getStringExtra("username");
		// 语音电话是否为接收的
		isInComingCall = getIntent().getBooleanExtra("isComingCall", false);
        try {
            mCostXiuxiubPerMinute = Integer.valueOf(getIntent().getStringExtra("xiuxiub"));
        }catch (Exception e){
            mCostXiuxiubPerMinute = 20;
        }

		// 设置通话人
//		nickTextView.setText(username); //huzhi
		if (!isInComingCall) {// 拨打电话
			soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
			outgoing = soundPool.load(this, R.raw.em_outgoing, 1);
            voiceContronlLayout.setVisibility(View.VISIBLE);
			comingBtnContainer.setVisibility(View.GONE);
			hangupBtn.setVisibility(View.VISIBLE);
            mMuteLayout.setVisibility(View.GONE);
            mHandsFreelayout.setVisibility(View.GONE);
			st1 = getResources().getString(R.string.Are_connected_to_each_other);
			callStateTextView.setText(st1);
			handler.sendEmptyMessage(MSG_CALL_MAKE_VOICE);
		} else { // 有电话进来
			voiceContronlLayout.setVisibility(View.GONE);
            comingBtnContainer.setVisibility(View.VISIBLE);
			Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			audioManager.setMode(AudioManager.MODE_RINGTONE);
			audioManager.setSpeakerphoneOn(true);
			ringtone = RingtoneManager.getRingtone(this, ringUri);
			ringtone.play();
		}

        initData(username);
	}

	/**
	 * 设置电话监听
	 */
	void addCallStateListener() {
	    callStateListener = new EMCallStateChangeListener() {
            
            @Override
            public void onCallStateChanged(CallState callState, final CallError error) {
                // Message msg = handler.obtainMessage();
                EMLog.d("EMCallManager", "onCallStateChanged:" + callState);
                switch (callState) {
                
                case CONNECTING: // 正在连接对方
                    runOnUiThread(new Runnable() {
                        
                        @Override
                        public void run() {
                            callStateTextView.setText(st1);
                        }
                    });
                    android.util.Log.d(TAG,"CONNECTING");
                    break;
                case CONNECTED: // 双方已经建立连接
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            String st3 = getResources().getString(R.string.have_connected_with);
                            callStateTextView.setText(st3);
                        }
                    });
                    android.util.Log.d(TAG, "CONNECTED");
                    break;

                case ACCEPTED: // 电话接通成功
                    android.util.Log.d(TAG, "ACCEPTED");
                    handler.removeCallbacks(timeoutHangup);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            try {
                                if (soundPool != null)
                                    soundPool.stop(streamID);
                            } catch (Exception e) {
                            }
                            if(!isHandsfreeState)
                                closeSpeakerOn();

                            mMuteLayout.setVisibility(View.VISIBLE);
                            mHandsFreelayout.setVisibility(View.VISIBLE);
                            voiceContronlLayout.setVisibility(View.VISIBLE);
                            comingBtnContainer.setVisibility(View.GONE);

                            //显示是否为直连，方便测试
                            // huzhi
                            /*
                            ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMClient.getInstance().callManager().isDirectCall()
                                    ? R.string.direct_call : R.string.relay_call);
                                    */

                            String str4 = getResources().getString(R.string.In_the_call);
                            callStateTextView.setText(str4);
                            callingState = CallingState.NORMAL;

                            chronometer.setVisibility(View.VISIBLE);
                            chronometer.setBase(SystemClock.elapsedRealtime());
                            // 开始记时
                            chronometer.start();

                        }
                    });
                    if(XiuxiuUserInfoResult.isMale(XiuxiuUserInfoResult.getInstance().getSex())){
                        XiuxiuApplication.getInstance().getUIHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CallCostMoneyManager.getInstance().costXiuxiuB(username, String.valueOf(mCostXiuxiubPerMinute), new XiuxiuUtils.CallBack() {
                                    @Override
                                    public void onPre() {
                                        android.util.Log.d("12345","onPre");
                                    }

                                    @Override
                                    public void onSccusee() {
                                        android.util.Log.d("12345","onSccusee");
                                    }
                                    @Override
                                    public void onFailure() {
                                        android.util.Log.d(TAG,"onFailure  您的余额已经不够");
                                        ToastUtil.showMessage(VoiceCallActivity.this,"您的余额已经不够！");
                                        CallCostMoneyManager.getInstance().stopLoop();
                                        hangupBtn.performClick();
                                    }
                                });
                            }
                        }, 15 * 1009);
                    }
                    break;
                case NETWORK_UNSTABLE:
                    android.util.Log.d(TAG,"  NETWORK_UNSTABLE");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.VISIBLE);
                            if(error == CallError.ERROR_NO_DATA){
                                netwrokStatusVeiw.setText(R.string.no_call_data);
                            }else{
                                netwrokStatusVeiw.setText(R.string.network_unstable);
                            }
                        }
                    });
                    break;
                case NETWORK_NORMAL:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            netwrokStatusVeiw.setVisibility(View.INVISIBLE);
                        }
                    });
                    break;
                case VOICE_PAUSE:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_PAUSE", 0).show();
                        }
                    });
                    break;
                case VOICE_RESUME:
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "VOICE_RESUME", 0).show();
                        }
                    });
                    break;
                case DISCONNNECTED: // 电话断了
                    android.util.Log.d(TAG,"  DISCONNNECTED");
                    handler.removeCallbacks(timeoutHangup);
                    final CallError fError = error;
                    runOnUiThread(new Runnable() {
                        private void postDelayedCloseMsg() {
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.d("AAA", "CALL DISCONNETED");
                                            saveCallRecord();
//                                            Animation animation = new AlphaAnimation(1.0f, 0.0f);
//                                            animation.setDuration(800);
//                                            findViewById(R.id.root_layout).startAnimation(animation);
                                            finish();
                                        }
                                    });
                                }
                            }, 200);
                        }

                        @Override
                        public void run() {
                            chronometer.stop();
                            callDruationText = chronometer.getText().toString();
                            String st2 = getResources().getString(R.string.The_other_party_refused_to_accept);
                            String st3 = getResources().getString(R.string.Connection_failure);
                            String st4 = getResources().getString(R.string.The_other_party_is_not_online);
                            String st5 = getResources().getString(R.string.The_other_is_on_the_phone_please);
                            
                            String st6 = getResources().getString(R.string.The_other_party_did_not_answer_new);
                            String st7 = getResources().getString(R.string.hang_up);
                            String st8 = getResources().getString(R.string.The_other_is_hang_up);
                            
                            String st9 = getResources().getString(R.string.did_not_answer);
                            String st10 = getResources().getString(R.string.Has_been_cancelled);
                            String st11 = getResources().getString(R.string.hang_up);
                            
                            if (fError == CallError.REJECTED) {
                                callingState = CallingState.BEREFUESD;
                                callStateTextView.setText(st2);
                            } else if (fError == CallError.ERROR_TRANSPORT) {
                                callStateTextView.setText(st3);
                            } else if (fError == CallError.ERROR_INAVAILABLE) {
                                callingState = CallingState.OFFLINE;
                                callStateTextView.setText(st4);
                            } else if (fError == CallError.ERROR_BUSY) {
                                callingState = CallingState.BUSY;
                                callStateTextView.setText(st5);
                            } else if (fError == CallError.ERROR_NORESPONSE) {
                                callingState = CallingState.NORESPONSE;
                                callStateTextView.setText(st6);
                            } else if (fError == CallError.ERROR_LOCAL_VERSION_SMALLER || fError == CallError.ERROR_PEER_VERSION_SMALLER){
                                callingState = CallingState.VERSION_NOT_SAME;
                                callStateTextView.setText(R.string.call_version_inconsistent);
                            } else {
                                if (isAnswered) {
                                    callingState = CallingState.NORMAL;
                                    if (endCallTriggerByMe) {
//                                        callStateTextView.setText(st7);
                                    } else {
                                        callStateTextView.setText(st8);
                                    }
                                } else {
                                    if (isInComingCall) {
                                        callingState = CallingState.UNANSWERED;
                                        callStateTextView.setText(st9);
                                    } else {
                                        if (callingState != CallingState.NORMAL) {
                                            callingState = CallingState.CANCED;
                                            callStateTextView.setText(st10);
                                        }else {
                                            callStateTextView.setText(st11);
                                        }
                                    }
                                }
                            }
                            postDelayedCloseMsg();
                        }

                    });

                    break;

                default:
                    break;
                }

            }
        };
		EMClient.getInstance().callManager().addCallStateChangeListener(callStateListener);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_refuse_call: // 拒绝接听
		    refuseBtn.setEnabled(false);
		    handler.sendEmptyMessage(MSG_CALL_REJECT);
			break;

		case R.id.btn_answer_call: // 接听电话
		    answerBtn.setEnabled(false);
		    closeSpeakerOn();
            callStateTextView.setText("正在接听...");
			comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            voiceContronlLayout.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(MSG_CALL_ANSWER);
			break;

		case R.id.btn_hangup_call: // 挂断电话
		    hangupBtn.setEnabled(false);
			chronometer.stop();
			endCallTriggerByMe = true;
			callStateTextView.setText(getResources().getString(R.string.hanging_up));
            handler.sendEmptyMessage(MSG_CALL_END);
			break;

		case R.id.iv_mute: // 静音开关
			if (isMuteState) {
				// 关闭静音
				muteImage.setImageResource(R.drawable.call_voice_mute_normal);
				EMClient.getInstance().callManager().resumeVoiceTransfer();
				isMuteState = false;
			} else {
				// 打开静音
				muteImage.setImageResource(R.drawable.call_voice_mute_on);
				EMClient.getInstance().callManager().pauseVoiceTransfer();
				isMuteState = true;
			}
			break;
		case R.id.iv_handsfree: // 免提开关
			if (isHandsfreeState) {
				// 关闭免提
				handsFreeImage.setImageResource(R.drawable.call_voice_speaker_off);
				closeSpeakerOn();
				isHandsfreeState = false;
			} else {
				handsFreeImage.setImageResource(R.drawable.call_voice_speaker_normal);
				openSpeakerOn();
				isHandsfreeState = true;
			}
			break;
		default:
			break;
		}
	}
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ImHelper.getInstance().isVoiceCalling = false;
        CallCostMoneyManager.getInstance().stopLoop();
    }

	@Override
	public void onBackPressed() {
		callDruationText = chronometer.getText().toString();
	}


    // ============================================================================================
    // 获取用户数据
    // ============================================================================================
    private static final String TAG = "VoiceCallActivity";
    private CircleImageView mHeadImg;
    private TextView mNameTv;
    private TextView mSignatureTv;
    private TextView muserAge;
    private ImageView mVipGradeIv;

    private void initData(String xiuxiuId){
        //用户信息相关
        mHeadImg = (CircleImageView) findViewById(R.id.head_img);
        mNameTv = (TextView) findViewById(R.id.name);
        mSignatureTv = (TextView) findViewById(R.id.signature);
        muserAge = (TextView) findViewById(R.id.user_age);
        mVipGradeIv = (ImageView) findViewById(R.id.vip_grade);

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
