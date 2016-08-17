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
 * Created by huzhi on 16-7-15.
 */
public class ChatRowImgNan2NvXiuxiu extends EaseChatRow implements View.OnClickListener{

    private TextView mTitleTv;

    private TextView mContentTv;

    private TextView mXiuxiuBSizeTv;

    private TextView mAgreeTv;

    private TextView mRefuseTv;

    private TextView mXiuxiuStatusTv;

    private View mBottomReceivedLayout;

    private View mContentLayout;

    protected XiuxiuTaskBean mXiuxiuTaskBean;

    protected int mXiuxiuTaskType = XiuxiuTaskBean.TYPE_IMAGE_XIUXIU;

    public ChatRowImgNan2NvXiuxiu(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflatView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_img_nan_2_nv_received_xiuxiu : R.layout.ease_row_img_nan_2_nv_sent_xiuxiu, this);
    }

    @Override
    protected void onFindViewById() {
        mTitleTv = (TextView) findViewById(R.id.xiuxiu_ask_title);
        mContentTv = (TextView) findViewById(R.id.xiuxiu_ask_content);
        mXiuxiuBSizeTv = (TextView) findViewById(R.id.xiuxiu_bi_size);
        mAgreeTv = (TextView) findViewById(R.id.agree_bt);
        mRefuseTv = (TextView) findViewById(R.id.refuse_bt);
        mXiuxiuStatusTv = (TextView) findViewById(R.id.xiuxiu_status);
        mBottomReceivedLayout = findViewById(R.id.bottom_received_layout);
        mContentLayout = findViewById(R.id.content_layout);

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
            if(mTitleTv!=null)
                mTitleTv.setText(mXiuxiuTaskBean.title);
            if(mContentTv!=null)
                mContentTv.setText(mXiuxiuTaskBean.content);
            if(mXiuxiuBSizeTv!=null)
                mXiuxiuBSizeTv.setText(mXiuxiuTaskBean.xiuxiub + "");
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

    protected void setStatus(String status){
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
        if(ChatRowUtils.isOverdue(message.getMsgTime(), EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG)){
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
                if(ChatFragment.mInstance!=null){
                    ChatFragment.mInstance.dealXiuxiuTask4Agree(mXiuxiuTaskBean.type,mXiuxiuTaskBean.xiuxiub);
                }
                try {
                    String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
                    ChatFragment.sendAgreeXiuxiuMessageAgree(message.getFrom(), askId);
                    XiuxiuActionMsgManager.getInstance().add(askId, EaseConstant.XIUXIU_STATUS_AGREE);
                } catch (Exception e) {
                }
                setStatus(EaseConstant.XIUXIU_STATUS_AGREE);
                /*
                XiuxiuUtils.transferCoinAsyn(message.getFrom(), mXiuxiuTaskBean.xiuxiub, new XiuxiuUtils.CallBack() {
                    @Override
                    public void onPre() {
                        XiuxiuUtils.showProgressDialog(getContext());
                    }

                    @Override
                    public void onSccusee() {
                        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                XiuxiuUtils.dismisslProgressDialog();
                                ToastUtil.showMessage(getContext(), "支付成功!");
                                try {
                                    String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
                                    ChatFragment.sendAgreeXiuxiuMessageAgree(message.getFrom(), askId);
                                    XiuxiuActionMsgManager.getInstance().add(askId, EaseConstant.XIUXIU_STATUS_AGREE);
                                } catch (Exception e) {
                                }
                                setStatus(EaseConstant.XIUXIU_STATUS_AGREE);
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        ToastUtil.showMessage(getContext(), "支付失败!");
                        XiuxiuUtils.dismisslProgressDialog();
                    }
                });*/

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
