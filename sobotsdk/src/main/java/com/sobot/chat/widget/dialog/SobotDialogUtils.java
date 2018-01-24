package com.sobot.chat.widget.dialog;

import android.app.Activity;
import android.content.Context;

/**
 * Created by jinxl on 2017/4/10.
 */

public class SobotDialogUtils {

    public static SobotLoadingDialog progressDialog;

    public static void startProgressDialog(Context context) {
        if (context == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = SobotLoadingDialog.createDialog(context);
        } else {
            progressDialog.setText(context, progressDialog, "");
        }

        try {
            progressDialog.show();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void startProgressDialog(Context context, String str) {
        if (context == null) {
            return;
        }
        if (progressDialog == null) {
            progressDialog = SobotLoadingDialog.createDialog(context, str);
        } else {
            progressDialog.setText(context, progressDialog, str);
        }
        try {
            progressDialog.show();
        } catch (Exception e) {
//            e.printStackTrace();
        }
    }

    public static void stopProgressDialog(Context context) {
        if (progressDialog != null && context != null && progressDialog.isShowing()) {
            Activity act = (Activity) context;
            try {
                if (!act.isFinishing()) {
                    progressDialog.dismiss();
                }
            } catch (Exception e) {
//            e.printStackTrace();
            }
        }
        progressDialog = null;
    }
}
