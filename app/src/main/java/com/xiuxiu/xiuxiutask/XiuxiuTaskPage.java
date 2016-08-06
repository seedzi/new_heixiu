package com.xiuxiu.xiuxiutask;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.util.PathUtil;
import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuSettingsConstant;
import com.xiuxiu.qupai.QuPaiManager;
import com.xiuxiu.qupai.RecordResult;
import com.xiuxiu.utils.ToastUtil;

import java.io.File;

/**
 * Created by huzhi on 16-5-9.
 */
public class XiuxiuTaskPage extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "XiuxiuTaskPage";

    public static void startActivity4Result(Fragment fra,int requestCode){
        Intent intent = new Intent(fra.getActivity().getApplication(),XiuxiuTaskPage.class);
        fra.startActivityForResult(intent, requestCode);
    }

    private View mVideoBt;

    private View mPicBt;

    private View mVoiceBt;

    private EditText mEditText;

    private EditText mXiuxiuBSizeEdt;


    public static String XIUXIU_TITLE_IMG_TXT = "咻咻图片";

    public static String XIUXIU_TITLE_VIDEO_TXT = "咻咻视频";

    public static String XIUXIU_TITLE_VOICE_TXT = "咻咻语聊";

    private String mXiuxiuTitle = XIUXIU_TITLE_IMG_TXT;


    private String mVideoFile = "";

    private String mThum = "";

    private String mDuration = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_xiuxiu_task_page);
        setupViews();
    }

    private void setupViews(){
        mVideoBt = findViewById(R.id.video);
        mPicBt = findViewById(R.id.pic);
        mVoiceBt = findViewById(R.id.voice);
        mVideoBt.setOnClickListener(this);
        mPicBt.setOnClickListener(this);
        mVoiceBt.setOnClickListener(this);

        mEditText = (EditText)findViewById(R.id.edit);
        mXiuxiuBSizeEdt = (EditText) findViewById(R.id.xiu_bi_size);
        mXiuxiuBSizeEdt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        findViewById(R.id.xiuxiu_bt).setOnClickListener(this);
        mXiuxiuBSizeEdt.setText(XiuxiuSettingsConstant.getXiuxiuImgPrice() + "");
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.video:
                mEditText.setHint("填写你对索要视频的期望和要求...(智能查看三次)");
                mXiuxiuTitle = XIUXIU_TITLE_VIDEO_TXT;
                mXiuxiuBSizeEdt.setText(XiuxiuSettingsConstant.getXiuxiuVideoPrice()+"");
                break;
            case R.id.pic:
                mEditText.setHint("填写你对索要图片的期望和要求...(智能查看三次)");
                mXiuxiuTitle = XIUXIU_TITLE_IMG_TXT;
                mXiuxiuBSizeEdt.setText(XiuxiuSettingsConstant.getXiuxiuImgPrice()+"");
                break;
            case R.id.voice:
                mEditText.setHint("填写你对索要声音的期望和要求...(智能查看三次)");
                mXiuxiuTitle = XIUXIU_TITLE_VOICE_TXT;
                mXiuxiuBSizeEdt.setText(XiuxiuSettingsConstant.getXiuxiuYuyinPrice()+"");
                break;
            case R.id.xiuxiu_bt:
                if(TextUtils.isEmpty(mEditText.getText().toString())){
                    ToastUtil.showMessage(XiuxiuTaskPage.this,"请填写咻咻要求！");
                    return;
                }
                if(XiuxiuUserInfoResult.isMale(XiuxiuUserInfoResult.getInstance().getSex())){//男性
                    sendMessage2ChatFragment(true);
                }else{//女性
                    if(mXiuxiuTitle.equals(XIUXIU_TITLE_IMG_TXT)){ //图片任务
                        selectPicFromCamera();
                    }else if(mXiuxiuTitle.equals(XIUXIU_TITLE_VIDEO_TXT)){ //视频任务
                        if(!QuPaiManager.getInstance().isInit()){
                            ToastUtil.showMessage(XiuxiuTaskPage.this,"初始化失败,如果使用次功能请您在网络流畅情况下退出app重新进入");
                            return;
                        }
                        QuPaiManager.getInstance().showRecordPage(this,REQUEST_CODE_VIDEO);
                    }else{ //语聊任务
                        sendMessage2ChatFragment(false);
                    }
                }
                break;
        }
    }

    private String mImagePath = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists()){
                    mImagePath = cameraFile.getAbsolutePath();
                    if(!TextUtils.isEmpty(mImagePath)){
                        sendMessage2ChatFragment(false);
                    }
                }
            } else if (requestCode == REQUEST_CODE_VIDEO){ //发送视频
                RecordResult result =new RecordResult(data);
                //得到视频地址，和缩略图地址的数组，返回十张缩略图
                try {
                    mVideoFile = result.getPath();
                    mThum = result.getThumbnail()[0];
                    mDuration = String.valueOf(result.getDuration()/1000000);
                }catch (Exception e){}
                sendMessage2ChatFragment(false);
            }
        }
    }

    protected File cameraFile;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_VIDEO = 3;
    /**
     * 照相获取图片
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isExitsSdcard()) {
            Toast.makeText(XiuxiuTaskPage.this, com.hyphenate.easeui.R.string.sd_card_does_not_exist, 0).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(), EMClient.getInstance().getCurrentUser()
                + System.currentTimeMillis() + ".jpg");
        cameraFile.getParentFile().mkdirs();
        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile)),
                REQUEST_CODE_CAMERA);
    }


    /**
     *　生成任务给ChatFragment
     * @param isNan2Nv 是否男发给女
     */
    private void sendMessage2ChatFragment(boolean isNan2Nv){
        Intent intent = new Intent();
        int type;
        if(mXiuxiuTitle.equals(XIUXIU_TITLE_IMG_TXT)){ //图片任务
            type = XiuxiuTaskBean.TYPE_IMAGE_XIUXIU;
        } else if(mXiuxiuTitle.equals(XIUXIU_TITLE_VIDEO_TXT)){//视频任务
            type = XiuxiuTaskBean.TYPE_VIDEO_XIUXIU;
        } else {//语聊任务
            type = XiuxiuTaskBean.TYPE_VOICE_XIUXIU;
        }
        XiuxiuTaskBean taskBean = XiuxiuTaskBean.createXiuxiuTaskBean(isNan2Nv, mXiuxiuTitle, mEditText.getText().toString(),
                mXiuxiuBSizeEdt.getText().toString(), mImagePath, mVideoFile, mThum, mDuration, type);
        intent.putExtra(XiuxiuTaskBean.TAG,taskBean);
        setResult(RESULT_OK, intent);
        finish();
    }
}
