package com.sobot.chat.widget.timePicker.lib;

import android.view.MotionEvent;

final class SobotLoopViewGestureListener extends android.view.GestureDetector.SimpleOnGestureListener {

    final SobotWheelView loopView;

    SobotLoopViewGestureListener(SobotWheelView loopview) {
        loopView = loopview;
    }

    @Override
    public final boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        loopView.scrollBy(velocityY);
        return true;
    }
}
