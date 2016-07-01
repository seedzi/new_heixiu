package com.xiuxiu.user;

import android.text.TextUtils;

import com.qiniu.android.storage.UpCompletionHandler;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.qiniu.QiniuManager;
import com.xiuxiu.utils.FileUtils;

import java.io.File;

/**
 * Created by huzhi on 16-6-6.
 */
public class FileUploadManager {

    private static String TAG = FileUploadManager.class.getSimpleName();

    private static FileUploadManager mInstance;

    private FileUploadManager(){}

    public static FileUploadManager getInstance(){
        if(mInstance == null){
            mInstance = new FileUploadManager();
        }
        return mInstance;
    }

    private static final String USER_IMG = "user_img";

    private static final String USER_VOICE = "user_voice";


    /**
     * 产生用户照片的路径
     * @param position
     * @param suffix
     * @return
     */
    public String generateUserImgFileName(int position,String suffix){
//        return XiuxiuLoginResult.getInstance().getXiuxiu_id() + "_" + USER_IMG + "_" +position  +  "." + suffix;
        return XiuxiuLoginResult.getInstance().getXiuxiu_id() + position;
    }

    /**
     * 产生语音文件的路径
     * @param position
     * @param suffix
     * @return
     */
    public String generateUserVoiceFileName(String suffix){
        if(TextUtils.isEmpty(XiuxiuUserInfoResult.getInstance().getVoice())){
            return (Long.valueOf(XiuxiuLoginResult.getInstance().getXiuxiu_id())) + "." + suffix;
        }else{
            return ((Long.valueOf(FileUtils.getFileName(XiuxiuUserInfoResult.getInstance().getVoice()))) + 1) + "." + suffix;
        }
    }

    /**
     * 上传文件
     * @param filePath　　文件绝对路径
     * @param key　　　　保存在服务器上的资源唯一标识
     * @param callback　回调函数
     */
    public void upload(final String filePath,final String key,final UpCompletionHandler callback){
        android.util.Log.d(TAG,"filePath = " + filePath + ",key = " + key);
        final File file = new File(filePath);
        if(!file.exists()|| TextUtils.isEmpty(key)){
            android.util.Log.d(TAG,"文件不存在");
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String token = QiniuManager.getInstance().getToken(key);
                android.util.Log.d(TAG, "token = " + token);
                QiniuManager.getInstance().uploadFile(filePath, key, token, callback);
            }
        }).start();
    }
}
