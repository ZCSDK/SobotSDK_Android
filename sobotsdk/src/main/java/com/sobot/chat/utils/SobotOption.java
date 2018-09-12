package com.sobot.chat.utils;

import com.sobot.chat.listener.HyperlinkListener;
import com.sobot.chat.listener.SobotConversationListCallback;
import com.sobot.chat.listener.SobotLeaveMsgListener;
import com.sobot.chat.listener.SobotViewListener;

/**
 * Created by jinxl on 2017/3/21.
 */
public class SobotOption {
    public static HyperlinkListener hyperlinkListener;//超链接的监听
    public static SobotViewListener sobotViewListener;//页面的监听
    public static SobotLeaveMsgListener sobotLeaveMsgListener;//留言按钮监听
    public static SobotConversationListCallback sobotConversationListCallback;//消息中心点击会话的回调
}
