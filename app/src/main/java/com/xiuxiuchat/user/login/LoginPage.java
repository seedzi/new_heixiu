package com.xiuxiuchat.user.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.xiuxiuchat.R;
import com.xiuxiuchat.api.XiuxiuResult;
import com.xiuxiuchat.api.XiuxiuLoginResult;
import com.xiuxiuchat.base.BaseActivity;
import com.xiuxiuchat.easeim.ImManager;
import com.xiuxiuchat.main.MainActivity;
import com.xiuxiuchat.user.register.RegisterPage;
import com.xiuxiuchat.user.thirdplatform.ThirdPlatformManager;
import com.xiuxiuchat.utils.UiUtil;
import com.xiuxiuchat.utils.XiuxiuUtils;

/**
 * Created by zhihu on 16-4-17.
 */
public class LoginPage extends BaseActivity implements View.OnClickListener{

    private static String TAG = "LoginPage";

    public static void startActivity(FragmentActivity ac){
        Intent intent = new Intent(ac,LoginPage.class);
        ac.startActivity(intent);
        ac.overridePendingTransition(R.anim.activity_slid_in_from_right, R.anim.activity_slid_out_no_change);
    }

    private ViewGroup mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_login_page);
        mRootLayout = (ViewGroup)findViewById(R.id.root_layout);
        UiUtil.findViewById(mRootLayout,R.id.qq_login_bt).setOnClickListener(this);
        UiUtil.findViewById(mRootLayout,R.id.wechat_login_bt).setOnClickListener(this);
        init();
    }

    private void init(){
        ThirdPlatformManager.getInstance().setActivity(this);
    }

    @Override
    public void onClick(View v){
        int id = v.getId();
        switch (id){
            case R.id.qq_login_bt:
                finish();
                enterRegister();
//                ThirdPlatformManager.getInstance().thirdLoginQQ();
                break;
            case R.id.wechat_login_bt:
                ThirdPlatformManager.getInstance().thirdLoginWechat();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LoginUserDataEditPage.REQUEST_CODE ){
            if(resultCode == RESULT_OK){
                ImManager.getInstance().login(XiuxiuLoginResult.getInstance().getXiuxiu_id(),
                        XiuxiuLoginResult.getInstance().getPasswordForYX(),
                        new Runnable() {
                            @Override
                            public void run() {
                                XiuxiuUtils.initAndEnterMainPage(LoginPage.this);
                            }
                        });
            }
        }else{
            ThirdPlatformManager.getInstance().onActivityResult4ThirdPlatform(requestCode, requestCode, data);
        }
    }

    // ========================================= Volley  login ================================================//
    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            android.util.Log.d("ccc","response = " + response);
            XiuxiuResult res = gson.fromJson(response, XiuxiuLoginResult.class);
            if (res.isSuccess()) {
                Toast.makeText(getApplication(),"登录成功",0).show();
                finish();
                MainActivity.startActivity(LoginPage.this);
            }
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };

    private void enterRegister(){
        RegisterPage.startActivity(LoginPage.this);
    }
}
