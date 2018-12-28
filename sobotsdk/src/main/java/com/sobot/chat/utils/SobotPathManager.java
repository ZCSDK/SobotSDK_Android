package com.sobot.chat.utils;

import android.content.Context;
import android.os.Environment;

import com.sobot.chat.api.apiUtils.SobotApp;

import java.io.File;
import java.security.MessageDigest;

/**
 * @author Created by jinxl on 2018/12/3.
 */
public class SobotPathManager {
    private Context mContext;

    private static String mRootPath;

    private static final String ROOT_DIR = "download";
    private static final String VIDEO_DIR = "video";
    private static final String VOICE_DIR = "voice";
    private static final String PIC_DIR = "pic";
    private static final String CACHE_DIR = "cache";

    private SobotPathManager(Context context) {
        mContext = context.getApplicationContext();
    }

    private static SobotPathManager instance;

    public static SobotPathManager getInstance() {
        if (instance == null) {
            synchronized (SobotPathManager.class) {
                if (instance == null) {
                    instance = new SobotPathManager(SobotApp.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public String getRootDir() {
        if (mRootPath == null) {
            String packageName = mContext != null ? mContext.getPackageName() : "";
            mRootPath = Environment.getExternalStorageDirectory().getPath() + File.separator + ROOT_DIR + File.separator + encode(packageName + "cache_sobot");
        }
        return mRootPath;
    }

    //sdcard/download/xxxx/video
    public String getVideoDir() {
        return getRootDir() + File.separator + VIDEO_DIR + File.separator;
    }

    //sdcard/download/xxxx/voice
    public String getVoiceDir() {
        return getRootDir() + File.separator + VOICE_DIR + File.separator;
    }

    //sdcard/download/xxxx/pic
    public String getPicDir() {
        return getRootDir() + File.separator + PIC_DIR + File.separator;
    }

    //sdcard/download/xxxx/cache
    public String getCacheDir() {
        return getRootDir() + File.separator + CACHE_DIR + File.separator;
    }

    private String encode(String str) {
        StringBuilder sb = new StringBuilder();

        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            byte[] digest = instance.digest(str.getBytes());
            for (byte b : digest) {
                int num = b & 0xff;
                String hex = Integer.toHexString(num);
                if (hex.length() < 2) {
                    sb.append("0");
                }
                sb.append(hex);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
