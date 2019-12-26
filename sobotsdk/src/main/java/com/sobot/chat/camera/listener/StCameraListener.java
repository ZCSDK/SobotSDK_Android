package com.sobot.chat.camera.listener;

import android.graphics.Bitmap;

public interface StCameraListener {

    void captureSuccess(Bitmap bitmap);

    void recordSuccess(String url, Bitmap firstFrame);

}
