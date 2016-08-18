package com.xiuxiu.easeim.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.DateUtils;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.chat.im.ChatFragment;
import com.xiuxiu.easeim.ChatRowUtils;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiu.utils.ScreenUtils;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.XiuxiuUtils;
import com.xiuxiu.xiuxiutask.XiuxiuTaskBean;

import java.util.Date;

/**
 * Created by huzhi on 16-7-14.
 * 咻咻语聊
 */
public class ChatRowVoiceXiuxiu extends EaseChatRow implements View.OnClickListener{


    private TextView mContentTv;

    private TextView mXiuxiuBSizeTv;

    private TextView mAgreeTv;

    private TextView mRefuseTv;

    private TextView mXiuxiuStatusTv;

    private TextView mPayTxtTv;

    private View mBottomReceivedLayout;

    private View mContentLayout;

    private XiuxiuTaskBean mXiuxiuTaskBean;

    private ImageView mXiuxiuStatusIv;

    private View mXiuxiuLine;

    public ChatRowVoiceXiuxiu(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }
    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_voice_received_xiuxiu : R.layout.ease_row_voice_sent_xiuxiu, this);
    }

    @Override
    protected void onFindViewById() {
        mContentTv = (TextView) findViewById(R.id.xiuxiu_ask_content);
        mXiuxiuBSizeTv = (TextView) findViewById(R.id.xiuxiu_bi_size);
        mAgreeTv = (TextView) findViewById(R.id.agree_bt);
        mRefuseTv = (TextView) findViewById(R.id.refuse_bt);
        mXiuxiuStatusTv = (TextView) findViewById(R.id.xiuxiu_status);
        mXiuxiuStatusIv = (ImageView) findViewById(R.id.xiuxiu_status_icon);
        mBottomReceivedLayout = findViewById(R.id.bottom_received_layout);
        mContentLayout = findViewById(R.id.content_layout);
        mPayTxtTv = (TextView) findViewById(R.id.pay_txt);
        mXiuxiuLine = findViewById(R.id.line);

        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ScreenUtils.getScreenWidth(getContext())*2/3, ViewGroup.LayoutParams.WRAP_CONTENT);
        mContentLayout.setLayoutParams(rl);
    }

    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        try {
            mXiuxiuTaskBean = XiuxiuTaskBean.parse2Bean(txtBody.getMessage());
        }catch (Exception e){
        }
        if(mXiuxiuTaskBean !=null){
            if(mContentTv!=null)
                mContentTv.setText(mXiuxiuTaskBean.content);
            if(mXiuxiuBSizeTv!=null)
                mXiuxiuBSizeTv.setText(mXiuxiuTaskBean.xiuxiub);
            if(mPayTxtTv!=null){
                try {
                    if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV)){
                        mPayTxtTv.setText("愿意支付:");
                    }else{
                        mPayTxtTv.setText("需要支付:");
                    }
                }catch (Exception e){}
            }
        }

        if(mBottomReceivedLayout!=null)
            mBottomReceivedLayout.setVisibility(View.GONE);
        if(mXiuxiuStatusTv!=null)
            mXiuxiuStatusTv.setVisibility(View.GONE);
        if(mXiuxiuStatusIv!=null)
            mXiuxiuStatusIv.setVisibility(View.GONE);
        if(mXiuxiuLine!=null)
            mXiuxiuLine.setVisibility(View.GONE);

        handleTextMessage();
    }

    protected void setStatus(String status){
        if(message.direct() == EMMessage.Direct.RECEIVE ) { //收到的咻咻ask消息
            mXiuxiuStatusIv.setVisibility(View.GONE);
            mXiuxiuLine.setVisibility(View.VISIBLE);
            if(status==null){//未操作
                mBottomReceivedLayout.setVisibility(View.VISIBLE);
                mXiuxiuStatusTv.setVisibility(View.GONE);
                mAgreeTv.setOnClickListener(this);
                mRefuseTv.setOnClickListener(this);
            }else if(status.equals(EaseConstant.XIUXIU_STATUS_AGREE)){
                mBottomReceivedLayout.setVisibility(View.GONE);
                mXiuxiuStatusTv.setVisibility(View.VISIBLE);
                mXiuxiuStatusTv.setText("已同意");
            }else if(status.equals(EaseConstant.XIUXIU_STATUS_REFUSE)){
                mBottomReceivedLayout.setVisibility(View.GONE);
                mXiuxiuStatusTv.setVisibility(View.VISIBLE);
                mXiuxiuStatusTv.setText("已拒绝");
            }
            if(ChatRowUtils.isOverdue(message.getMsgTime(), EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG)){
                mBottomReceivedLayout.setVisibility(View.GONE);
                mXiuxiuStatusTv.setVisibility(View.VISIBLE);
                mXiuxiuStatusTv.setText("已过期");
            }
        }else{  //发出的咻咻ask消息
            mBottomReceivedLayout.setVisibility(View.GONE);
            mXiuxiuLine.setVisibility(View.GONE);
            mXiuxiuStatusTv.setVisibility(View.GONE);
            mXiuxiuStatusIv.setVisibility(View.VISIBLE);
            if(status==null){//未操作
                mXiuxiuStatusIv.setImageResource(R.drawable.xiuxiu_task_status_no_accepted);//未接受
            }else if(status.equals(EaseConstant.XIUXIU_STATUS_AGREE)){
                mXiuxiuStatusIv.setImageResource(R.drawable.xiuxiu_task_status_accepted);//已接受
            }else if(status.equals(EaseConstant.XIUXIU_STATUS_REFUSE)){
                mXiuxiuStatusIv.setImageResource(R.drawable.xiuxiu_task_status_no_accepted);//未接受
            }
            if(ChatRowUtils.isOverdue(message.getMsgTime(), EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG)){
                mXiuxiuStatusIv.setImageResource(R.drawable.xiuxiu_task_status_invalid);//已过期
            }
        }
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

                    String status;
                    try {
                        String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
                        status = XiuxiuActionMsgManager.getInstance().getStatusById(askId);
                    }catch (Exception e){
                        status = null;
                    }
                    setStatus(status);
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
            if(!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat){
                try {
                    EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }
            String status;
            try {
                String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
                status = XiuxiuActionMsgManager.getInstance().getStatusById(askId);
            }catch (Exception e){
                status = null;
            }
            setStatus(status);
        }
    }

    @Override
    protected void onUpdateView() {
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onBubbleClick() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.agree_bt:
                try {
                    String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
                    ChatFragment.sendAgreeXiuxiuMessageAgree4VoiceCall(message.getFrom(), askId,mXiuxiuTaskBean.xiuxiub);
                    XiuxiuActionMsgManager.getInstance().add(askId, EaseConstant.XIUXIU_STATUS_AGREE);
                } catch (Exception e) {
                }
                setStatus(EaseConstant.XIUXIU_STATUS_AGREE);

                break;
            case R.id.refuse_bt:
                try {
                    String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
                    ChatFragment.sendAgreeXiuxiuMessageRefuse(message.getFrom(),
                            message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID));
                    XiuxiuActionMsgManager.getInstance().add(askId, EaseConstant.XIUXIU_STATUS_REFUSE);
                    setStatus(EaseConstant.XIUXIU_STATUS_REFUSE);
                } catch (Exception e){}
                break;
        }
    }




    //覆盖掉父类方法　不让干扰子类
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
        }else{
            EaseUserUtils.setUserAvatar(context, message.getFrom(), userAvatarView);
            EaseUserUtils.setUserNick(message.getFrom(), usernickView);
        }
    }
}
