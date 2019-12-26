package com.sobot.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sobot.chat.api.apiUtils.GsonUtil;
import com.sobot.chat.api.apiUtils.ZhiChiConstants;
import com.sobot.chat.api.model.SobotMsgCenterModel;
import com.sobot.chat.api.model.ZhiChiPushMessage;
import com.sobot.chat.api.model.ZhiChiReplyAnswer;
import com.sobot.chat.utils.ZhiChiConstant;

import java.util.Calendar;
import java.util.List;

/**
 * 消息中心广播接受者的逻辑处理类
 *
 * @author Created by jinxl on 2018/10/25.
 */
public abstract class SobotMsgCenterReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (ZhiChiConstants.receiveMessageBrocast.equals(intent.getAction())) {
            // 接受下推的消息
            Bundle extras = intent.getExtras();
            if (extras == null) {
                return;
            }
            ZhiChiPushMessage pushMessage = (ZhiChiPushMessage) extras.getSerializable(ZhiChiConstants.ZHICHI_PUSH_MESSAGE);
            if (pushMessage == null || TextUtils.isEmpty(pushMessage.getAppId())) {
                return;
            }
            if (ZhiChiConstant.push_message_receverNewMessage == pushMessage.getType()) {
                // 接收到新的消息
                List<SobotMsgCenterModel> datas = getMsgCenterDatas();
                if (datas != null) {
                    for (int i = 0; i < datas.size(); i++) {
                        SobotMsgCenterModel sobotMsgCenterModel = datas.get(i);
                        if (sobotMsgCenterModel.getInfo() != null && pushMessage.getAppId().equals(sobotMsgCenterModel.getInfo().getApp_key())) {
                            sobotMsgCenterModel.setLastDateTime(Calendar.getInstance().getTime().getTime() + "");
                            if (pushMessage.getAnswer() != null) {
                                sobotMsgCenterModel.setLastMsg(pushMessage.getAnswer().getMsg());
                            }
                            int unreadCount = sobotMsgCenterModel.getUnreadCount() + 1;
                            sobotMsgCenterModel.setUnreadCount(unreadCount);
                            onDataChanged(sobotMsgCenterModel);
                            break;
                        }
                    }
                }
            }
        } else if (ZhiChiConstant.SOBOT_ACTION_UPDATE_LAST_MSG.equals(intent.getAction())) {
            SobotMsgCenterModel lastMsg = (SobotMsgCenterModel) intent.getSerializableExtra("lastMsg");
            if (lastMsg == null || lastMsg.getInfo() == null || TextUtils.isEmpty(lastMsg.getInfo().getApp_key())) {
                return;
            }
            onDataChanged(lastMsg);
        }
    }

    /**
     * 会话列表数据改变的回调
     *
     * @param data
     */
    public abstract void onDataChanged(SobotMsgCenterModel data);

    /**
     * 获取当前数据列表中的数据
     *
     * @return
     */
    public abstract List<SobotMsgCenterModel> getMsgCenterDatas();
}
