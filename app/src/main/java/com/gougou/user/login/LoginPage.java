package com.gougou.user.login;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.gougou.R;
import com.gougou.im.ImManager;
import com.gougou.utils.UiUtil;

/**
 * Created by zhihu on 16-4-17.
 */
public class LoginPage extends FragmentActivity implements View.OnClickListener{

    private ViewGroup mRootLayout;

    private EditText mUserNameEt;

    private EditText mPasswordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        mRootLayout = (ViewGroup)findViewById(R.id.root_layout);
        UiUtil.findViewById(mRootLayout,R.id.login_bt).setOnClickListener(this);
        mUserNameEt = (EditText)UiUtil.findViewById(mRootLayout, R.id.user_name);
        mPasswordEt = (EditText)UiUtil.findViewById(mRootLayout, R.id.password);
    }

    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.login_bt:
                String username = mUserNameEt.getText().toString();
                String password = mPasswordEt.getText().toString();
                ImManager.getInstance().login(username, password, new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),"登陆成功", 0).show();
                    }
                });
                break;
        }
    }
}
