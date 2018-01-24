package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.sobot.chat.activity.SobotChatActivity;
import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;
import com.sobot.chat.widget.lablesview.SobotLabelsView;
import com.sobot.chat.widget.lablesview.SobotLablesViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotTemplateMessageHolder2 extends MessageHolderBase implements SobotLabelsView.OnLabelClickListener {
    private Context mContext;
    // 聊天的消息内容
    private TextView tv_msg;
    // 标签页控件
    private SobotLabelsView slv_labels;

    private ZhiChiMessageBase zhiChiMessageBase;

    public RobotTemplateMessageHolder2(Context context, View convertView) {
        super(context, convertView);
        mContext = context;
        tv_msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template2_msg"));
        slv_labels = (SobotLabelsView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template2_labels"));
    }

    @Override
    public void bindData(Context context, ZhiChiMessageBase message) {
        zhiChiMessageBase = message;
        if (message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null) {
            final SobotMultiDiaRespInfo multiDiaRespInfo = message.getAnswer().getMultiDiaRespInfo();
            tv_msg.setText(ChatUtils.getMultiMsgTitle(multiDiaRespInfo));
            if ("000000".equals(multiDiaRespInfo.getRetCode())) {
                List<Map<String, String>> interfaceRetList = multiDiaRespInfo.getInterfaceRetList();
                String[] inputContent = multiDiaRespInfo.getInputContentList();
                ArrayList<SobotLablesViewModel> label = new ArrayList<>();
                if (interfaceRetList != null && interfaceRetList.size() > 0) {
                    for (int i = 0; i < interfaceRetList.size(); i++) {
                        Map<String, String> interfaceRet = interfaceRetList.get(i);
                        SobotLablesViewModel lablesViewModel = new SobotLablesViewModel();
                        lablesViewModel.setTitle(interfaceRet.get("title"));
                        lablesViewModel.setAnchor(interfaceRet.get("anchor"));
                        label.add(lablesViewModel);
                    }
                    slv_labels.setVisibility(View.VISIBLE);
                    slv_labels.setLabels(label);
                } else if (inputContent != null && inputContent.length > 0) {
                    for (String title:inputContent) {
                        SobotLablesViewModel lablesViewModel = new SobotLablesViewModel();
                        lablesViewModel.setTitle(title);
                        label.add(lablesViewModel);
                    }
                    slv_labels.setVisibility(View.VISIBLE);
                    slv_labels.setLabels(label);
                } else {
                    slv_labels.setVisibility(View.GONE);
                }

                if (message.getSugguestionsFontColor() == 0) {
                    slv_labels.setOnLabelClickListener(this);
                    slv_labels.setTabEnable(true);
                } else {
                    if (multiDiaRespInfo.getEndFlag()){
                        slv_labels.setOnLabelClickListener(this);
                        slv_labels.setTabEnable(true);
                    } else {
                        slv_labels.setOnLabelClickListener(null);
                        slv_labels.setTabEnable(false);
                    }
                }
            } else {
                slv_labels.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onLabelClick(View label, SobotLablesViewModel data, int position) {
        if (zhiChiMessageBase == null || zhiChiMessageBase.getAnswer() == null) {
            return;
        }
        SobotMultiDiaRespInfo multiDiaRespInfo = zhiChiMessageBase.getAnswer().getMultiDiaRespInfo();
        if (multiDiaRespInfo != null && multiDiaRespInfo.getEndFlag() && !TextUtils.isEmpty(data.getAnchor())) {
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("url", data.getAnchor());
            mContext.startActivity(intent);
        } else {
            sendMultiRoundQuestions(data.getTitle(), multiDiaRespInfo);
        }
    }

    private void sendMultiRoundQuestions(String labelText, SobotMultiDiaRespInfo multiDiaRespInfo) {
        String[] outputParam = multiDiaRespInfo.getOutPutParamList();
        if (mContext != null && zhiChiMessageBase != null) {
            ZhiChiMessageBase msgObj = new ZhiChiMessageBase();

            Map<String, String> map = new HashMap<>();
            map.put("level", multiDiaRespInfo.getLevel());
            map.put("conversationId", multiDiaRespInfo.getConversationId());
            if (outputParam != null && outputParam.length > 0) {

                for (String anOutputParam : outputParam) {
                    map.put(anOutputParam, labelText);
                }
            }
            msgObj.setContent(GsonUtil.map2Str(map));
            msgObj.setId(System.currentTimeMillis() + "");
            ((SobotChatActivity) mContext).sendMessageToRobot(msgObj, 4, 2, labelText, labelText);
        }
    }
}