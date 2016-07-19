package com.xiuxiu.easeim;

import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.bean.ChatNickNameAndAvatarBean;
import com.xiuxiu.db.ChatNameAndAvatarTable;
import com.xiuxiu.db.XiuxiuUserInfoTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huzhi on 16-6-19.
 */
public class EaseUserCacheManager {

    private static String TAG = "EaseUserCacheManager";

    private static EaseUserCacheManager mInstance;

    private ExecutorService mExecutor;

    private EaseUserCacheManager(){
        mExecutor = Executors.newSingleThreadExecutor();
        mData = new ConcurrentHashMap<String, XiuxiuUserInfoResult>();
        init();
    }
    /** 初始化 最好放在异步线程*/
    public void init(){
        mData.putAll(XiuxiuUserInfoTable.queryAll());
    }


    public static EaseUserCacheManager getInstance(){
        if(mInstance==null){
            mInstance = new EaseUserCacheManager();
        }
        return mInstance;
    }

    private Map<String,XiuxiuUserInfoResult> mData;

    public Map<String,XiuxiuUserInfoResult> getMap(){
        return mData;
    }

    public void setMap(Map<String,XiuxiuUserInfoResult> map){
        mData = map;
    }

    public XiuxiuUserInfoResult getBeanById(String xiuxiu_id){
        if(xiuxiu_id == null){
            android.util.Log.d(TAG,"xiuxiu_id == null");
        }
        if(mData == null){
            android.util.Log.d(TAG,"mData == null");
        }
        return mData.get(xiuxiu_id);
    }

    public void add(final XiuxiuUserInfoResult info){
        if(info==null || info.getXiuxiu_id()==null || mData.get(info.getXiuxiu_id())!=null){
            return;
        }
        mData.put(info.getXiuxiu_id(),info);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if(!XiuxiuUserInfoTable.isExist(info.getXiuxiu_id())){
                    XiuxiuUserInfoTable.insert(info);
                } else {
                    XiuxiuUserInfoTable.update(info);
                }
            }
        });
    }

}
