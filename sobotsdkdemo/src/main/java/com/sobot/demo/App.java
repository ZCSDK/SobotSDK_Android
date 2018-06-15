package com.sobot.demo;

import android.app.Application;
import android.text.TextUtils;

import com.sobot.chat.SobotApi;

/**
 *
 * Created by Administrator on 2017/12/29.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        String appkey = SobotSPUtil.getStringData(this, "sobot_appkey", "");
        if (TextUtils.isEmpty(appkey)) {
            appkey = "07a5ff1050c047c4b9d3c57eeb7ced29";
        }
        SobotApi.initSobotSDK(this, appkey, SobotSPUtil.getStringData(this, "sobot_partnerId", ""));
    }
}