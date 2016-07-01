package com.xiuxiu.user.voice;

import android.media.MediaPlayer;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.ImageView;

import com.xiuxiu.R;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.utils.ToastUtil;

/**
 * Created by huzhi on 16-6-8.
 */
public class VoicePlayManager {

    private static VoicePlayManager mInstance;

    public static VoicePlayManager getInstance(){
        if(mInstance == null){
            mInstance = new VoicePlayManager();
        }
        return mInstance;
    }

    private VoicePlayManager(){}

    private static String TAG = "VoicePlayManager";

    private boolean isPlaying = false;
    private boolean isPause = false;
    private MediaPlayer mMediaPlayer;
    private ImageView mControl;
    private int mStopIconRes;
    private int mPlayIconRes;
    private FragmentActivity mAc;

    public void init(FragmentActivity ac,ImageView control,int stopIconRes,int playIconRes){
        mAc = ac;
        release();
        mControl = control;
        mStopIconRes = stopIconRes;
        mPlayIconRes = playIconRes;
    }

    public void play(String path){
        android.util.Log.d(TAG,"path = " + path);
        if(TextUtils.isEmpty(path)){
            ToastUtil.showMessage(mAc,"没有语音");
            return;
        }
        if(mMediaPlayer!=null && !mMediaPlayer.isPlaying() && isPause){
            mControl.setImageResource(mStopIconRes);
            mMediaPlayer.start();
            isPause = false;
        }else{
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    isPause = false;
                    mControl.setImageResource(mPlayIconRes);
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    isPlaying = true;
                    isPause = true;
                    mMediaPlayer.start();
                    mControl.setImageResource(mStopIconRes);
                }
            });
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    isPlaying = false;
                    isPause = false;
                    mControl.setImageResource(mPlayIconRes);
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

    public boolean isPlaying(){
        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
            return true;
        }else{
            return false;
        }
    }


    private void stopPlayVoice(){
        android.util.Log.d("ccc","stopPlayVoice");
        isPlaying = false;
        isPause = false;
        mMediaPlayer.stop();
        mMediaPlayer.release();
        mMediaPlayer = null;
        mControl.setImageResource(mPlayIconRes);
    }

    public void pause(){
        isPause = true;
        isPlaying = false;
        if(mMediaPlayer!=null) {
            mMediaPlayer.pause();
        }
        mControl.setImageResource(mPlayIconRes);
    }

    public void release(){
        if(mMediaPlayer!=null){
            if(mControl!=null){
                mControl.setImageResource(mPlayIconRes);
            }
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
