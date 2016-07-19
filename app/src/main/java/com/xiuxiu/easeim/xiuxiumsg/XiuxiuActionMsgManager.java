package com.xiuxiu.easeim.xiuxiumsg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huzhi on 16-7-16.
 */
public class XiuxiuActionMsgManager {

    private static XiuxiuActionMsgManager mInstance;

    public static XiuxiuActionMsgManager getInstance(){
        if(mInstance==null){
            mInstance = new XiuxiuActionMsgManager();
        }
        return mInstance;
    }

    public XiuxiuActionMsgManager(){
        mData = new HashMap<String, String>();
    }

    private Map<String,String> mData;

    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();

    /** 初始化 最好放在异步线程*/
    public void init(){
        mData.clear();
        mData.putAll(XiuxiuActionMsgTable.queryAll());
    }


    public String getStatusById(String meassgeId){
        if(mData==null){
            return null;
        }
        return mData.get(meassgeId);
    }

    public void setMap(Map<String,String> map){
        mData = map;
    }


    public void add(final String messageId,String status){
        if(mData.get(messageId)!=null){
            android.util.Log.d("12345","mData.get(messageId)!=null");
            return;
        }
        final XiuxiuActionInfo info = new XiuxiuActionInfo();
        info.ask_id = messageId;
        info.status = status;
        mData.put(messageId,status);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if(!XiuxiuActionMsgTable.isExist(messageId)){
                    XiuxiuActionMsgTable.insert(info);
                } else {
                    XiuxiuActionMsgTable.update(info);
                }
            }
        });

    }


    // =================================================================
    // action接受者
    // =================================================================
    private List<Listener> mListener = new ArrayList<Listener>();

    public synchronized void addListener(Listener l){
        mListener.add(l);
    }

    public synchronized void removeListener(Listener l){
        mListener.remove(l);
    }

    public synchronized void notifyListener(){
        for(Listener l:mListener){
            l.onReciverActionMsg();
        }
    }

    public interface Listener{
        void onReciverActionMsg();
    }
}
