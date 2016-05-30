package com.xiuxiu.api;

import com.xiuxiu.SharePreferenceWrap;

/**
 * Created by huzhi on 16-5-25.
 */
public class XiuxiuUserInfoResult {

    public static final String SHARE_PREFERENCE_NAME = "xiuxiuuerinforesult";

    public static final String XIUXIU_ID = "xiuxiu_id";

    public static final String XIUXIU_NAME = "xiuxiu_name";

    public static final String SIGN = "sign";

    public static final String AGE = "age";

    public static final String CITY = "city";

    public static final String CHARM = "charm";

    private static XiuxiuUserInfoResult mInstance;

    private XiuxiuUserInfoResult(){}

    public static XiuxiuUserInfoResult getInstance(){
        if(mInstance == null ){
            mInstance = getFromShareP();
        }
        return mInstance;
    }

    private String sign;

    private String age;

    private String sex;

    private String city;

    private String xiuxiu_id;

    private String xiuxiu_name;

    private String weixin_token;

    private int charm;

    private int fortune;

    public int getCharm() {
        return charm;
    }

    public int getFortune() {
        return fortune;
    }

    public void setCharm(int charm) {
        this.charm = charm;
    }

    public void setFortune(int fortune) {
        this.fortune = fortune;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setXiuxiu_id(String xiuxiu_id) {
        this.xiuxiu_id = xiuxiu_id;
    }

    public void setXiuxiu_name(String xiuxiu_name) {
        this.xiuxiu_name = xiuxiu_name;
    }

    public void setWeixin_token(String weixin_token) {
        this.weixin_token = weixin_token;
    }

    public String getSign() {
        return sign;
    }

    public String getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getCity() {
        return city;
    }

    public String getXiuxiu_id() {
        return xiuxiu_id;
    }

    public String getXiuxiu_name() {
        return xiuxiu_name;
    }

    public String getWeixin_token() {
        return weixin_token;
    }

    /*
    "sign": "heh",
            "attrs": null,
            "xiuxiu_id": "13180",
            "xiuxiu_name": "huzhi",
            "password": null,
            "age": null,
            "sex": null,
            "birthday": null,
            "register_time": null,
            "fortune": null,
            "charm": null,
            "get_gift": null,
            "send_gift": null,
            "city": null,
            "voice": null,
            "pic": null,
            "bond_status": null,
            "mobile": null,
            "invite_code": null,
            "beinvite_code": null,
            "weixin_token": "huzhi",
            "qq_token": null,
            "friend_ids": null,
            "attrs_1": null,
            "attrs_2": null
            */

    /**
     * @param user
     */
    public static void save(XiuxiuUserInfoResult user){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putString(XIUXIU_ID, user.getXiuxiu_id());
        sharePreferenceWrap.putString(XIUXIU_NAME, user.getXiuxiu_name());
        sharePreferenceWrap.putString(SIGN, user.getSign());
        sharePreferenceWrap.putString(AGE, user.getAge());
        sharePreferenceWrap.putString(CITY, user.getCity());
        sharePreferenceWrap.putInt(CHARM, user.getCharm());
    }

    /**
     * @return
     */
    public static XiuxiuUserInfoResult getFromShareP(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        XiuxiuUserInfoResult user = new XiuxiuUserInfoResult();
        user.setXiuxiu_id(sharePreferenceWrap.getString(XIUXIU_ID, ""));
        user.setXiuxiu_name(sharePreferenceWrap.getString(XIUXIU_NAME, ""));
        user.setSign(sharePreferenceWrap.getString(SIGN, ""));
        user.setCity(sharePreferenceWrap.getString(CITY, ""));
        user.setAge(sharePreferenceWrap.getString(AGE, ""));
        user.setCharm(sharePreferenceWrap.getInt(CHARM, 1));
        return user;
    }

    public static void clear(){
        mInstance = null;
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putString(XIUXIU_ID, "");
        sharePreferenceWrap.putString(XIUXIU_NAME, "");
        sharePreferenceWrap.putString(SIGN,"");
        sharePreferenceWrap.putString(AGE,"");
        sharePreferenceWrap.putString(CITY,"");
        sharePreferenceWrap.putInt(CHARM,1);
    }
}
