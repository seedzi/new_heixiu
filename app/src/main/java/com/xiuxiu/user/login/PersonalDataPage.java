package com.xiuxiu.user.login;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.xiuxiu.R;
import com.xiuxiu.base.BaseActivity;

/**
 * Created by huzhi on 16-4-27.
 */
public class PersonalDataPage extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_personal_data_page);
    }
}
