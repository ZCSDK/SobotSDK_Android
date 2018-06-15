package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.List;
import java.util.Map;

public class RobotTemplateMessageHolder4 extends MessageHolderBase {

    private ImageView sobot_template4_thumbnail;
    private TextView sobot_template4_title;
    private TextView sobot_template4_summary;
    private TextView sobot_template4_anchor;

    public RobotTemplateMessageHolder4(Context context, View convertView) {
        super(context, convertView);
        sobot_template4_thumbnail = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template4_thumbnail"));
        sobot_template4_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template4_title"));
        sobot_template4_summary = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template4_summary"));
        sobot_template4_anchor = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template4_anchor"));
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        if (message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null) {
            final SobotMultiDiaRespInfo multiDiaRespInfo = message.getAnswer().getMultiDiaRespInfo();
            if ("000000".equals(multiDiaRespInfo.getRetCode())) {
                final List<Map<String, String>> interfaceRetList = multiDiaRespInfo.getInterfaceRetList();
                if (interfaceRetList != null && interfaceRetList.size() > 0) {
                    final Map<String, String> interfaceRet = interfaceRetList.get(0);
                    if (interfaceRet != null && interfaceRet.size() > 0) {
                        setSuccessView();
                        sobot_template4_title.setText(interfaceRet.get("title"));
                        SobotBitmapUtil.display(context, interfaceRet.get("thumbnail"), sobot_template4_thumbnail, ResourceUtils.getIdByName(context, "drawable", "sobot_logo_icon"), ResourceUtils.getIdByName(context, "drawable", "sobot_logo_icon"));
                        sobot_template4_summary.setText(interfaceRet.get("summary"));

                        if (multiDiaRespInfo.getEndFlag() && interfaceRet.get("anchor") != null) {
                            sobot_template4_anchor.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, WebViewActivity.class);
                                    intent.putExtra("url", interfaceRet.get("anchor"));
                                    context.startActivity(intent);
                                }
                            });
                        }
                    }
                } else {
                    sobot_template4_title.setText(multiDiaRespInfo.getAnswerStrip());
                    setFailureView();
                }
            } else {
                sobot_template4_title.setText(multiDiaRespInfo.getRetErrorMsg());
                setFailureView();
            }
        }
    }

    private void setSuccessView(){
        sobot_template4_title.setVisibility(View.VISIBLE);
        sobot_template4_thumbnail.setVisibility(View.VISIBLE);
        sobot_template4_summary.setVisibility(View.VISIBLE);
        sobot_template4_anchor.setVisibility(View.VISIBLE);
    }

    private void setFailureView(){
        sobot_template4_title.setVisibility(View.VISIBLE);
        sobot_template4_thumbnail.setVisibility(View.GONE);
        sobot_template4_summary.setVisibility(View.GONE);
        sobot_template4_anchor.setVisibility(View.GONE);
    }
}