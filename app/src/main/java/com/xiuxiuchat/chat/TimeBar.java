package com.xiuxiuchat.chat;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiuxiuchat.R;

/**
 * Created by huzhi on 16-6-30.
 */
public class TimeBar extends FrameLayout{

    public TimeBar(Context context) {
        super(context);
        initViews(context);
    }

    public TimeBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public TimeBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(context);
    }

    private TextView mint;
    private TextView sec;
    private long timeusedinsec;
    private boolean isstop = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    // 添加更新ui的代码
                    if (!isstop) {
                        updateView();
                        mHandler.sendEmptyMessageDelayed(1, 1000);
                    }
                    break;
                case 0:
                    break;
            }
        }

    };



    private void initViews(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.call_voice_time_bar_layout,this);
        mint = (TextView) findViewById(R.id.mint);
        sec = (TextView) findViewById(R.id.sec);
    }

    public void start(){
        mHandler.removeMessages(1);
        mHandler.sendEmptyMessage(1);
        isstop = false;
    }

    public void stop(){
        mHandler.sendEmptyMessage(0);
        isstop = true;
    }

    private void updateView() {
        timeusedinsec += 1;
        int minute = (int) (timeusedinsec / 60)%60;
        int second = (int) (timeusedinsec % 60);
        if (minute < 10)
            mint.setText("0" + minute);
        else
            mint.setText("" + minute);
        if (second < 10)
            sec.setText("0" + second);
        else
            sec.setText("" + second);
    }
}
