package com.xiuxiu.payment;


import android.app.Activity;
import android.support.v4.app.FragmentActivity;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xiuxiu.api.XiuxiuWechatBean;

public class WeiXinPayManager {

	private static String TAG = WeiXinPayManager.class.getSimpleName();

	private static WeiXinPayManager mInstance;
	
	public static WeiXinPayManager getInstance(){
		if(mInstance == null)
			mInstance = new WeiXinPayManager();
		return mInstance;
	}
	
	private WeiXinPayManager(){}
	
	private String appId;
	
	public String getAppId(){
		return appId;
	}
	
	public void pay(XiuxiuWechatBean wechatBean,FragmentActivity ac){
		if(wechatBean == null || ac == null){
			return;
		}

		final IWXAPI msgApi = WXAPIFactory.createWXAPI(ac, wechatBean.appid,false);

//		final IWXAPI msgApi = WXAPIFactory.createWXAPI(ac, null);

		boolean isPaySupported = msgApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
		android.util.Log.d(TAG,"isPaySupported = " + isPaySupported);

		msgApi.registerApp(wechatBean.appid);
		PayReq	req = new PayReq();
		appId =  wechatBean.appid;
		req.appId = wechatBean.appid;
		req.partnerId = wechatBean.partnerid;
		req.prepayId = wechatBean.prepayid;
		req.packageValue = wechatBean.package_info;
		req.nonceStr = wechatBean.noncestr;
		req.timeStamp = wechatBean.timestamp;
		req.sign = wechatBean.sign;

		android.util.Log.d(TAG,
				"req.appId = " + req.appId
				+",req.partnerId = " + req.partnerId
				+",req.prepayId  = " + req.prepayId
				+",req.packageValue = " + req.packageValue
				+",req.nonceStr = " + req.nonceStr
				+",req.timestamp  = " + req.timeStamp
				+",req.sign = " + 	req.sign
		);

		android.util.Log.d(TAG,"req = " + req);
		msgApi.sendReq(req);
		android.util.Log.d(TAG, "wechatBean = " + wechatBean);

	}
	

}
