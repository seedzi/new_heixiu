/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hyphenate.easeui;

public class EaseConstant {
    public static final String MESSAGE_ATTR_IS_VOICE_CALL = "is_voice_call";
    public static final String MESSAGE_ATTR_IS_VIDEO_CALL = "is_video_call";
    
    public static final String MESSAGE_ATTR_IS_BIG_EXPRESSION = "em_is_big_expression";
    public static final String MESSAGE_ATTR_EXPRESSION_ID = "em_expression_id";
//    /**
//     * app自带的动态表情，直接去resource里获取图片
//     */
//    public static final String MESSAGE_ATTR_BIG_EXPRESSION_ICON = "em_big_expression_icon";
//    /**
//     * 动态下载的表情图片，需要知道表情url
//     */
//    public static final String MESSAGE_ATTR_BIG_EXPRESSION_URL = "em_big_expression_url";
    
    
	public static final int CHATTYPE_SINGLE = 1;
    public static final int CHATTYPE_GROUP = 2;
    public static final int CHATTYPE_CHATROOM = 3;
    
    public static final String EXTRA_CHAT_TYPE = "chatType";
    public static final String EXTRA_USER_ID = "userId";
    public static final String EXTRA_USER_NAME = "userName";

    // =========================================================================================
    // 扩展消息类型
    // =========================================================================================
    public static final String MESSAGE_ATTR_IS_XIUXIU = "is_xiuxiu"; //是否是xiuxiu消息
    public static final String MESSAGE_ATTR_IS_XIUXIU_NAN_2_NV = "is_xiuxiu_nan_2_nv"; //是否是xiuxiu男发给女的

    public static final String MESSAGE_ATTR_XIUXIU_STATUS = "xiu_xiu_status"; //咻咻回执消息的状态: 1 同意　2.拒绝
    public static final String MESSAGE_ATTR_XIUXIU_ACTION = "xiu_xiu_action"; //咻咻回执消息ACTION
    public static final String MESSAGE_ATTR_XIUXIU_ACTION_CALL_VOICE = "xiu_xiu_action_call_voice"; //咻咻回执消息ACTION 主动拨打电话
    public static final String MESSAGE_ATTR_XIUXIU_ACTION_CALL_VOICE_COST_XIUXIUB = "xiu_xiu_action_call_voice_cost_xiuxiub"; //电话消耗的xiuxiub
    public static final String MESSAGE_ATTR_XIUXIU_MSG_ID = "xiu_xiu_msg_id"; //咻咻消息的 msg ID 用于处理回执消息

    public static final String XIUXIU_STATUS_AGREE = "1"; //同意
    public static final String XIUXIU_STATUS_REFUSE = "0"; //拒绝



    public static final String MESSAGE_ATTR_XIUXIU_TYPE = "xiuxiu_type"; //咻咻type
    public static final int MESSAGE_ATTR_XIUXIU_TYPE_IMG = 1001; //咻咻图片
    public static final int MESSAGE_ATTR_XIUXIU_TYPE_VIDEO = 1002; //咻咻视频
    public static final int MESSAGE_ATTR_XIUXIU_TYPE_VOICE = 1003; //咻咻语聊


    public static final String MESSAGE_ATTR_XIUXIU_TXT_BODY= "xiuxiu_txt_body"; //咻咻任务txt消息
    public static final String MESSAGE_ATTR_XIUXIU_B_SIZE = "xiu_xiu_b_size"; //咻咻b的size
    public static final String MESSAGE_ATTR_XIUXIU_TXT_CONTENT = "xiuxiu_txt_content"; //咻咻内容


}
