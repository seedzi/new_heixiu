package com.xiuxiu.user.register;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.R;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuAllUserResult;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuUserInfoResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.easeim.ImManager;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.user.login.LoginUserDataEditPage;
import com.xiuxiu.utils.Md5Util;
import com.xiuxiu.utils.UiUtil;
import com.xiuxiu.utils.XiuxiuUtils;

/**
 * Created by zhihu on 16-4-17.
 */
public class RegisterPage extends BaseActivity implements View.OnClickListener{

    private static String TAG = RegisterPage.class.getSimpleName();

    private EditText mUserNameEdt;

    private EditText mTokenEdt;


    public static void startActivity(Context context){
        Intent intent = new Intent(context,RegisterPage.class);
        context.startActivity(intent);
    }

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

        mUserNameEdt =  UiUtil.findEditViewById(mRootLayout, R.id.username);
        mTokenEdt = UiUtil.findEditViewById(mRootLayout, R.id.token);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_bt:
                login();
                break;

        }
    }


    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            android.util.Log.d(TAG,"response = " + response);
            Gson gson = new Gson();
            final XiuxiuLoginResult res = gson.fromJson(response, XiuxiuLoginResult.class);

            if (res.isSuccess()) {
                android.util.Log.d(TAG,"res = " + res);
                XiuxiuLoginResult.save(res);
                ImManager.getInstance().login(res.getXiuxiu_id(), res.getPasswordForYX(), new Runnable() {
                    @Override
                    public void run() {
                        android.util.Log.d(TAG, "环信完成");
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                XiuxiuUtils.onAppStart(getApplicationContext());
                                XiuxiuApplication.getInstance().getUIHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        XiuxiuUserInfoResult xiuxiuUserQueryResult = XiuxiuUserInfoResult.getInstance();
                                        android.util.Log.d(TAG, "XiuxiuUserInfoResult.getInstance() = " + xiuxiuUserQueryResult);
                                        boolean isEnterFirstLoginPage = false;
                                        if(xiuxiuUserQueryResult!=null ){
                                            if(TextUtils.isEmpty(xiuxiuUserQueryResult.getSex())
                                                    || "unknow".equals(xiuxiuUserQueryResult.getSex())){
                                                android.util.Log.d(TAG,"xiuxiuUserQueryResult.getSex() = " + xiuxiuUserQueryResult.getSex());
                                                isEnterFirstLoginPage = true;
                                            }
                                        }
                                        LoginUserDataEditPage.startActivity(RegisterPage.this);
                                        /*
                                        if(res.getIsFirstLogin()|| isEnterFirstLoginPage){
                                            LoginUserDataEditPage.startActivity(RegisterPage.this);
                                        }else {
                                            finish();
                                            MainActivity.startActivity(RegisterPage.this);
                                        }*/
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.d(TAG,"error = "+ error.getMessage());
        }
    };

    /**
     * 登录
     */
    private void login() {
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getTopicListUrl(), mRefreshListener, mRefreshErroListener));
    }
    private String getTopicListUrl() {
        return Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.LOGIN_BY_PLAT)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("weixin_token", mTokenEdt.getText().toString())
                .appendQueryParameter("xiuxiu_name", mUserNameEdt.getText().toString())
//                .appendQueryParameter("attrs", String.valueOf("huzhi"))
                .build().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == LoginUserDataEditPage.REQUEST_CODE && resultCode == RESULT_OK){
            finish();
            MainActivity.startActivity(this);
        }
    }
}
