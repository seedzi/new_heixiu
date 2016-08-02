package com.xiuxiu.user;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.xiuxiu.R;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.easeim.xiuxiumsg.XiuxiuSettingsConstant;

/**
 * Created by huzhi on 16-4-24.
 */
public class XiuxiuSettingsPage extends BaseActivity implements View.OnClickListener{

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,XiuxiuSettingsPage.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_heixiu_settings_layout);

        findViewById(R.id.xiuxiu_yuyin).setOnClickListener(this);
        findViewById(R.id.xiuxiu_pic).setOnClickListener(this);
        findViewById(R.id.xiuxiu_video).setOnClickListener(this);

        mYuyinPriceTv = (TextView) findViewById(R.id.xiuxiu_yuyin_price);
        mPicPriceTv = (TextView) findViewById(R.id.xiuxiu_pic_price);
        mVideoPriceTv = (TextView) findViewById(R.id.xiuxiu_video_price);

        mYuyinPriceTv.setText(XiuxiuSettingsConstant.getXiuxiuYuyinPrice()+"咻币/分钟");

        mPicPriceTv.setText(XiuxiuSettingsConstant.getXiuxiuImgPrice()+"咻币");

        mVideoPriceTv.setText(XiuxiuSettingsConstant.getXiuxiuVideoPrice()+"咻币");
    }

    private TextView mYuyinPriceTv;
    private TextView mPicPriceTv;
    private TextView mVideoPriceTv;


    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.xiuxiu_yuyin:
                showDialog("语音");
                break;
            case R.id.xiuxiu_pic:
                showDialog("照片");
                break;
            case R.id.xiuxiu_video:
                showDialog("视频");
                break;
        }

    }

    // ============================================================================================
    //　对话框
    // ============================================================================================
    private AlertDialog mAlertDialog;

    private EditText mEditText;

    public void showDialog(final String txt){
        LayoutInflater inflater = getLayoutInflater();
        ViewGroup layout = (ViewGroup) inflater.inflate(R.layout.dailog_xiuxiu_settings_layout, null);
        mEditText = (EditText) layout.findViewById(R.id.edit);
        mAlertDialog = new AlertDialog.Builder(this).setTitle("请输入"+txt +"咻羞价格").setIcon(
                android.R.drawable.ic_dialog_info).setView(
                layout).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!TextUtils.isEmpty(mEditText.getText().toString())) {
                    if (txt.equals("语音")) {
                        XiuxiuSettingsConstant.saveXiuxiuYuyinPrice(Integer.valueOf(mEditText.getText().toString()));

                        mYuyinPriceTv.setText(XiuxiuSettingsConstant.getXiuxiuYuyinPrice() + "咻币/分钟");
                    } else if (txt.equals("照片")) {
                        XiuxiuSettingsConstant.saveXiuxiuImgPrice(Integer.valueOf(mEditText.getText().toString()));

                        mPicPriceTv.setText(XiuxiuSettingsConstant.getXiuxiuImgPrice() + "咻币");
                    } else {
                        XiuxiuSettingsConstant.saveXiuxiuVideoPrice(Integer.valueOf(mEditText.getText().toString()));

                        mVideoPriceTv.setText(XiuxiuSettingsConstant.getXiuxiuVideoPrice() + "咻币");
                    }

                }
            }
        })
                .setNegativeButton("取消", null).show();
    }
}
