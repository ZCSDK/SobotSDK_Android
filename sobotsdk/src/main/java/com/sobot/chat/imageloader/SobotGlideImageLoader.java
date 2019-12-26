package com.sobot.chat.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

import com.bumptech.glide.BitmapRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

/**
 * 图片加载器  Glide
 */
public class SobotGlideImageLoader extends SobotImageLoader {

    @Override
    public void displayImage(Context context, final ImageView imageView, final String path, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final SobotDisplayImageListener listener) {
        BitmapRequestBuilder<String, Bitmap> builder = Glide.with(context).load(path).asBitmap().placeholder(loadingResId).error(failResId).centerCrop();
        if (width != 0 || height != 0) {
            builder.override(width, height);
        }
        builder.listener(new RequestListener<String, Bitmap>() {
            @Override
            public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (listener != null) {
                    listener.onSuccess(imageView, path);
                }
                return false;
            }
        }).into(imageView);
    }

    @Override
    public void displayImage(Context context, final ImageView imageView, @DrawableRes int targetResId, @DrawableRes int loadingResId, @DrawableRes int failResId, int width, int height, final SobotDisplayImageListener listener) {
        BitmapRequestBuilder<Integer, Bitmap> builder = Glide.with(context).load(targetResId).asBitmap().placeholder(loadingResId).error(failResId).centerCrop();
        if (width != 0 || height != 0) {
            builder.override(width, height);
        }
        builder.listener(new RequestListener<Integer, Bitmap>() {
            @Override
            public boolean onException(Exception e, Integer model, Target<Bitmap> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Integer model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                if (listener != null) {
                    listener.onSuccess(imageView, "");
                }
                return false;
            }

        }).into(imageView);
    }

}