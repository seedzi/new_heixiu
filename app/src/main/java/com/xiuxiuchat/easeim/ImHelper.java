package com.xiuxiuchat.easeim;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMError;
import com.hyphenate.EMMessageListener;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.controller.EaseUI;
import com.hyphenate.easeui.domain.EaseEmojicon;
import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseNotifier;
import com.hyphenate.easeui.model.EmojiconXiuxiuGroupData;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.EMLog;
import com.xiuxiuchat.Xiuxiubroadcast.XiuxiuBroadcastManager;
import com.xiuxiuchat.api.XiuxiuApi;
import com.xiuxiuchat.api.XiuxiuUserInfoResult;
import com.xiuxiuchat.call.voice.VoiceCallActivity;
import com.xiuxiuchat.chat.ChatPage;
import com.xiuxiuchat.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiuchat.easeim.xiuxiumsg.XiuxiuSettingsConstant;
import com.xiuxiuchat.main.MainActivity;
import com.xiuxiuchat.user.invitation.ImModel;
import com.xiuxiuchat.user.invitation.InviteMessage;
import com.xiuxiuchat.user.invitation.InviteMessage.InviteMesageStatus;
import com.xiuxiuchat.user.invitation.InviteMessgeDao;
import com.xiuxiuchat.user.invitation.UserDao;
import com.xiuxiuchat.utils.XiuxiuUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class ImHelper {

    /**
     * 数据同步listener
     */
    static public interface DataSyncListener {
        /**
         * 同步完毕
         * @param success true：成功同步到数据，false失败
         */
        public void onSyncComplete(boolean success);
    }

    protected static final String TAG = "ImHelper";

    private static ImHelper instance;

    public synchronized static ImHelper getInstance() {
        if (instance == null) {
            instance = new ImHelper();
        }
        return instance;
    }

    private Context appContext;

    private EaseUI easeUI;

    private ImModel mImModel = null;

    private UserProfileManager userProManager = null;

    /**
     * 当前好友列表
     */
    private Map<String, EaseUser> contactList;

    /**
     * EMEventListener
     */
    protected EMMessageListener messageListener = null;

    private EMConnectionListener connectionListener;

    /**
     * 是否登录成功过
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
    }

    private boolean isGroupAndContactListenerRegisted;

    private boolean isContactsSyncedWithServer;

    private boolean isSyncingContactsWithServer;

    private InviteMessgeDao inviteMessgeDao;

    private LocalBroadcastManager broadcastManager;

    private ImModel imModel;

    private UserDao userDao;

    public boolean isVoiceCalling;
    public boolean isVideoCalling;


    /**
     * HuanXin sync contacts status listener
     */
    private List<DataSyncListener> syncContactsListeners;

    /**
     * 退出登录
     *
     * @param unbindDeviceToken
     *            是否解绑设备token(使用GCM才有)
     * @param callback
     *            callback
     */
    public void logout(boolean unbindDeviceToken, final EMCallBack callback) {
        endCall();
        Log.d(TAG, "logout: " + unbindDeviceToken);
        EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onProgress(int progress, String status) {
                if (callback != null) {
                    callback.onProgress(progress, status);
                }
            }

            @Override
            public void onError(int code, String error) {
                Log.d(TAG, "logout: onSuccess");
                reset();
                if (callback != null) {
                    callback.onError(code, error);
                }
            }
        });
    }

    /**
     * 全局事件监听
     * 因为可能会有UI页面先处理到这个消息，所以一般如果UI页面已经处理，这里就不需要再次处理
     * activityList.size() <= 0 意味着所有页面都已经在后台运行，或者已经离开Activity Stack
     */
    protected void registerEventListener() {
        messageListener = new EMMessageListener() {
            private BroadcastReceiver broadCastReceiver = null;

            @Override
            public void onMessageReceived(List<EMMessage> messages) {
                android.util.Log.d(TAG,"onMessageReceived");
                for (final EMMessage message : messages) {
                    EMLog.d(TAG, "onMessageReceived id : " + message.getMsgId());
                    //应用在后台，不需要刷新UI,通知栏提示新消息
                    if(!EaseUI.getInstance().hasForegroundActivies()){
                        getNotifier().onNewMsg(message);
                    }
                    if(EaseUserCacheManager.getInstance().getBeanById(message.getUserName())==null){
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                XiuxiuApi.queryUserInfoSyn(message.getUserName());
                            }
                        }).start();
                    }
                }
            }

            @Override
            public void onCmdMessageReceived(List<EMMessage> messages) {
                try {
                    for (EMMessage message : messages) {
                        //获取消息body
                        EMCmdMessageBody cmdMsgBody = (EMCmdMessageBody) message.getBody();
                        final String action = cmdMsgBody.action();//获取自定义action
                        if (EaseConstant.MESSAGE_ATTR_XIUXIU_ACTION.equals(action)) {
                            XiuxiuActionMsgManager.getInstance().add(message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID),
                                    message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_STATUS));
                            XiuxiuActionMsgManager.getInstance().notifyListener();
                            if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_ACTION_CALL_VOICE)){
                                if (!EMClient.getInstance().isConnected()) {
                                } else { //拨打电话
                                    MainActivity.getInstance().startActivity(new Intent(MainActivity.getInstance(),
                                            VoiceCallActivity.class).putExtra("username", message.getFrom())
                                            .putExtra("isComingCall", false)
                                            .putExtra("xiuxiub", message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_ACTION_CALL_VOICE_COST_XIUXIUB)));
                                }
                            }
                        }else if (EaseConstant.MESSAGE_ATTR_XIUXIU_BROADCAST_ACTION.equals(action)){//咻咻广播
                            XiuxiuBroadcastManager.getInstance().saveXiuxiuBroadcastMsg(message);
                            //应用在后台，不需要刷新UI,通知栏提示新消息
                            if(!EaseUI.getInstance().hasForegroundActivies()){
                                getNotifier().onNewMsg(message);
                            }
                        }
                    }
                }catch (Exception e){
                }
            }

            @Override
            public void onMessageReadAckReceived(List<EMMessage> messages) {
            }

            @Override
            public void onMessageDeliveryAckReceived(List<EMMessage> message) {
            }

            @Override
            public void onMessageChanged(EMMessage message, Object change) {

            }
        };

        EMClient.getInstance().chatManager().addMessageListener(messageListener);
    }

    /**
     * 获取消息通知类
     * @return
     */
    public EaseNotifier getNotifier(){
        return easeUI.getNotifier();
    }

    void endCall() {
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    synchronized void reset(){
        /*
        isSyncingGroupsWithServer = false;
        isSyncingContactsWithServer = false;
        isSyncingBlackListWithServer = false;

        demoModel.setGroupsSynced(false);
        demoModel.setContactSynced(false);
        demoModel.setBlacklistSynced(false);

        isGroupsSyncedWithServer = false;
        isContactsSyncedWithServer = false;
        isBlackListSyncedWithServer = false;

        alreadyNotified = false;
        isGroupAndContactListenerRegisted = false;

        setContactList(null);
        setRobotList(null);
        getUserProfileManager().reset();
        DemoDBManager.getInstance().closeDB();
        */
        isSyncingContactsWithServer = false;
        isContactsSyncedWithServer = false;
        isGroupAndContactListenerRegisted = false;
    }

    /**
     * init helper
     *
     * @param context
     *            application context
     */
    public void init(Context context) {
        /*
        demoModel = new DemoModel(context);
        EMOptions options = initChatOptions();
        //options传null则使用默认的
        if (EaseUI.getInstance().init(context, options)) {
            appContext = context;

            //设为调试模式，打成正式包时，最好设为false，以免消耗额外的资源
            EMClient.getInstance().setDebugMode(true);
            //get easeui instance
            easeUI = EaseUI.getInstance();
            //调用easeui的api设置providers
            setEaseUIProviders();
            //初始化PreferenceManager
            PreferenceManager.init(context);
            //初始化用户管理类
            getUserProfileManager().init(context);

            //设置全局监听
            setGlobalListeners();
            broadcastManager = LocalBroadcastManager.getInstance(appContext);
            initDbDao();
        }
        */
        imModel = new ImModel(context);
        if (EaseUI.getInstance().init(context, null)) {
            android.util.Log.d(TAG,"init()");
            appContext = context;

            //get easeui instance
            easeUI = EaseUI.getInstance();
            //调用easeui的api设置providers
            setEaseUIProviders();
            //初始化用户管理类
            getUserProfileManager().init(context);
            //设置全局监听
            setGlobalListeners();
            broadcastManager = LocalBroadcastManager.getInstance(appContext);
            initDbDao();
        }
    }


    private EMOptions initChatOptions(){
        Log.d(TAG, "init HuanXin Options");

        // 获取到EMChatOptions对象
        EMOptions options = new EMOptions();
        // 默认添加好友时，是不需要验证的，改成需要验证
        options.setAcceptInvitationAlways(false);
        // 设置是否需要已读回执
        options.setRequireAck(true);
        // 设置是否需要已送达回执
        options.setRequireDeliveryAck(false);

        //使用gcm和mipush时，把里面的参数替换成自己app申请的
        //设置google推送，需要的GCM的app可以设置此参数
        options.setGCMNumber("324169311137");
        //在小米手机上当app被kill时使用小米推送进行消息提示，同GCM一样不是必须的
        options.setMipushConfig("2882303761517426801", "5381742660801");
        //集成华为推送时需要设置
//        options.setHuaweiPushAppId("10492024");

        /* //暂时关闭 huzhi
        options.allowChatroomOwnerLeave(getModel().isChatroomOwnerLeaveAllowed());
        options.setDeleteMessagesAsExitGroup(getModel().isDeleteMessagesAsExitGroup());
        options.setAutoAcceptGroupInvitation(getModel().isAutoAcceptGroupInvitation());
        */
        return options;
    }

    protected void setEaseUIProviders() {
        //需要easeui库显示用户头像和昵称设置此provider
        easeUI.setUserProfileProvider(new EaseUI.EaseUserProfileProvider() {

            @Override
            public EaseUser getUser(String username) {
                return getUserInfo(username);
            }
        });

        //不设置，则使用easeui默认的
        easeUI.setSettingsProvider(new EaseUI.EaseSettingsProvider() {
            @Override
            public boolean isSpeakerOpened() {
                return true;
            }
            @Override
            public boolean isMsgVibrateAllowed(EMMessage message) {
                return XiuxiuSettingsConstant.isVibrationOn();
            }
            @Override
            public boolean isMsgSoundAllowed(EMMessage message) {
                return XiuxiuSettingsConstant.isVoicOn();
            }
            @Override
            public boolean isMsgNotifyAllowed(EMMessage message) {
                try{
                    if(!XiuxiuSettingsConstant.isNewMessagePromptOn()){
                        return false;
                    }
                    if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU)&&!XiuxiuSettingsConstant.isXiuxiuPromptOn()){
                        return false;
                    }
                    if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_BROADCAST)&&!XiuxiuSettingsConstant.isXiuxiuPromptOn()){
                        return false;
                    }
                }catch (Exception e){
                }
                return true;
            }
        });

        //设置表情provider
        easeUI.setEmojiconInfoProvider(new EaseUI.EaseEmojiconInfoProvider() {

            @Override
            public EaseEmojicon getEmojiconInfo(String emojiconIdentityCode) {
                EaseEmojiconGroupEntity data = EmojiconXiuxiuGroupData.getData();
                for (EaseEmojicon emojicon : data.getEmojiconList()) {
                    if (emojicon.getIdentityCode().equals(emojiconIdentityCode)) {
                        return emojicon;
                    }
                }
                return null;
            }

            @Override
            public Map<String, Object> getTextEmojiconMapping() {
                //返回文字表情emoji文本和图片(resource id或者本地路径)的映射map
                return null;
            }
        });

        //不设置，则使用easeui默认的
        easeUI.getNotifier().setNotificationInfoProvider(new EaseNotifier.EaseNotificationInfoProvider() {

            @Override
            public String getTitle(EMMessage message) {
                //修改标题,这里使用默认
                EaseUser user = getUserInfo(message.getFrom());
                if(user != null){
                    return getUserInfo(message.getFrom()).getNick() + " ";
                }else{
                    return message.getFrom() + " ";
                }
            }

            @Override
            public int getSmallIcon(EMMessage message) {
                //设置小图标，这里为默认
                return 0;
            }

            @Override
            public String getDisplayedText(EMMessage message) {
                // 设置状态栏的消息提示，可以根据message的类型做相应提示
                String ticker = EaseCommonUtils.getMessageDigest(message, appContext);
                if(message.getType() == EMMessage.Type.TXT){
                    ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
                }

                String txt = XiuxiuUtils.getXiuxiuNotifMsgDisplay(message);
                if(!TextUtils.isEmpty(txt)){
                    ticker = txt;
                }

                EaseUser user = getUserInfo(message.getFrom());
                if(user != null){
                    return getUserInfo(message.getFrom()).getNick() + ": " + ticker;
                }else{
                    return message.getFrom() + ": " + ticker;
                }
            }

            @Override
            public String getLatestText(EMMessage message, int fromUsersNum, int messageNum) {
                 return fromUsersNum + "个咻咻用户，发来了" + messageNum + "条消息";
            }

            @Override
            public Intent getLaunchIntent(EMMessage message) {
                //有电话时优先跳转到通话页面
                /*
                if(isVideoCalling){
                    intent = new Intent(appContext, VideoCallActivity.class);
                }else if(isVoiceCalling){
                    intent = new Intent(appContext, VoiceCallActivity.class);
                }else{
                    EMMessage.ChatType chatType = message.getChatType();
                    if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                    } else { // 群聊信息
                        // message.getTo()为群聊id
                        intent.putExtra("userId", message.getTo());
                        if(chatType == EMMessage.ChatType.GroupChat){
                            intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                        }else{
                            intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                        }

                    }
                }*/
                EaseUser user = getUserInfo(message.getFrom());
                String username = message.getFrom();
                if(user!=null){
                    username = user.getNick();
                }
                //设置点击通知栏跳转事件
                Intent intent = new Intent(appContext, ChatPage.class);
                //有电话时优先跳转到通话页面
                if(isVideoCalling){
//                    intent = new Intent(appContext, VideoCallActivity.class); //added by huzhi 暂时不要
                    intent = new Intent(appContext, VoiceCallActivity.class);
                }else {
                    EMMessage.ChatType chatType = message.getChatType();
                    if (chatType == EMMessage.ChatType.Chat) { // 单聊信息
                        intent.putExtra("userId", message.getFrom());
                        intent.putExtra("userName", username);
                        intent.putExtra("chatType", Constant.CHATTYPE_SINGLE);
                    } else { // 群聊信息
                        // message.getTo()为群聊id
                        intent.putExtra("userId", message.getTo());
                        intent.putExtra("userName", username);
                        if (chatType == EMMessage.ChatType.GroupChat) {
                            intent.putExtra("chatType", Constant.CHATTYPE_GROUP);
                        } else {
                            intent.putExtra("chatType", Constant.CHATTYPE_CHATROOM);
                        }

                    }
                }
                return intent;
            }
        });

    }

    private EaseUser getUserInfo(final String username){
        //获取user信息，demo是从内存的好友列表里获取，
        //实际开发中，可能还需要从服务器获取用户信息,
        //从服务器获取的数据，最好缓存起来，避免频繁的网络请求
        EaseUser user = null;
        if(username.equals(EMClient.getInstance().getCurrentUser())) {
            return XiuxiuUserInfoResult.toEaseUser(XiuxiuUserInfoResult.getInstance());
            /*getUserProfileManager().getCurrentUserInfo();*/
        }
        user = getContactList().get(username);
        if(user == null || TextUtils.isEmpty(user.getAvatar())){
            XiuxiuUserInfoResult xiuxiuUserInfoResult = EaseUserCacheManager.getInstance().getBeanById(username);
            if(xiuxiuUserInfoResult!=null) {
                user = XiuxiuUserInfoResult.toEaseUser(xiuxiuUserInfoResult);
            }else{
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XiuxiuApi.queryUserInfoSyn(username);
                    }
                }).start();
            }
        }
        return user;
    }


    private void initDbDao() {
        inviteMessgeDao = new InviteMessgeDao(appContext);
        userDao = new UserDao(appContext);
    }

    /**
     * 设置全局事件监听
     */
    protected void setGlobalListeners(){
        /*
        syncGroupsListeners = new ArrayList<DataSyncListener>();

        syncBlackListListeners = new ArrayList<DataSyncListener>();

        isGroupsSyncedWithServer = demoModel.isGroupsSynced();

        isBlackListSyncedWithServer = demoModel.isBacklistSynced();

         */

        syncContactsListeners = new ArrayList<DataSyncListener>();

        // create the global connection listener
        connectionListener = new EMConnectionListener() {
            @Override
            public void onDisconnected(int error) {
                if (error == EMError.USER_REMOVED) {
//                    onCurrentAccountRemoved();
                }else if (error == EMError.USER_LOGIN_ANOTHER_DEVICE) {
//                    onConnectionConflict();
                }
            }

            @Override
            public void onConnected() {
                android.util.Log.d(TAG,"onConnected()");
                /*
                // in case group and contact were already synced, we supposed to notify sdk we are ready to receive the events
                if(isGroupsSyncedWithServer && isContactsSyncedWithServer){
                    new Thread(){
                        @Override
                        public void run(){
                            DemoHelper.getInstance().notifyForRecevingEvents();
                        }
                    }.start();
                }else{
                    if(!isGroupsSyncedWithServer){
                        asyncFetchGroupsFromServer(null);
                    }

                    if(!isContactsSyncedWithServer){
                        asyncFetchContactsFromServer(null);
                    }

                    if(!isBlackListSyncedWithServer){
                        asyncFetchBlackListFromServer(null);
                    }
                }
                */
                if(!isContactsSyncedWithServer){
                    asyncFetchContactsFromServer(null);
                }
            }
        };
        //注册连接监听
        EMClient.getInstance().addConnectionListener(connectionListener);
        //注册群组和联系人监听
        registerGroupAndContactListener();
        //注册消息事件监听
        registerEventListener();

        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        if(callReceiver == null){
            callReceiver = new CallReceiver();
        }
        //注册通话广播接收者
        appContext.registerReceiver(callReceiver, callFilter);
        //注册连接监听
        EMClient.getInstance().addConnectionListener(connectionListener);

    }
    private CallReceiver callReceiver;

    private class CallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            // 拨打方username
            String from = intent.getStringExtra("from");
            // call type
            String type = intent.getStringExtra("type");
                   /*
            //跳转到通话页面
            CallVoicePage.startActivity(context, from,false);
            */
//            android.util.Log.d(TAG, " CallReceiver onReceive  from = " + from + ",type = " + type);

            context.startActivity(new Intent(context, VoiceCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private String username;
    /**
     * 获取当前用户的环信id
     */
    public String getCurrentUsernName(){
        if(username == null){
            username = XiuxiuUserInfoResult.getInstance().getXiuxiu_id();
        }
        return username;
    }


    /**
     * 注册群组和联系人监听，由于logout的时候会被sdk清除掉，再次登录的时候需要再注册一下
     */
    public void registerGroupAndContactListener(){
        if(!isGroupAndContactListenerRegisted){
            //注册群组变动监听
//            EMClient.getInstance().groupManager().addGroupChangeListener(new MyGroupChangeListener());
            //注册联系人变动监听
            EMClient.getInstance().contactManager().setContactListener(new MyContactListener());
            isGroupAndContactListenerRegisted = true;
        }

    }

    /**
     * 从服务器上同步好友信息
     * @param callback
     */
    public void asyncFetchContactsFromServer(final EMValueCallBack<List<String>> callback){
        android.util.Log.d(TAG,"asyncFetchContactsFromServer()");
        if(isSyncingContactsWithServer){
            return;
        }

        isSyncingContactsWithServer = true;

        new Thread(){
            @Override
            public void run(){
                List<String> usernames = null;
                try {
                    usernames = DataSyncManager.getInstance().getAllContactsFromServer();
                    android.util.Log.d(TAG,"usernames = " + usernames);
                    // in case that logout already before server returns, we should return immediately
                    if(!isLoggedIn()){
                        isContactsSyncedWithServer = false;
                        isSyncingContactsWithServer = false;
                        //通知listeners联系人同步完毕
                        notifyContactsSyncListener(false);
                        return;
                    }

                    Map<String, EaseUser> userlist = new HashMap<String, EaseUser>();
                    for (String username : usernames) {
                        EaseUser user = new EaseUser(username);
//                        EaseCommonUtils.setUserInitialLetter(user);
                        userlist.put(username, user);
                    }
                    // 存入内存
                    getContactList().clear();
                    getContactList().putAll(userlist);
                    // 存入db
                    UserDao dao = new UserDao(appContext);
                    List<EaseUser> users = new ArrayList<EaseUser>(userlist.values());
                    dao.saveContactList(users);

//                    imModel.setContactSynced(true);
                    EMLog.d(TAG, "set contact syn status to true");

                    isContactsSyncedWithServer = true;
                    isSyncingContactsWithServer = false;

                    //通知listeners联系人同步完毕
                    notifyContactsSyncListener(true);
                    /*
                    if(isGroupsSyncedWithServer()){
                        notifyForRecevingEvents();
                    }*/

                    DataSyncManager.getInstance().asyncFetchContactInfosFromServer(usernames,new DataSyncManager.Callback() {
                        @Override
                        public void onSuccess(List<XiuxiuUserInfoResult> ulist) {
                            android.util.Log.d(TAG,"onSuccess ulist.size() = " + ulist.size());
                            updateContactList(ulist);
                            getUserProfileManager().notifyContactInfosSyncListener(true);
                        }

                        @Override
                        public void onError() {
                        }
                    });
                    /**/
                    if(callback != null){
                        callback.onSuccess(usernames);
                    }
                } catch (Exception e) {
//                    imModel.setContactSynced(false);
                    isContactsSyncedWithServer = false;
                    isSyncingContactsWithServer = false;
                    notifyContactsSyncListener(false);
                    e.printStackTrace();
                    /*
                    if(callback != null){
                        callback.onError(e.getErrorCode(), e.toString());
                    }*/
                }

            }
        }.start();
    }

    /**
     * update user list to cach And db
     *
     * @param contactList
     */
    public void updateContactList(List<XiuxiuUserInfoResult> contactInfoList) {
        for (XiuxiuUserInfoResult r : contactInfoList) {
            EaseUser u = XiuxiuUserInfoResult.toEaseUser(r);
            android.util.Log.d(TAG,"u = " + u);
            contactList.put(u.getUsername(), u);
        }
        ArrayList<EaseUser> mList = new ArrayList<EaseUser>();
        mList.addAll(contactList.values());
        imModel.saveContactList(mList);
    }

    /***
     * 好友变化listener
     *
     */
    public class MyContactListener implements EMContactListener {

        @Override
        public void onContactAdded(String username) {
            // 保存增加的联系人
            Map<String, EaseUser> localUsers = getContactList();
            Map<String, EaseUser> toAddUsers = new HashMap<String, EaseUser>();
            EaseUser user = new EaseUser(username);
            // 添加好友时可能会回调added方法两次
            if (!localUsers.containsKey(username)) {
                userDao.saveContact(user);
            }
            toAddUsers.put(username, user);
            localUsers.putAll(toAddUsers);

            //发送好友变动广播
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactDeleted(String username) {
            // 被删除
            Map<String, EaseUser> localUsers = ImHelper.getInstance().getContactList();
            localUsers.remove(username);
            userDao.deleteContact(username);
            inviteMessgeDao.deleteMessage(username);

            //发送好友变动广播
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
        }

        @Override
        public void onContactInvited(final String username, String reason) {

            // 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不需要重复提醒
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();

            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getGroupId() == null && inviteMessage.getFrom().equals(username)) {
                    inviteMessgeDao.deleteMessage(username);
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            msg.setReason(reason);
            android.util.Log.d(TAG, username + "请求加你为好友,reason: " + reason);
            // 设置相应status
            msg.setStatus(InviteMesageStatus.BEINVITEED);
            notifyNewIviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));
            if(EaseUserCacheManager.getInstance().getBeanById(username)==null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XiuxiuApi.queryUserInfoSyn(username);
                    }
                }).start();
            }
        }

        @Override
        public void onContactAgreed(final String username) {
            List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
            for (InviteMessage inviteMessage : msgs) {
                if (inviteMessage.getFrom().equals(username)) {
                    return;
                }
            }
            // 自己封装的javabean
            InviteMessage msg = new InviteMessage();
            msg.setFrom(username);
            msg.setTime(System.currentTimeMillis());
            android.util.Log.d(TAG, username + "同意了你的好友请求");
            msg.setStatus(InviteMesageStatus.BEAGREED);
            notifyNewIviteMessage(msg);
            broadcastManager.sendBroadcast(new Intent(Constant.ACTION_CONTACT_CHANAGED));



            if(EaseUserCacheManager.getInstance().getBeanById(username)==null) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        XiuxiuApi.queryUserInfoSyn(username);
                    }
                }).start();
            }
        }

        @Override
        public void onContactRefused(String username) {
            // 参考同意，被邀请实现此功能,demo未实现
            android.util.Log.d(TAG, username + "拒绝了你的好友请求");
        }
    }

    /**
     * 保存并提示消息的邀请消息
     * @param msg
     */
    private void notifyNewIviteMessage(InviteMessage msg){
        if(inviteMessgeDao == null){
            inviteMessgeDao = new InviteMessgeDao(appContext);
        }
        inviteMessgeDao.saveMessage(msg);
        //保存未读数，这里没有精确计算
        inviteMessgeDao.saveUnreadMessageCount(1);
        // 提示有新消息
        getNotifier().viberateAndPlayTone(null);
    }

    /**
     * 获取好友list
     *
     * @return
     */
    public Map<String, EaseUser> getContactList() {
        if (isLoggedIn() && contactList == null) {
            contactList = imModel.getContactList();
        }

        // return a empty non-null object to avoid app crash
        if(contactList == null){
            return new Hashtable<String, EaseUser>();
        }

        return contactList;
    }

    public void notifyContactsSyncListener(boolean success){
        for (DataSyncListener listener : syncContactsListeners) {
            listener.onSyncComplete(success);
        }
    }

    public void addSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (!syncContactsListeners.contains(listener)) {
            syncContactsListeners.add(listener);
        }
    }

    public void removeSyncContactListener(DataSyncListener listener) {
        if (listener == null) {
            return;
        }
        if (syncContactsListeners.contains(listener)) {
            syncContactsListeners.remove(listener);
        }
    }


    public UserProfileManager getUserProfileManager() {
        if (userProManager == null) {
            userProManager = new UserProfileManager();
        }
        return userProManager;
    }
}
