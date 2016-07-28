package com.xiuxiu.easeim.widget;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.xiuxiu.R;
import com.xiuxiu.utils.ScreenUtils;

/**
 * Created by huzhi on 16-7-26.
 */
public class ChatRowGift extends EaseChatRow {

    public ChatRowGift(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_gift_received : R.layout.ease_row_gift_sent, this);
    }

    private ImageView giftImg;


    @Override
    protected void onFindViewById() {
        giftImg = (ImageView) findViewById(R.id.gift_img);

        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ScreenUtils.getScreenWidth(getContext())*1/2,
                ScreenUtils.getScreenWidth(getContext())*1/2);
        giftImg.setLayoutParams(rl);

        int type = 0;
        try {
            type = message.getIntAttribute(EaseConstant.MESSAGE_ATTR_GIFT_TYPE);
        } catch (Exception e){}
        switch (type){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            default:
                break;
        }
        giftImg.setImageResource(R.drawable.head_default);
    }

    @Override
    protected void onUpdateView() {

    }

    @Override
    protected void onSetUpView() {

    }

    @Override
    protected void onBubbleClick() {

    }
}
