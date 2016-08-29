package com.xiuxiuchat.call.voice;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.xiuxiuchat.utils.XiuxiuUtils;

/**
 * Created by huzhi on 16-7-24.
 */
public class CallCostMoneyManager {

    private static CallCostMoneyManager mInstance;

    public static CallCostMoneyManager getInstance(){
        if(mInstance == null){
            mInstance = new CallCostMoneyManager();
        }
        return mInstance;
    }

    private CallCostMoneyManager(){
    }

    public void costXiuxiuB(String toXiuxiuId,String costXiuxiuB,XiuxiuUtils.CallBack callBack){
        startLoop(toXiuxiuId,costXiuxiuB,callBack);
    }


    // ===========================================   工作线程  30分钟一次获取数据 ==============================================================
    private static final long LOOP_TIME = 60*1000;
    private HandlerThread mWorkThread;
    private Handler mWorkHandler;
    /**
     * start循环获取数据
     */
    private void startLoop(final String toXiuxiuId,final String costXiuxiuB,final XiuxiuUtils.CallBack callBack){
        android.util.Log.d("12345","startLoop() toXiuxiuId = " + toXiuxiuId + ",costXiuxiuB = " + costXiuxiuB);
        mWorkThread = new HandlerThread("cost_xiuxiub_thread");
        mWorkThread.setPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        mWorkThread.start();
        Looper looper = mWorkThread.getLooper();
        mWorkHandler = new Handler(looper){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                XiuxiuUtils.transferCoinAsyn(toXiuxiuId,costXiuxiuB,callBack);
                mWorkHandler.sendEmptyMessageDelayed(0, LOOP_TIME);
            }
        };
        mWorkHandler.sendEmptyMessageDelayed(0, LOOP_TIME);
    }
    /**
     * end循环获取数据
     */
    public void stopLoop(){
        if(mWorkHandler!=null){
            mWorkHandler.removeMessages(0);
        }
        if(mWorkThread!=null){
            mWorkThread.quit();
        }
        mWorkHandler = null;
        mWorkThread = null;
    }

}
