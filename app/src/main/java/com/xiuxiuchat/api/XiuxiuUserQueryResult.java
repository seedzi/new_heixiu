package com.xiuxiuchat.api;

import java.util.List;

/**
 * Created by huzhi on 16-5-25.
 */
public class XiuxiuUserQueryResult extends XiuxiuResult{

    private List<XiuxiuUserInfoResult> userinfos;

    public List<XiuxiuUserInfoResult> getUserinfos(){
        return userinfos;
    }
}
