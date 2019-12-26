package com.sobot.chat.listener;

/**
 * 超链接点击的监听事件
 */
public interface NewHyperlinkListener {

    // 链接的点击事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
    // 可为订单号,商品详情地址等等;客户可自定义规则拦截,返回true时会把自定义的信息返回
    // 拦截范围  （帮助中心、留言、聊天、留言记录、商品卡片，订单卡片）
    boolean onUrlClick(String url);

    //邮箱的点击拦截事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
    boolean onEmailClick(String email);

    //电话的点击拦截事件, 根据返回结果判断是否拦截 如果返回true,拦截;false 不拦截
    boolean onPhoneClick(String phone);
}