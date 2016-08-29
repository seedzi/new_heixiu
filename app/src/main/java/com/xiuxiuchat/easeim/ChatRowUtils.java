package com.xiuxiuchat.easeim;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.xiuxiuchat.easeim.xiuxiumsg.XiuxiuActionMsgManager;
import com.xiuxiuchat.easeim.xiuxiumsg.XiuxiuActionMsgTable;

/**
 * Created by huzhi on 16-7-20.
 */
public class ChatRowUtils {

    private static final String TAG = "ChatRowUtils";

    /**
     * 是否付费
     */
    public static boolean isPaid(EMMessage message){
        String status;
        try {
            String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
            status = XiuxiuActionMsgManager.getInstance().getStatusById(askId);
        }catch (Exception e){
            status = null;
        }
        if(status!=null && status.equals(EaseConstant.XIUXIU_STATUS_AGREE)){ //如果已经付费
            return true;
        }
        return false;
    }

    /**
     * 获取查看次数(主要用于咻咻图片)
     */
    public static int getLookCount(EMMessage message){
        int count = 0;
        try {
            String askId = message.getStringAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_MSG_ID);
            count = XiuxiuActionMsgTable.queryLookCountById(askId);
        }catch (Exception e){
        }
        return count;
    }

    /**
     * 是否过期
     * @return
     */
    public static boolean isOverdue(long taskTime,int taskType){
        switch (taskType){
            case EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VOICE:
                if(System.currentTimeMillis() - taskTime >15*60*1000){
                    return true;
                }
                break;
            case EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG:
            case EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VIDEO:
                long diff = System.currentTimeMillis() - taskTime;
                android.util.Log.d(TAG,"System.currentTimeMillis() = " + System.currentTimeMillis()
                        + ",taskTime = " + taskTime + ",diff = " + diff);
                if( diff>24*60*60*1000){
                    return true;
                }
                break;
        }
        return false;
    }
}
