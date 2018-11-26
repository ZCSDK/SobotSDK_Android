package com.sobot.chat.viewHolder;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sobot.chat.adapter.SobotMsgAdapter;
import com.sobot.chat.api.model.SobotQuestionRecommend;
import com.sobot.chat.api.model.ZhiChiMessageBase;
import com.sobot.chat.utils.HtmlTools;
import com.sobot.chat.utils.SobotBitmapUtil;
import com.sobot.chat.utils.CommonUtils;
import com.sobot.chat.utils.ResourceUtils;
import com.sobot.chat.viewHolder.base.MessageHolderBase;

import java.util.List;

/**
 * 热点问题引导的holder
 */
public class RobotQRMessageHolder extends MessageHolderBase {

    private TextView tv_title;
    private LinearLayout sobot_horizontal_scrollview_layout;
    private HorizontalScrollView sobot_horizontal_scrollview;

    public RobotQRMessageHolder(Context context, View convertView) {
        super(context, convertView);
        tv_title = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_msg"));
        sobot_horizontal_scrollview_layout = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template1_horizontal_scrollview_layout"));
        sobot_horizontal_scrollview = (HorizontalScrollView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template1_horizontal_scrollview"));
    }

    @Override
    public void bindData(final Context context, final ZhiChiMessageBase message) {
        if (message.getAnswer() != null && message.getAnswer().getQuestionRecommend() != null) {
            final SobotQuestionRecommend recommend = message.getAnswer().getQuestionRecommend();
            // 设置标题
            if (TextUtils.isEmpty(recommend.getGuide())) {
                tv_title.setVisibility(View.GONE);
            } else {
                HtmlTools.getInstance(context).setRichText(tv_title, recommend.getGuide(), getLinkTextColor());
                applyTextViewUIConfig(tv_title);
                tv_title.setVisibility(View.VISIBLE);
            }
            List<SobotQuestionRecommend.SobotQRMsgBean> msgResult = recommend.getMsg();
            if (msgResult != null && msgResult.size() > 0) {
                sobot_horizontal_scrollview.setVisibility(View.VISIBLE);
                int childCount = sobot_horizontal_scrollview_layout.getChildCount();
                for (int i = msgResult.size(); i < childCount; i++) {
                    sobot_horizontal_scrollview_layout.getChildAt(i).setVisibility(View.GONE);
                }
                for (int i = 0; i < msgResult.size(); i++) {
                    final SobotQuestionRecommend.SobotQRMsgBean qrMsgBean = msgResult.get(i);
                    QuestionRecommendViewHolder viewHolder;
                    if (i < childCount) {
                        View convertView = sobot_horizontal_scrollview_layout.getChildAt(i);
                        convertView.setVisibility(View.VISIBLE);
                        viewHolder = (QuestionRecommendViewHolder) convertView.getTag();
                    } else {
                        View view = View.inflate(context, ResourceUtils.getIdByName(context, "layout", "sobot_chat_msg_item_qr_item"), null);
                        viewHolder = new QuestionRecommendViewHolder(context, view,msgCallBack);
                        view.setTag(viewHolder);
                        sobot_horizontal_scrollview_layout.addView(view);
                    }
                    viewHolder.bindData(context, qrMsgBean, i == (msgResult.size() - 1));
                }
            } else {
                sobot_horizontal_scrollview.setVisibility(View.GONE);
            }
        }
    }

    public static class QuestionRecommendViewHolder implements OnClickListener {
        LinearLayout sobotLayout;
        ImageView sobotThumbnail;
        TextView sobotTitle;
        Context mContext;
        SobotQuestionRecommend.SobotQRMsgBean mQrMsgBean;
        SobotMsgAdapter.SobotMsgCallBack msgCallBack;

        private QuestionRecommendViewHolder(Context context, View convertView,SobotMsgAdapter.SobotMsgCallBack msgCallBack) {
            this.msgCallBack = msgCallBack;
            sobotLayout = (LinearLayout) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_template1_item"));
            sobotThumbnail = (ImageView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_item_thumbnail"));
            sobotTitle = (TextView) convertView.findViewById(ResourceUtils.getIdByName(context, "id", "sobot_item_title"));
        }

        public void bindData(final Context context, final SobotQuestionRecommend.SobotQRMsgBean qrMsgBean, boolean isLast) {
            mContext = context;
            mQrMsgBean = qrMsgBean;
            if (qrMsgBean != null) {
                SobotBitmapUtil.display(context, qrMsgBean.getIcon(), sobotThumbnail, 0, 0);
                sobotTitle.setText(TextUtils.isEmpty(qrMsgBean.getTitle()) ? qrMsgBean.getQuestion() : qrMsgBean.getTitle());
                sobotLayout.setOnClickListener(this);
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) sobotLayout.getLayoutParams();
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, isLast ? (int) CommonUtils.getDimensPix(context, "sobot_item_qr_divider") : 0, layoutParams.bottomMargin);
                sobotLayout.setLayoutParams(layoutParams);
            }
        }

        @Override
        public void onClick(View v) {
            if (msgCallBack == null || mQrMsgBean == null) {
                return;
            }
            // 点击发出问题
            ZhiChiMessageBase msgObj = new ZhiChiMessageBase();
            msgObj.setContent(mQrMsgBean.getQuestion());
            msgCallBack.sendMessageToRobot(msgObj, 0, 0, null);
        }
    }
}