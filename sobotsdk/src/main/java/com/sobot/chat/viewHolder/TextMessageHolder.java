package com.sobot.chat.viewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sobot.chat.adapter.base.SobotMsgAdapter;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 文本消息
 * Created by jinxl on 2017/3/17.
 */
public class TextMessageHolder extends MessageHolderBase {
    TextView msg; // 聊天的消息内容
    public TextMessageHolder(Context context, View convertView){
        super(context,convertView);
        msg = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msg"));
    }

    @Override
    public void bindData(final Context context,final ZhiChiMessageBase message) {
        if (message.getAnswer() != null && (!TextUtils.isEmpty(message.getAnswer().getMsg()) || !TextUtils.isEmpty(message.getAnswer().getMsgTransfer()))) {// 纯文本消息
            String content = !TextUtils.isEmpty(message.getAnswer().getMsgTransfer())?message.getAnswer().getMsgTransfer():message.getAnswer().getMsg();
            msg.setVisibility(View.VISIBLE);
            HtmlTools.getInstance(context).setRichText(msg, content,
                    isRight ? ResourceUtils.getIdByName(context, "color","sobot_color_rlink") : ResourceUtils.getIdByName(context, "color","sobot_color_link"));
            if(isRight){
                try {
                    msgStatus.setClickable(true);
                    if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_SUCCESS) {// 成功的状态
                        msgStatus.setVisibility(View.GONE);
                        frameLayout.setVisibility(View.GONE);
                        msgProgressBar.setVisibility(View.GONE);
                    } else if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_ERROR) {
                        frameLayout.setVisibility(View.VISIBLE);
                        msgStatus.setVisibility(View.VISIBLE);
                        msgProgressBar.setVisibility(View.GONE);
                        msgStatus.setOnClickListener(new ReSendTextLisenter(context,message
                                .getId(), content,msgStatus,msgCallBack));
                    } else if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_LOADING) {
                        frameLayout.setVisibility(View.VISIBLE);
                        msgProgressBar.setVisibility(View.VISIBLE);
                        msgStatus.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            msg.setText(CommonUtils.getResString(context,ResourceUtils.getIdByName(context, "string", "sobot_data_wrong_hint")));
        }
        msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(msg.getText().toString())){
                    ToastUtil.showCopyPopWindows(context,view, msg.getText().toString().replace("&amp;","&"), 30,0);
                }
                return false;
            }
        });
    }

    public static class ReSendTextLisenter implements View.OnClickListener {

        private String id;
        private String msgContext;
        private ImageView msgStatus;
        private Context context;
        private SobotMsgAdapter.SobotMsgCallBack msgCallBack;

        public ReSendTextLisenter(final Context context, String id, String msgContext, ImageView
                msgStatus, SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
            super();
            this.context=context;
            this.msgCallBack = msgCallBack;
            this.id = id;
            this.msgContext = msgContext;
            this.msgStatus = msgStatus;
        }

        @Override
        public void onClick(View arg0) {
            if (msgStatus != null) {
                msgStatus.setClickable(false);
            }
            showReSendTextDialog(context,id, msgContext,msgStatus);
        }

        private void showReSendTextDialog(final Context context,final String mid,
                                          final String mmsgContext, final ImageView msgStatus) {
            showReSendDialog(context,msgStatus,new ReSendListener(){

                @Override
                public void onReSend() {
                    sendTextBrocast(context, mid, mmsgContext);
                }
            });
        }

        private void sendTextBrocast(Context context, String id, String msgContent) {
            if (msgCallBack != null){
                ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
                msgObj.setContent(msgContent);
                msgObj.setId(id);
                msgCallBack.sendMessageToRobot(msgObj,1, 0, "");
            }
        }
    }
}
