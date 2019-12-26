package com.sobot.chat.utils;

import com.sobot.chat.listener.HyperlinkListener;
import com.sobot.chat.listener.NewHyperlinkListener;
import com.sobot.chat.listener.SobotChatStatusListener;
import com.sobot.chat.listener.SobotConversationListCallback;
import com.sobot.chat.listener.SobotLeaveMsgListener;
import com.sobot.chat.listener.SobotOrderCardListener;
import com.sobot.chat.listener.SobotTransferOperatorInterceptor;
import com.sobot.chat.listener.SobotViewListener;

/**
 * Created by jinxl on 2017/3/21.
 */
public class SobotOption {
    public static HyperlinkListener hyperlinkListener;//超链接的监听
    public static NewHyperlinkListener newHyperlinkListener;//超链接的监听(动态分开拦截（帮助中心、留言、聊天、留言记录、商品卡片，订单卡片）点击事件)
    public static SobotViewListener sobotViewListener;//页面的监听
    public static SobotLeaveMsgListener sobotLeaveMsgListener;//留言按钮监听
    public static SobotConversationListCallback sobotConversationListCallback;//消息中心点击会话的回调
    public static SobotTransferOperatorInterceptor transferOperatorInterceptor;//转人工拦截器
    public static SobotOrderCardListener orderCardListener;//订单卡片的监听
    public static SobotChatStatusListener sobotChatStatusListener;//聊天状态变化的监听
}
