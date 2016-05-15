package com.xiuxiu.im;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.controller.EaseUI;

public class ImHelper {

    protected static final String TAG = "ImHelper";

    private Context appContext;

    private EaseUI easeUI;

    /**
     * 是否登录成功过
     *
     * @return
     */
    public boolean isLoggedIn() {
        return EMClient.getInstance().isLoggedInBefore();
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

}
