package com.xiuxiuchat.user;

import android.text.TextUtils;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.utils.FileUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户编辑页面Manager 主要负责图片以及语音的上传
 * Created by huzhi on 16-6-7.
 */
public class UserEditDetailUploadManager {

    private static String TAG = "UserEditDetailUploadManager";

    private static UserEditDetailUploadManager mInstance;

    private UserEditDetailUploadManager(){}

    public static UserEditDetailUploadManager getInstance(){
        if(mInstance == null){
            mInstance = new UserEditDetailUploadManager();
        }
        return mInstance;
    }

    private List<Task> mTaskList ;

    private CallBack mCallback;

    private int checkedTaskSize = 0;

    public void excute(List<FileBean> imgsBeans,String voicePath,CallBack callback){
        checkedTaskSize = 0;
        if((imgsBeans==null ||imgsBeans.size()==0)
                && TextUtils.isEmpty(voicePath)){
            callback.onSuccess();
            return;
        }
        mTaskList = new ArrayList<Task>();
        mCallback = callback;
        if(imgsBeans!=null) {
            for (FileBean bean : imgsBeans) {
                Task imgTask = new Task();
                imgTask.filePath = bean.localPath;
                imgTask.key = bean.key;
                android.util.Log.d(TAG,"key = " + imgTask.key);
                android.util.Log.d(TAG,"filePath = " + imgTask.filePath);
                mTaskList.add(imgTask);
            }
        }
        if(!TextUtils.isEmpty(voicePath)){
            Task t = new Task();
            t.filePath = voicePath;
            t.key = FileUploadManager.getInstance().generateUserVoiceFileName(FileUtils.getFileSuffix(voicePath));
            android.util.Log.d(TAG,"voicePath = " + voicePath);
            android.util.Log.d(TAG,"voicePath key " + t.key);
            mTaskList.add(t);
        }
        android.util.Log.d(TAG,"mTaskList = " + mTaskList.size());
        for(Task t:mTaskList){
            excuteTask(t);
        }
    }

    private void excuteTask(final Task task){
        android.util.Log.d(TAG,"task.filePath = " + task.filePath + ",task.key = " + task.key);
        FileUploadManager.getInstance().upload(task.filePath,
                task.key,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                        android.util.Log.d(TAG, "complete");
                        synchronized (mTaskList) {
                            android.util.Log.d(TAG, "responseInfo = " + responseInfo + ",s = " + s);
                            if (responseInfo != null && responseInfo.isOK() && !TextUtils.isEmpty(s)) {
                                task.updatePath = s;
                                task.status = Task.STATUS_SUCCESS;
                            } else {
                                task.status = Task.STATUS_FAILUE;
                            }
                        }
                        checkTasksIsFinshed();
                    }
                });
    }

    private void checkTasksIsFinshed(){
        checkedTaskSize = checkedTaskSize +1;
        if(checkedTaskSize!=mTaskList.size()){
            return;
        }
        boolean isSccess = true;
        synchronized (mTaskList){
            for(Task t:mTaskList){
                if(t.status == Task.STATUS_DOING){
                    return;
                }else if(t.status == Task.STATUS_FAILUE){
                    isSccess = false;
                }
            }
        }
        if(mCallback ==null){
            return;
        }
        final boolean result = isSccess;
        XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                if(result){
                    android.util.Log.d(TAG,"onSuccess()");
                    mCallback.onSuccess();
                }else{
                    android.util.Log.d(TAG,"onFailure()");
                    mCallback.onFailure();
                }
            }
        });
    }


    public static class Task{

        public static int STATUS_FAILUE = 0;
        public static int STATUS_SUCCESS = 1;
        public static int STATUS_DOING= 2;
        /*本地文件路径*/
        public String filePath;
        /*图片位置*/
        public int position;
        /*七牛key*/
        public String key;
        /*更新到服务器上的文件路径*/
        public String updatePath = "";
        public int status = STATUS_FAILUE;
    }

    public interface CallBack{
        public void onSuccess();
        public void onFailure();
    }


    public static class FileBean{
        public String key;
        public String localPath;
    }

    public static String getKeys(List<UserEditDetailUploadManager.FileBean> mImgFiles){
        String picKey = "";
        int position = 0;
        for(UserEditDetailUploadManager.FileBean fileBean : mImgFiles){
            if(position==0) {
                picKey = fileBean.key;
            }else{
                picKey = picKey + "," + fileBean.key;
            }
            position++;
        }
        return picKey;
    }

}
