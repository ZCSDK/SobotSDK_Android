package com.sobot.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sobot.chat.utils.LogUtils;
import com.sobot.chat.utils.ZhiChiConstant;

/**
 * 点击通知以后发出的广播接收者
 */
public class SobotNotificationClickReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ZhiChiConstant.SOBOT_NOTIFICATION_CLICK.equals(intent.getAction())){
            LogUtils.i("点击了通知发出的广播........................");
            //可以在这里打开想去的页面
        }
    }
}