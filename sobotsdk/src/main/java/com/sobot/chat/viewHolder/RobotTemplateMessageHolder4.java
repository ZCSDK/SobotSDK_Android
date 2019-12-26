package com.sobot.chat.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.activity.WebViewActivity;
import com.sobot.chat.api.model.SobotMultiDiaRespInfo;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.listener.NoDoubleClickListener;
import com.sobot.chat.utils.ChatUtils;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.ScreenUtils;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.List;
import java.util.Map;

public class RobotTemplateMessageHolder4 extends MessageHolderBase {

    private TextView sobot_template4_temp_title;
    private ImageView sobot_template4_thumbnail;
    private TextView sobot_template4_title;
    private TextView sobot_template4_summary;
    private TextView sobot_template4_anchor;

    public ZhiChiMessageBase message;
    private LinearLayout sobot_ll_transferBtn;//只包含转人工按钮
    private TextView sobot_tv_transferBtn;//机器人转人工按钮

    public RobotTemplateMessageHolder4(Context context, View convertView) {
        super(context, convertView);
        sobot_template4_temp_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template4_temp_title"));
        sobot_template4_thumbnail = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template4_thumbnail"));
        sobot_template4_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template4_title"));
        sobot_template4_summary = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template4_summary"));
        sobot_template4_anchor = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template4_anchor"));
        sobot_ll_transferBtn = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_ll_transferBtn"));
        sobot_tv_transferBtn = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_tv_transferBtn"));

    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        this.message=message;
        if (message.getAnswer() != null && message.getAnswer().getMultiDiaRespInfo() != null) {

            checkShowTransferBtn();
            final SobotMultiDiaRespInfo multiDiaRespInfo = message.getAnswer().getMultiDiaRespInfo();
            String msgStr = ChatUtils.getMultiMsgTitle(multiDiaRespInfo);
            if (!TextUtils.isEmpty(msgStr)) {
                HtmlTools.getInstance(context).setRichText(sobot_template4_temp_title, msgStr.replaceAll("\n", "<br/>"), getLinkTextColor());
                sobot_ll_content.setVisibility(View.VISIBLE);
            } else {
                sobot_ll_content.setVisibility(View.INVISIBLE);
            }
            if ("000000".equals(multiDiaRespInfo.getRetCode())) {
                final List<Map<String, String>> interfaceRetList = multiDiaRespInfo.getInterfaceRetList();
                if (interfaceRetList != null && interfaceRetList.size() > 0) {
                    final Map<String, String> interfaceRet = interfaceRetList.get(0);
                    if (interfaceRet != null && interfaceRet.size() > 0) {
                        setSuccessView();
                        sobot_template4_title.setText(interfaceRet.get("title"));
                        if (!TextUtils.isEmpty(interfaceRet.get("thumbnail"))) {
                            SobotBitmapUtil.display(context, interfaceRet.get("thumbnail"), sobot_template4_thumbnail, ResourceUtils.getIdByName(context, "drawable", "sobot_bg_default_long_pic"), ResourceUtils.getIdByName(context, "drawable", "sobot_bg_default_long_pic"));
                            sobot_template4_thumbnail.setVisibility(View.VISIBLE);
                        } else {
                            sobot_template4_thumbnail.setVisibility(View.GONE);
                        }
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

        refreshRevaluateItem();//左侧消息刷新顶和踩布局
    }

    private void setSuccessView() {
        sobot_template4_title.setVisibility(View.VISIBLE);
        sobot_template4_thumbnail.setVisibility(View.VISIBLE);
        sobot_template4_summary.setVisibility(View.VISIBLE);
        sobot_template4_anchor.setVisibility(View.VISIBLE);
    }

    private void setFailureView() {
        sobot_template4_title.setVisibility(View.VISIBLE);
        sobot_template4_thumbnail.setVisibility(View.GONE);
        sobot_template4_summary.setVisibility(View.GONE);
        sobot_template4_anchor.setVisibility(View.GONE);
    }

    private void checkShowTransferBtn() {
        if (message.getTransferType() == 4) {
            //4 多次命中 显示转人工
            showTransferBtn();
        } else {
            //隐藏转人工
            hideTransferBtn();
        }
    }


    /**
     * 隐藏转人工按钮
     */
    public void hideTransferBtn() {
        sobot_ll_transferBtn.setVisibility(View.GONE);
        sobot_tv_transferBtn.setVisibility(View.GONE);
        if (message != null) {
            message.setShowTransferBtn(false);
        }
    }

    /**
     * 显示转人工按钮
     */
    public void showTransferBtn() {
        sobot_tv_transferBtn.setVisibility(View.VISIBLE);
        sobot_ll_transferBtn.setVisibility(View.VISIBLE);
        if (message != null) {
            message.setShowTransferBtn(true);
        }
        sobot_ll_transferBtn.setOnClickListener(new NoDoubleClickListener() {

            @Override
            public void onNoDoubleClick(View v) {
                if (msgCallBack != null) {
                    msgCallBack.doClickTransferBtn();
                }
            }
        });
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
    }
}