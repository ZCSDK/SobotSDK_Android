package com.sobot.chat.viewHolder;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sobot.chat.api.model.SobotLocationModel;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.StMapOpenHelper;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 位置消息
 * Created by jinxl on 2017/3/17.
 */
public class LocationMessageHolder extends MessageHolderBase implements View.OnClickListener {
    private TextView st_localName;
    private TextView st_localLabel;
    private ImageView st_snapshot;
    private ImageView sobot_msgStatus;
    private ProgressBar sobot_msgProgressBar;
    private LinearLayout sobot_msg_container;
    private ZhiChiMessageBase mMessage;
    private SobotLocationModel mLocationData;

    private int sobot_bg_default_map;

    public LocationMessageHolder(Context context, View convertView) {
        super(context, convertView);
        st_localName = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "st_localName"));
        st_localLabel = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "st_localLabel"));
        sobot_msgStatus = (ImageView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_msgStatus"));
        st_snapshot = (ImageView) convertView.findViewById(ResourceUtils.getResId(context, "st_snapshot"));
        sobot_msg_container = (LinearLayout) convertView.findViewById(ResourceUtils.getResId(context, "sobot_msg_container"));
        sobot_msgProgressBar = (ProgressBar) convertView.findViewById(ResourceUtils.getResId(context, "sobot_msgProgressBar"));
        sobot_msg_container.setOnClickListener(this);
        sobot_bg_default_map = ResourceUtils.getDrawableId(context, "sobot_bg_default_map");
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        mMessage = message;
        if (message.getAnswer() != null && message.getAnswer().getLocationData() != null) {
            mLocationData = message.getAnswer().getLocationData();
            st_localName.setText(mLocationData.getLocalName());
            st_localLabel.setText(mLocationData.getLocalLabel());
            SobotBitmapUtil.display(context, mLocationData.getSnapshot(), st_snapshot,sobot_bg_default_map,sobot_bg_default_map);
            if (isRight) {
                refreshUi();
            }
        }
    }

    private void refreshUi() {
        try {
            if (mMessage == null) {
                return;
            }
            if (mMessage.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_SUCCESS) {// 成功的状态
                sobot_msgStatus.setVisibility(View.GONE);
                sobot_msgProgressBar.setVisibility(View.GONE);
            } else if (mMessage.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_ERROR) {
                sobot_msgStatus.setVisibility(View.VISIBLE);
                sobot_msgProgressBar.setVisibility(View.GONE);
                sobot_msgProgressBar.setClickable(true);
                sobot_msgStatus.setOnClickListener(this);
            } else if (mMessage.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_LOADING) {
                sobot_msgStatus.setVisibility(View.GONE);
                sobot_msgProgressBar.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == sobot_msgStatus) {
            showReSendDialog(mContext, msgStatus, new ReSendListener() {

                @Override
                public void onReSend() {
                    if (msgCallBack != null && mMessage != null && mMessage.getAnswer() != null) {
                        msgCallBack.sendMessageToRobot(mMessage, 5, 0, null);
                    }
                }
            });
        }

        if (v == sobot_msg_container) {
            if (mLocationData != null) {
                StMapOpenHelper.openMap(mContext,mLocationData);
            }
        }
    }
}
