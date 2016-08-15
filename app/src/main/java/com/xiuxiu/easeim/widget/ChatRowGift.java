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

//        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ScreenUtils.getScreenWidth(getContext())*1/4,
//                ScreenUtils.getScreenWidth(getContext())*1/4);
//        giftImg.setLayoutParams(rl);

    }

    @Override
    protected void onUpdateView() {

    }

    @Override
    protected void onSetUpView() {

        int type = 0;
        try {
            type = message.getIntAttribute(EaseConstant.MESSAGE_ATTR_GIFT_TYPE);
        } catch (Exception e){}

        giftImg.setImageResource(R.drawable.head_default);

        switch (type){
            case 0:
                giftImg.setImageResource(R.drawable.gift_01);
                break;
            case 1:
                giftImg.setImageResource(R.drawable.gift_02);
                break;
            case 2:
                giftImg.setImageResource(R.drawable.gift_03);
                break;
            case 3:
                giftImg.setImageResource(R.drawable.gift_04);
                break;
            case 4:
                giftImg.setImageResource(R.drawable.gift_05);
                break;
            case 5:
                giftImg.setImageResource(R.drawable.gift_06);
                break;
            case 6:
                giftImg.setImageResource(R.drawable.gift_07);
                break;
            case 7:
                giftImg.setImageResource(R.drawable.gift_08);
                break;
            default:
                break;
        }

    }

    @Override
    protected void onBubbleClick() {

    }
}
