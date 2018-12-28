package com.sobot.chat.camera.util;

import android.graphics.Bitmap;
import android.os.Environment;

import com.sobot.chat.utils.IOUtils;
import com.sobot.chat.utils.SobotPathManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class FileUtil {

    public static String saveBitmap(int quality, Bitmap b) {
        String picDir = SobotPathManager.getInstance().getPicDir();
        IOUtils.createFolder(picDir);
        long dataTake = System.currentTimeMillis();
        String jpegName = picDir + "pic_" + dataTake + ".jpg";
        try {
            FileOutputStream fout = new FileOutputStream(jpegName);
            BufferedOutputStream bos = new BufferedOutputStream(fout);
            b.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            return jpegName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (b != null && !b.isRecycled()) {
                b.recycle();
            }
        }
    }

    public static boolean deleteFile(String url) {
        boolean result = false;
        File file = new File(url);
        if (file.exists()) {
            result = file.delete();
        }
        return result;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
