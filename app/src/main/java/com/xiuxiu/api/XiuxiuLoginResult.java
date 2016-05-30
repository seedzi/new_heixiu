package com.xiuxiu.api;

import com.xiuxiu.SharePreferenceWrap;

/**
 * Created by huzhi on 16-5-8.
 */
public class XiuxiuLoginResult extends XiuxiuResult{

    public static final String SHARE_PREFERENCE_NAME = "xiuxiuloginresult";

    public static final String XIUXIU_ID = "xiuxiu_id";

    public static final String PASSWORD_FOR_YX = "passwordForYX";

    /**
     * xiuxiu_id:环信登录账号id
     * passwordForYX:环信登录密码
     */
    public String passwordForYX;
    /**咻咻id　环信登录id*/
    public String xiuxiu_id;

    public String getPasswordForYX(){
        return passwordForYX;
    }

    public String getXiuxiu_id(){
        return xiuxiu_id;
    }


    private static XiuxiuLoginResult mXiuxiuLoginResult;

    public static XiuxiuLoginResult getInstance(){
        if(mXiuxiuLoginResult == null){
            mXiuxiuLoginResult = getFromShareP();
        }
        return mXiuxiuLoginResult;
    }

    @Override
    public String toString() {
        return "[passwordForYX = " + passwordForYX
                + ",xiuxiu_id = " + xiuxiu_id
                +"]";
    }

    /**
     * @param user
     */
    public static void save(XiuxiuLoginResult user){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putString(XIUXIU_ID, user.getXiuxiu_id());
        sharePreferenceWrap.putString(PASSWORD_FOR_YX, user.getPasswordForYX());
    }

    /**
     * @return
     */
    public static XiuxiuLoginResult getFromShareP(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        XiuxiuLoginResult user = new XiuxiuLoginResult();
        user.xiuxiu_id = sharePreferenceWrap.getString(XIUXIU_ID,"");
        user.passwordForYX = sharePreferenceWrap.getString(PASSWORD_FOR_YX,"");
        return user;
    }

    public static void clear(){
        mXiuxiuLoginResult = null;
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putString(XIUXIU_ID, "");
        sharePreferenceWrap.putString(PASSWORD_FOR_YX, "");
    }
}
