package com.sobot.chat.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotAnswerItemsMsgHolder extends MessageHolderBase implements View.OnClickListener {
    // 聊天的消息内容
    private TextView tv_msg;
    private LinearLayout answersListView;

    private ZhiChiMessageBase zhiChiMessageBase;

    private static final int PAGE_SIZE = 9;

    public RobotAnswerItemsMsgHolder(Context context, View convertView) {
        super(context, convertView);
        tv_msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template2_msg"));
        answersListView = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_answersList"));
    }

    @Override
    public void bindData(Context context, ZhiChiMessageBase message) {
        zhiChiMessageBase = message;
        if (message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null) {
            final SobotMultiDiaRespInfo multiDiaRespInfo = message.getAnswer().getMultiDiaRespInfo();
            String msgStr = ChatUtils.getMultiMsgTitle(multiDiaRespInfo);
            HtmlTools.getInstance(context).setRichText(tv_msg, msgStr.replaceAll("\n", "<br/>"), getLinkTextColor());
            if ("000000".equals(multiDiaRespInfo.getRetCode())) {
                List<Map<String, String>> icLists = multiDiaRespInfo.getIcLists();
                if (icLists != null && icLists.size() > 0) {
                    answersListView.setVisibility(View.VISIBLE);
                    answersListView.removeAllViews();
                    for (int i = 0; i < icLists.size(); i++) {
                        Map<String, String> icObj = icLists.get(i);
                        for (Map.Entry<String, String> vo : icObj.entrySet()) {
                            TextView answer = ChatUtils.initAnswerItemTextView(context, isHistoryMsg(message));
                            answer.setOnClickListener(this);
                            String tempStr = vo.getKey() + ":" + vo.getValue();
                            answer.setText(tempStr);
                            answer.setTag(icObj);
                            answersListView.addView(answer);
                            break;
                        }
                    }
                } else {
                    answersListView.setVisibility(View.GONE);
                }
            } else {
                answersListView.setVisibility(View.GONE);
            }
        }
    }

    private void sendMultiRoundQuestions(String labelText, Map<String, String> tmpMap, SobotMultiDiaRespInfo multiDiaRespInfo) {
        if (multiDiaRespInfo == null) {
            return;
        }
        if (msgCallBack != null && zhiChiMessageBase != null) {
            ZhiChiMessageBase msgObj = new ZhiChiMessageBase();

            Map<String, String> map = new HashMap<>();
            map.put("level", multiDiaRespInfo.getLevel());
            map.put("conversationId", multiDiaRespInfo.getConversationId());
            map.putAll(tmpMap);
            msgObj.setContent(GsonUtil.map2Str(map));
            msgObj.setId(System.currentTimeMillis() + "");
            msgCallBack.sendMessageToRobot(msgObj, 4, 2, labelText, labelText);
        }
    }

    @Override
    public void onClick(View v) {
        if (zhiChiMessageBase == null || zhiChiMessageBase.getAnswer() == null) {
            return;
        }
        if (v instanceof TextView && v.getTag() != null && v.getTag() instanceof Map) {
            TextView textView = (TextView) v;
            Map<String, String> tmpMap = (Map<String, String>) v.getTag();
            SobotMultiDiaRespInfo multiDiaRespInfo = zhiChiMessageBase.getAnswer().getMultiDiaRespInfo();
            sendMultiRoundQuestions(textView.getText().toString(), tmpMap, multiDiaRespInfo);
        }
    }

    /**
     * 判断数据是否为历史记录
     * @param message
     * @return
     */
    private boolean isHistoryMsg(ZhiChiMessageBase message){
        return message.getSugguestionsFontColor() == 1;
    }
}