package com.xiuxiuchat.easeim.widget;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hyphenate.chat.EMMessage;
import com.xiuxiuchat.R;
import com.xiuxiuchat.chat.im.ChatFragment;

/**
 * Created by huzhi on 16-7-25.
 */
public class ChatRowVideoNan2NvXiuxiu extends ChatRowImgNan2NvXiuxiu{

    public ChatRowVideoNan2NvXiuxiu(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    public void onSetUpView() {
        super.onSetUpView();
        ((ImageView)findViewById(R.id.img)).setImageResource(R.drawable.xiuxiu_video_icon);
    }
}
