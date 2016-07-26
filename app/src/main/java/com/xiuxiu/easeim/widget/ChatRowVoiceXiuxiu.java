package com.xiuxiu.easeim.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.exceptions.HyphenateException;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.chat.im.ChatFragment;
import com.xiuxiu.easeim.ChatRowUtils;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiu.utils.ScreenUtils;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.XiuxiuUtils;
import com.xiuxiu.xiuxiutask.XiuxiuTaskBean;

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
        mXiuxiuBSizeTv = (TextView) findViewById(R.id.xiuxiu_bi);
        mAgreeTv = (TextView) findViewById(R.id.agree_bt);
        mRefuseTv = (TextView) findViewById(R.id.refuse_bt);
        mXiuxiuStatusTv = (TextView) findViewById(R.id.xiuxiu_status);
        mBottomReceivedLayout = findViewById(R.id.bottom_received_layout);
        mContentLayout = findViewById(R.id.content_layout);
        mPayTxtTv = (TextView) findViewById(R.id.pay_txt);

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
                mXiuxiuBSizeTv.setText(mXiuxiuTaskBean.xiuxiub + "咻币/分钟");
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
            mXiuxiuStatusTv.setVisibility(View.VISIBLE);

        String status;
        try {
            String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
            status = XiuxiuActionMsgManager.getInstance().getStatusById(askId);
        }catch (Exception e){
            status = null;
        }
        setStatus(status);
        handleTextMessage();
    }

    private void setStatus(String status){
        if(status==null){
            if(message.direct() == EMMessage.Direct.RECEIVE ){ //收到的咻咻ask消息
                if(mBottomReceivedLayout!=null)
                    mBottomReceivedLayout.setVisibility(View.VISIBLE);
                if(mXiuxiuStatusTv!=null)
                    mXiuxiuStatusTv.setVisibility(View.GONE);
                mAgreeTv.setOnClickListener(this);
                mRefuseTv.setOnClickListener(this);
            }else{
                if(mBottomReceivedLayout!=null)
                    mBottomReceivedLayout.setVisibility(View.GONE);
                if(mXiuxiuStatusTv!=null)
                    mXiuxiuStatusTv.setVisibility(View.VISIBLE);
                mXiuxiuStatusTv.setText("未接收");
            }
        }else if(status.equals(EaseConstant.XIUXIU_STATUS_AGREE)){
            mBottomReceivedLayout.setVisibility(View.GONE);
            mXiuxiuStatusTv.setVisibility(View.VISIBLE);
            mXiuxiuStatusTv.setText("已同意");
            return;
        }else if(status.equals(EaseConstant.XIUXIU_STATUS_REFUSE)){
            mBottomReceivedLayout.setVisibility(View.GONE);
            mXiuxiuStatusTv.setVisibility(View.VISIBLE);
            mXiuxiuStatusTv.setText("已拒绝");
            return;
        }
        if(ChatRowUtils.isOverdue(message.getMsgTime(),EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VOICE)){
            mBottomReceivedLayout.setVisibility(View.GONE);
            mXiuxiuStatusTv.setVisibility(View.VISIBLE);
            mXiuxiuStatusTv.setText("已过期");
            return;
        }
    }

    protected void handleTextMessage() {
        if (message.direct() == EMMessage.Direct.SEND) {
            setMessageSendCallback();
            switch (message.status()) {
                case CREATE:
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.VISIBLE);
                    // 发送消息
//                sendMsgInBackground(message);
                    break;
                case SUCCESS: // 发送成功
                    progressBar.setVisibility(View.GONE);
                    statusView.setVisibility(View.GONE);
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
}
