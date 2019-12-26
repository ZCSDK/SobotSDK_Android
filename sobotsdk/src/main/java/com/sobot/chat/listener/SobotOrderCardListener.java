package com.sobot.chat.listener;

import com.sobot.chat.api.model.OrderCardContentModel;

/**
 * 订单卡片的监听
 */

public interface SobotOrderCardListener {

    void onClickOrderCradMsg(OrderCardContentModel orderCardContent);
}