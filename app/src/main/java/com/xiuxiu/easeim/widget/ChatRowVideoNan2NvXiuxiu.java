package com.xiuxiu.easeim.widget;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.xiuxiu.R;
import com.xiuxiu.chat.im.ChatFragment;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgManager;

/**
 * Created by huzhi on 16-7-25.
 */
public class ChatRowVideoNan2NvXiuxiu extends ChatRowImgNan2NvXiuxiu{

    public ChatRowVideoNan2NvXiuxiu(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }
}
