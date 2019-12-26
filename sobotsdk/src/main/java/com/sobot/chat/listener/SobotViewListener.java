package com.sobot.chat.listener;

import com.sobot.chat.api.enumtype.CustomerState;

/**
 * Created by jinxl on 2017/6/7.
 */

public interface SobotViewListener {

    void onChatActClose(CustomerState customerState);
}
