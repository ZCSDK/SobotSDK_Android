package com.sobot.chat.listener;

/**
 * 超链接点击的监听事件
 */
public interface HyperlinkListener {

    // 链接的点击拦截事件
    void onUrlClick(String url);

    //邮箱的点击拦截事件
    void onEmailClick(String email);

    //电话的点击拦截事件
    void onPhoneClick(String phone);
}