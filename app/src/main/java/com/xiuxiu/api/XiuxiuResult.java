package com.xiuxiu.api;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class XiuxiuResult {
    /*
    public static final int SUCCESS = 0;

    private int code;

    private JsonElement msg;

    public int getCode() {
        return code;
    }

    public JsonElement getMessage() {
        return msg;
    }

    public boolean isSuccess() {
        return code == SUCCESS;
    }
    */

    public static final String SUCCESS = "ok";
    private String status;

    public String getStatus() {
        return status;
    }

    public boolean isSuccess() {
        return SUCCESS.equals(status);
    }
}
