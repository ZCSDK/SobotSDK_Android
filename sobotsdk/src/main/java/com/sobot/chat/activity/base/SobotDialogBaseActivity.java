package com.sobot.chat.activity.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.sobot.chat.application.MyApplication;
import com.sobot.chat.core.http.OkHttpUtils;
import com.sobot.chat.utils.ResourceUtils;

/**
 * 从界面下方弹出的activity
 *
 * @author Created by jinxl on 2019/2/21.
 */
public abstract class SobotDialogBaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        setBottomWindow();

        MyApplication.getInstance().addActivity(this);
        setContentView(getRootViewLayoutId());
        initView();
    }

    /**
     * 获取当前页面的布局
     *
     * @return
     */
    protected abstract int getRootViewLayoutId();

    /**
     * 初始化view
     *
     * @return
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    private void setBottomWindow() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
            DisplayMetrics dm = new DisplayMetrics();
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
            if (wm != null) {
                wm.getDefaultDisplay().getMetrics(dm);
            }

            Rect rect = new Rect();
            if (getWindow() != null) {
                View view = getWindow().getDecorView();
                view.getWindowVisibleDisplayFrame(rect);
                layoutParams.width = dm.widthPixels;
            }
            window.setAttributes(layoutParams);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (event.getY() <= 0) {
                finish();
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onDestroy() {
        OkHttpUtils.getInstance().cancelTag(SobotDialogBaseActivity.this);
        MyApplication.getInstance().deleteActivity(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePending();
    }

    private void overridePending() {
        overridePendingTransition(ResourceUtils.getIdByName(
                getApplicationContext(), "anim", "sobot_popupwindow_in"),
                ResourceUtils.getIdByName(getApplicationContext(),
                        "anim", "sobot_popupwindow_out"));
    }
}
