package com.xiuxiu.xiuxiutask;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.qupai.QuPaiManager;
import com.xiuxiu.qupai.RecordResult;
import com.xiuxiu.utils.ToastUtil;

/**
 * Created by huzhi on 16-7-26.
 */
public class XiuxiuQupaiPage extends BaseActivity{

    private static final String TAG = "XiuxiuQupaiPage";

    public static void startActivity4Result(Fragment fra,int requestCode,String xiuxiub){
        Intent intent = new Intent(fra.getActivity().getApplication(),XiuxiuQupaiPage.class);
        intent.putExtra("xiuxiub", xiuxiub);
        fra.startActivityForResult(intent, requestCode);
    }

    /**
     * 用户点击同意后记录的咻咻B
     */
    private String mCurrentCostXiuxiuB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        android.util.Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        QuPaiManager.getInstance().showRecordPage(XiuxiuQupaiPage.this, 101);
        mCurrentCostXiuxiuB = getIntent().getStringExtra("xiuxiub");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            String costXiuxiuB = mCurrentCostXiuxiuB;
            if(TextUtils.isEmpty(costXiuxiuB)){
                costXiuxiuB = "20";
            }
            RecordResult result =new RecordResult(data);
            String videoFile = "";
            String thum = "";
            String duration = "";
            try {
                videoFile = result.getPath();
                thum = result.getThumbnail()[0];
                duration = String.valueOf(result.getDuration()/1000000);
            }catch (Exception e){}
            XiuxiuTaskBean bean = XiuxiuTaskBean.createXiuxiuTaskBean(false,"咻羞视频","咻羞视频",costXiuxiuB,"",
                    videoFile,thum,duration,XiuxiuTaskBean.TYPE_VIDEO_XIUXIU);
            Intent intent = new Intent();
            intent.putExtra(XiuxiuTaskBean.TAG, bean);
            setResult(RESULT_OK,intent);
            finish();
        }
    }
}
