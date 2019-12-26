package com.sobot.chat.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.sobot.chat.R;
import com.sobot.chat.api.model.SobotLocationModel;

/**
 * @author Created by jinxl on 2018/12/6.
 */
public class StMapOpenHelper {
    private static final String MAP_BAIDU = "baidumap://map/marker?location=%1$s,%2$s&title=%3$s&content=%4$s&traffic=on&src=%5$s&coord_type=gcj02";
    private static final String MAP_GAODE = "androidamap://viewMap?lat=%1$s&lon=%2$s&poiname=%3$s&sourceApplication=%4$s&dev=0";

    public static void openMap(Context context, SobotLocationModel data) {
        String packageName = context.getPackageName();
        Intent gaodeIntent = obtainGaoDeMap(packageName, data);
        if (gaodeIntent != null) {
            if (openAct(context, gaodeIntent)) {
                return;
            }
        }

        Intent baiduIntent = obtainBaiduMap(packageName, data);
        if (baiduIntent != null) {
            if (openAct(context, baiduIntent)) {
                return;
            }
        }
        ToastUtil.showToast(context, ResourceUtils.getResString(context,"sobot_not_open_map"));
    }

    private static boolean openAct(Context context, Intent intent) {
        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {
            //ignore
        }

        return false;
    }

    public static Intent obtainBaiduMap(String packageName, SobotLocationModel data) {
        try {
            if (data != null) {
                Intent intent = new Intent();
                String tmpStr = String.format(MAP_BAIDU, data.getLat(), data.getLng(), data.getLocalName(), data.getLocalLabel(), packageName);
                intent.setData(Uri.parse(tmpStr));
                return intent;
            }
        } catch (Exception e) {
            //ignore
        }
        return null;
    }

    public static Intent obtainGaoDeMap(String packageName, SobotLocationModel data) {
        try {
            if (data != null) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                String tmpStr = String.format(MAP_GAODE, data.getLat(), data.getLng(), data.getLocalName(), packageName);
                intent.setData(Uri.parse(tmpStr));
                intent.setPackage("com.autonavi.minimap");
                return intent;
            }
        } catch (Exception e) {
            //ignore
        }
        return null;
    }
}
