package com.sobot.chat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import java.io.File;

/**
 * 打开文件操作
 */
public class FileOpenHelper {

    //android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(Context context, File file) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = getUri(context, file, intent);

        intent.setDataAndType(uri, "image/*");

        return intent;
    }


    //android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(Context context, File file) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = getUri(context, file, intent);

        intent.setDataAndType(uri, "application/pdf");

        return intent;
    }


    //android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(Context context, File file) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = getUri(context, file, intent);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }


    //android获取一个用于打开音频文件的intent
    public static Intent getAudioFileIntent(Context context, File file) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("oneshot", 0);

        intent.putExtra("configchange", 0);

        Uri uri = getUri(context, file, intent);

        intent.setDataAndType(uri, "audio/*");

        return intent;

    }


    //android获取一个用于打开视频文件的intent
    public static Intent getVideoFileIntent(Context context, File file) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("oneshot", 0);

        intent.putExtra("configchange", 0);

        Uri uri = getUri(context, file, intent);

        intent.setDataAndType(uri, "video/*");

        return intent;
    }


    //android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(Context context, File file) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = getUri(context, file, intent);

        intent.setDataAndType(uri, "application/msword");

        return intent;
    }


    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(Context context, File file) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = getUri(context, file, intent);

        intent.setDataAndType(uri, "application/vnd.ms-excel");

        return intent;

    }


    //android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(Context context, File file) {

        Intent intent = new Intent("android.intent.action.VIEW");

        intent.addCategory("android.intent.category.DEFAULT");

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Uri uri = getUri(context, file, intent);

        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");

        return intent;
    }

    //android获取一个用于打开package文件的intent
    public static Intent getPackageFileIntent(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = getUri(context, file, intent);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        return intent;
    }

    //android获取一个用于打开不识别文件的intent
    public static Intent getOtherFileIntent(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        Uri uri = getUri(context, file, intent);
        intent.setDataAndType(uri, "application");
        return intent;
    }

    private static Uri getUri(Context context, File file, Intent intent) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            uri = FileProvider.getUriForFile(context, context.getPackageName() + ".sobot_fileprovider", file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    public static boolean checkEndsWithInStringArray(String checkItsEnd, Context context
            , String fileEndingsArray) {
        try {
            String[] fileEndings = context.getResources().getStringArray(ResourceUtils
                    .getIdByName(context, "array", fileEndingsArray));

            for (String aEnd : fileEndings) {
                if (checkItsEnd.endsWith(aEnd)) {
                    return true;
                }
            }
        } catch (Exception e) {
            //ignor
        }
        return false;
    }

    public static void openFileWithType(Context context, File file) {
        if (context == null) {
            return;
        }
        if (file != null && file.exists() && file.isFile()) {
            String fileName = file.getName().toLowerCase();
            Intent intent;
            if (checkEndsWithInStringArray(fileName, context, "sobot_fileEndingPackage")) {
                intent = getOtherFileIntent(context, file);
            } else if (checkEndsWithInStringArray(fileName, context, "sobot_fileEndingVideo")) {
                intent = getVideoFileIntent(context, file);
            } else if (checkEndsWithInStringArray(fileName, context, "sobot_fileEndingAudio")) {
                intent = getAudioFileIntent(context, file);
            } else if (checkEndsWithInStringArray(fileName, context, "sobot_fileEndingWord")) {
                intent = getWordFileIntent(context, file);
            } else if (checkEndsWithInStringArray(fileName, context, "sobot_fileEndingExcel")) {
                intent = getExcelFileIntent(context, file);
            } else if (checkEndsWithInStringArray(fileName, context, "sobot_fileEndingPPT")) {
                intent = getPptFileIntent(context, file);
            } else if (checkEndsWithInStringArray(fileName, context, "sobot_fileEndingPdf")) {
                intent = getPdfFileIntent(context, file);
            } else if (checkEndsWithInStringArray(fileName, context, "sobot_fileEndingText")) {
                intent = getTextFileIntent(context, file);
            } else if (checkEndsWithInStringArray(fileName, context, "sobot_fileEndingImage")) {
                intent = getImageFileIntent(context, file);
            } else {
                intent = getOtherFileIntent(context, file);
            }
            try {
                context.startActivity(intent);
            } catch (Exception e) {
                //ignor
                ToastUtil.showToast(context,context.getResources().getString(ResourceUtils.getIdByName(context,"string","sobot_cannot_open_file")));
//                e.printStackTrace();
            }
        }
    }
}