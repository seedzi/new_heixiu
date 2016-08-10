package com.xiuxiu.api;



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
	/**获取咻羞招呼次数*/
	public static String GET_XX_TIMES = "getXXtimes";
	/**消耗咻羞次数*/
	public static String COST_CALL_LIMIT_BY_TYPE = "costCallLimitByType";
	/**活跃用户更新*/
	public static String UPDATE_ACTIVE_USER = "updateActiveUser";
	/**解散好友*/
	public static String RELEASE_FRIENDS_RELATION = "releaseFriendsRelation";
	/**微信支付*/
	public static String WEIXIN_PAY = "getWXPayParam";
	/**加载支付配置*/
	public static String GET_PAY_CONF = "getPayConf";
	/**根据用户id得到用户两个钱包的咻咻钱币*/
	public static String GET_USER_XIUXIU_COIN = "getUserXiuXiuCoin";
	/**扣除用户咻咻币*/
	public static String COST_USER_COIN = "costUserCoin";
	/**一个用户向另一个用户付费*/
	public static String TRANSFER_COIN = "transferCoin";
	/**发送礼物*/
	public static String SEND_GIFT = "sendGift";
	/**查询财富排行榜*/
	public static String QUERY_FORTUNR_USER = "queryFortuneUser";
	/**查询魅力排行榜*/
	public static String QUERY_CHARM_USER = "queryCharmUser";
	/**投诉用户*/
	public static String COMPLAIN_USER = "complainUser";
	/**分页查看被投诉用户与标签理由*/
	public static String SEARCH_BECOMPLAIN_USERS = "searchUserLables";
	/**获取礼物列表*/
	public static String GET_GIFTS_DISPATH = "getGiftsDispath";

	public static String commondUrl(){
		return HOST + "/user_command";
	}

	public static String weixinPayUrl(){
		return "http://123.56.255.19:999/weixin_pay";
	}


}   



