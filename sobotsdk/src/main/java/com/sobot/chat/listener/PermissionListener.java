package com.sobot.chat.listener;

import android.app.Activity;

//权限成功后回调监听
public interface PermissionListener {
    void onPermissionSuccessListener();

    void onPermissionErrorListener(Activity activity, String title);
}