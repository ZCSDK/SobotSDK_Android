package com.sobot.chat.widget.timePicker.lib;

final class SobotOnItemSelectedRunnable implements Runnable {
    final SobotWheelView loopView;

    SobotOnItemSelectedRunnable(SobotWheelView loopview) {
        loopView = loopview;
    }

    @Override
    public final void run() {
        loopView.onItemSelectedListener.onItemSelected(loopView.getCurrentItem());
    }
}
