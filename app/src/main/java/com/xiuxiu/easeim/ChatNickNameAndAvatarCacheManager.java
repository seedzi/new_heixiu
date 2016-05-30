package com.xiuxiu.easeim;

import com.xiuxiu.bean.ChatNickNameAndAvatarBean;
import com.xiuxiu.db.ChatNameAndAvatarTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by huzhi on 16-5-22.
 * 所有聊天对象(包括好友与陌生人)的头像和nichname的缓存管理器
 */
public class ChatNickNameAndAvatarCacheManager {

    private static ChatNickNameAndAvatarCacheManager mInstance;

    private ExecutorService mExecutor;

    private ChatNickNameAndAvatarCacheManager(){
        mExecutor = Executors.newSingleThreadExecutor();
        mData = new ConcurrentHashMap<String, ChatNickNameAndAvatarBean>();
    }
    /** 初始化 最好放在异步线程*/
    public void init(){
        mData.putAll(ChatNameAndAvatarTable.queryAll());
    }


    public static ChatNickNameAndAvatarCacheManager getInstance(){
        if(mInstance==null){
            mInstance = new ChatNickNameAndAvatarCacheManager();
        }
        return mInstance;
    }

    private Map<String,ChatNickNameAndAvatarBean> mData;

    public Map<String,ChatNickNameAndAvatarBean> getMap(){
//        if(mData==null){
//            mData = new ConcurrentHashMap<String, ChatNickNameAndAvatarBean>();
//        }
        return mData;
    }

    public void setMap(Map<String,ChatNickNameAndAvatarBean> map){
        mData = map;
    }

    public ChatNickNameAndAvatarBean getBeanById(String xiuxiu_id){
        if(xiuxiu_id == null){
            android.util.Log.d("ccc","xiuxiu_id == null");
        }
        if(mData == null){
            android.util.Log.d("ccc","mData == null");
        }
        return mData.get(xiuxiu_id);
    }

    public void add(final ChatNickNameAndAvatarBean info){
        if(info==null || info.getXiuxiu_id()==null || mData.get(info.getXiuxiu_id())!=null){
            return;
        }
//        if(mData==null){
//            mData = new ConcurrentHashMap<String, ChatNickNameAndAvatarBean>();
//        }
        mData.put(info.getXiuxiu_id(),info);
        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                if(!ChatNameAndAvatarTable.isExist(info.getXiuxiu_id())){
                    ChatNameAndAvatarTable.insert(info);
                } else {
                    ChatNameAndAvatarTable.update(info);
                }
            }
        });
    }

}
