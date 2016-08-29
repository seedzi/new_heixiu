package com.xiuxiuchat.Xiuxiubroadcast;

import android.database.Cursor;

import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.db.XiuxiuBroadcastMsgTable;
import com.xiuxiuchat.utils.XiuxiuUtils;

/**
 * Created by huzhi on 16-8-11.
 */
public class BroadcastLoaderManager {

    private static final String TAG = "BroadcastLoaderManager";

    private static BroadcastLoaderManager mInstance;

    private BroadcastLoaderManager(){}

    public static BroadcastLoaderManager getInstance(){
        if(mInstance==null){
            mInstance = new BroadcastLoaderManager();
        }
        return mInstance;
    }

    public void syncData(final int size,final Callback callback){
        XiuxiuUtils.runInNewThread(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = XiuxiuBroadcastMsgTable.queryCursor(size);
                if(cursor!=null&&cursor.getCount()>0){
                    if(callback!=null){
                        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                callback.onSuccess(cursor);
                            }
                        });
                    }
                }
            }
        });
    }

    public static interface Callback{
        public void onSuccess(Cursor cursor);
    }

}
