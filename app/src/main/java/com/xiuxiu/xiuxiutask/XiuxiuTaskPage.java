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
import com.xiuxiu.utils.ToastUtil;

import java.io.File;

/**
 * Created by huzhi on 16-5-9.
 */
public class XiuxiuTaskPage extends BaseActivity implements View.OnClickListener{

    public static void startActivity4Result(Fragment fra,int requestCode){
        Intent intent = new Intent(fra.getContext(),XiuxiuTaskPage.class);
        fra.startActivityForResult(intent, requestCode);
//        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
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
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.video:
                mEditText.setHint("填写你对索要视频的期望和要求...(智能查看三次)");
                mXiuxiuTitle = XIUXIU_TITLE_VIDEO_TXT;
                break;
            case R.id.pic:
                mEditText.setHint("填写你对索要图片的期望和要求...(智能查看三次)");
                mXiuxiuTitle = XIUXIU_TITLE_IMG_TXT;
                break;
            case R.id.voice:
                mEditText.setHint("填写你对索要声音的期望和要求...(智能查看三次)");
                mXiuxiuTitle = XIUXIU_TITLE_VOICE_TXT;
                break;
            case R.id.xiuxiu_bt:
                if(TextUtils.isEmpty(mEditText.getText().toString())){
                    ToastUtil.showMessage(XiuxiuTaskPage.this,"请填写咻咻要求！");
                    return;
                }
                if(XiuxiuUserInfoResult.isMale(XiuxiuUserInfoResult.getInstance().getSex())){//男性
                    Intent intent = new Intent();
                    intent.putExtra(XiuxiuTaskBean.TITLE_KEY,mXiuxiuTitle);
                    intent.putExtra(XiuxiuTaskBean.CONTENT_KEY,mEditText.getText().toString());
                    intent.putExtra(XiuxiuTaskBean.XIUXIUB_KEY,mXiuxiuBSizeEdt.getText().toString());
                    intent.putExtra(XiuxiuTaskBean.TYPE_KEY,XiuxiuTaskBean.TYPE_ASK_XIUXIU);
                    setResult(RESULT_OK, intent);
                    finish();
                }else{//女性
                    if(mXiuxiuTitle.equals(XIUXIU_TITLE_IMG_TXT)){
                        selectPicFromCamera();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CAMERA) { // 发送照片
                if (cameraFile != null && cameraFile.exists()){
                    Intent intent = new Intent();
                    android.util.Log.d("12345",cameraFile.getAbsolutePath());
                    intent.putExtra(XiuxiuTaskBean.TITLE_KEY, mXiuxiuTitle);
                    intent.putExtra(XiuxiuTaskBean.CONTENT_KEY,mEditText.getText().toString());
                    intent.putExtra(XiuxiuTaskBean.XIUXIUB_KEY,mXiuxiuBSizeEdt.getText().toString());
                    intent.putExtra(XiuxiuTaskBean.TYPE_KEY,XiuxiuTaskBean.TYPE_IMAGE_XIUXIU);
                    intent.putExtra(XiuxiuTaskBean.FILE_PATH_KEY,cameraFile.getAbsolutePath());
                    setResult(RESULT_OK, intent);
                    finish();
                }

            }
        }
    }

    protected File cameraFile;
    protected static final int REQUEST_CODE_CAMERA = 2;
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
}
