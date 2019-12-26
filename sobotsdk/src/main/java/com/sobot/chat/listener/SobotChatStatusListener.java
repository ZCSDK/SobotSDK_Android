package com.sobot.chat.listener;

import com.sobot.chat.api.enumtype.SobotChatStatusMode;

/**
 * 用户可监听当前聊天状态变化（机器人、人工、离线）
 */

public interface SobotChatStatusListener {

    void onChatStatusListener(SobotChatStatusMode chatStatusMode);
}