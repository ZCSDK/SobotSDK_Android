package com.sobot.chat.imageloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

/**
 * 图片加载器  Picasso
 */
public class SobotPicassoImageLoader extends SobotImageLoader {

    @Override
    public void displayImage(Context context, final ImageView imageView, final String path,  int loadingResId,  int failResId, int width, int height, final SobotDisplayImageListener listener) {
        String pathStr = path;
        if (TextUtils.isEmpty(path)){
            pathStr = "error";
        }
        RequestCreator creator = Picasso.with(context).load(pathStr);
        if(loadingResId != 0){
            creator.placeholder(loadingResId);
        }
        if(failResId != 0){
            creator.error(failResId);
        }
        creator.config(Bitmap.Config.RGB_565);
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
    public void displayImage(Context context, final ImageView imageView,  int targetResId,  int loadingResId,  int failResId, int width, int height, final SobotDisplayImageListener listener) {
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