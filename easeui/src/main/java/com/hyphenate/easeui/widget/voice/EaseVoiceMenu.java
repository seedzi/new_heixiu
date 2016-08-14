package com.hyphenate.easeui.widget.voice;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.hyphenate.easeui.R;

/**
 * Created by huzhi on 16-6-27.
 */
public class EaseVoiceMenu extends FrameLayout{

    public EaseVoiceMenu(Context context) {
        super(context);
        init(context);
    }

    public EaseVoiceMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EaseVoiceMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private ImageView mBt;

    private Chronometer mChronometer;

    public void init(Context context){
        LayoutInflater.from(context).inflate(R.layout.ease_widget_voice, this);
        mBt = (ImageView) findViewById(R.id.voice_press_bt);
        mChronometer = (Chronometer) findViewById(R.id.chronometer);
        mChronometer.setText("按住说话");
    }

    public void setOnBtPressListener(OnTouchListener l){
        if(mBt!=null && l!=null) {
            mBt.setOnTouchListener(l);
        }
    }

}
