package com.sobot.chat.listener;

import android.content.Context;

import com.sobot.chat.api.model.Information;

/**
 *
 * 消息中心 点击会话的回调
 * Created by Administrator on 2017/6/29.
 */

public interface SobotConversationListCallback {

    void onConversationInit(Context context,Information information);
}