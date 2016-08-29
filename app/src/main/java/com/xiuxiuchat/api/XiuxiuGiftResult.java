package com.xiuxiuchat.api;


import org.json.JSONObject;

/**
 * Created by huzhi on 16-8-8.
 */
public class XiuxiuGiftResult extends XiuxiuResult{

    public JSONObject gifts;

    public JSONObject getGifts() {
        return gifts;
    }

    public void setGifts(JSONObject gifts) {
        this.gifts = gifts;
    }

}
