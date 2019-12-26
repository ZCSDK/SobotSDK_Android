package com.sobot.chat.viewHolder;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.ToastUtil;
import com.sobot.chat.utils.ZhiChiConstant;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

/**
 * 文本消息
 */
public class TextMessageHolder extends MessageHolderBase {
    TextView msg; // 聊天的消息内容
    //离线留言信息标志
    TextView sobot_tv_icon;


    public TextMessageHolder(Context context, View convertView) {
        super(context, convertView);
        msg = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_msg"));
        sobot_tv_icon = (TextView) convertView.findViewById(ResourceUtils.getResId(context, "sobot_tv_icon"));

        rightEmptyRL = (RelativeLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_right_empty_rl"));
        sobot_ll_likeBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_likeBtn"));
        sobot_ll_dislikeBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_dislikeBtn"));
        sobot_tv_likeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_likeBtn"));
        sobot_tv_dislikeBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_dislikeBtn"));

        //102=左间距12+内间距30+右间距60
        msg.setMaxWidth(ScreenUtils.getScreenWidth((Activity) mContext) - ScreenUtils.dip2px(mContext, 102));
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        if (message.getAnswer() != null && (!TextUtils.isEmpty(message.getAnswer().getMsg()) || !TextUtils.isEmpty(message.getAnswer().getMsgTransfer()))) {// 纯文本消息
            String content = !TextUtils.isEmpty(message.getAnswer().getMsgTransfer()) ? message.getAnswer().getMsgTransfer() : message.getAnswer().getMsg();
            msg.setVisibility(View.VISIBLE);

            HtmlTools.getInstance(context).setRichText(msg, content, isRight ? getLinkTextColor() : getLinkTextColor());

            applyTextViewUIConfig(msg);

            if (isRight) {
                try {
                    msgStatus.setClickable(true);
                    if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_SUCCESS) {// 成功的状态
                        msgStatus.setVisibility(View.GONE);
                        msgProgressBar.setVisibility(View.GONE);
                    } else if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_ERROR) {
                        msgStatus.setVisibility(View.VISIBLE);
                        msgProgressBar.setVisibility(View.GONE);
                        msgStatus.setOnClickListener(new ReSendTextLisenter(context, message
                                .getId(), content, msgStatus, msgCallBack));
                    } else if (message.getSendSuccessState() == ZhiChiConstant.MSG_SEND_STATUS_LOADING) {
                        msgProgressBar.setVisibility(View.VISIBLE);
                        msgStatus.setVisibility(View.GONE);
                    }
                    if (sobot_tv_icon != null) {
                        sobot_tv_icon.setVisibility(message.isLeaveMsgFlag() ? View.VISIBLE : View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            msg.setText(CommonUtils.getResString(context, ResourceUtils.getIdByName(context, "string", "sobot_data_wrong_hint")));
        }
        msg.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (!TextUtils.isEmpty(msg.getText().toString())) {
                    ToastUtil.showCopyPopWindows(context, view, msg.getText().toString().replace("&amp;", "&"), 30, 0);
                }
                return false;
            }
        });

        refreshRevaluateItem();//左侧消息刷新顶和踩布局
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
            this.context = context;
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
            showReSendTextDialog(context, id, msgContext, msgStatus);
        }

        private void showReSendTextDialog(final Context context, final String mid,
                                          final String mmsgContext, final ImageView msgStatus) {
            showReSendDialog(context, msgStatus, new ReSendListener() {

                @Override
                public void onReSend() {
                    sendTextBrocast(context, mid, mmsgContext);
                }
            });
        }

        private void sendTextBrocast(Context context, String id, String msgContent) {
            if (msgCallBack != null) {
                ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
                msgObj.setContent(msgContent);
                msgObj.setId(id);
                msgCallBack.sendMessageToRobot(msgObj, 1, 0, "");
            }
        }
    }


    public void refreshRevaluateItem() {
        if (message == null) {
            return;
        }
        //找不到顶和踩就返回
        if (sobot_tv_likeBtn == null ||
                sobot_tv_dislikeBtn == null ||
                sobot_ll_likeBtn == null ||
                sobot_ll_dislikeBtn == null) {
            return;
        }
        //顶 踩的状态 0 不显示顶踩按钮  1显示顶踩 按钮  2 显示顶之后的view  3显示踩之后view
        switch (message.getRevaluateState()) {
            case 1:
                showRevaluateBtn();
                break;
            case 2:
                showLikeWordView();
                break;
            case 3:
                showDislikeWordView();
                break;
            default:
                hideRevaluateBtn();
                break;
        }
    }

    /**
     * 显示 顶踩 按钮
     */
    public void showRevaluateBtn() {
        sobot_tv_likeBtn.setVisibility(View.VISIBLE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        sobot_ll_likeBtn.setVisibility(View.VISIBLE);
        sobot_ll_dislikeBtn.setVisibility(View.VISIBLE);
        rightEmptyRL.setVisibility(View.VISIBLE);
        //有顶和踩时显示信息显示两行 72-10-10=52 总高度减去上下内间距
        msg.setMinHeight(ScreenUtils.dip2px(mContext, 52));
        sobot_tv_likeBtn.setEnabled(true);
        sobot_tv_dislikeBtn.setEnabled(true);
        sobot_tv_likeBtn.setSelected(false);
        sobot_tv_dislikeBtn.setSelected(false);
        sobot_tv_likeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(true);
            }
        });
        sobot_tv_dislikeBtn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                doRevaluate(false);
            }
        });
    }

    /**
     * 顶踩 操作
     *
     * @param revaluateFlag true 顶  false 踩
     */
    private void doRevaluate(boolean revaluateFlag) {
        if (msgCallBack != null && message != null) {
            msgCallBack.doRevaluate(revaluateFlag, message);
        }
    }

    /**
     * 隐藏 顶踩 按钮
     */
    public void hideRevaluateBtn() {
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.GONE);
        sobot_ll_likeBtn.setVisibility(View.GONE);
        sobot_ll_dislikeBtn.setVisibility(View.GONE);
        rightEmptyRL.setVisibility(View.GONE);
        //没有顶和踩时显示信息显示一行 42-10-10=22 总高度减去上下内间距
        msg.setMinHeight(ScreenUtils.dip2px(mContext, 22));
    }

    /**
     * 显示顶之后的view
     */
    public void showLikeWordView() {
        sobot_tv_likeBtn.setSelected(true);
        sobot_tv_likeBtn.setEnabled(false);
        sobot_tv_dislikeBtn.setEnabled(false);
        sobot_tv_dislikeBtn.setSelected(false);
        sobot_tv_likeBtn.setVisibility(View.VISIBLE);
        sobot_tv_dislikeBtn.setVisibility(View.GONE);
        sobot_ll_likeBtn.setVisibility(View.VISIBLE);
        sobot_ll_dislikeBtn.setVisibility(View.GONE);
        rightEmptyRL.setVisibility(View.VISIBLE);
        //有顶和踩时显示信息显示两行 72-10-10=52 总高度减去上下内间距
        msg.setMinHeight(ScreenUtils.dip2px(mContext, 52));
    }

    /**
     * 显示踩之后的view
     */
    public void showDislikeWordView() {
        sobot_tv_dislikeBtn.setSelected(true);
        sobot_tv_dislikeBtn.setEnabled(false);
        sobot_tv_likeBtn.setEnabled(false);
        sobot_tv_likeBtn.setSelected(false);
        sobot_tv_likeBtn.setVisibility(View.GONE);
        sobot_tv_dislikeBtn.setVisibility(View.VISIBLE);
        sobot_ll_likeBtn.setVisibility(View.GONE);
        sobot_ll_dislikeBtn.setVisibility(View.VISIBLE);
        rightEmptyRL.setVisibility(View.VISIBLE);
        //有顶和踩时显示信息显示两行 72-10-10=52 总高度减去上下内间距
        msg.setMinHeight(ScreenUtils.dip2px(mContext, 52));
    }
}
