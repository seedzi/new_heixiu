package com.xiuxiu.bean;

import android.database.Cursor;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.domain.EaseUser;
import com.xiuxiu.api.XiuxiuLoginResult;
import com.xiuxiu.db.XiuxiuBroadcastMsgTable;

import java.util.concurrent.Executor;

/**
 * Created by huzhi on 16-8-10.
 */
public class XiuxiuBroadcastMsg {

    public String toXiuxiuId;
    public String fromXiuxiuId;
    public String content;
    public Long updateTime;

    public static XiuxiuBroadcastMsg fromEmMessage2Bean(EMMessage message){
        XiuxiuBroadcastMsg msg = new XiuxiuBroadcastMsg();
        try {
            msg.content = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_IS_XIUXIU_BROADCAST_CONTENT);
        }catch (Exception e){
            msg.content = "有空吗?";
        }
        msg.toXiuxiuId = XiuxiuLoginResult.getInstance().getXiuxiu_id();
        msg.fromXiuxiuId = message.getFrom();
        msg.updateTime = message.getMsgTime();
        return msg;
    }

    public static XiuxiuBroadcastMsg fromCursor2Bean(Cursor cursor){
        if(cursor==null){
            return null;
        }
        XiuxiuBroadcastMsg msg = new XiuxiuBroadcastMsg();
        msg.content = cursor.getString(cursor.getColumnIndex(XiuxiuBroadcastMsgTable._CONTENT));
        msg.toXiuxiuId = XiuxiuLoginResult.getInstance().getXiuxiu_id();
        msg.fromXiuxiuId = cursor.getString(cursor.getColumnIndex(XiuxiuBroadcastMsgTable._FROM_XIUXIU_ID));
        msg.updateTime = cursor.getLong(cursor.getColumnIndex(XiuxiuBroadcastMsgTable._UPDATE_TIME));
        cursor.close();
        return msg;
    }

    @Override
    public String toString() {
        return "XiuxiuBroadcastMsg{" +
                "toXiuxiuId='" + toXiuxiuId + '\'' +
                ", fromXiuxiuId='" + fromXiuxiuId + '\'' +
                ", content='" + content + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }
}
