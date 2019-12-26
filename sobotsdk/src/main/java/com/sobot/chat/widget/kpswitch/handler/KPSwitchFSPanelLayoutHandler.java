package com.sobot.chat.widget.kpswitch.handler;

import android.view.View;
import android.view.Window;

import com.sobot.chat.widget.kpswitch.IFSPanelConflictLayout;
import com.sobot.chat.widget.kpswitch.util.KeyboardUtil;

public class KPSwitchFSPanelLayoutHandler implements IFSPanelConflictLayout {
    private final View panelLayout;
    private boolean isKeyboardShowing;

    public KPSwitchFSPanelLayoutHandler(final View panelLayout) {
        this.panelLayout = panelLayout;
    }

    public void onKeyboardShowing(boolean showing) {
        isKeyboardShowing = showing;
        if (!showing && panelLayout.getVisibility() == View.INVISIBLE) {
            panelLayout.setVisibility(View.GONE);
        }

        if (!showing && recordedFocusView != null) {
            restoreFocusView();
            recordedFocusView = null;
        }
    }

    @Override
    public void recordKeyboardStatus(Window window) {
        final View focusView = window.getCurrentFocus();
        if (focusView == null) {
            return;
        }

        if (isKeyboardShowing) {
            saveFocusView(focusView);
        } else {
            focusView.clearFocus();
        }
    }

    private View recordedFocusView;

    private void saveFocusView(final View focusView) {
        recordedFocusView = focusView;
        focusView.clearFocus();
        panelLayout.setVisibility(View.GONE);
    }

    private void restoreFocusView() {
        panelLayout.setVisibility(View.INVISIBLE);
        KeyboardUtil.showKeyboard(recordedFocusView);

    }
}
