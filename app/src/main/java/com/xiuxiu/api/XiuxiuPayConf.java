package com.xiuxiu.api;

/**
 * Created by huzhi on 16-6-29.
 */
public class XiuxiuPayConf {

    public String xiuxiuB;

    public int cash;

    public String desc;

    public int orderCode;

    public int realcoin;

    public String getXiuxiuB() {
        return xiuxiuB;
    }

    public int getCash() {
        return cash;
    }

    public String getDesc() {
        return desc;
    }

    public void setXiuxiuB(String xiuxiuB) {
        this.xiuxiuB = xiuxiuB;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getOrderCode() {
        return orderCode;
    }

    public int getRealcoin() {
        return realcoin;
    }

    public void setOrderCode(int orderCode) {
        this.orderCode = orderCode;
    }

    public void setRealcoin(int realcoin) {
        this.realcoin = realcoin;
    }

    @Override
    public String toString() {
        return "XiuxiuPayConf{" +
                "xiuxiuB='" + xiuxiuB + '\'' +
                ", cash=" + cash +
                ", desc='" + desc + '\'' +
                ", orderCode=" + orderCode +
                ", realcoin=" + realcoin +
                '}';
    }
}
