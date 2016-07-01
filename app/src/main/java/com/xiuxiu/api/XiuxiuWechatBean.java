package com.xiuxiu.api;

/**
 * Created by huzhi on 16-6-23.
 */
public class XiuxiuWechatBean {

//    req.appId = wechatBean.getAppid();
//    req.partnerid = wechatBean.getPartnerid();
//    req.prepayid = wechatBean.getPrepayid();
//    req.packagevalue = wechatBean.getPackageTxt();
//    req.noncestr = wechatBean.getNoncestr();
//    req.timestamp = wechatBean.getTimestamp();
//    req.sign = wechatBean.getSign();

    public String appid;

    public String partnerid;

    public String prepayid;

    public String package_info;

    public String noncestr;

    public String timestamp;

    public String sign;

    public String getAppid() {
        return appid;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public String getPackage_info() {
        return package_info;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getSign() {
        return sign;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public void setPackage_info(String packagevalue) {
        this.package_info = packagevalue;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "XiuxiuWechatBean{" +
                "appid='" + appid + '\'' +
                ", partnerid='" + partnerid + '\'' +
                ", prepayid='" + prepayid + '\'' +
                ", package_info='" + package_info + '\'' +
                ", noncestr='" + noncestr + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}
