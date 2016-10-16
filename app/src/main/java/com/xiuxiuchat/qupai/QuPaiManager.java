package com.xiuxiuchat.qupai;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.hardware.Camera;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.duanqu.qupai.auth.AuthService;
import com.duanqu.qupai.auth.QupaiAuthListener;
import com.duanqu.qupai.bean.QupaiUploadTask;
import com.duanqu.qupai.engine.session.MovieExportOptions;
import com.duanqu.qupai.engine.session.ProjectOptions;
import com.duanqu.qupai.engine.session.ThumbnailExportOptions;
import com.duanqu.qupai.engine.session.UISettings;
import com.duanqu.qupai.engine.session.VideoSessionCreateInfo;
import com.duanqu.qupai.sdk.android.QupaiManager;
import com.duanqu.qupai.sdk.android.QupaiService;
import com.duanqu.qupai.upload.QupaiUploadListener;
import com.duanqu.qupai.upload.UploadService;
import com.xiuxiuchat.SharePreferenceWrap;
import com.xiuxiuchat.XiuxiuApplication;
import com.xiuxiuchat.api.XiuxiuLoginResult;

import java.io.File;
import java.util.UUID;

/**
 * Created by huzhi on 16-7-23.
 */
public class QuPaiManager {

    public static final String SHARE_PREFERENCE_NAME = "qupaimanager";

    private static final String TAG = QuPaiManager.class.getSimpleName();

    private static final String KEY_ACCESS_TOKEN = "access_toke";

    private static QuPaiManager mInstance;

    public static QuPaiManager getInstance(){
        if(mInstance == null){
            mInstance = new QuPaiManager();
        }
        return mInstance;
    }

    private static final String APPKEY = "209fbc18edf81e4";

    private static final String APPSECRET = "5f22a0857d00453f91cd039343557235";


    private QupaiService mQupaiService;

    public void startAuth(Context context){
        android.util.Log.d(TAG, "startAuth");
        AuthService service = AuthService.getInstance();
        service.setQupaiAuthListener(new QupaiAuthListener() {
            @Override
            public void onAuthError(int errorCode, String message) {
                android.util.Log.d(TAG, "ErrorCode = " + errorCode + ",message = " + message);
//                initOpotions();
            }
            @Override
            public void onAuthComplte(int responseCode, String responseMessage) {
                String accessToken = responseMessage;//鉴权完成返回accessToke
                if(!TextUtils.isEmpty(accessToken)){
                    SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
                    sharePreferenceWrap.putString(KEY_ACCESS_TOKEN, accessToken);
                    mAccessToken = accessToken;
                    android.util.Log.d(TAG, "mAccessToken = " + mAccessToken);
                    initOpotions();
                }
            }
        });
        service.startAuth(context, APPKEY, APPSECRET, "xiuxiu_id_" + XiuxiuLoginResult.getInstance().getXiuxiu_id());

    }

    private String mAccessToken;

    private static boolean isInit = false;

    public void init(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        mAccessToken = sharePreferenceWrap.getString(KEY_ACCESS_TOKEN,"");
        android.util.Log.d(TAG, "mAccessToken = " + mAccessToken);
        if(!isInit || TextUtils.isEmpty(mAccessToken)){
            startAuth(XiuxiuApplication.getInstance().getApplicationContext());
        }else{
            initOpotions();
        }
    }

    /**
     * 判断趣拍是否初始化完毕
     */
    public boolean isInit(){
        if(mQupaiService==null){
            return false;
        }
        return true;
    }

    /**
     * 趣拍基本配置
     */
    private void initOpotions(){
        android.util.Log.d(TAG,"initOpotions()");
        try {
            mQupaiService =  QupaiManager.getQupaiService(XiuxiuApplication.getInstance().getApplicationContext());

            //UI设置参数
            UISettings _UISettings = new UISettings() {
                @Override
                public boolean hasEditor() {
                    return true;//是否需要编辑功能
                }
                @Override
                public boolean hasImporter() {
                    return false;//是否需要导入功能
                }
                @Override
                public boolean hasGuide() {
                    return false;//是否启动引导功能，建议用户第一次使用时设置为true
                }
                @Override
                public boolean hasSkinBeautifer() {
                    return true;//是否显示美颜图标
                }
            };
            //压缩参数
            MovieExportOptions movie_options = new MovieExportOptions.Builder()
    //                    .setVideoBitrate(mVideoBitrate)
                    .configureMuxer("movflags", "+faststart")
                    .build();

            //输出视频的参数
            ProjectOptions projectOptions = new ProjectOptions.Builder()
                    //输出视频宽高目前只能设置1：1的宽高，建议设置480*480.
                    .setVideoSize(480, 480)
                            //帧率
                    .setVideoFrameRate(30)
                            //时长区间
                    .setDurationRange(2,8)
                    .get();
            //缩略图参数,可设置取得缩略图的数量，默认10张
            ThumbnailExportOptions thumbnailExportOptions =new ThumbnailExportOptions.Builder().setCount(1).get();
            VideoSessionCreateInfo info =new VideoSessionCreateInfo.Builder()
                    //水印地址，如"assets://Qupai/watermark/qupai-logo.png"
    //                    .setWaterMarkPath(waterMarkPath)
                    //水印的位置
    //                .setWaterMarkPosition(1)
                            //摄像头方向,可配置前置或后置摄像头
                    .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT)
                            //美颜百分比,设置之后内部会记住，多次设置无效
                    .setBeautyProgress(80)
                            //默认是否开启
                    .setBeautySkinOn(true)
                    .setMovieExportOptions(movie_options)
                    .setThumbnailExportOptions(thumbnailExportOptions)
                    .build();
            //初始化，建议在application里面做初始化，这里做是为了方便开发者认识参数的意义
            mQupaiService.initRecord(info,projectOptions,_UISettings);

            isInit = true;
        }catch (Exception e){
            android.util.Log.d(TAG,"Exception = " + e.getMessage());
            return;
        }
    }

    /**
     * 进入录制页面
     */
    public void showRecordPage(FragmentActivity ac,int requestCode){
        if(mQupaiService==null){
            android.util.Log.d(TAG,"showRecordPage==NULL");
            return;
        }
        android.util.Log.d(TAG,"showRecordPage");
        mQupaiService.showRecordPage(ac, requestCode, false);
    }

    /**
     * 进入录制页面
     */
    public void showRecordPage(android.app.Fragment f,int requestCode){
        if(mQupaiService==null){
            android.util.Log.d(TAG,"showRecordPage==NULL");
            return;
        }
        mQupaiService.showRecordPage(f, requestCode, false);
    }

    /**
     * 清空accessToken
     */
    public void clearAccessToken(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putString(KEY_ACCESS_TOKEN, "");
        mQupaiService = null;
    }

    public void upload(final File _VideoFile,final File _Thumbnail){
        String sapce =  "xiuxiu_id_" + XiuxiuLoginResult.getInstance().getXiuxiu_id();
        android.util.Log.d(TAG,"upload");
        UploadService uploadService = UploadService.getInstance();
            uploadService.setQupaiUploadListener(new QupaiUploadListener() {
                @Override
                public void onUploadProgress(String uuid, long uploadedBytes, long totalBytes) {
                    android.util.Log.d(TAG,"onUploadProgress");
                }
                @Override
                public void onUploadError(String uuid, int errorCode, String message) {
                    android.util.Log.d(TAG,"onUploadError message = " + message);
                }
                @Override
                public void onUploadComplte(String uuid, int responseCode, String responseMessage) {
                    android.util.Log.d(TAG,"onUploadComplte");
                }
            });
            String uuid = UUID.randomUUID().toString();
            QupaiUploadTask task = uploadService.createTask(XiuxiuApplication.getInstance().getApplicationContext()
                , uuid, _VideoFile, _Thumbnail, mAccessToken, sapce, 0, "video", "video");
            try {
                uploadService.startUpload(task);
            } catch (IllegalArgumentException exc) {
                android.util.Log.d(TAG,"exc = " + exc.getMessage());
            }
        }
}
