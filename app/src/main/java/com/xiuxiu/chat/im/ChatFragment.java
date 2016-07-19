package com.xiuxiu.chat.im;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.ui.EaseChatFragment;
import com.hyphenate.easeui.widget.chatrow.EaseChatRow;
import com.hyphenate.easeui.widget.chatrow.EaseChatRowImage;
import com.hyphenate.easeui.widget.chatrow.EaseCustomChatRowProvider;
import com.hyphenate.util.PathUtil;
import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.easeim.widget.ChatRowAskXiuxiu;
import com.xiuxiu.easeim.widget.ChatRowImgXiuxiu;
import com.xiuxiu.easeim.widget.ChatRowVoiceCall;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiu.user.invitation.AddFriendsPage;
import com.xiuxiu.xiuxiutask.XiuxiuTaskBean;
import com.xiuxiu.xiuxiutask.XiuxiuTaskPage;

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
    protected static final int REQUEST_CODE_XIUXIU = 15;//ask xiuxiu request code

    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;

    //咻咻的图片
    private static final int MESSAGE_TYPE_SENT_IMAGE_XIUXIU = 5;
    private static final int MESSAGE_TYPE_RECV_IMAGE_XIUXIU = 6;
    //收费的语音
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL_XIUXIU = 7;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL_XIUXIU = 8;
    //收费的视频
    private static final int MESSAGE_TYPE_SENT_VIDEO_XIUXIU = 9;
    private static final int MESSAGE_TYPE_RECV_VIDEO_XIUXIU = 10;
    //咻咻请求消息
    private static final int MESSAGE_TYPE_SENT_ASK_XIUXIU = 11;
    private static final int MESSAGE_TYPE_RECV_ASK_XIUXIU = 12;

    // huzhi
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void setUpView() {
        setChatFragmentListener(this);
        super.setUpView();

        // 咻羞任务入口
        RelativeLayout rootView = (RelativeLayout)inputMenu.getParent();
        ImageView xiuxiuTaskBt = new ImageView(getContext());
        xiuxiuTaskBt.setImageResource(R.drawable.xiuxiu_task_bt_selector);
        RelativeLayout.LayoutParams rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        rl.addRule(RelativeLayout.BELOW, R.id.title_bar);
        rl.setMargins(0, 36, 10, 0);
        rootView.addView(xiuxiuTaskBt, rl);
        xiuxiuTaskBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XiuxiuTaskPage.startActivity4Result(ChatFragment.this, REQUEST_CODE_XIUXIU);
            }
        });
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
        if(resultCode == Activity.RESULT_OK){
            switch (requestCode) {
                case REQUEST_CODE_SELECT_VIDEO: //发送选中的视频
                    if (data != null) {
                        int duration = data.getIntExtra("dur", 0);
                        String videoPath = data.getStringExtra("path");
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
                    int xiuxiuType = data.getIntExtra(XiuxiuTaskBean.TYPE_KEY,0);
                    switch (xiuxiuType){
                        case XiuxiuTaskBean.TYPE_ASK_XIUXIU: //ask 咻咻
                            sendAskXiuxiuMessage(data.getStringExtra(XiuxiuTaskBean.TITLE_KEY),
                                    data.getStringExtra(XiuxiuTaskBean.CONTENT_KEY),
                                    data.getStringExtra(XiuxiuTaskBean.XIUXIUB_KEY));
                            break;
                        case XiuxiuTaskBean.TYPE_IMAGE_XIUXIU:
                            sendImgXiuxiuMessage(data);
                            break;
                    }
                default:
                    break;
            }
        }
    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        android.util.Log.d("12345","onSetMessageAttributes()");
        message.setAttribute("em_from_nick_name", XiuxiuUserInfoResult.getInstance().getXiuxiu_name()); //huzhi
    }

    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String username) {
        android.util.Log.d(TAG,"onAvatarClick()");
    }

    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        android.util.Log.d(TAG,"onMessageBubbleClick()");
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
        AddFriendsPage.startActivity(getContext(), toChatUsername);//huzhi
    }




    /**
     * chat row provider
     * 咻咻任务六种状态
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //音、视频通话发送、接收共4种 + 咻咻8种
            return 12;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            //１.收费图片
            if(message.getType() == EMMessage.Type.IMAGE){
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, false)){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE_XIUXIU : MESSAGE_TYPE_SENT_IMAGE_XIUXIU;
                }
            }
            //2.收费视频
            if(message.getType() == EMMessage.Type.VIDEO){
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, false)){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO_XIUXIU : MESSAGE_TYPE_SENT_VIDEO_XIUXIU;
                }
            }
            //3.收费语聊
            if(message.getType() == EMMessage.Type.TXT){
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)&&
                        message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, false)){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE_CALL_XIUXIU : MESSAGE_TYPE_SENT_VOICE_CALL_XIUXIU;
                }
            }

            //4.咻咻要求消息
            if(message.getType() == EMMessage.Type.TXT){
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_ASK_XIUXIU, false)){
                    return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_ASK_XIUXIU : MESSAGE_TYPE_SENT_ASK_XIUXIU;
                }
            }

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

            //１.收费图片
            if(message.getType() == EMMessage.Type.IMAGE){
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, false)){
                    return new ChatRowImgXiuxiu(getActivity(), message, position, adapter);
                }
            }
            //2.收费视频
            if(message.getType() == EMMessage.Type.VIDEO){
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, false)){
                    return new ChatRowImgXiuxiu(getActivity(), message, position, adapter);
                }
            }
            //3.收费语聊
            if(message.getType() == EMMessage.Type.TXT){
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)&&
                        message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, false)){
                    return new EaseChatRowImage(getActivity(), message, position, adapter);
                }
            }

            //4.咻咻请求消息(男给女发)
            if(message.getType() == EMMessage.Type.TXT){
                if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU, false)&&
                message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_ASK_XIUXIU, false)){
                    return new ChatRowAskXiuxiu(getActivity(), message, position, adapter);
                }
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


    //发送咻咻请求消息(男对女发)
    //==========================================================================
    public void sendAskXiuxiuMessage(String title,String content,String xiuxiuB){
        XiuxiuTaskBean xiuxiuTaskBean = XiuxiuTaskBean.createBean(title,content,xiuxiuB);
        EMMessage message = EMMessage.createTxtSendMessage(XiuxiuTaskBean.createJsonString(xiuxiuTaskBean),
                toChatUsername);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_ASK_XIUXIU,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID,XiuxiuTaskBean.createXiuxiuMsgId());
        sendMessage(message);
    }
    //咻咻付费消息的同意回执
    public static void sendAgreeXiuxiuMessageAgree(String toUsername,String askId){
        EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);
        String action=EaseConstant.MESSAGE_ATTR_XIUXIU_ACTION;//action可以自定义
        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
        cmdMsg.setReceipt(toUsername);
        cmdMsg.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_STATUS, EaseConstant.XIUXIU_STATUS_AGREE);
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

    //发送咻咻请求消息(图片 女对男发)
    public void sendImgXiuxiuMessage(Intent data){
        android.util.Log.d("12345","sendImgXiuxiuMessage()");
        XiuxiuTaskBean xiuxiuTaskBean = XiuxiuTaskBean.createBean(data);
        EMMessage message = EMMessage.createImageSendMessage(xiuxiuTaskBean.path, false, toChatUsername);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_B_SIZE,xiuxiuTaskBean.xiuxiub);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU,true);
        message.setAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID, XiuxiuTaskBean.createXiuxiuMsgId());
        sendMessage(message);
    }
}