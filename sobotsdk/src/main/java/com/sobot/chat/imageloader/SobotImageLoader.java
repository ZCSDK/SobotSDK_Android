package com.sobot.chat.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.view.View;
import android.widget.ImageView;

/**
 * 图片加载接口
 */
public abstract class SobotImageLoader {

    public abstract void displayImage(Context context, ImageView imageView, String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, SobotDisplayImageListener displayImageListener);

    public abstract void displayImage(Context context, ImageView imageView, @DrawableRes int targetResId, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, SobotDisplayImageListener displayImageListener);

    public interface SobotDisplayImageListener {
        void onSuccess(View view, String path);
    }

}