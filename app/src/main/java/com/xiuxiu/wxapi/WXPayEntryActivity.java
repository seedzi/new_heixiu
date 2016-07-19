package com.xiuxiu.wxapi;




import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiuxiu.base.BaseActivity;
import com.xiuxiu.payment.WeiXinPayManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler{
	
	private static final String TAG = "WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.pay_result);
        android.util.Log.d(TAG, "appid = " + WeiXinPayManager.getInstance().getAppId());
		api = WXAPIFactory.createWXAPI(this, WeiXinPayManager.getInstance().getAppId());

        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		android.util.Log.d(TAG, "req = " + req);
	}

	@Override
	public void onResp(BaseResp resp) {
//		Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		android.util.Log.d(TAG, "resp.errStr = " + resp.errStr + ", code = " +resp.errCode);
		/*
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("提示");
			builder.setMessage(getString(R.string.pay_result_callback_msg, resp.errStr +";code=" + String.valueOf(resp.errCode)));
			builder.show();
		}*/
		String msg = "支付失败";
		switch (resp.errCode) {
        case 0:
            msg = "支付成功";
            break;
        case -1:
            msg = "支付失败";
            
            break;
        case -2:
            msg = "已取消支付";
            break;
        default:
            break;
        }
		 if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
		     Toast.makeText(getApplicationContext(), msg, 0).show();
		 }
		 finish();
	}
	
}