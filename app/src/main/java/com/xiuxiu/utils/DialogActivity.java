package com.xiuxiu.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;

import com.xiuxiu.R;

/**
 * Created by huzhi on 16-6-21.
 */
public class DialogActivity extends FragmentActivity implements View.OnClickListener{

    public static void startActivity(FragmentActivity ac,int requestCode){
        Intent intent = new Intent(ac,DialogActivity.class);
        ac.startActivityForResult(intent, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_dialog_layout);
        setupViews();
    }

    private void setupViews(){
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.delete_friend).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                finish();
                break;
            case R.id.delete_friend:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }
}
