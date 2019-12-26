package com.sobot.chat.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.List;
import java.util.Map;

public class SobotChatMsgItemSDKHistoryR extends MessageHolderBase {

    private TextView sobot_sdk_history_msg;

    public SobotChatMsgItemSDKHistoryR(Context context, View convertView) {
        super(context, convertView);
        sobot_sdk_history_msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_sdk_history_msg"));
    }

    @Override
    public void bindData(Context context, ZhiChiMessageBase message) {
        if (message.getAnswer() != null && message.getAnswer().getInterfaceRetList() != null && message.getAnswer().getInterfaceRetList().size() > 0) {
            List<Map<String, String>> listMap = message.getAnswer().getInterfaceRetList();
            StringBuilder sbuffer = new StringBuilder();
            for (int i = 0; i < listMap.size(); i++) {
                Map<String, String> map = listMap.get(i);
                if (map != null && map.size() > 0){
                    sbuffer.append(map.get("title"));
                }
            }
            sobot_sdk_history_msg.setText(sbuffer);
        }
    }
}