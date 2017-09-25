package com.sobot.chat.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

/**
 * 图片加载器  Picasso
 */
public class SobotPicassoImageLoader extends SobotImageLoader {

    @Override
    public void displayImage(Context context, final ImageView imageView, final String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final SobotDisplayImageListener listener) {
        RequestCreator creator = Picasso.with(context).load(path).placeholder(loadingResId).error(failResId).config(Bitmap.Config.RGB_565);
        if (width != 0 || height != 0) {
            creator.resize(width, height).centerCrop();
        } else {
            creator.fit().centerCrop();
        }
        creator.into(imageView, new Callback.EmptyCallback() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess(imageView, path);
                }
            }
        });
    }

    @Override
    public void displayImage(Context context, final ImageView imageView, @DrawableRes int targetResId, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final SobotDisplayImageListener listener) {
        RequestCreator creator = Picasso.with(context).load(targetResId).config(Bitmap.Config.RGB_565);
        if (loadingResId != 0) {
            creator.placeholder(loadingResId);
        }
        if (failResId != 0) {
            creator.error(failResId);
        }
        if (width != 0 || height != 0) {
            creator.resize(width, height).centerCrop();
        } else {
            creator.fit().centerCrop();
        }
        creator.into(imageView, new Callback.EmptyCallback() {
            @Override
            public void onSuccess() {
                if (listener != null) {
                    listener.onSuccess(imageView, "");
                }
            }
        });
    }
}