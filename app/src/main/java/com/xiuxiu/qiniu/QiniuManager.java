package com.xiuxiu.qiniu;


import android.text.TextUtils;

import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by huzhi on 16-5-29.
 */
public class QiniuManager {


    private UploadManager mUploadManager;

    private QiniuManager(){
        mUploadManager = new UploadManager();
    }

    /**
     * 上传文件
     * @param filePath
     * @param key
     * @param token
     * @return false:参数错误;true:参数有效
     */
    public boolean uploadFile(String filePath,String key,String token){
        if(TextUtils.isEmpty(filePath)||TextUtils.isEmpty(key)||TextUtils.isEmpty(token)){
            return false;
        }
        File data = new File(filePath);
        if(!data.exists()){
            return false;
        }
        mUploadManager.put(data, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        android.util.Log.d("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, null);
        return true;
    }

}
