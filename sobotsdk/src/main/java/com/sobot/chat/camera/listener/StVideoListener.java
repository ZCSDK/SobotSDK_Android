package com.sobot.chat.camera.listener;

public interface StVideoListener {

    void onStart();
    void onEnd();
    void onError();
    void onCancel();
}
