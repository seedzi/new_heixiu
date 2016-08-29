package com.xiuxiuchat.easeim;

import android.content.Context;

import com.xiuxiuchat.XiuxiuApplication;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;

import java.util.List;

/**
 * Created by zhihu on 16-4-17.
 */
public class ImManager {


    private static ImManager mInstance;

    private ImManager(){
    }

    public static ImManager getInstance(){
        if(mInstance == null){
            mInstance = new ImManager();
        }
        return mInstance;
    }

    public void init(Context context){
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 建议初始化sdk的时候设置成每个会话默认load一条消息，节省加载会话的时间，方法为：
//        options.setNumberOfMessagesLoaded(1);
        //初始化
        EMClient.getInstance().init(context, options);
        //在做打包混淆时，关闭debug模式，避免消耗不必要的资源
        EMClient.getInstance().setDebugMode(true);
    }

    public void register(final String username, final String pwd){
       new Thread(new Runnable() {
           @Override
           public void run() {
               registerReal(username,pwd);
           }
       }).start();
    }

    public void registerReal(String username,String pwd){
        //注册失败会抛出HyphenateException
        try {
            EMClient.getInstance().createAccount(username, pwd);//同步方法
        } catch (Exception e){
        }
    }

    public void login(String userName,String password,final Runnable runnable){
        EMClient.getInstance().login(userName,password,new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                android.util.Log.d("ccc","EASE  onSuccess");
                XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        runnable.run();
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
                android.util.Log.d("ccc","EASE  onProgress progress = " + progress + ",status = " + status);
            }

            @Override
            public void onError(int code, String message) {
                android.util.Log.d("ccc","EASE  error code = " + code + ",message = " + message);
            }
        });
    }

    public List<EMMessage> getMessages(String username){
        return null;
        /*
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        //获取此会话的所有消息
        List<EMMessage> messages = conversation.getAllMessages();
        //sdk初始化加载的聊天记录为20条，到顶时需要去db里获取更多
        //获取startMsgId之前的pagesize条消息，此方法获取的messages sdk会自动存入到此会话中，app中无需再次把获取到的messages添加到会话中
        List<EMMessage> messages = conversation.loadMoreMsgFromDB(startMsgId, pagesize);
        */
    }


    // =============================================================================================
    // 发送消息 ：文字，图片，语音，视频
    // =============================================================================================

    public void sendMessage(String content,String toChatUsername){
        //创建一条文本消息,content为消息文字内容，toChatUsername为对方用户或者群聊的id
        EMMessage message = EMMessage.createTxtSendMessage(content, toChatUsername);
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        //发送消息
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    public void sendMessage(String filePath,int length,String toChatUsername){
        //filePath为语音文件路径，length为录音时间(秒)toChatUsername为对方用户或者群聊的id
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length, toChatUsername);
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    public void sendMessage(String videoPath,String thumbPath,int videoLength,String toChatUsername){
        //videoPath为视频本地路径，thumbPath为视频预览图路径，videoLength为视频时间长度
        EMMessage message = EMMessage.createVideoSendMessage(videoPath, thumbPath, videoLength, toChatUsername);
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    public void sendMessage(String imagePath,boolean isOriginal,String toChatUsername){
        //imagePath为图片本地路径，false为不发送原图(默认超过100k的图片会压缩后发给对方),需要发送原图传false
        EMMessage message = EMMessage.createImageSendMessage(imagePath, isOriginal, toChatUsername);
        //如果是群聊，设置chattype,默认是单聊
        message.setChatType(EMMessage.ChatType.GroupChat);
        EMClient.getInstance().chatManager().sendMessage(message);
    }
}
