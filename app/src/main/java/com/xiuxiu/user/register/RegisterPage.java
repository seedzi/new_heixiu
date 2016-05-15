package com.xiuxiu.user.register;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.xiuxiu.GougouApplication;
import com.xiuxiu.R;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuResult;
import com.xiuxiu.api.XiuxiuUser;
import com.xiuxiu.im.ImManager;
import com.xiuxiu.main.MainActivity;
import com.xiuxiu.utils.Md5Util;
import com.xiuxiu.utils.UiUtil;

/**
 * Created by zhihu on 16-4-17.
 */
public class RegisterPage extends FragmentActivity implements View.OnClickListener{


    private EditText mUserNameEdt;

    private EditText mPasswordEdt;


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
        mPasswordEdt = UiUtil.findEditViewById(mRootLayout, R.id.password);
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.login_bt:
//                ImManager.getInstance().register("huzhi","123456");
                login();
                break;

        }
    }


    private Response.Listener<String> mRefreshListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            Gson gson = new Gson();
            android.util.Log.d("ccc","response = " + response);
            XiuxiuResult res = gson.fromJson(response, XiuxiuUser.class);
            if (res.isSuccess()) {
                Toast.makeText(getApplication(), "登录成功", 0).show();
                finish();
                MainActivity.startActivity(RegisterPage.this);
            }
        }
    };
    private Response.ErrorListener mRefreshErroListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
        }
    };

    /**
     * 登录
     */
    private void login() {
        GougouApplication.getInstance().getQueue()
                .add(new StringRequest(getTopicListUrl(), mRefreshListener, mRefreshErroListener));
    }
    private String getTopicListUrl() {
        return Uri.parse(HttpUrlManager.loginUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.LOGIN_BY_PLAT)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("xiuxiu_id", String.valueOf("1234560"))
                .appendQueryParameter("xiuxiu_name", String.valueOf("huzhi1"))
                .appendQueryParameter("attrs", String.valueOf("huzhi"))
                .build().toString();
    }
}
