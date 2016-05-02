package com.gougou.user.register;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.gougou.R;
import com.gougou.im.ImManager;
import com.gougou.utils.UiUtil;

/**
 * Created by zhihu on 16-4-17.
 */
public class RegisterPage extends FragmentActivity implements View.OnClickListener{

    private ViewGroup mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_register_page);
        setupViews();
    }

    private void setupViews(){
        mRootLayout = (ViewGroup)findViewById(R.id.root_layout);
        UiUtil.findViewById(mRootLayout,R.id.login_bt).setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_bt:
                ImManager.getInstance().register("huzhi","123456");
                break;

        }
    }
}
