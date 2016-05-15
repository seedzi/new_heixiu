package com.xiuxiu.xiuxiutask;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.xiuxiu.R;

/**
 * Created by huzhi on 16-5-9.
 */
public class XiuxiuTaskPage extends FragmentActivity implements View.OnClickListener{


    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,XiuxiuTaskPage.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    private View mVideoBt;

    private View mPicBt;

    private View mVoiceBt;

    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_xiuxiu_task_page);
        setupViews();
    }

    private void setupViews(){
        mVideoBt = findViewById(R.id.video);
        mPicBt = findViewById(R.id.pic);
        mVoiceBt = findViewById(R.id.voice);
        mVideoBt.setOnClickListener(this);
        mPicBt.setOnClickListener(this);
        mVoiceBt.setOnClickListener(this);

        mEditText = (EditText)findViewById(R.id.edit);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.video:
                mEditText.setHint("填写你对索要视频的期望和要求...(智能查看三次)");
                break;
            case R.id.pic:
                mEditText.setHint("填写你对索要图片的期望和要求...(智能查看三次)");
                break;
            case R.id.voice:
                mEditText.setHint("填写你对索要声音的期望和要求...(智能查看三次)");
                break;
        }
    }
}
