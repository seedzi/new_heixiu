package com.xiuxiuchat.easeim.widget;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.adapter.EaseMessageAdapter;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;
import com.xiuxiuchat.R;

import java.util.Date;

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
        handleTextMessage();
    }

    @Override
    protected void onBubbleClick() {

    }


    protected void handleTextMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case SUCCESS: // 发送成功
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
                    setStatus();
                    break;
                case FAIL: // 发送失败
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    break;
                case INPROGRESS: // 发送中
                    progressBar.setVisibility(View.VISIBLE);
                    statusView.setVisibility(View.GONE);
                    break;
                default:
                    break;
            }
        }else{
            progressBar.setVisibility(View.GONE);
            statusView.setVisibility(View.GONE);
            if(!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat){
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    protected void setStatus(){
        if(deliveredView != null){
            deliveredView.setBackgroundResource(com.hyphenate.easeui.R.drawable.xiuxiu_conversation_unread);
            deliveredView.setText("");
            //huzhi TODO  这块逻辑环信没做处理　目前这么处理
//            if (message.isDelivered()) {
//                deliveredView.setVisibility(View.VISIBLE);
//            } else {
//                deliveredView.setVisibility(View.INVISIBLE);
//            }
            deliveredView.setVisibility(View.VISIBLE);
        }

        if(ackedView != null){
            //huzhi
            ackedView.setBackgroundResource(com.hyphenate.easeui.R.drawable.xiuxiu_conversation_read);
            ackedView.setText("");
            if (message.isAcked()) {
                if (deliveredView != null) {
                    deliveredView.setVisibility(View.INVISIBLE);
                }
                ackedView.setVisibility(View.VISIBLE);
            } else {
                ackedView.setVisibility(View.INVISIBLE);
            }
        }
    }

    @Override
    protected void setUpBaseView() {
        // 设置用户昵称头像，bubble背景等
        TextView timestamp = (TextView) findViewById(com.hyphenate.easeui.R.id.timestamp);
        if (timestamp != null) {
            if (position == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // 两条消息时间离得如果稍长，显示时间
                EMMessage prevMessage = (EMMessage) adapter.getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
        //设置头像和nick
        if(message.direct() == EMMessage.Direct.SEND){
            EaseUserUtils.setUserAvatar(context, EMClient.getInstance().getCurrentUser(), userAvatarView);
            //发送方不显示nick
//            UserUtils.setUserNick(EMChatManager.getInstance().getCurrentUser(), usernickView);
        }else{
            EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
            EaseUserUtils.setUserNick(message.getFrom(), usernickView);
        }

        if (adapter instanceof EaseMessageAdapter) {
            if (((EaseMessageAdapter) adapter).isShowAvatar())
                userAvatarView.setVisibility(View.VISIBLE);
            else
                userAvatarView.setVisibility(View.GONE);
            if (usernickView != null) {
                if (((EaseMessageAdapter) adapter).isShowUserNick())
                    usernickView.setVisibility(View.VISIBLE);
                else
                    usernickView.setVisibility(View.GONE);
            }
            if (message.direct() == EMMessage.Direct.SEND) {
                if (((EaseMessageAdapter) adapter).getMyBubbleBg() != null)
                    bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getMyBubbleBg());
                // else
                // bubbleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.chatto_bg));
            } else if (message.direct() == EMMessage.Direct.RECEIVE) {
                if (((EaseMessageAdapter) adapter).getOtherBuddleBg() != null)
                    bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getOtherBuddleBg());
//                else
//                    bubbleLayout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.ease_chatfrom_bg));
            }
        }
    }
}
