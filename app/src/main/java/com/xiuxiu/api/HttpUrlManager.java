package com.xiuxiu.api;


public class HttpUrlManager {
	
	public static String HOST = "http://123.56.255.19:888";
	/**登录注册接口*/
	public static String LOGIN_BY_PLAT = "loginByPlat";
	/**获取所有用户接口*/
	public static String QUERY_USER_INFO = "queryUserInfo";
	/**用户维度更新*/
	public static String UPDATE_USER_INFO = "updateUserInfo";
	/**获取七牛token*/
	public static String GET_QINIU_TOKEN = "getQiNiuToken";

	public static String commondUrl(){
		return HOST + "/user_command";
	}
}   



