package com.xiuxiu.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.xiuxiu.R;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.easeim.ImHelper;

/**
 * Created by huzhi on 16-6-21.
 */
public class DialogActivity extends BaseActivity implements View.OnClickListener{

    public static final int OPERATION_PULL_BACK = 101;

    public static final int OPERATION_DELETE_FRIEND = 102;

    /*
    public static void startActivity(FragmentActivity ac,int requestCode){
        Intent intent = new Intent(ac,DialogActivity.class);
        ac.startActivityForResult(intent, requestCode);
    }

    public static void startActivity(FragmentActivity ac,int requestCode,boolean isFriend){
        Intent intent = new Intent(ac,DialogActivity.class);
        intent.putExtra("isFriend",isFriend);
        ac.startActivityForResult(intent, requestCode);
    }
    */

    public static void startActivity(FragmentActivity ac,int requestCode,String xiuxiuId){
        Intent intent = new Intent(ac,DialogActivity.class);
        intent.putExtra("xiuxiu_id",xiuxiuId);
        ac.startActivityForResult(intent, requestCode);
    }



    private boolean isFriend = false;

    private String xiuxiuId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_dialog_layout);
//        isFriend = getIntent().getBooleanExtra("isFriend", false);
        xiuxiuId = getIntent().getStringExtra("xiuxiu_id");
        if(ImHelper.getInstance().getContactList().get(xiuxiuId)!=null){
            isFriend = true;
        }
        setupViews();
    }

    private void setupViews(){
        findViewById(R.id.pull_black).setOnClickListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.delete_friend).setOnClickListener(this);
        if(!isFriend){
            findViewById(R.id.delete_friend).setVisibility(View.GONE);
            findViewById(R.id.delete_friend_line).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        switch (v.getId()){
            case R.id.pull_black:
                intent.putExtra("type",OPERATION_PULL_BACK);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.delete_friend:
                intent.putExtra("type",OPERATION_DELETE_FRIEND);
                setResult(RESULT_OK,intent);
                finish();
                break;
            case R.id.cancel:
                finish();
                break;
        }
    }
}