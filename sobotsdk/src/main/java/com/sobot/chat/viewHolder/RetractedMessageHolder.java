package com.sobot.chat.viewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 消息撤回
 * Created by jinxl on 2017/3/17.
 */
public class RetractedMessageHolder extends MessageHolderBase {
    TextView sobot_tv_tip; // 中间提醒消息
    String tipStr;

    public RetractedMessageHolder(Context context, View convertView) {
        super(context, convertView);
        sobot_tv_tip = (TextView) convertView
                .findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_tip"));
        tipStr = context.getResources().getString(ResourceUtils.getIdByName(context, "string", "sobot_retracted_msg_tip"));
    }

    @Override
    public void bindData(Context context, ZhiChiMessageBase message) {
        if (message != null) {
            sobot_tv_tip.setText(String.format(tipStr, TextUtils.isEmpty(message.getSenderName()) ? "" : message.getSenderName()));
        }
    }
}
