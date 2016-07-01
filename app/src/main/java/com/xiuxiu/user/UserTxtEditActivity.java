package com.xiuxiu.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.xiuxiu.R;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.utils.ToastUtil;

/**
 * Created by huzhi on 16-5-15.
 */
public class UserTxtEditActivity extends FragmentActivity implements View.OnClickListener{

    public static void startActivity(FragmentActivity ac,String value){
        Intent intent = new Intent(ac,UserTxtEditActivity.class);
        intent.putExtra("txt",value);
        ac.startActivityForResult(intent, REQUEST_CODE);
    }


    public static void startActivity(FragmentActivity ac,String value,int code){
        Intent intent = new Intent(ac,UserTxtEditActivity.class);
        intent.putExtra("txt",value);
        ac.startActivityForResult(intent, code);
    }

    public static int REQUEST_CODE = 103;

    public static int REQUEST_CODE_2 = 104;

    private EditText mEdit;

    private TextView mTitleTv;

    public static final String TXT_KEY = "txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_txt_edit_layout);
        setupViews();
    }

    private void setupViews(){
        mEdit = (EditText)findViewById(R.id.edit);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.ok).setOnClickListener(this);
        String txt = getIntent().getStringExtra("txt");
        if(!TextUtils.isEmpty(txt)){
            mEdit.setText(txt);
        }
        mTitleTv = (TextView) findViewById(R.id.title);
        if(!TextUtils.isEmpty(XiuxiuUserInfoResult.getInstance().getXiuxiu_name())) {
            mTitleTv.setText(XiuxiuUserInfoResult.getInstance().getXiuxiu_name());
        }else{
            mTitleTv.setText(XiuxiuUserInfoResult.getInstance().getXiuxiu_id());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.back:
                finish();
                break;
            case R.id.ok:
                String txt = mEdit.getText().toString();
                if(TextUtils.isEmpty(txt)){
                    ToastUtil.showMessage(UserTxtEditActivity.this,"内容不能为空!");
                    return;
                }
                Intent intent=new Intent();
                intent.putExtra(TXT_KEY, txt);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}
