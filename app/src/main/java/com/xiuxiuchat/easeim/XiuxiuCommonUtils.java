package com.xiuxiuchat.easeim;

import android.content.Context;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;

/**
 * Created by huzhi on 16-7-25.
 */
public class XiuxiuCommonUtils {

    /**
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    public static String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        int type = -1;
        try {
            type = message.getIntAttribute(EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE);
        }catch (Exception e){}
        switch (type) {
            case EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_IMG: //
                return "[1条咻羞]";
            case EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VIDEO: //
                return "[1条咻羞]";
            case EaseConstant.MESSAGE_ATTR_XIUXIU_TYPE_VOICE: //
                return "[1条咻羞]";
//            case LOCATION: // 位置消息
//                if (message.direct() == EMMessage.Direct.RECEIVE) {
//                    //从sdk中提到了ui中，使用更简单不犯错的获取string方法
////              digest = EasyUtils.getAppResourceString(context, "location_recv");
//                    digest = getString(context, com.hyphenate.easeui.R.string.location_recv);
//                    digest = String.format(digest, message.getFrom());
//                    return digest;
//                } else {
////              digest = EasyUtils.getAppResourceString(context, "location_prefix");
//                    digest = getString(context, com.hyphenate.easeui.R.string.location_prefix);
//                }
//                break;
//            case IMAGE: // 图片消息
//                digest = getString(context, com.hyphenate.easeui.R.string.picture);
//                break;
//            case VOICE:// 语音消息
//                digest = getString(context, com.hyphenate.easeui.R.string.voice_prefix);
//                break;
//            case VIDEO: // 视频消息
//                digest = getString(context, com.hyphenate.easeui.R.string.video);
//                break;
//            case TXT: // 文本消息
//                EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
//            /*if(((DemoHXSDKHelper)HXSDKHelper.getInstance()).isRobotMenuMessage(message)){
//                digest = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getRobotMenuMessageDigest(message);
//            }else */if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VOICE_CALL, false)){
//                digest = getString(context, com.hyphenate.easeui.R.string.voice_call) + txtBody.getMessage();
//            }else if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_VIDEO_CALL, false)){
//                digest = getString(context, com.hyphenate.easeui.R.string.video_call) + txtBody.getMessage();
//            }else if(message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)){
//                if(!TextUtils.isEmpty(txtBody.getMessage())){
//                    digest = txtBody.getMessage();
//                }else{
//                    digest = getString(context, com.hyphenate.easeui.R.string.dynamic_expression);
//                }
//            }else{
//                digest = txtBody.getMessage();
//            }
//                break;
//            case FILE: //普通文件消息
//                digest = getString(context, com.hyphenate.easeui.R.string.file);
//                break;
            default:
                return "";
        }
    }
}
