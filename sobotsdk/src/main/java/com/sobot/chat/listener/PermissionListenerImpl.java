package com.sobot.chat.listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.sobot.chat.widget.dialog.SobotPermissionDialog;

public class PermissionListenerImpl implements PermissionListener {

    @Override
    public void onPermissionSuccessListener() {

    }

    @Override
    public void onPermissionErrorListener(Activity activity,String title) {
        if (activity==null){
            return;
        }
         SobotPermissionDialog dialog=new SobotPermissionDialog(activity,title, new SobotPermissionDialog.ClickViewListener() {
            @Override
            public void clickRightView(Context context,SobotPermissionDialog dialog) {
                Uri packageURI = Uri.parse("package:" + context.getPackageName());
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
                context.startActivity(intent);
                dialog.dismiss();

            }

            @Override
            public void clickLeftView(Context context,SobotPermissionDialog dialog) {
               dialog.dismiss();

            }
        });
        dialog.show();


    }
}
