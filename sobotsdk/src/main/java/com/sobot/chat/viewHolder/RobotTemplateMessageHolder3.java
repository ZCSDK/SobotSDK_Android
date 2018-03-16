package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.BitmapUtil;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.List;
import java.util.Map;

public class RobotTemplateMessageHolder3 extends MessageHolderBase {

    private TextView sobot_msg;
    private LinearLayout sobot_template3_layout;

    public RobotTemplateMessageHolder3(Context context, View convertView) {
        super(context, convertView);
        sobot_msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template3_msg"));
        sobot_template3_layout = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template3_layout"));
    }

    @Override
    public void bindData(final Context context, ZhiChiMessageBase message) {
        if (message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null) {
            final SobotMultiDiaRespInfo multiDiaRespInfo = message.getAnswer().getMultiDiaRespInfo();
            sobot_msg.setText(ChatUtils.getMultiMsgTitle(multiDiaRespInfo));
            final List<Map<String, String>> interfaceRetList = multiDiaRespInfo.getInterfaceRetList();
            if ("000000".equals(multiDiaRespInfo.getRetCode()) && interfaceRetList != null && interfaceRetList.size() > 0) {
                sobot_template3_layout.setVisibility(View.VISIBLE);
                sobot_template3_layout.removeAllViews();
                for (int i = 0; i < interfaceRetList.size(); i++) {
                    final Map<String, String> interfaceRet = interfaceRetList.get(i);
                    if (interfaceRet != null && interfaceRet.size() > 0) {
                        View view = View.inflate(context, ResourceUtils.getIdByName(context, "layout", "sobot_chat_msg_item_template3_item_l"), null);
                        LinearLayout sobot_template3_anchor = (LinearLayout) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template3_anchor"));
                        ImageView sobot_template3_thumbrail = (ImageView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template3_thumbrail"));
                        TextView sobot_template3_title = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template3_title"));
                        TextView sobot_template3_summary = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template3_summary"));
                        TextView sobot_template3_tag = (TextView) view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template3_tag"));
                        View sobot_template3_line = view.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template3_line"));
                        if (i == interfaceRetList.size() - 1){
                            sobot_template3_line.setVisibility(View.VISIBLE);
                        } else {
                            sobot_template3_line.setVisibility(View.GONE);
                        }

                        if (!TextUtils.isEmpty(interfaceRet.get("thumbnail"))){
                            sobot_template3_thumbrail.setVisibility(View.VISIBLE);
                            BitmapUtil.display(context, interfaceRet.get("thumbnail"), sobot_template3_thumbrail, ResourceUtils.getIdByName(context, "drawable", "sobot_logo_icon"), ResourceUtils.getIdByName(context, "drawable", "sobot_logo_icon"));
                        } else {
                            sobot_template3_thumbrail.setVisibility(View.GONE);
                        }

                        sobot_template3_title.setText(interfaceRet.get("title"));
                        String summary = interfaceRet.get("summary");
                        if (!TextUtils.isEmpty(summary)){
                            sobot_template3_summary.setVisibility(View.VISIBLE);
                            sobot_template3_summary.setText(summary);
                        } else {
                            sobot_template3_summary.setVisibility(View.GONE);
                        }
                        sobot_template3_tag.setText(interfaceRet.get("tag"));

                        sobot_template3_anchor.setTag(interfaceRet);
                        if (message.getSugguestionsFontColor() == 0) {
                            sobot_template3_anchor.setEnabled(true);
                            if (multiDiaRespInfo.getEndFlag() && !TextUtils.isEmpty(interfaceRet.get("anchor"))) {
                                sobot_template3_anchor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, WebViewActivity.class);
                                        intent.putExtra("url", interfaceRet.get("anchor"));
                                        context.startActivity(intent);
                                    }
                                });
                            } else {
                                sobot_template3_anchor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ChatUtils.sendMultiRoundQuestions(context, multiDiaRespInfo, (Map<String, String>) v.getTag(),msgCallBack);
                                    }
                                });
                            }
                        } else {
                            if (multiDiaRespInfo.getEndFlag() && !TextUtils.isEmpty(interfaceRet.get("anchor"))) {
                                sobot_template3_anchor.setEnabled(true);
                                sobot_template3_anchor.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, WebViewActivity.class);
                                        intent.putExtra("url", interfaceRet.get("anchor"));
                                        context.startActivity(intent);
                                    }
                                });
                            } else {
                                sobot_template3_anchor.setEnabled(false);
                            }
                        }
                        sobot_template3_layout.addView(view);
                    }
                }
            } else {
                sobot_template3_layout.setVisibility(View.GONE);
            }
        }
    }
}