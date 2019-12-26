package com.sobot.chat.utils;

import android.widget.TextView;

/**
 * @author Created by jinxl on 2019/4/11.
 */
public class StTextUtils {
    public static void ellipsizeEnd(int maxLins, TextView textView) {
        try {
            if (textView.getLineCount() > maxLins) {
                int lineEndIndex = textView.getLayout().getLineEnd(maxLins-1);
                String text = textView.getText().subSequence(0, lineEndIndex - 1) + "...";
                textView.setText(text);
            }
        } catch (Exception e) {
            //ingnore
        }
    }
}
