package com.xiuxiu.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.xiuxiu.R;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.api.HttpUrlManager;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.api.XiuxiuTimsResult;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.utils.Md5Util;
import com.xiuxiu.utils.ToastUtil;
import com.xiuxiu.utils.XiuxiuUtils;

/**
 * Created by huzhi on 16-4-20.
 */
public class PullBlackReportActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "PullBlackReportActivity";

    public static void startActivity(Context context,String toXiuxiuId){
        Intent intent = new Intent(context,PullBlackReportActivity.class);
        intent.putExtra("to_xiuxiu_id",toXiuxiuId);
        context.startActivity(intent);
    }

    private String toXiuxiuId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //设置无标题
        setContentView(R.layout.activity_pull_black_report_layout);
        toXiuxiuId = getIntent().getStringExtra("to_xiuxiu_id");
        findViewById(R.id.commit).setOnClickListener(this);
        android.util.Log.d(TAG,"to_xiuxiu_id = " + toXiuxiuId);
    }



    // ============================================================================================
    // ============================================================================================

    private Response.Listener<String> mCallBackListener = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            android.util.Log.d(TAG," response = " + response);
            XiuxiuUtils.dismisslProgressDialog();
            Gson gson = new Gson();
            XiuxiuTimsResult res = gson.fromJson(response, XiuxiuTimsResult.class);
            if(res!=null&&res.isSuccess()){
                try {
                    EMClient.getInstance().contactManager().deleteContact(toXiuxiuId);
                    EMClient.getInstance().contactManager().addUserToBlackList(toXiuxiuId, false);
                    ToastUtil.showMessage(PullBlackReportActivity.this,"拉黑举报成功!");
                } catch (Exception e) {
                    android.util.Log.d(TAG," e = " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    };
    private Response.ErrorListener mCallBackErrorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            android.util.Log.d(TAG, "error = " + error);
            XiuxiuUtils.dismisslProgressDialog();
        }
    };

    private void complain() {
        XiuxiuUtils.showProgressDialog(PullBlackReportActivity.this,"加载中...");
        XiuxiuApplication.getInstance().getQueue()
                .add(new StringRequest(getComplainUrl(), mCallBackListener, mCallBackErrorListener));
    }
    private String getComplainUrl() {
        String url = Uri.parse(HttpUrlManager.commondUrl()).buildUpon()
                .appendQueryParameter("m", HttpUrlManager.COMPLAIN_USER)
                .appendQueryParameter("password", Md5Util.md5())
                .appendQueryParameter("user_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("xiuxiu_id", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("from", XiuxiuLoginResult.getInstance().getXiuxiu_id())
                .appendQueryParameter("spam", "1")
                .appendQueryParameter("to", toXiuxiuId)
                .appendQueryParameter("cookie", XiuxiuLoginResult.getInstance().getCookie())
                .build().toString();
        android.util.Log.d(TAG, "url = " + url);
        return url;
    }

    @Override
    public void onClick(View v) {
        complain();
    }
}
