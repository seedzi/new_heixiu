package com.xiuxiuchat.api;

import com.xiuxiuchat.SharePreferenceWrap;

/**
 * Created by huzhi on 16-5-8.
 */
public class XiuxiuLoginResult extends XiuxiuResult{

    private static final String TAG = XiuxiuLoginResult.class.getSimpleName();

    public static final String SHARE_PREFERENCE_NAME = "xiuxiuloginresult";

    public static final String XIUXIU_ID = "xiuxiu_id";

    public static final String PASSWORD_FOR_YX = "passwordForYX";

    public static final String COOKIE = "cookie";

    /**
     * xiuxiu_id:环信登录账号id
     * passwordForYX:环信登录密码
     */
    public String passwordForYX;
    /**咻咻id　环信登录id*/
    public String xiuxiu_id;
    /**用户cookie*/
    public String cookie;

    public boolean isFirstlogin;

    public String getPasswordForYX(){
        return passwordForYX;
    }

    public String getXiuxiu_id(){
        return xiuxiu_id;
    }

    public boolean getIsFirstLogin(){
        return isFirstlogin;
    }

    public String getCookie() {
        return cookie;
    }

    private static XiuxiuLoginResult mXiuxiuLoginResult;

    public static XiuxiuLoginResult getInstance(){
        if(mXiuxiuLoginResult == null){
            mXiuxiuLoginResult = getFromShareP();
            android.util.Log.d(TAG,"mXiuxiuLoginResult = " + mXiuxiuLoginResult);
        }
        return mXiuxiuLoginResult;
    }

    @Override
    public String toString() {
        return "[passwordForYX = " + passwordForYX
                + ",xiuxiu_id = " + xiuxiu_id
                + ",cookie = " + cookie
                +"]";
    }

    /**
     * @param user
     */
    public static void save(XiuxiuLoginResult user){

        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putString(XIUXIU_ID, user.getXiuxiu_id());
        sharePreferenceWrap.putString(PASSWORD_FOR_YX, user.getPasswordForYX());
        sharePreferenceWrap.putString(COOKIE, user.getCookie());

        mXiuxiuLoginResult = getFromShareP();
        android.util.Log.d(TAG,"mXiuxiuLoginResult = " + mXiuxiuLoginResult);
    }

    /**
     * @return
     */
    private static XiuxiuLoginResult getFromShareP(){
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        XiuxiuLoginResult user = new XiuxiuLoginResult();
        user.xiuxiu_id = sharePreferenceWrap.getString(XIUXIU_ID,"");
        user.passwordForYX = sharePreferenceWrap.getString(PASSWORD_FOR_YX,"");
        user.cookie = sharePreferenceWrap.getString(COOKIE,"");
        return user;
    }

    public static void clear(){
        mXiuxiuLoginResult = null;
        SharePreferenceWrap sharePreferenceWrap = new SharePreferenceWrap(SHARE_PREFERENCE_NAME);
        sharePreferenceWrap.putString(XIUXIU_ID, "");
        sharePreferenceWrap.putString(PASSWORD_FOR_YX, "");
        sharePreferenceWrap.putString(COOKIE, "");
    }
}
