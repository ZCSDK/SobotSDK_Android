package com.sobot.chat.listener;

import android.content.Context;

import com.sobot.chat.api.model.SobotTransferOperatorParam;

/**
 * 转人工拦截器
 * @author Created by jinxl on 2019/4/24.
 */
public interface SobotTransferOperatorInterceptor {
    void onTransferStart(Context context,SobotTransferOperatorParam param);
}
