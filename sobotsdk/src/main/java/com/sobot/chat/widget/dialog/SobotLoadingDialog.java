package com.sobot.chat.widget.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import com.sobot.chat.R;
import com.sobot.chat.utils.ResourceUtils;

/**
 * Created by jinxl on 2017/4/10.
 */

public class SobotLoadingDialog extends Dialog {


    private static final String TAG = "SobotLoadingDialog";

    private String mMessage;
    private boolean mCancelable;
    private TextView tv_loading;

    public SobotLoadingDialog(@NonNull Context context, String message) {
        this(context, R.style.sobot_dialog_Progress, message, false);
    }

    public SobotLoadingDialog(@NonNull Context context, int themeResId, String message, boolean cancelable) {
        super(context, themeResId);
        mMessage = message;
        mCancelable = cancelable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        initView();
    }

    private void initView() {
        setContentView(R.layout.sobot_progress_dialog);
        // 设置窗口大小
        WindowManager windowManager = getWindow().getWindowManager();
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
      //  attributes.alpha = 0.3f;
        //attributes.width = (screenWidth / 5) * 2;
       // attributes.height = (screenWidth / 5) * 2;
        attributes.gravity= Gravity.CENTER;
        getWindow().setAttributes(attributes);
        setCancelable(mCancelable);

        tv_loading = findViewById(ResourceUtils.getResId(getContext(), "tv_loading"));
        tv_loading.setText(mMessage);
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 屏蔽返回键
            return mCancelable;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getmMessage() {
        return mMessage;
    }

    public void setmMessage(String mMessage) {
        this.mMessage = mMessage;
        tv_loading.setText(mMessage);
    }
}
