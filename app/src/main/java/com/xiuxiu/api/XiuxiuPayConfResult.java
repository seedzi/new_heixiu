package com.xiuxiu.api;

import java.util.List;

/**
 * Created by huzhi on 16-6-29.
 */
public class XiuxiuPayConfResult extends XiuxiuResult{

    public List<XiuxiuPayConf> result;

    public List<XiuxiuPayConf> getResult() {
        return result;
    }

    public void setResult(List<XiuxiuPayConf> result) {
        this.result = result;
    }
}
