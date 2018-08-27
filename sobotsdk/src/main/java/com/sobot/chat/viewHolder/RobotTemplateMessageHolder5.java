package com.sobot.chat.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.List;
import java.util.Map;

public class RobotTemplateMessageHolder5 extends MessageHolderBase {

    private TextView sobot_template5_title;
    private TextView sobot_template5_msg;

    public RobotTemplateMessageHolder5(Context context, View convertView) {
        super(context, convertView);
        sobot_template5_msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template5_msg"));
        sobot_template5_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template5_title"));
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        if (message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null) {
            final SobotMultiDiaRespInfo multiDiaRespInfo = message.getAnswer().getMultiDiaRespInfo();
            sobot_template5_msg.setText(ChatUtils.getMultiMsgTitle(multiDiaRespInfo));
            applyTextViewUIConfig(sobot_template5_msg);
            final List<Map<String, String>> interfaceRetList = multiDiaRespInfo.getInterfaceRetList();
            if ("000000".equals(multiDiaRespInfo.getRetCode()) && interfaceRetList != null && interfaceRetList.size() > 0) {
                final Map<String, String> interfaceRet = interfaceRetList.get(0);
                if (interfaceRet != null && interfaceRet.size() > 0) {
                    setSuccessView();
                    sobot_template5_title.setText(interfaceRet.get("title"));
                }
            } else {
                setFailureView();
            }
        }
    }

    private void setSuccessView() {
        sobot_template5_msg.setVisibility(View.VISIBLE);
        sobot_template5_title.setVisibility(View.VISIBLE);
    }

    private void setFailureView() {
        sobot_template5_msg.setVisibility(View.VISIBLE);
        sobot_template5_title.setVisibility(View.GONE);
    }
}