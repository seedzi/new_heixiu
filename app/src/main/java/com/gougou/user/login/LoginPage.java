package com.gougou.user.login;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.gougou.R;
import com.gougou.im.ImManager;
import com.gougou.main.MainActivity;
import com.gougou.utils.UiUtil;

/**
 * Created by zhihu on 16-4-17.
 */
public class LoginPage extends FragmentActivity implements View.OnClickListener{

    private ViewGroup mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_login_page);
        mRootLayout = (ViewGroup)findViewById(R.id.root_layout);
        UiUtil.findViewById(mRootLayout,R.id.qq_login_bt).setOnClickListener(this);
        UiUtil.findViewById(mRootLayout,R.id.wechat_login_bt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.qq_login_bt:
                MainActivity.startActivity(this);
                break;
            case R.id.wechat_login_bt:

                break;
        }
    }
}
