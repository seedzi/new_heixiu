package com.xiuxiuchat.chat.im;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.easeui.widget.gift.GiftItemClickListener;
import com.hyphenate.easeui.widget.gift.GiftManager;
import com.hyphenate.util.PathUtil;
import com.xiuxiuchat.R;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.easeim.XiuxiuSayHelloManager;
import com.xiuxiuchat.easeim.widget.ChatRowGift;
import com.xiuxiuchat.easeim.widget.ChatRowImgNan2NvXiuxiu;
import com.xiuxiuchat.easeim.widget.ChatRowImgNv2NanXiuxiu;
import com.xiuxiuchat.easeim.widget.ChatRowVideoNan2NvXiuxiu;
import com.xiuxiuchat.easeim.widget.ChatRowVideoNv2NanXiuxiu;
import com.xiuxiuchat.easeim.widget.ChatRowVoiceCall;
import com.xiuxiuchat.easeim.widget.ChatRowVoiceXiuxiu;
import com.xiuxiuchat.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiuchat.qupai.QuPaiManager;
import com.xiuxiuchat.user.WalletActivity;
import com.xiuxiuchat.user.invitation.AddFriendsPage;
import com.xiuxiuchat.utils.ToastUtil;
import com.xiuxiuchat.utils.XiuxiuUtils;
import com.xiuxiuchat.xiuxiutask.XiuxiuQupaiPage;
import com.xiuxiuchat.xiuxiutask.XiuxiuTaskBean;
import com.xiuxiuchat.xiuxiutask.XiuxiuTaskPage;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by huzhi on 16-5-8.
 */
public class ChatFragment extends EaseChatFragment implements EaseChatFragment.EaseChatFragmentListener {

    private static String TAG = "ChatFragment";

    //避免和基类定义的常量可能发生的冲突，常量从11开始定义
    private static final int ITEM_FILE = 12;
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;

    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    protected static final int REQUEST_CODE_XIUXIU = 10001;//xiuxiu request code
    protected static final int REQUEST_CODE_CAMERA = 10002;//男发女咻咻点击同意后进入相机页面
    protected static final int REQUEST_CODE_VIDEO = 10003;//点击同意后进入视频页面

    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;

    //咻咻的图片(男给女发)
    private static final int MESSAGE_XIUXIU_TYPE_IMG_NAN_2_NV_SENT = 5;
    private static final int MESSAGE_XIUXIU_TYPE_IMG_NAN_2_NV_RECV = 6;
    //咻咻的图片(女给男发)
    private static final int MESSAGE_XIUXIU_TYPE_IMG_NV_2_NAN_SENT = 7;
    private static final int MESSAGE_XIUXIU_TYPE_IMG_NV_2_NAN_RECV = 8;
    //咻咻的视频(男给女发)
    private static final int MESSAGE_XIUXIU_TYPE_VIDEO_NAN_2_NV_SENT = 9;
    private static final int MESSAGE_XIUXIU_TYPE_VIDEO_NAN_2_NV_RECV = 10;
    //咻咻的视频(女给男发)
    private static final int MESSAGE_XIUXIU_TYPE_VIDEO_NV_2_NAN_SENT = 11;
    private static final int MESSAGE_XIUXIU_TYPE_VIDEO_NV_2_NAN_RECV = 12;
    //咻咻的语聊天(男给女发)
    private static final int MESSAGE_XIUXIU_TYPE_VOICE_NAN_2_NV_SENT = 13;
    private static final int MESSAGE_XIUXIU_TYPE_VOICE_NAN_2_NV_RECV = 14;
    //咻咻的语聊(女给男发)
    private static final int MESSAGE_XIUXIU_TYPE_VOICE_NV_2_NAN_SENT = 15;
    private static final int MESSAGE_XIUXIU_TYPE_VOICE_NV_2_NAN_RECV = 16;

    //礼物
    private static final int MESSAGE_GIFT_SENT = 17;
    private static final int MESSAGE_GIFT_RECV = 18;

    //拍照相关
    private String mImagePath = "";//huzhi
    //趣拍相关
    private String mVideoFile = "";
    private String mThum = "";
    private String mDuration = "";

    public static ChatFragment mInstance;

    private boolean isWhenCreateEnterXiuxiuTaskPage;

    // huzhi
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isWhenCreateEnterXiuxiuTaskPage = getActivity().getIntent().getBooleanExtra("enterXiuxiuTaskPage",false);
        android.util.Log.d(TAG,"onCreateView()");
        mInstance = this;
        initGift();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mInstance = null;
        android.util.Log.d(TAG, "onDestroyView()");
    }

    ImageView xiuxiuTaskBt;
    private int _xDelta;
    private int _yDelta;


    private int lastPosition ;
    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
        super.setUpView();

        // 咻羞任务入口
        RelativeLayout rootView = (RelativeLayout)inputMenu.getParent();
        xiuxiuTaskBt = new ImageView(getActivity().getApplicationContext());
        xiuxiuTaskBt.setImageResource(R.drawable.xiuxiu_task_bt_selector);
        final RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rl.addRule(RelativeLayout.BELOW, R.id.title_bar);
        rl.setMargins(0, 25, 10, 0);
        rootView.addView(xiuxiuTaskBt, rl);
        xiuxiuTaskBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XiuxiuTaskPage.startActivity4Result(ChatFragment.this, REQUEST_CODE_XIUXIU);
            }
        });

        xiuxiuTaskBt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int Y = (int) event.getRawY();
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) xiuxiuTaskBt
                                .getLayoutParams();
                        _yDelta = Y - lParams.topMargin;
                        lastPosition = Y;
                        break;
                    case MotionEvent.ACTION_UP:
                        if(Math.abs(Y-lastPosition)>5){
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) xiuxiuTaskBt
                                .getLayoutParams();
                        layoutParams.topMargin = Y - _yDelta;
                        if (layoutParams.topMargin < 0) {
                            layoutParams.topMargin = 0;
                        }
                        if (layoutParams.topMargin > 1000) {
                            layoutParams.topMargin = 1000;
                        }
                        xiuxiuTaskBt.setLayoutParams(layoutParams);
                        break;
                }
                xiuxiuTaskBt.requestLayout();
                return false;
            }
        });

        if(isWhenCreateEnterXiuxiuTaskPage){
            xiuxiuTaskBt.performClick();
        }
    }

    /**
     * 咻咻action消息监听者
     */
    private XiuxiuActionMsgManager.Listener listener = new XiuxiuActionMsgManager.Listener() {
        @Override
        public void onReciverActionMsg() {
            if(messageList!=null){
                messageList.refresh();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        XiuxiuActionMsgManager.getInstance().addListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        XiuxiuActionMsgManager.getInstance().removeListener(listener);
    }

    @Override
    protected void registerExtendMenuItem() {
        //增加扩展item
        inputMenu.registerExtendItem(ITEM_VIDEO, extendMenuItemClickListener);
        //demo这里不覆盖基类已经注册的item,item点击listener沿用基类的
        super.registerExtendMenuItem();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_XIUXIU &&resultCode!=Activity.RESULT_OK && isWhenCreateEnterXiuxiuTaskPage ){
            getActivity().finish();
        }
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //发送选中的视频
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("imgPath");
                        File file = new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap = ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath, file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_FILE: //发送选中的文件
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_XIUXIU:
                    dealIntentData(data);
                    break;
                case REQUEST_CODE_CAMERA: //男发女咻咻点击同意后进入相机页面
                    if (cameraFile != null && cameraFile.exists()){
                        mImagePath = cameraFile.getAbsolutePath();
                        if(!TextUtils.isEmpty(mImagePath)){
                            String costXiuxiuB = mCurrentCostXiuxiuB;
                            if(TextUtils.isEmpty(costXiuxiuB)){
                                costXiuxiuB = "20";
                            }
                            XiuxiuTaskBean bean = XiuxiuTaskBean.createXiuxiuTaskBean(false,"咻羞图片","咻羞图片",costXiuxiuB,mImagePath);
                            Intent intent = new Intent();
                            intent.putExtra(XiuxiuTaskBean.TAG, bean);
                            dealIntentData(intent);
                        }
                    }
                    break;
                case REQUEST_CODE_VIDEO://男发女咻咻点击同意后进入趣拍摄影页面(先进入隐形页面　拍摄完成后再回来)
                    dealIntentData(data);
                    break;
                default:
                    break;
            }
        }
    }

    private void dealIntentData(Intent data){
        XiuxiuTaskBean xiuxiuTaskBean = (XiuxiuTaskBean)data.getSerializableExtra(XiuxiuTaskBean.TAG);
        if(xiuxiuTaskBean.isNan2Nv){
            switch (xiuxiuTaskBean.type){
                case XiuxiuTaskBean.TYPE_IMAGE_XIUXIU: //图片 咻咻　(男2女)
                    sendXiuxiuImgNan2NvMessage(xiuxiuTaskBean);
                    break;
                case XiuxiuTaskBean.TYPE_VIDEO_XIUXIU: //视频　咻咻　(男2女)
                    sendXiuxiuVideoNan2NvMessage(xiuxiuTaskBean);
                    break;
                case XiuxiuTaskBean.TYPE_VOICE_XIUXIU: //语聊　咻咻  (男2女)
                    sendXiuxiuVoiceNan2NvMessage(xiuxiuTaskBean);
                    break;
            }
        }else{
            switch (xiuxiuTaskBean.type){
                case XiuxiuTaskBean.TYPE_IMAGE_XIUXIU: //图片 咻咻　(女2男)
                    sendXiuxiuImgNv2NanMessage(xiuxiuTaskBean);
                    break;
                case XiuxiuTaskBean.TYPE_VIDEO_XIUXIU: //视频　咻咻　(女2男)
                    sendXiuxiuVideoNv2NanMessage(xiuxiuTaskBean);
                    break;
                case XiuxiuTaskBean.TYPE_VOICE_XIUXIU: //语聊　咻咻  (女2男)
                    sendXiuxiuVoiceNv2NanMessage(xiuxiuTaskBean);
                    break;
            }
        }
    }


    @Override
    public void onSetMessageAttributes(EMMessage message) {
        message.setAttribute("em_from_nick_name", XiuxiuUserInfoResult.getInstance().getXiuxiu_name()); //huzhi
    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {
        android.util.Log.d(TAG, "onAvatarClick()");
    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        android.util.Log.d(TAG, "onMessageBubbleClick()");
        return false;
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {

    }

    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_VIDEO: //视频
                Intent intent = new Intent(getActivity(), ImageGridActivity.class);
                startActivityForResult(intent, REQUEST_CODE_SELECT_VIDEO);
                break;
            default:
                break;
        }
        //不覆盖已有的点击事件
        return false;
    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        //设置自定义listview item提供者
        return new CustomChatRowProvider();
    }

    @Override
    protected void addFriends(){
        AddFriendsPage.startActivity(getActivity().getApplicationContext(), toChatUsername);//huzhi
    }




    /**
     * chat row provider
     * 咻咻任务六种状态
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //音、视频通话发送、接收共4种 + 咻咻12种 + 礼物2种
            return 4+12+2;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            try {
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, false)){
                    int type = message.getIntAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE);
                    switch (type){
                        case  EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG:            //１.收费图片
                            if( message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV, false))   {
                                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_XIUXIU_TYPE_IMG_NAN_2_NV_RECV :
                                        MESSAGE_XIUXIU_TYPE_IMG_NAN_2_NV_SENT;
                            }else{
                                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_XIUXIU_TYPE_IMG_NV_2_NAN_RECV :
                                        MESSAGE_XIUXIU_TYPE_IMG_NV_2_NAN_SENT;
                            }
                        case  EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VIDEO:            //2.收费视频
                            if( message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV, false))   {
                                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_XIUXIU_TYPE_VIDEO_NAN_2_NV_RECV :
                                        MESSAGE_XIUXIU_TYPE_VIDEO_NAN_2_NV_SENT;
                            }else{
                                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_XIUXIU_TYPE_VIDEO_NV_2_NAN_RECV :
                                        MESSAGE_XIUXIU_TYPE_VIDEO_NV_2_NAN_SENT;
                            }
                        case  EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VOICE:            //3.收费语聊
                            if( message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV, false))   {
                                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_XIUXIU_TYPE_VOICE_NAN_2_NV_RECV :
                                        MESSAGE_XIUXIU_TYPE_VOICE_NAN_2_NV_SENT;
                            }else{
                                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_XIUXIU_TYPE_VOICE_NV_2_NAN_RECV :
                                        MESSAGE_XIUXIU_TYPE_VOICE_NV_2_NAN_SENT;
                            }
                    }
                }
            }catch (Exception e){}

            if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_GIFT, false)){
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_GIFT_RECV :
                        MESSAGE_GIFT_SENT;
            }

            //暂时不要普通的视频以及音频
            if(message.getType() == EMMessage.Type.TXT){
                //语音通话
                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL : MESSAGE_TYPE_SENT_VOICE_CALL;
                }else if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
                    //视频通话
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_CALL : MESSAGE_TYPE_SENT_VIDEO_CALL;
                }
            }
            return 0;
        }

        @Override
        public EaseChatRow getCustomChatRow(EMMessage message, int position, BaseAdapter adapter) {

            try {
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, false)){
                    int type = message.getIntAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE);
                    switch (type){
                        case  EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG:            //１.收费图片
                            if( message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV, false)){
                                return new ChatRowImgNan2NvXiuxiu(getActivity(), message, position, adapter);
                            }else{
                                return new ChatRowImgNv2NanXiuxiu(getActivity(), message, position, adapter);
                            }
                        case  EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VIDEO:            //2.收费视频
                            if( message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV, false)){
                                return new ChatRowVideoNan2NvXiuxiu(getActivity(), message, position, adapter);
                            }else{
                                return new ChatRowVideoNv2NanXiuxiu(getActivity(), message, position, adapter);
                            }
                        case  EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VOICE:            //3.收费语聊
                            return new ChatRowVoiceXiuxiu(getActivity(), message, position, adapter);
                    }
                }
            }catch (Exception e){}

            if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_GIFT, false)){
                return new ChatRowGift(getActivity(), message, position, adapter);
            }

            if(message.getType() == EMMessage.Type.TXT){
                // 语音通话,  视频通话
                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
                    return new ChatRowVoiceCall(getActivity(), message, position, adapter);
                }
            }
            return null;
        }

    }



    //==========================================================================
    //发送咻咻请求消息(图片　男对女发)
    public void sendXiuxiuImgNan2NvMessage(XiuxiuTaskBean xiuxiuTaskBean){
        if(xiuxiuTaskBean==null){
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(XiuxiuTaskBean.createJsonString(xiuxiuTaskBean), toChatUsername);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE,EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID,XiuxiuTaskBean.createXiuxiuMsgId());
        sendMessage(message);
    }
    //发送咻咻请求消息(图片 女对男发)
    public void sendXiuxiuImgNv2NanMessage(XiuxiuTaskBean xiuxiuTaskBean){
        if(xiuxiuTaskBean==null){
            return;
        }
        EMMessage message = EMMessage.createImageSendMessage(xiuxiuTaskBean.imgPath, false, toChatUsername);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV,false);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE,EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID, XiuxiuTaskBean.createXiuxiuMsgId());
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TXT_CONTENT, xiuxiuTaskBean.content);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_B_SIZE,xiuxiuTaskBean.xiuxiub);
        sendMessage(message);
    }

    //==========================================================================
    //发送咻咻请求消息(语聊　男对女发)
    public void sendXiuxiuVoiceNan2NvMessage(XiuxiuTaskBean xiuxiuTaskBean){
        if(xiuxiuTaskBean==null){
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(XiuxiuTaskBean.createJsonString(xiuxiuTaskBean), toChatUsername);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE,EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VOICE);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID,XiuxiuTaskBean.createXiuxiuMsgId());
        sendMessage(message);
    }
    //发送咻咻请求消息(语聊 女对男发)
    public void sendXiuxiuVoiceNv2NanMessage(XiuxiuTaskBean xiuxiuTaskBean){
        if(xiuxiuTaskBean==null){
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(XiuxiuTaskBean.createJsonString(xiuxiuTaskBean), toChatUsername);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV,false);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE,EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VOICE);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID, XiuxiuTaskBean.createXiuxiuMsgId());
        sendMessage(message);
    }

    //==========================================================================
    //发送咻咻请求消息(视频　男对女发)
    public void sendXiuxiuVideoNan2NvMessage(XiuxiuTaskBean xiuxiuTaskBean){
        if(xiuxiuTaskBean==null){
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(XiuxiuTaskBean.createJsonString(xiuxiuTaskBean), toChatUsername);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE,EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VIDEO);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID,XiuxiuTaskBean.createXiuxiuMsgId());
        sendMessage(message);
    }
    //发送咻咻请求消息(视频 女对男发)
    public void sendXiuxiuVideoNv2NanMessage(XiuxiuTaskBean xiuxiuTaskBean){
        if(xiuxiuTaskBean==null){
            return;
        }
        EMMessage message = EMMessage.createVideoSendMessage(xiuxiuTaskBean.videoPath,
                xiuxiuTaskBean.thumbPath, Integer.valueOf(xiuxiuTaskBean.videoLength), toChatUsername);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV,false);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE,EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VIDEO);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID, XiuxiuTaskBean.createXiuxiuMsgId());
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TXT_CONTENT, xiuxiuTaskBean.content);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_B_SIZE,xiuxiuTaskBean.xiuxiub);
        sendMessage(message);
    }


    //==========================================================================
    //咻咻付费消息的同意回执
    public static void sendAgreeXiuxiuMessageAgree(String toUsername,String askId){
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action = EaseConstant.MESSAGE_ATTR_XIUXIU_ACTION;//action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        cmdMsg.setReceipt(toUsername);
        cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_STATUS, EaseConstant.XIUXIU_STATUS_AGREE);
        cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID, askId);
        cmdMsg.addBody(cmdBody);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }
    //咻咻付费消息的同意回执
    public static void sendAgreeXiuxiuMessageAgree4VoiceCall(String toUsername,String askId,String xiuxiub){
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action = EaseConstant.MESSAGE_ATTR_XIUXIU_ACTION;//action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        cmdMsg.setReceipt(toUsername);
        cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_STATUS, EaseConstant.XIUXIU_STATUS_AGREE);
        cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_ACTION_CALL_VOICE, true);
        cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_ACTION_CALL_VOICE_COST_XIUXIUB, xiuxiub);
        cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID, askId);
        cmdMsg.addBody(cmdBody);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }

    //咻咻付费消息的拒绝回执
    public static void sendAgreeXiuxiuMessageRefuse(String toUsername,String askId){
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action=EaseConstant.MESSAGE_ATTR_XIUXIU_ACTION;//action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        cmdMsg.setReceipt(toUsername);
        cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_STATUS, EaseConstant.XIUXIU_STATUS_REFUSE);
        cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID, askId);
        cmdMsg.addBody(cmdBody);
        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
    }
    // =============================================================================================
    // 发送礼物消息
    // =============================================================================================
    public void sendGiftMessage(int type){
        EMMessage message = EMMessage.createTxtSendMessage("gift", toChatUsername);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_GIFT,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_GIFT_TYPE,type);
        sendMessage(message);
    }

    // =============================================================================================
    // 处理同意后的回调
    // =============================================================================================
    /**
     * 用户点击同意后记录的咻咻B
     */
    private String mCurrentCostXiuxiuB;


    public void dealXiuxiuTask4Agree(int type,String costXiuxiuB){
        if(type == XiuxiuTaskBean.TYPE_IMAGE_XIUXIU){
            selectPicFromCamera(costXiuxiuB);
        }else{
            selectVideoFromQuPai(costXiuxiuB);
        }
    }
    /**
     * 照相获取图片
     */
    public void selectPicFromCamera(String costXiuxiuB) {
        if (!EaseCommonUtils.isExitsSdcard()) {
            Toast.makeText(getActivity(), com.hyphenate.easeui.R.string.sd_card_does_not_exist,0).show();
            return;
        }
        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
        mCurrentCostXiuxiuB = costXiuxiuB;
    }
    /**
     * 趣拍获取视频
     */
    public void selectVideoFromQuPai(String costXiuxiuB){
//        QuPaiManager.getInstance().showRecordPage(ChatFragment.this,REQUEST_CODE_VIDEO);
//        mCurrentCostXiuxiuB = costXiuxiuB;
        if(!QuPaiManager.getInstance().isInit()){
            ToastUtil.showMessage(getActivity(), "初始化失败,如果使用此功能请您在网络流畅情况下退出app重新进入");
            return;
        }
        XiuxiuQupaiPage.startActivity4Result(ChatFragment.this,REQUEST_CODE_VIDEO,costXiuxiuB);
    }


    // =============================================================================================
    // 处理礼物点击事件
    // =============================================================================================
    private void initGift(){
        GiftManager.getInstance().setListener(new GiftItemClickListener() {
            @Override
            public void onItemClick(final int poistion) {
                XiuxiuUtils.showProgressDialog(getActivity(), "加载中...");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        final boolean isSuccess = XiuxiuUtils.sendGift(String.valueOf(poistion),toChatUsername);
                        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                if(isSuccess){
                                    sendGiftMessage(poistion);
                                }else{
                                    ToastUtil.showMessage(getActivity(),"余额不足");
                                }
                                XiuxiuUtils.dismisslProgressDialog();
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onWalletClick() {
                WalletActivity.startActivity(getActivity());
            }
        });
    }

    // =============================================================================================
    // 对父类的发送消息进行拦截处理　(招呼扣费)
    // =============================================================================================
    @Override
    protected void sendTextMessage(final String content) {
        XiuxiuSayHelloManager.getInstance().sayHello(toChatUsername, new XiuxiuSayHelloManager.Callback() {
            @Override
            public void onSuccess() {
                EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
                sendMessage(message);
            }
        }, getActivity());
    }
    @Override
    protected void sendBigExpressionMessage(final String name, final String identityCode){
        XiuxiuSayHelloManager.getInstance().sayHello(toChatUsername, new XiuxiuSayHelloManager.Callback() {
            @Override
            public void onSuccess() {
                EMMessage message = EaseCommonUtils.createExpressionMessage(toChatUsername, name, identityCode);
                sendMessage(message);
            }
        }, getActivity());

    }
    @Override
    protected void sendVoiceMessage(final String filePath, final int length) {
        XiuxiuSayHelloManager.getInstance().sayHello(toChatUsername, new XiuxiuSayHelloManager.Callback() {
            @Override
            public void onSuccess() {
                EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
                sendMessage(message);
            }
        }, getActivity());
    }
    @Override
    protected void sendImageMessage(final String imagePath) {
        XiuxiuSayHelloManager.getInstance().sayHello(toChatUsername, new XiuxiuSayHelloManager.Callback() {
            @Override
            public void onSuccess() {
                EMMessage message = EMMessage.createImageSendMessage(imagePath, false, toChatUsername);
                sendMessage(message);
            }
        }, getActivity());
    }
    @Override
    protected void sendLocationMessage(final double latitude, final double longitude, final String locationAddress) {
        XiuxiuSayHelloManager.getInstance().sayHello(toChatUsername, new XiuxiuSayHelloManager.Callback() {
            @Override
            public void onSuccess() {
                EMMessage message = EMMessage.createLocationSendMessage(latitude, longitude, locationAddress, toChatUsername);
                sendMessage(message);
            }
        }, getActivity());
    }
    @Override
    protected void sendVideoMessage(final String videoPath,final String thumbPath,final int videoLength) {
        XiuxiuSayHelloManager.getInstance().sayHello(toChatUsername, new XiuxiuSayHelloManager.Callback() {
            @Override
            public void onSuccess() {
                EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
                sendMessage(message);
            }
        }, getActivity());
    }
    @Override
    protected void sendFileMessage(final String filePath) {
        XiuxiuSayHelloManager.getInstance().sayHello(toChatUsername, new XiuxiuSayHelloManager.Callback() {
            @Override
            public void onSuccess() {
                EMMessage message = EMMessage.createFileSendMessage(filePath, toChatUsername);
                sendMessage(message);
            }
        }, getActivity());
    }
}