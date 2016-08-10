package com.xiuxiu.api;

public class XiuxiuResult {

    public static final String SUCCESS = "0";
    public String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return SUCCESS.equals(status);
    }
}
