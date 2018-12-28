package com.sobot.chat.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;

import com.sobot.chat.api.model.ZhiChiPushMessage;

public class NotificationUtils {

    private static final  String SOBOT_CHANNEL_ID= "sobot_channel_id";

    public static void createNotification(Context context, String title, String content, String ticker, int id,ZhiChiPushMessage pushMessage){

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if(manager == null){
            return;
        }

        Intent detailIntent = new Intent(ZhiChiConstant.SOBOT_NOTIFICATION_CLICK);
        if (pushMessage != null) {
            detailIntent.putExtra("sobot_appId", pushMessage.getAppId());
        }
        detailIntent.setPackage(context.getPackageName());
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(context, 0,
                detailIntent, 0);
        int smallicon = SharedPreferencesUtil.getIntData(context, ZhiChiConstant
                .SOBOT_NOTIFICATION_SMALL_ICON, ResourceUtils.getIdByName(context, "drawable", "sobot_logo_small_icon"));
        int largeicon = SharedPreferencesUtil.getIntData(context, ZhiChiConstant
                .SOBOT_NOTIFICATION_LARGE_ICON, ResourceUtils.getIdByName(context, "drawable", "sobot_logo_icon"));

        BitmapDrawable bd = (BitmapDrawable) context.getResources().getDrawable(largeicon);
        Bitmap bitmap = bd.getBitmap();
        Notification.Builder builder = new Notification.Builder(context)
                .setSmallIcon(smallicon) // 设置状态栏中的小图片，尺寸一般建议在24×24，这个图片同样也是在下拉状态栏中所显示，如果在那里需要更换更大的图片，可以使用setLargeIcon(Bitmap
                // icon)
                .setLargeIcon(bitmap)
                .setTicker(ticker)
                .setContentTitle(title)
                .setContentText(content)
                .setContentIntent(pendingIntent2);

        boolean compatFlag = CommonUtils.getTargetSdkVersion(context) >= 26;
        if (Build.VERSION.SDK_INT >= 26 && compatFlag) {
            String SOBOT_CHANNEL_NAME = context.getResources().getString(ResourceUtils.getIdByName(context, "string", "sobot_notification_name"));//"客服通知";
            NotificationChannel mChannel = new NotificationChannel(SOBOT_CHANNEL_ID, SOBOT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(mChannel);
            builder.setChannelId(SOBOT_CHANNEL_ID);
        }

        Notification notify2 = builder.getNotification();
        notify2.flags |= Notification.FLAG_AUTO_CANCEL;

        notify2.defaults = Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND;
        manager.notify(id, notify2);
    }

    public static void cancleAllNotification(Context context){
        NotificationManager nm =(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            try {
                nm.cancelAll();
            } catch (Exception e) {
                //ignore
            }
        }
    }
}