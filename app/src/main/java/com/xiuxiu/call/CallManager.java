package com.xiuxiu.call;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.EMNoActiveCallException;
import com.hyphenate.exceptions.EMServiceNotReadyException;
import com.xiuxiu.call.voice.CallVoicePage;

/**
 * Created by huzhi on 16-6-26.
 */
public class CallManager {

    private static String TAG = "CallManager";

    private static CallManager mInstance;

    public static CallManager getInstance(Context context){
        if(mInstance==null){
            mInstance = new CallManager(context);
        }
        return mInstance;
    }

    private CallManager(Context context){
        IntentFilter callFilter = new IntentFilter(EMClient.getInstance().callManager().getIncomingCallBroadcastAction());
        context.registerReceiver(new CallReceiver(), callFilter);
    }

    private class CallReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 拨打方username
            String from = intent.getStringExtra("from");
            // call type
            String type = intent.getStringExtra("type");
            //跳转到通话页面
            CallVoicePage.startActivity(context,from,type,false);

            android.util.Log.d(TAG,"from = " + from + ",type = " + type);
        }
    }


    public void call2Person(String username){
        try {
            EMClient.getInstance().callManager().makeVoiceCall(username);
        } catch (EMServiceNotReadyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 接听通话
     * @throws EMNoActiveCallException
     */
    public void answserCall(){

        try {
            EMClient.getInstance().callManager().answerCall();
        } catch (Exception e) {

        }
        /*
        try {
            EMClient.getInstance().callManager().answerCall();
        } catch (EMNoActiveCallException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (EMNetworkUnconnectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
    }
    /**
     * 拒绝接听
     * @throws EMNoActiveCallException
     */
    public void rejectCall(){

        try {
            EMClient.getInstance().callManager().rejectCall();
        } catch (EMNoActiveCallException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * 挂断通话
     */
    public void endCall(){
        try {
            EMClient.getInstance().callManager().endCall();
        } catch (EMNoActiveCallException e) {
            e.printStackTrace();
        }
    }
}
