package com.xiuxiu.api;


import android.net.Uri;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.RequestFuture;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.xiuxiu.XiuxiuApplication;
import com.xiuxiu.utils.Md5Util;

public class HttpUrlManager {
	
	public static String HOST = "http://123.56.255.19:888";
	/**七牛HOST*/
	public static String QI_NIU_HOST = "http://o8cimqbdp.bkt.clouddn.com/";
	/**获取用户好友*/
	public static String GET_FRIENDS = "getFriends";
	/**互加好友*/
	public static String BE_FRIENDS = "beFriends";
	/**获取所有用户接口*/
	public static String QUERY_USER_INFO = "queryUserInfo";
	/**用户维度更新*/
	public static String UPDATE_USER_INFO = "updateUserInfo";
	/**获取七牛token*/
	public static String GET_QINIU_TOKEN = "getQiNiuToken";
	/**登录注册接口*/
	public static String LOGIN_BY_PLAT = "loginByPlat";
	/**批量查询用户信息*/
	public static String QUERY_BATCH_USERINFOS = "queryBatchUserInfos";
	/**活跃用户查询*/
	public static String QUERY_ACTIVE_USER = "queryActiveUser";
	/**获取咻羞招呼时间*/
	public static String GET_XX_TIMES = "getXXtimes";
	/**活跃用户更新*/
	public static String UPDATE_ACTIVE_USER = "updateActiveUser";
	/**解散好友*/
	public static String RELEASE_FRIENDS_RELATION = "releaseFriendsRelation";
	/**微信支付*/
	public static String WEIXIN_PAY = "getWXPayParam";
	/**加载支付配置*/
	public static String GET_PAY_CONF = "getPayConf";


	public static String commondUrl(){
		return HOST + "/user_command";
	}

	public static String weixinPayUrl(){
		return "http://123.56.255.19:999/weixin_pay";
	}


}   



