package com.sobot.chat.widget.rich;

import android.content.Context;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.SobotOption;

public class PhoneSpan extends ClickableSpan {

    private String phone;
    private int color;
    private Context context;

    public PhoneSpan(Context context, String phone, int color) {
        this.phone = phone;
        this.color = context.getResources().getColor(color);
        this.context = context;
    }

    @Override
    public void onClick(View widget) {
        if (SobotOption.hyperlinkListener != null) {
            SobotOption.hyperlinkListener.onPhoneClick("tel:" + phone);
            return;
        }
        if (SobotOption.newHyperlinkListener != null) {
            boolean isIntercept = SobotOption.newHyperlinkListener.onPhoneClick("tel:" + phone);
            if (isIntercept) {
                return;
            }
        }
        CommonUtils.callUp(phone, context);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(false); // 去掉下划线
    }
}