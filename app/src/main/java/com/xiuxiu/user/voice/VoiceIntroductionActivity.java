package com.xiuxiu.user.voice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hyphenate.EMError;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.FileUploadManager;
import com.xiuxiu.utils.FileUtils;
import com.xiuxiu.utils.ToastUtil;

import org.json.JSONObject;


/**
 * Created by huzhi on 16-5-27.
 */
public class VoiceIntroductionActivity extends BaseActivity implements View.OnClickListener,
        View.OnTouchListener {

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,VoiceIntroductionActivity.class);
        ac.startActivityForResult(intent,REQUEST_CODE);
    }

    public static int REQUEST_CODE = 102;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_voice_introduction_layout);
        setupViews();
        initVoice();
    }

    private ImageView mControl;
    private View mBt;
    private ProgressBar mProgressBar;
    private TextView mTimeTxt;

    private void setupViews(){
        mControl = (ImageView) findViewById(R.id.control);
        mBt = findViewById(R.id.bt);
        mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
        mProgressBar.setMax(20 * 1000); //20000毫秒
        mProgressBar.setIndeterminate(false);

        mControl.setOnClickListener(this);
        mBt.setOnTouchListener(this);

        mTimeTxt = (TextView)findViewById(R.id.time_txt);
        findViewById(R.id.save).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(voiceRecorder!=null){
            voiceRecorder.discardRecording();
        }
        if(mMediaPlayer!=null){
            mMediaPlayer.release();
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.control:
                if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
                    pause();
                }else{
                    play();
                }
                break;
            case R.id.save:
                if(voiceRecorder==null ||
                        TextUtils.isEmpty(voiceRecorder.getVoiceFilePath())){
                    return;
                }
                Intent intent = new Intent();
                Bundle bundle=new Bundle();
                bundle.putString("data", voiceRecorder.getVoiceFilePath());
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }
    }

    private VoiceRecorder voiceRecorder;
    protected PowerManager.WakeLock wakeLock;

    protected Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
//            micImage.setImageDrawable(micImages[msg.what]);
            if(msg.what==0){
                mProgressBar.setProgress(msg.arg1);
                mTimeTxt.setText(msg.arg1/1000 + "秒");
            }else if(msg.what==1){
                ToastUtil.showMessage(VoiceIntroductionActivity.this,(CharSequence)("最多智能录音20秒"));
                mProgressBar.setProgress(20 * 1000);
                mTimeTxt.setText(20 + "秒");
                stopRecoding();
            }
        }
    };

    private void initVoice(){
        voiceRecorder = new VoiceRecorder(micImageHandler);
        wakeLock = ((PowerManager) getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "xiuxiu");
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                try {
                    if (isPlaying || (mMediaPlayer!=null&&mMediaPlayer.isPlaying()))
                        stopPlayVoice();
                    v.setPressed(true);
                    startRecording();
                } catch (Exception e) {
                    v.setPressed(false);
                }
                return true;
            case MotionEvent.ACTION_MOVE:
                if (event.getY() < 0) {
//                    showReleaseToCancelHint();
                } else {
//                    showMoveUpToCancelHint();
                }
                return true;
            case MotionEvent.ACTION_UP:
                v.setPressed(false);
                if (event.getY() < 0) {
                    // discard the recorded audio.
                    discardRecording();
                } else {
                    // stop recording and send voice file
                    try {
                        // huzhi
                        if(!voiceRecorder.isRecording()){
                            return true;
                        }// huzhi
                        int length = stopRecoding();
                        if (length > 0) {
                            /*
                            if (recorderCallback != null) {
                                recorderCallback.onVoiceRecordComplete(getVoiceFilePath(), length);
                            }*/
                        } else if (length == EMError.FILE_INVALID) {
                            ToastUtil.showMessage(this, com.hyphenate.easeui.R.string.Recording_without_permission);
                        } else {
                            ToastUtil.showMessage(this, com.hyphenate.easeui.R.string.The_recording_time_is_too_short);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        ToastUtil.showMessage(this, (CharSequence)"录制文件异常");
                    }
                }
                return true;
            default:
//                discardRecording();
                return false;
        }
    }

    public void startRecording() {
        // 检测sdk 是否存在
        if (!EaseCommonUtils.isExitsSdcard()) {
            ToastUtil.showMessage(VoiceIntroductionActivity.this, com.hyphenate.easeui.R.string.Send_voice_need_sdcard_support);
            return;
        }
        try {
            wakeLock.acquire();
//            this.setVisibility(View.VISIBLE);
//            recordingHint.setText(context.getString(com.hyphenate.easeui.R.string.move_up_to_cancel));
//            recordingHint.setBackgroundColor(Color.TRANSPARENT);
            voiceRecorder.startRecording(this);
        } catch (Exception e) {
            e.printStackTrace();
            if (wakeLock.isHeld())
                wakeLock.release();
            if (voiceRecorder != null)
                voiceRecorder.discardRecording();
            ToastUtil.showMessage(VoiceIntroductionActivity.this,com.hyphenate.easeui.R.string.recoding_fail);
//            this.setVisibility(View.INVISIBLE);
//            Toast.makeText(context, com.hyphenate.easeui.R.string.recoding_fail, Toast.LENGTH_SHORT).show();
            return;
        }
    }

    public int stopRecoding() {
        if (wakeLock.isHeld())
            wakeLock.release();
        return voiceRecorder.stopRecoding();
    }


    public void discardRecording() {
        if (wakeLock.isHeld())
            wakeLock.release();
        try {
            // 停止录音
            if (voiceRecorder.isRecording()) {
                voiceRecorder.discardRecording();
            }
        } catch (Exception e) {
        }
    }


    // ====================================  play ====================================================

    private boolean isPlaying = false;
    private boolean isPause = false;
    private MediaPlayer mMediaPlayer;

    private void play(){
        if(mMediaPlayer!=null && !mMediaPlayer.isPlaying() && isPause){
            mControl.setImageResource(R.drawable.user_voice_stop_icon);
            mMediaPlayer.start();
            isPause = false;
        }else{
            String path = voiceRecorder.getVoiceFilePath();
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    isPause = false;
                    mControl.setImageResource(R.drawable.user_voice_play_icon);
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isPlaying = true;
                    isPause = true;
                    mMediaPlayer.start();
                    mControl.setImageResource(R.drawable.user_voice_stop_icon);
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    isPlaying = false;
                    isPause = false;
                    mControl.setImageResource(R.drawable.user_voice_play_icon);
                    return false;
                }
            });
            try {
                mMediaPlayer.setDataSource(path);
                mMediaPlayer.prepareAsync();
            }catch (Exception e){
            }
        }

    }

    private void stopPlayVoice(){
        android.util.Log.d("ccc","stopPlayVoice");
        isPlaying = false;
        isPause = false;
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        mControl.setImageResource(R.drawable.user_voice_play_icon);
    }

    private void pause(){
        isPause = true;
        isPlaying = false;
        mMediaPlayer.pause();
        mControl.setImageResource(R.drawable.user_voice_play_icon);
    }

    // ============================================================================================
    // 网络层
    // ============================================================================================
        /*
    private ProgressDialog mProgressDialog;

    private void showProgressDialog(){
        mProgressDialog = ProgressDialog.show(this, "提示", "正在上传中...");
    }

    private void dismisslProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }




     private String mUploadFilePath;
    private void updateUserData() {
        if(voiceRecorder == null || TextUtils.isEmpty( voiceRecorder.getVoiceFilePath())){
            return;
        }
        FileUploadManager.getInstance().upload( voiceRecorder.getVoiceFilePath(),
                FileUploadManager.getInstance().generateUserVoiceFileName(FileUtils.getFileNameBySuffix(voiceRecorder.getVoiceFilePath())),
                new UpCompletionHandler() {
                    @Override
                    public void complete(String s, ResponseInfo responseInfo, JSONObject jsonObject) {
                        android.util.Log.d("aaaa","s = " + s);
                        android.util.Log.d("aaaa","responseInfo = " + responseInfo);
                        android.util.Log.d("aaaa","jsonObject = " + jsonObject);
                        if (responseInfo != null && responseInfo.isOK() && !TextUtils.isEmpty(s)) {
                            mUploadFilePath = HttpUrlManager.QI_NIU_HOST + s;
                            android.util.Log.d("aaaa","mUploadFilePath = " + mUploadFilePath);
                            XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                                @Override
                                public void run() {
                                    XiuxiuApplication.getInstance().getQueue()
                                            .add(new StringRequest(getUpdateUrl(), mRefreshListener, mRefreshErroListener));
                                }
                            });
                        }
                    }
                });
    }

    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            XiuxiuResult res = gson.fromJson(response, XiuxiuResult.class);
            android.util.Log.d("cccc","error response= " + response);
            if(res.isSuccess()){
                XiuxiuUserInfoResult info = new XiuxiuUserInfoResult();
                info.setVoice(mUploadFilePath);
                XiuxiuUserInfoResult.save(info);
                dismisslProgressDialog();
                ToastUtil.showMessage(VoiceIntroductionActivity.this, "资料更新成功!");
                finish();
            }else{
                dismisslProgressDialog();
                ToastUtil.showMessage(VoiceIntroductionActivity.this, "修改失败!");
            }
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.d("cccc","error = " + error.getMessage());
            dismisslProgressDialog();
            ToastUtil.showMessage(VoiceIntroductionActivity.this, "修改失败!");
        }
    };
    private String getUpdateUrl() {
        return Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.UPDATE_USER_INFO)
                .appendQueryParameter(XiuxiuUserInfoResult.XIUXIU_ID, XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter(XiuxiuUserInfoResult.VOICE, mUploadFilePath)
                .build().toString();
    }
    */
}
