package com.xiuxiu.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;

import com.xiuxiu.R;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuSettingsConstant;

/**
 * Created by zhihu on 16-5-2.
 */
public class SetupPage extends BaseActivity implements View.OnClickListener{

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,SetupPage.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_setup_page);
        setupViews();
        initData();
    }

    private void setupViews(){
        findViewById(R.id.voice_layout).setOnClickListener(this);
        findViewById(R.id.vibration_layout).setOnClickListener(this);
        findViewById(R.id.new_message_alert_layout).setOnClickListener(this);
        findViewById(R.id.xiuxiu_layout).setOnClickListener(this);
        findViewById(R.id.xiuxiu_broadcast_layout).setOnClickListener(this);

        mVoiceCheck = (CheckBox) findViewById(R.id.voice_check);
        mVibrationCheck = (CheckBox) findViewById(R.id.vibration_check);
        mNewMessageAlertCheck = (CheckBox) findViewById(R.id.new_message_alert_check);
        mXiuxiuCheck = (CheckBox) findViewById(R.id.xiuxiu_check);
        mXiuxiuBroadcastCheck = (CheckBox) findViewById(R.id.xiuxiu_broadcast_check);

    }

    private void initData(){
        mVoiceCheck.setChecked(XiuxiuSettingsConstant.isVoicOn());
        mVibrationCheck.setChecked(XiuxiuSettingsConstant.isVibrationOn());
        mNewMessageAlertCheck.setChecked(XiuxiuSettingsConstant.isNewMessagePromptOn());
        mXiuxiuCheck.setChecked(XiuxiuSettingsConstant.isXiuxiuPromptOn());
        mXiuxiuBroadcastCheck.setChecked(XiuxiuSettingsConstant.isXiuxiuBroadcastOn());
    }


    private CheckBox mVoiceCheck,mVibrationCheck,mNewMessageAlertCheck,mXiuxiuCheck,mXiuxiuBroadcastCheck;


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.voice_layout:
                mVoiceCheck.setChecked(!mVoiceCheck.isChecked());
                break;
            case R.id.vibration_layout:
                mVibrationCheck.setChecked(!mVibrationCheck.isChecked());
                break;
            case R.id.new_message_alert_layout:
                mNewMessageAlertCheck.setChecked(!mNewMessageAlertCheck.isChecked());
                break;
            case R.id.xiuxiu_layout:
                mXiuxiuCheck.setChecked(!mXiuxiuCheck.isChecked());
                break;
            case R.id.xiuxiu_broadcast_layout:
                mXiuxiuBroadcastCheck.setChecked(!mXiuxiuBroadcastCheck.isChecked());
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        XiuxiuSettingsConstant.setNewMessagePromptOn(mNewMessageAlertCheck.isChecked());
        XiuxiuSettingsConstant.setVibrationOn(mVibrationCheck.isChecked());
        XiuxiuSettingsConstant.setVoicOn(mVoiceCheck.isChecked());
        XiuxiuSettingsConstant.setXiuxiuBroadcastOn(mXiuxiuBroadcastCheck.isChecked());
        XiuxiuSettingsConstant.setXiuxiuPromptOn(mXiuxiuCheck.isChecked());
    }
}
